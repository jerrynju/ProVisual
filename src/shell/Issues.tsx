/*
 * Issue center (§3.4.8). Aggregates validation & run feedback as actionable
 * cards (not logs). Logs live in advanced tools.
 */
import { useStore } from '../shared/store'
import { IssueCard } from '../shared/ui/IssueCard'
import type { SuggestedAction } from '../shared/types'

export function Issues() {
  const { design, plugin, dispatch } = useStore()
  if (!design) return <div className="empty">没有可检查的设计。</div>
  const issues = plugin.validate(design)
  const errors = issues.filter((i) => i.severity === 'error')
  const warnings = issues.filter((i) => i.severity === 'warning')
  const infos = issues.filter((i) => i.severity === 'info')

  const onAction = (a: SuggestedAction) => {
    if (!a.intent) return
    if (a.intent.type === 'open-component') dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'component', params: { uid: a.intent.uid } } })
    else if (a.intent.type === 'open-run') dispatch({ type: 'NAV', ref: { tab: 'run', screen: 'run' } })
  }
  const onJump = (uid: string) => dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'component', params: { uid } } })

  const Group = ({ title, list }: { title: string; list: typeof issues }) =>
    list.length === 0 ? null : (
      <>
        <h2 className="section-title">{title} ({list.length})</h2>
        <div className="stack">
          {list.map((i) => (
            <IssueCard key={i.id} issue={i} onAction={onAction} onJump={onJump} />
          ))}
        </div>
      </>
    )

  return (
    <div className="page fade-in">
      <h1 className="page__title">问题</h1>
      {issues.length === 0 ? (
        <div className="empty">没有未处理的问题，链路校验通过。</div>
      ) : (
        <>
          <p className="muted" style={{ marginTop: -8 }}>
            共 {issues.length} 项：{errors.length} 错误 · {warnings.length} 警告 · {infos.length} 提示
          </p>
          <Group title="错误" list={errors} />
          <Group title="警告" list={warnings} />
          <Group title="提示" list={infos} />
        </>
      )}
    </div>
  )
}
