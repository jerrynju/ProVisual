/*
 * Shared app store — domain-neutral. Holds projects, navigation and run-result
 * cache. Knows nothing about RF; it only speaks the schema contracts and talks
 * to the active DomainPlugin for templates, validation and calculation.
 */
import {
  createContext,
  useContext,
  useMemo,
  useReducer,
  type ReactNode,
} from 'react'
import type { DomainPlugin } from './domain'
import type {
  Design,
  Project,
  RunResult,
  Scenario,
  Status,
} from './types'
import { rfPlugin } from '../domains/rf/plugin'
import { makeInstance, uid, linearDesign } from '../domains/rf/factory'

export type TabKey = 'design' | 'run' | 'results' | 'library' | 'more'
export type Screen =
  | 'home'
  | 'design-overview'
  | 'flow'
  | 'canvas'
  | 'component'
  | 'run'
  | 'results'
  | 'library'
  | 'issues'
  | 'scenarios'
  | 'more'

export interface ScreenRef {
  tab: TabKey
  screen: Screen
  params?: Record<string, string>
}

interface State {
  plugin: DomainPlugin
  projects: Project[]
  activeProjectId: string | null
  activeDesignId: string | null
  stack: ScreenRef[]                       // navigation history (top = current)
  results: Record<string, RunResult>       // keyed by designId
  runHash: Record<string, string>          // design hash captured at run time
}

type Action =
  | { type: 'NAV'; ref: ScreenRef }
  | { type: 'BACK' }
  | { type: 'TAB'; tab: TabKey }
  | { type: 'NEW_FROM_TEMPLATE'; templateId: string }
  | { type: 'OPEN_PROJECT'; projectId: string }
  | { type: 'ADD_COMPONENT'; schemaId: string; atIndex?: number }
  | { type: 'RENAME_COMPONENT'; compUid: string; name: string }
  | { type: 'SET_PARAM'; compUid: string; key: string; value: number | string }
  | { type: 'TOGGLE_DISABLE'; compUid: string }
  | { type: 'REMOVE_COMPONENT'; compUid: string }
  | { type: 'REPLACE_COMPONENT'; compUid: string; schemaId: string }
  | { type: 'MOVE_COMPONENT'; compUid: string; dir: -1 | 1 }
  | { type: 'SET_CANVAS_POS'; compUid: string; x: number; y: number }
  | { type: 'CONNECT'; from: string; to: string }
  | { type: 'RUN' }
  | { type: 'NEW_SCENARIO' }
  | { type: 'DUPLICATE_SCENARIO'; scenarioId: string }
  | { type: 'SET_ACTIVE_SCENARIO'; scenarioId: string }
  | { type: 'SET_BASELINE'; scenarioId: string }
  | { type: 'RENAME_SCENARIO'; scenarioId: string; name: string }
  | { type: 'DELETE_PROJECT'; projectId: string }

const tabRoot: Record<TabKey, Screen> = {
  design: 'design-overview',
  run: 'run',
  results: 'results',
  library: 'library',
  more: 'more',
}

function hashDesign(d: Design): string {
  return JSON.stringify({
    c: d.components.map((c) => [c.uid, c.schemaId, c.disabled, c.params]),
    n: d.connections.map((x) => [x.from, x.to]),
    s: d.activeScenarioId,
    o: d.scenarios.find((s) => s.id === d.activeScenarioId)?.overrides,
  })
}

function activeDesign(s: State): Design | null {
  const p = s.projects.find((x) => x.id === s.activeProjectId)
  if (!p) return null
  return p.designs.find((d) => d.id === s.activeDesignId) ?? p.designs[0] ?? null
}

/** Apply an updater to the active design immutably. */
function updateDesign(s: State, fn: (d: Design) => Design): State {
  return {
    ...s,
    projects: s.projects.map((p) =>
      p.id !== s.activeProjectId
        ? p
        : {
            ...p,
            updatedAt: Date.now(),
            designs: p.designs.map((d) => (d.id === (s.activeDesignId ?? p.designs[0]?.id) ? fn(d) : d)),
          },
    ),
  }
}

