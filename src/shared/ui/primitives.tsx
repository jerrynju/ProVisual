/*
 * Shared UI primitives — domain-neutral, schema/status-driven only.
 */
import type { ReactNode } from 'react'
import type { Status, KeyValue } from '../types'
import { STATUS_COLOR, STATUS_LABEL, STATUS_SOFT } from './status'

export function StatusBadge({ status, size = 'md' }: { status: Status; size?: 'sm' | 'md' }) {
  return (
    <span
      className={`badge badge--${size}`}
      style={{ color: STATUS_COLOR[status], background: STATUS_SOFT[status] }}
    >
      <span className="badge__dot" style={{ background: STATUS_COLOR[status] }} />
      {STATUS_LABEL[status]}
    </span>
  )
}

export function Card({
  children,
  onClick,
  className = '',
  accent,
}: {
  children: ReactNode
  onClick?: () => void
  className?: string
  accent?: string
}) {
  const style = accent ? { borderLeft: `3px solid ${accent}` } : undefined
  if (onClick) {
    return (
      <button className={`card card--button ${className}`} style={style} onClick={onClick}>
        {children}
      </button>
    )
  }
  return (
    <div className={`card ${className}`} style={style}>
      {children}
    </div>
  )
}

export function SectionHeader({ title, action }: { title: string; action?: ReactNode }) {
  return (
    <div className="section-head">
      <h2 className="section-title">{title}</h2>
      {action}
    </div>
  )
}

export function Pill({ children, color }: { children: ReactNode; color?: string }) {
  return (
    <span className="pill" style={color ? { color, background: 'transparent', borderColor: color } : undefined}>
      {children}
    </span>
  )
}

export function EmptyState({ title, hint }: { title: string; hint?: string }) {
  return (
    <div className="empty">
      <div style={{ fontWeight: 700, color: 'var(--color-text)' }}>{title}</div>
      {hint && <div className="meta" style={{ marginTop: 6 }}>{hint}</div>}
    </div>
  )
}

/** A single key metric (§3.10 关键值). Big number, weak unit. */
export function KeyMetric({ kv }: { kv: KeyValue }) {
  const color = kv.status ? STATUS_COLOR[kv.status] : 'var(--color-text)'
  return (
    <div className={`metric ${kv.emphasis ? 'metric--hero' : ''}`}>
      <div className="metric__label">{kv.label}</div>
      <div className="metric__value" style={{ color }}>
        {kv.value}
        {kv.unit && <span className="metric__unit">{kv.unit}</span>}
      </div>
      {kv.hint && <div className="metric__hint">{kv.hint}</div>}
    </div>
  )
}

export function KeyMetricGrid({ items }: { items: KeyValue[] }) {
  return (
    <div className="metric-grid">
      {items.map((kv) => (
        <KeyMetric key={kv.key} kv={kv} />
      ))}
    </div>
  )
}
