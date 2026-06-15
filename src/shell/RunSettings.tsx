/*
 * Run settings (§3.4.6). Understandable without opening raw config: current
 * scenario, run options, scope, validation status, start.
 */
import { useStore } from '../shared/store'
import { Card, SectionHeader, StatusBadge } from '../shared/ui/primitives'
import { STATUS_COLOR } from '../shared/ui/status'

export function RunSettings() {
  const { design, plugin, dispatch, designStatus } = useStore()
  if (!design) {
    return <div className="empty">请先从「设计」或模板创建一个链路。</div>
  }
  const issues = plugin.validate(design)
  const errors = issues.filter((i) => i.severity === 'error')
  const warnings = issues.filter((i) => i.severity === 'warning')
  const status = designStatus(design)
  const active = design.scenarios.find((s) => s.id === design.activeScenarioId)

  return (
    <div className="page fade-in">
      <h1 className="page__title">运行</h1>

      <SectionHeader title="场景" action={<button className="section-head__link" onClick={() => dispatch({ type: 'NAV', ref: { tab: 'run', screen: 'scenarios' } })}>管理</button>} />
      <div className="scenario-pills">
        {design.scenarios.map((s) => (
          <button
            key={s.id}
            className={`scenario-pill ${s.id === design.activeScenarioId ? 'is-active' : ''}`}
            onClick={() => dispatch({ type: 'SET_ACTIVE_SCENARIO', scenarioId: s.id })}
          >
            {s.name}{s.baseline ? ' ·基准' : ''}
          </button>
        ))}
      </div>

      <SectionHeader title="运行选项" />
      <Card>
        <div className="kv-line"><span className="kv-line__label">执行范围</span><span className="kv-line__value">当前场景</span></div>
        <div className="kv-line"><span className="kv-line__label">当前场景</span><span className="kv-line__value">{active?.name ?? '基准'}</span></div>
        <div className="kv-line"><span className="kv-line__label">组件数量</span><span className="kv-line__value">{design.components.filter((c) => !c.disabled).length} / {design.components.length}</span></div>
        <div className="kv-line"><span className="kv-line__label">校验状态</span><StatusBadge status={status} /></div>
      </Card>

      {(errors.length > 0 || warnings.length > 0) && (
        <Card accent={errors.length ? STATUS_COLOR.error : STATUS_COLOR.warning} onClick={() => dispatch({ type: 'NAV', ref: { tab: 'results', screen: 'issues' } })}>
          <div className="spread">
            <div>
              <div style={{ fontWeight: 700 }}>
                {errors.length > 0 ? `${errors.length} 个错误` : `${warnings.length} 个警告`}
              </div>
              <div className="meta" style={{ marginTop: 2 }}>
                {errors.length > 0 ? '需先解决才能运行' : '可运行，但建议检查'}
              </div>
            </div>
            <span className="cc__chevron">›</span>
          </div>
        </Card>
      )}

      <div className="action-bar">
        <button
          className="btn btn--primary btn--block"
          disabled={errors.length > 0}
          onClick={() => {
            dispatch({ type: 'RUN' })
            dispatch({ type: 'NAV', ref: { tab: 'results', screen: 'results' } })
          }}
        >
          开始运行
        </button>
      </div>
    </div>
  )
}
