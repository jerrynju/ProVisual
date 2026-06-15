/*
 * Shared schema contracts — domain-neutral.
 * These types are the ONLY thing the shared UI and shell understand.
 * Domain plugins (e.g. RF) describe their components, results and issues with
 * these structures; they never reach into the shared UI with domain formulas.
 *
 * Source of truth: §3.2 共享对象模型, §3.6 共享状态, §5.3 Schema 合约.
 */

/* ---- §3.6 共享状态 (shared status model) ---- */
export type Status =
  | 'unconfigured' // 未配置 — 缺少必要信息
  | 'ready'        // 就绪 — 可以运行
  | 'running'      // 运行中
  | 'done'         // 已完成 — 最近一次结果有效
  | 'stale'        // 需更新 — 参数/输入在最近一次运行后变化
  | 'warning'      // 警告 — 可用但存在风险
  | 'error'        // 错误 — 无法产生有效结果
  | 'disabled'     // 已禁用 — 不参与执行

export type Severity = 'error' | 'warning' | 'info'

/* ---- Parameter schema (§3.7 参数输入规则) ---- */
export type ParamControl = 'number' | 'slider' | 'stepper' | 'text' | 'select' | 'expression'

export interface ParamSchema {
  key: string
  label: string
  unit?: string          // 带单位的值是一等输入
  control: ParamControl
  hint?: string          // 简短说明
  min?: number
  max?: number
  step?: number
  /** recommended operating range, shown inline (§3.7) */
  recommended?: [number, number]
  options?: { value: string; label: string }[]
  advanced?: boolean     // 高级参数默认折叠
  readOnly?: boolean     // 派生值标记为只读 (§6.6)
  /** default value used by templates / when adding from library */
  default?: number | string
}

/* ---- Port schema (inputs / outputs render from schema, §3.4.5) ---- */
export interface PortSchema {
  key: string
  label: string
  unit?: string
}

/* ---- A small key/role display field shown on a component card (§3.5) ---- */
export interface DisplayField {
  key: string            // references a parameter key or a derived output key
  label: string
  unit?: string
  source?: 'param' | 'output'
}

/* ---- Component schema (§5.3 最小组件 schema) ---- */
export interface ComponentSchema {
  id: string             // schema id, e.g. "rf.amplifier"
  name: string           // default display name
  role: string           // human role label, e.g. "增益级"
  category: string       // category id; maps to category color tokens
  parameters: ParamSchema[]
  inputs: PortSchema[]
  outputs: PortSchema[]
  display: DisplayField[]   // which 2–3 values to surface on the standard card
  advanced?: string[]       // parameter keys treated as advanced
  /** short natural-language description for the library (§3.8) */
  description?: string
  icon?: string
}

/* ---- Runtime instance of a component placed in a design ---- */
export interface ComponentInstance {
  uid: string                       // unique within a design
  schemaId: string
  name: string                      // user-editable name
  params: Record<string, number | string>
  disabled?: boolean
  /** optional manual canvas position (§3.4.4 默认自动布局, 手动可选) */
  pos?: { x: number; y: number }
}

/* ---- Connection (§3.2) — ordered link between two components ---- */
export interface Connection {
  id: string
  from: string // component uid
  to: string   // component uid
}

/* ---- Scenario: a named set of parameter values (§3.9) ---- */
export interface Scenario {
  id: string
  name: string
  baseline?: boolean
  /** sparse overrides: componentUid -> paramKey -> value */
  overrides: Record<string, Record<string, number | string>>
}

/* ---- Design ---- */
export interface Design {
  id: string
  name: string
  domainId: string
  components: ComponentInstance[]
  connections: Connection[]
  scenarios: Scenario[]
  activeScenarioId: string
  /** uid of the last component computed against; used by results */
  lastRunAt?: number
}

/* ---- Project (§3.2) ---- */
export interface Project {
  id: string
  name: string
  domainId: string
  context?: string
  designs: Design[]
  createdAt: number
  updatedAt: number
}

/* ---- Result schema (§5.3 最小结果 schema) ---- */
export interface KeyValue {
  key: string
  label: string
  value: number | string
  unit?: string
  status?: Status        // optional status tint for the value
  hint?: string
  /** emphasize as a hero metric */
  emphasis?: boolean
}

export type ChartKind = 'line' | 'bar' | 'stacked-bar'

export interface ChartSeries {
  name: string
  color?: string
  points: { x: number | string; y: number }[]
}

export interface ChartSpec {
  id: string
  kind: ChartKind
  title: string
  xLabel?: string
  yLabel?: string
  yUnit?: string
  series: ChartSeries[]
  /** optional horizontal reference line, e.g. a limit/threshold */
  reference?: { value: number; label: string; color?: string }
  explanation?: string
}

export interface SuggestedAction {
  label: string
  /** what tapping this should do, interpreted by the shell */
  intent?: { type: 'open-component'; uid: string } | { type: 'open-issues' } | { type: 'open-run' }
}

/* ---- Issue schema (§5.3 最小问题 schema) ---- */
export interface Issue {
  id: string
  severity: Severity
  title: string
  affectedObject?: string       // component uid or label
  affectedLabel?: string
  explanation: string           // natural language (§2.6)
  suggestedActions: SuggestedAction[]
  jumpTarget?: { type: 'component'; uid: string }
}

export interface RawRow {
  label: string
  values: { key: string; value: string }[]
}

export interface RunResult {
  status: Status
  summary: string                // natural-language overall explanation
  keyValues: KeyValue[]
  charts: ChartSpec[]
  issues: Issue[]
  rawRows: RawRow[]
  suggestedActions: SuggestedAction[]
  /** per-component derived outputs, keyed by uid, for cards & details */
  outputs: Record<string, Record<string, number | string>>
  /** per-component status, keyed by uid */
  componentStatus: Record<string, Status>
}