/** Write a parameter to the active scenario: baseline edits base params, others edit overrides. */
function setParam(d: Design, compUid: string, key: string, value: number | string): Design {
  const sc = d.scenarios.find((x) => x.id === d.activeScenarioId)
  if (sc && !sc.baseline) {
    return {
      ...d,
      scenarios: d.scenarios.map((x) =>
        x.id !== sc.id
          ? x
          : { ...x, overrides: { ...x.overrides, [compUid]: { ...x.overrides[compUid], [key]: value } } },
      ),
    }
  }
  return {
    ...d,
    components: d.components.map((c) => (c.uid === compUid ? { ...c, params: { ...c.params, [key]: value } } : c)),
  }
}

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'NAV':
      return { ...state, stack: [...state.stack, action.ref] }
    case 'BACK':
      return state.stack.length > 1 ? { ...state, stack: state.stack.slice(0, -1) } : state
    case 'TAB': {
      const hasDesign = !!activeDesign(state)
      // §3.3 设计 tab: land on project home when no design is open yet.
      const screen = action.tab === 'design' && !hasDesign ? 'home' : tabRoot[action.tab]
      const ref: ScreenRef = { tab: action.tab, screen }
      return { ...state, stack: [ref] }
    }
    case 'NEW_FROM_TEMPLATE': {
      const tpl = state.plugin.templates.find((t) => t.id === action.templateId)
      if (!tpl) return state
      const base = tpl.build()
      const designId = uid('d')
      const design: Design = { ...base, id: designId, name: tpl.name, domainId: state.plugin.id }
      const project: Project = {
        id: uid('p'),
        name: tpl.name,
        domainId: state.plugin.id,
        context: tpl.appliesTo,
        designs: [design],
        createdAt: Date.now(),
        updatedAt: Date.now(),
      }
      return {
        ...state,
        projects: [project, ...state.projects],
        activeProjectId: project.id,
        activeDesignId: designId,
        stack: [{ tab: 'design', screen: 'design-overview' }],
      }
    }
    case 'OPEN_PROJECT': {
      const p = state.projects.find((x) => x.id === action.projectId)
      if (!p) return state
      return {
        ...state,
        activeProjectId: p.id,
        activeDesignId: p.designs[0]?.id ?? null,
        stack: [{ tab: 'design', screen: 'design-overview' }],
      }
    }
    case 'DELETE_PROJECT': {
      const projects = state.projects.filter((p) => p.id !== action.projectId)
      const wasActive = state.activeProjectId === action.projectId
      return {
        ...state,
        projects,
        activeProjectId: wasActive ? null : state.activeProjectId,
        activeDesignId: wasActive ? null : state.activeDesignId,
        stack: wasActive ? [{ tab: 'design', screen: 'home' }] : state.stack,
      }
    }
    case 'ADD_COMPONENT':
      return updateDesign(state, (d) => {
        const inst = makeInstance(action.schemaId)
        const at = action.atIndex ?? d.components.length
        const components = [...d.components.slice(0, at), inst, ...d.components.slice(at)]
        return rewire({ ...d, components })
      })
    case 'RENAME_COMPONENT':
      return updateDesign(state, (d) => ({
        ...d,
        components: d.components.map((c) => (c.uid === action.compUid ? { ...c, name: action.name } : c)),
      }))
    case 'SET_PARAM':
      return updateDesign(state, (d) => setParam(d, action.compUid, action.key, action.value))
    case 'TOGGLE_DISABLE':
      return updateDesign(state, (d) => ({
        ...d,
        components: d.components.map((c) => (c.uid === action.compUid ? { ...c, disabled: !c.disabled } : c)),
      }))
    case 'REMOVE_COMPONENT':
      return updateDesign(state, (d) =>
        rewire({ ...d, components: d.components.filter((c) => c.uid !== action.compUid) }),
      )
    case 'REPLACE_COMPONENT':
      return updateDesign(state, (d) => ({
        ...d,
        components: d.components.map((c) =>
          c.uid === action.compUid ? { ...makeInstance(action.schemaId, undefined), uid: c.uid, pos: c.pos } : c,
        ),
      }))
    case 'MOVE_COMPONENT':
      return updateDesign(state, (d) => {
        const i = d.components.findIndex((c) => c.uid === action.compUid)
        const j = i + action.dir
        if (i < 0 || j < 0 || j >= d.components.length) return d
        const components = [...d.components]
        ;[components[i], components[j]] = [components[j], components[i]]
        return rewire({ ...d, components })
      })
    case 'SET_CANVAS_POS':
      return updateDesign(state, (d) => ({
        ...d,
        components: d.components.map((c) =>
          c.uid === action.compUid ? { ...c, pos: { x: action.x, y: action.y } } : c,
        ),
      }))
    case 'CONNECT':
      return updateDesign(state, (d) => {
        if (action.from === action.to) return d
        if (d.connections.some((c) => c.from === action.from && c.to === action.to)) return d
        return { ...d, connections: [...d.connections, { id: uid('conn'), from: action.from, to: action.to }] }
      })
    case 'RUN': {
      const d = activeDesign(state)
      if (!d) return state
      const result = state.plugin.calculate(d, d.activeScenarioId)
      return {
        ...state,
        results: { ...state.results, [d.id]: result },
        runHash: { ...state.runHash, [d.id]: hashDesign(d) },
      }
    }
    case 'NEW_SCENARIO':
      return updateDesign(state, (d) => {
        const sc: Scenario = { id: uid('sc'), name: `场景 ${d.scenarios.length + 1}`, overrides: {} }
        return { ...d, scenarios: [...d.scenarios, sc], activeScenarioId: sc.id }
      })
    case 'DUPLICATE_SCENARIO':
      return updateDesign(state, (d) => {
        const src = d.scenarios.find((s) => s.id === action.scenarioId)
        if (!src) return d
        const sc: Scenario = {
          id: uid('sc'),
          name: `${src.name} 副本`,
          overrides: JSON.parse(JSON.stringify(src.overrides)),
        }
        return { ...d, scenarios: [...d.scenarios, sc], activeScenarioId: sc.id }
      })
    case 'SET_ACTIVE_SCENARIO':
      return updateDesign(state, (d) => ({ ...d, activeScenarioId: action.scenarioId }))
    case 'SET_BASELINE':
      return updateDesign(state, (d) => ({
        ...d,
        scenarios: d.scenarios.map((s) => ({ ...s, baseline: s.id === action.scenarioId })),
      }))
    case 'RENAME_SCENARIO':
      return updateDesign(state, (d) => ({
        ...d,
        scenarios: d.scenarios.map((s) => (s.id === action.scenarioId ? { ...s, name: action.name } : s)),
      }))
    default:
      return state
  }
}

