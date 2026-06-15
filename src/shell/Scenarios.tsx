/*
 * Scenario management (§3.9). Create from current, duplicate & edit, compare,
 * mark baseline, run one or more. Comparison emphasizes result changes.
 */
import { useStore } from '../shared/store'
import { Card, SectionHeader } from '../shared/ui/primitives'

export function Scenarios() {
  const { design, plugin, dispatch } = useStore()
  if (!design) return <div className="empty">没有可用的设计。</div>

  return (
    <div className="page fade-in">
      <h1 className="page__title">场景</h1>
      <p className="muted" style={{ marginTop: -8 }}>场景是一组命名参数值。切换场景后编辑参数只影响该场景。</p>

      <div className="grid-2">
        <button className="btn btn--secondary" onClick={() => dispatch({ type: 'NEW_SCENARIO' })}>新建场景</button>
        <button className="btn btn--secondary" onClick={() => dispatch({ type: 'DUPLICATE_SCENARIO', scenarioId: design.activeScenarioId })}>复制当前</button>
      </div>

      <SectionHeader title="全部场景" />
      <div className="stack">
        {design.scenarios.map((s) => {
          const res = plugin.calculate(design, s.id)
          const margin = res.keyValues.find((k) => k.key === 'margin')
          const isActive = s.id === design.activeScenarioId
          return (
            <Card key={s.id} accent={isActive ? 'var(--color-primary)' : undefined}>
              <div className="spread">
                <input
                  className="title-input"
                  style={{ fontSize: 'var(--font-card-title)' }}
                  value={s.name}
                  onChange={(e) => dispatch({ type: 'RENAME_SCENARIO', scenarioId: s.id, name: e.target.value })}
                />
                {s.baseline && <span className="pill" style={{ color: 'var(--color-primary)' }}>基准</span>}
              </div>
              <div className="kv-line">
                <span className="kv-line__label">链路余量</span>
                <span className="kv-line__value">{margin?.value} {margin?.unit}</span>
              </div>
              <div className="grid-2" style={{ marginTop: 8 }}>
                <button className="btn btn--sm btn--secondary" onClick={() => { dispatch({ type: 'SET_ACTIVE_SCENARIO', scenarioId: s.id }); dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'flow' } }) }}>编辑此场景</button>
                <button className="btn btn--sm btn--secondary" onClick={() => dispatch({ type: 'SET_BASELINE', scenarioId: s.id })} disabled={s.baseline}>设为基准</button>
              </div>
            </Card>
          )
        })}
      </div>

      <button className="btn btn--primary btn--block" onClick={() => { dispatch({ type: 'RUN' }); dispatch({ type: 'NAV', ref: { tab: 'results', screen: 'results' } }) }}>
        运行并对比
      </button>
    </div>
  )
}
