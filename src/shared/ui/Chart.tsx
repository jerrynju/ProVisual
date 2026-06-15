/*
 * Generic chart container (§5.2 通用图表容器). Renders line / bar charts from a
 * ChartSpec with plain SVG. No domain knowledge; data & labels come from schema.
 */
import type { ChartSpec } from '../types'

const W = 320
const H = 168
const PAD_L = 36
const PAD_R = 12
const PAD_T = 14
const PAD_B = 34

export function Chart({ spec }: { spec: ChartSpec }) {
  const allY = spec.series.flatMap((s) => s.points.map((p) => p.y))
  if (spec.reference) allY.push(spec.reference.value)
  let minY = Math.min(0, ...allY)
  let maxY = Math.max(0, ...allY)
  if (minY === maxY) maxY = minY + 1
  const range = maxY - minY
  // pad range a touch
  minY -= range * 0.08
  maxY += range * 0.08

  const xs = spec.series[0]?.points.map((p) => String(p.x)) ?? []
  const n = xs.length
  const plotW = W - PAD_L - PAD_R
  const plotH = H - PAD_T - PAD_B
  const yToPx = (y: number) => PAD_T + plotH * (1 - (y - minY) / (maxY - minY))
  const xToPx = (i: number) => (n <= 1 ? PAD_L + plotW / 2 : PAD_L + (plotW * i) / (n - 1))
  const bandW = n > 0 ? plotW / n : plotW

  // gridlines (3)
  const ticks = [minY, (minY + maxY) / 2, maxY]

  return (
    <div className="chart">
      <div className="chart__title">{spec.title}</div>
      <svg viewBox={`0 0 ${W} ${H}`} className="chart__svg" role="img" aria-label={spec.title}>
        {ticks.map((t, i) => (
          <g key={i}>
            <line x1={PAD_L} y1={yToPx(t)} x2={W - PAD_R} y2={yToPx(t)} stroke="var(--color-border)" strokeWidth={1} />
            <text x={PAD_L - 6} y={yToPx(t) + 3} textAnchor="end" className="chart__tick">
              {t.toFixed(0)}
            </text>
          </g>
        ))}

        {spec.reference && (
          <g>
            <line
              x1={PAD_L}
              y1={yToPx(spec.reference.value)}
              x2={W - PAD_R}
              y2={yToPx(spec.reference.value)}
              stroke={spec.reference.color ?? 'var(--color-error)'}
              strokeWidth={1.5}
              strokeDasharray="4 3"
            />
            <text
              x={W - PAD_R}
              y={yToPx(spec.reference.value) - 4}
              textAnchor="end"
              className="chart__ref"
              fill={spec.reference.color ?? 'var(--color-error)'}
            >
              {spec.reference.label}
            </text>
          </g>
        )}

        {spec.kind === 'line'
          ? spec.series.map((s, si) => {
              const color = s.color ?? 'var(--color-primary)'
              const d = s.points
                .map((p, i) => `${i === 0 ? 'M' : 'L'} ${xToPx(i).toFixed(1)} ${yToPx(p.y).toFixed(1)}`)
                .join(' ')
              return (
                <g key={si}>
                  <path d={d} fill="none" stroke={color} strokeWidth={2} strokeLinejoin="round" strokeLinecap="round" />
                  {s.points.map((p, i) => (
                    <circle key={i} cx={xToPx(i)} cy={yToPx(p.y)} r={2.6} fill={color} />
                  ))}
                </g>
              )
            })
          : spec.series.map((s) =>
              s.points.map((p, i) => {
                const zero = yToPx(0)
                const y = yToPx(p.y)
                const barW = Math.max(6, bandW * 0.5)
                const cx = PAD_L + bandW * i + bandW / 2
                const color = p.y < 0 ? 'var(--color-warning)' : s.color ?? 'var(--color-primary)'
                return (
                  <rect
                    key={i}
                    x={cx - barW / 2}
                    y={Math.min(zero, y)}
                    width={barW}
                    height={Math.abs(zero - y)}
                    rx={2}
                    fill={color}
                  />
                )
              }),
            )}

        {/* x labels (abbreviated) */}
        {xs.map((x, i) => (
          <text key={i} x={xToPx(i)} y={H - 12} textAnchor="middle" className="chart__xlabel">
            {abbreviate(x)}
          </text>
        ))}
      </svg>
      {spec.explanation && <p className="chart__explain">{spec.explanation}</p>}
    </div>
  )
}

function abbreviate(s: string): string {
  return s.length > 5 ? s.slice(0, 4) + '…' : s
}