/** Rebuild linear connections to match component order (flow view is linear). */
function rewire(d: Design): Design {
  const connections = []
  for (let i = 0; i < d.components.length - 1; i++) {
    connections.push({ id: uid('conn'), from: d.components[i].uid, to: d.components[i + 1].uid })
  }
  return { ...d, connections }
}

function seedProjects(): Project[] {
  const tpl = rfPlugin.templates.find((t) => t.id === 'rx-chain')!
  const base = tpl.build()
  const design: Design = { ...base, id: uid('d'), name: '2.4G 接收链路', domainId: rfPlugin.id }
  return [
    {
      id: uid('p'),
      name: '2.4G 接收机研究',
      domainId: rfPlugin.id,
      context: tpl.appliesTo,
      designs: [design],
      createdAt: Date.now() - 86400000,
      updatedAt: Date.now() - 3600000,
    },
  ]
}

function init(): State {
  const projects = seedProjects()
  return {
    plugin: rfPlugin,
    projects,
    activeProjectId: null,
    activeDesignId: null,
    stack: [{ tab: 'design', screen: 'home' }],
    results: {},
    runHash: {},
  }
}

interface StoreValue {
  state: State
  dispatch: React.Dispatch<Action>
  // selectors
  plugin: DomainPlugin
  project: Project | null
  design: Design | null
  current: ScreenRef
  canBack: boolean
  result: RunResult | null
  designStatus: (d: Design) => Status
}

const StoreCtx = createContext<StoreValue | null>(null)

export function StoreProvider({ children }: { children: ReactNode }) {
  const [state, dispatch] = useReducer(reducer, undefined, init)

  const value = useMemo<StoreValue>(() => {
    const project = state.projects.find((p) => p.id === state.activeProjectId) ?? null
    const design = activeDesign(state)
    const current = state.stack[state.stack.length - 1]
    const result = design ? state.results[design.id] ?? null : null

    const designStatus = (d: Design): Status => {
      const issues = state.plugin.validate(d)
      if (issues.some((i) => i.severity === 'error')) return 'error'
      const ran = state.results[d.id]
      const isStale = ran && state.runHash[d.id] !== hashDesign(d)
      if (!ran) return issues.some((i) => i.severity === 'warning') ? 'warning' : 'ready'
      if (isStale) return 'stale'
      if (issues.some((i) => i.severity === 'warning')) return 'warning'
      return 'done'
    }

    return {
      state,
      dispatch,
      plugin: state.plugin,
      project,
      design,
      current,
      canBack: state.stack.length > 1,
      result,
      designStatus,
    }
  }, [state])

  return <StoreCtx.Provider value={value}>{children}</StoreCtx.Provider>
}

export function useStore(): StoreValue {
  const v = useContext(StoreCtx)
  if (!v) throw new Error('useStore must be used within StoreProvider')
  return v
}

export { hashDesign, linearDesign }
