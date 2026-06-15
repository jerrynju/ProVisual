/*
 * Design overview (§3.4.2, §6.4). Concept before detail: status, key result
 * cards, issue count, primary action (run / view issues), secondary actions.
 */
import { useState } from 'react'
import { useStore } from '../shared/store'
import { Card, KeyMetricGrid, SectionHeader, StatusBadge } from '../shared/ui/primitives'
import { STATUS_COLOR, STATUS_SOFT } from '../shared/ui/status'
import { AddComponentSheet } from './AddComponentSheet'

export function DesignOverview() {
  const { design, plugin, result, dispatch, designStatus, state } = useStore()
  const [adding, setAdding] = useState(false)
  if (!design) return null

  const status = designStatus(design)
  const issues = plugin.validate(design)
  const errorCount = issues.filter((i) => i.severity === 'error').length
  const ran = !!state.results[design.id]
  // §6.4 recommended key cards
  const heroCards = result
    ? result.keyValues.filter((k) => ['margin', 'totalGain', 'outputPower', 'systemNf'].includes(k.key))
    : []

  return (
    <div className="page fade-in">
      <div className="spread">
        <div>
          <h1 className="page__title" style={{ fontSize: 'var(--font-page-title)' }}>{design.name}</h1>
          <div className="meta" style={{ marginTop: 4 }}>{plugin.label}</div>
        </div>
        <StatusBadge status={status} />
      </div>

      {/* §2.2 顶部状态 explanation */}
      <div className="summary" style={{ background: STATUS_SOFT[status] }}>
        <div className="summary__text">
          {result?.summary ?? '尚未运行。点击下方「运行」计算当前链路的余量、增益与噪声。'}
        </div>
      </div>

      {heroCards.length > 0 && (
        <>
          <SectionHeader title="关键结果" action={<button className="section-head__link" onClick={() => dispatch({ type: 'NAV', ref: { tab: 'results', screen: 'results' } })}>查看全部</button>} />
          <KeyMetricGrid items={heroCards} />
        </>
      )}

      {issues.length > 0 && (
        <Card onClick={() => dispatch({ type: 'NAV', ref: { tab: 'results', screen: 'issues' } })} accent={errorCount ? STATUS_COLOR.error : STATUS_COLOR.warning}>
          <div className="spread">
            <div>
              <div style={{ fontWeight: 700 }}>{issues.length} 个问题待处理</div>
              <div className="meta" style={{ marginTop: 2 }}>
                {errorCount > 0 ? `${errorCount} 个错误需先解决` : '存在警告，可继续运行'}
              </div>
            </div>
            <span className="cc__chevron">›</span>
          </div>
        </Card>
      )}

      <div className="stack" style={{ marginTop: 'auto', paddingTop: 'var(--space-2)' }}>
        <button
          className="btn btn--primary btn--block"
          onClick={() => {
            dispatch({ type: 'RUN' })
            dispatch({ type: 'NAV', ref: { tab: 'results', screen: 'results' } })
          }}
          disabled={errorCount > 0}
        >
          {ran ? '重新运行' : '运行'}
        </button>
        {errorCount > 0 && (
          <button className="btn btn--secondary btn--block" onClick={() => dispatch({ type: 'NAV', ref: { tab: 'results', screen: 'issues' } })}>
            查看问题
          </button>
        )}
        <div className="grid-2">
          <button className="btn btn--secondary" onClick={() => dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'flow' } })}>
            编辑链路
          </button>
          <button className="btn btn--secondary" onClick={() => setAdding(true)}>
            添加组件
          </button>
        </div>
      </div>

      <AddComponentSheet open={adding} onClose={() => setAdding(false)} />
    </div>
  )
}
