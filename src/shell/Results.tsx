/*
 * Results overview (§3.4.7, §6.7). Explains what happened: overall status,
 * key values, charts, scenario compare, explanation, suggested actions, then
 * raw data last (§3.10 原始数据不是第一层).
 */
import { useState } from 'react'
import { useStore } from '../shared/store'
import { Chart } from '../shared/ui/Chart'
import { KeyMetricGrid, SectionHeader, StatusBadge } from '../shared/ui/primitives'
import { IssueCard } from '../shared/ui/IssueCard'
import { STATUS_SOFT } from '../shared/ui/status'
import type { SuggestedAction } from '../shared/types'

export function Results() {
  const { design, plugin, result, dispatch, state } = useStore()
  const [showRaw, setShowRaw] = useState(false)
  if (!design) return <div className="empty">请先创建并运行一个链路。</div>
  if (!result) {
    return (
      <div className="page fade-in">
        <h1 className="page__title">结果</h1>
        <div className="empty">尚未运行。前往「运行」开始计算，结果会显示在这里。</div>
        <button className="btn btn--primary btn--block" onClick={() => { dispatch({ type: 'RUN' }) }}>立即运行</button>
      </div>
    )
  }

  const onAction = (a: SuggestedAction) => {
    if (!a.intent) return
    if (a.intent.type === 'open-component') dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'component', params: { uid: a.intent.uid } } })
    else if (a.intent.type === 'open-issues') dispatch({ type: 'NAV', ref: { tab: 'results', screen: 'issues' } })
    else if (a.intent.type === 'open-run') dispatch({ type: 'NAV', ref: { tab: 'run', screen: 'run' } })
  }
  const onJump = (uid: string) => dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'component', params: { uid } } })

  const isStale = state.runHash[design.id] !== undefined && state.runHash[design.id] !== hashOf(design)
  const scenarios = design.scenarios
  const compareKeys = ['margin', 'totalGain', 'outputPower', 'systemNf'] as const

  return (
    <div className="page fade-in">
      <div className="spread">
        <h1 className="page__title">结果</h1>
        <StatusBadge status={result.status} />
      </div>

      {isStale && (
        <div className="summary" style={{ background: STATUS_SOFT.stale }}>
          <div className="summary__text">参数在上次运行后发生变化，结果需更新。</div>
          <button className="btn btn--sm btn--secondary" style={{ alignSelf: 'flex-start', marginTop: 6 }} onClick={() => dispatch({ type: 'RUN' })}>重新运行</button>
        </div>
      )}

      {/* 1. overall explanation */}
      <div className="summary" style={{ background: STATUS_SOFT[result.status] }}>
        <div className="summary__text">{result.summary}</div>
      </div>

      {/* 2. key values */}
      <KeyMetricGrid items={result.keyValues} />

      {/* 3. charts */}
      {result.charts.map((c) => (
        <Chart key={c.id} spec={c} />
      ))}

      {/* 4. scenario compare (§3.9) */}
      {scenarios.length > 1 && (
        <>
          <SectionHeader title="场景对比" />
          <div className="card" style={{ overflowX: 'auto' }}>
            <table className="compare">
              <thead>
                <tr>
                  <th>指标</th>
                  {scenarios.map((s) => <th key={s.id}>{s.name}{s.baseline ? ' ·基准' : ''}</th>)}
                </tr>
              </thead>
              <tbody>
                {compareKeys.map((k) => {
                  const base = scenarios.find((s) => s.baseline) ?? scenarios[0]
                  const baseRes = plugin.calculate(design, base.id)
                  const baseKv = baseRes.keyValues.find((x) => x.key === k)
                  return (
                    <tr key={k}>
                      <td>{baseKv?.label}</td>
                      {scenarios.map((s) => {
                        const res = plugin.calculate(design, s.id)
                        const kv = res.keyValues.find((x) => x.key === k)
                        const v = Number(kv?.value)
                        const bv = Number(baseKv?.value)
                        const delta = Number.isFinite(v) && Number.isFinite(bv) ? v - bv : NaN
                        const cls = !s.baseline && Number.isFinite(delta) && delta !== 0 ? (delta > 0 ? 'delta-up' : 'delta-down') : ''
                        return (
                          <td key={s.id} className={cls}>
                            {kv?.value}{kv?.unit ? ` ${kv.unit}` : ''}
                            {!s.baseline && Number.isFinite(delta) && delta !== 0 && (
                              <span className="meta"> ({delta > 0 ? '+' : ''}{delta.toFixed(1)})</span>
                            )}
                          </td>
                        )
                      })}
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        </>
      )}

      {/* 5. issues + suggestions */}
      {result.issues.length > 0 && (
        <>
          <SectionHeader title="问题与建议" action={<button className="section-head__link" onClick={() => dispatch({ type: 'NAV', ref: { tab: 'results', screen: 'issues' } })}>全部</button>} />
          <div className="stack">
            {result.issues.slice(0, 2).map((i) => (
              <IssueCard key={i.id} issue={i} onAction={onAction} onJump={onJump} />
            ))}
          </div>
        </>
      )}

      {result.suggestedActions.length > 0 && (
        <div className="chips">
          {result.suggestedActions.map((a, i) => (
            <button key={i} className="btn btn--sm btn--secondary" onClick={() => onAction(a)}>{a.label}</button>
          ))}
        </div>
      )}

      {/* 6. export + raw data (last) */}
      <div className="grid-2">
        <button className="btn btn--secondary" onClick={() => exportResult(design.name, result)}>导出 CSV</button>
        <button className="btn btn--secondary" onClick={() => setShowRaw((v) => !v)}>{showRaw ? '隐藏原始数据' : '查看原始数据'}</button>
      </div>

      {showRaw && (
        <div className="card" style={{ overflowX: 'auto' }}>
          <table className="raw">
            <thead>
              <tr><th>元件</th><th>角色</th><th>输入</th><th>输出</th><th>增益</th><th>NF</th></tr>
            </thead>
            <tbody>
              {result.rawRows.map((r, i) => (
                <tr key={i}>
                  <td>{r.label}</td>
                  {r.values.map((v) => <td key={v.key}>{v.value}</td>)}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

function hashOf(design: import('../shared/types').Design): string {
  // mirror of store.hashDesign for staleness display
  return JSON.stringify({
    c: design.components.map((c) => [c.uid, c.schemaId, c.disabled, c.params]),
    n: design.connections.map((x) => [x.from, x.to]),
    s: design.activeScenarioId,
    o: design.scenarios.find((s) => s.id === design.activeScenarioId)?.overrides,
  })
}

function exportResult(name: string, result: import('../shared/types').RunResult) {
  const header = ['元件', '角色', '输入(dBm)', '输出(dBm)', '增益(dB)', 'NF(dB)']
  const lines = [header.join(',')]
  for (const r of result.rawRows) lines.push([r.label, ...r.values.map((v) => v.value)].join(','))
  const blob = new Blob([lines.join('\n')], { type: 'text/csv' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${name}-结果.csv`
  a.click()
  URL.revokeObjectURL(url)
}
