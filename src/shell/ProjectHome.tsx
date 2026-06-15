/*
 * Project home (§3.4.1). Lands in a useful state: recent work, templates,
 * run status, open issues, continue. Never a blank editor.
 */
import { useStore } from '../shared/store'
import { Card, SectionHeader, StatusBadge } from '../shared/ui/primitives'
import { STATUS_COLOR } from '../shared/ui/status'

function ago(ts: number): string {
  const d = Date.now() - ts
  const h = Math.floor(d / 3600000)
  if (h < 1) return '刚刚'
  if (h < 24) return `${h} 小时前`
  return `${Math.floor(h / 24)} 天前`
}

export function ProjectHome() {
  const { state, plugin, dispatch, designStatus } = useStore()
  const projects = state.projects
  const pinnedTemplates = plugin.templates.filter((t) => ['simple-link', 'rx-chain', 'link-budget'].includes(t.id))

  return (
    <div className="page fade-in">
      <h1 className="page__title">{plugin.label}</h1>
      <p className="muted" style={{ marginTop: -8 }}>{plugin.tagline}</p>

      {projects.length > 0 && (
        <>
          <SectionHeader title="最近项目" />
          <div className="stack">
            {projects.map((p) => {
              const d = p.designs[0]
              const status = d ? designStatus(d) : 'ready'
              const issues = d ? plugin.validate(d).length : 0
              return (
                <Card key={p.id} onClick={() => dispatch({ type: 'OPEN_PROJECT', projectId: p.id })} accent={STATUS_COLOR[status]}>
                  <div className="spread">
                    <div style={{ minWidth: 0 }}>
                      <div style={{ fontWeight: 700, fontSize: 'var(--font-card-title)' }}>{p.name}</div>
                      <div className="meta" style={{ marginTop: 2 }}>
                        {d?.name} · {ago(p.updatedAt)}
                      </div>
                    </div>
                    <StatusBadge status={status} />
                  </div>
                  {issues > 0 && (
                    <div className="meta" style={{ marginTop: 8, color: 'var(--color-warning)' }}>
                      {issues} 个未处理问题
                    </div>
                  )}
                </Card>
              )
            })}
          </div>
        </>
      )}

      <SectionHeader title="从模板开始" />
      <div className="stack">
        {pinnedTemplates.map((t) => (
          <button key={t.id} className="tpl" onClick={() => dispatch({ type: 'NEW_FROM_TEMPLATE', templateId: t.id })}>
            <div className="tpl__name">
              {t.name}
              <span style={{ color: 'var(--color-primary)', fontSize: 'var(--font-body)' }}>使用 ›</span>
            </div>
            <p className="tpl__desc">{t.description}</p>
            <div className="tpl__meta">适用：{t.appliesTo}</div>
          </button>
        ))}
        <button className="btn btn--secondary btn--block" onClick={() => dispatch({ type: 'TAB', tab: 'library' })}>
          浏览全部模板与库
        </button>
      </div>
    </div>
  )
}
