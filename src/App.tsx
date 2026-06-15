/*
 * App shell. Wires the top status bar, the screen router and the bottom
 * navigation. Domain-neutral: it routes by generic Screen keys and reads the
 * active DomainPlugin only for labels and validation counts.
 */
import { useEffect } from 'react'
import { StoreProvider, useStore, type TabKey } from './shared/store'
import { BottomNav } from './shared/ui/BottomNav'
import { ProjectHome } from './shell/ProjectHome'
import { DesignOverview } from './shell/DesignOverview'
import { FlowView } from './shell/FlowView'
import { CanvasView } from './shell/CanvasView'
import { ComponentDetails } from './shell/ComponentDetails'
import { RunSettings } from './shell/RunSettings'
import { Results } from './shell/Results'
import { Library } from './shell/Library'
import { Issues } from './shell/Issues'
import { Scenarios } from './shell/Scenarios'
import { More } from './shell/More'

const TITLES: Record<string, string> = {
  home: '项目',
  'design-overview': '设计',
  flow: '链路',
  canvas: '画布',
  component: '组件详情',
  run: '运行',
  results: '结果',
  library: '库',
  issues: '问题',
  scenarios: '场景',
  more: '更多',
}

// screens that are tab roots (no back button)
const ROOTS = new Set(['home', 'design-overview', 'run', 'results', 'library', 'more'])

function Shell() {
  const { state, plugin, design, current, canBack, dispatch } = useStore()

  // Inject the domain's default category tokens (§4.4 共享 UI 只消费 token).
  useEffect(() => {
    const c = plugin.categories[0]
    if (!c) return
    const root = document.documentElement.style
    root.setProperty('--category-primary', c.primary)
    root.setProperty('--category-secondary', c.secondary)
    root.setProperty('--category-accent', c.accent)
  }, [plugin])

  const issueCount = design ? plugin.validate(design).length : 0
  const showBack = canBack && !ROOTS.has(current.screen)

  const onTab = (tab: TabKey) => dispatch({ type: 'TAB', tab })

  const screen = current.screen
  const title = current.tab === 'design' && screen === 'home' ? plugin.label : TITLES[screen] ?? plugin.label

  return (
    <div className="app-frame">
      <header className="appbar">
        {showBack ? (
          <button className="appbar__back" onClick={() => dispatch({ type: 'BACK' })} aria-label="返回">
            ‹
          </button>
        ) : null}
        <div className="appbar__title">
          <h1>{title}</h1>
          {design && screen !== 'home' && <p>{design.name}</p>}
        </div>
        {!showBack && state.projects.length > 0 && screen !== 'home' && current.tab === 'design' && (
          <button className="appbar__action" onClick={() => dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'home' } })}>
            项目
          </button>
        )}
      </header>

      <main className="app-scroll">
        <Router screen={screen} params={current.params} />
      </main>

      <BottomNav active={current.tab} issueCount={issueCount} onSelect={onTab} />
    </div>
  )
}

function Router({ screen, params }: { screen: string; params?: Record<string, string> }) {
  switch (screen) {
    case 'home':
      return <ProjectHome />
    case 'design-overview':
      return <DesignOverview />
    case 'flow':
      return <FlowView />
    case 'canvas':
      return <CanvasView />
    case 'component':
      return <ComponentDetails uid={params?.uid ?? ''} />
    case 'run':
      return <RunSettings />
    case 'results':
      return <Results />
    case 'library':
      return <Library />
    case 'issues':
      return <Issues />
    case 'scenarios':
      return <Scenarios />
    case 'more':
      return <More />
    default:
      return <ProjectHome />
  }
}

export default function App() {
  return (
    <StoreProvider>
      <Shell />
    </StoreProvider>
  )
}
