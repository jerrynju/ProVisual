/*
 * Component cards (§3.5 组件卡片体系). Three densities: compact / standard / expanded.
 * Domain-neutral: the shell resolves display fields & category color and passes them in.
 */
import type { Status } from '../types'
import { StatusBadge } from './primitives'

export interface CardField {
  label: string
  value: string | number
  unit?: string
}

interface BaseProps {
  name: string
  role: string
  icon?: string
  categoryColor: string
  status: Status
  fields: CardField[]
}

function FieldList({ fields }: { fields: CardField[] }) {
  return (
    <div className="cc__fields">
      {fields.slice(0, 3).map((f, i) => (
        <div className="cc__field" key={i}>
          <span className="cc__field-val">
            {f.value}
            {f.unit && <span className="cc__field-unit"> {f.unit}</span>}
          </span>
          <span className="cc__field-label">{f.label}</span>
        </div>
      ))}
    </div>
  )
}

/** Standard card — used in the flow view (§3.5). */
export function StandardCard({
  name,
  role,
  icon,
  categoryColor,
  status,
  fields,
  onClick,
  disabled,
}: BaseProps & { onClick?: () => void; disabled?: boolean }) {
  return (
    <button className={`cc cc--standard ${disabled ? 'cc--off' : ''}`} onClick={onClick}>
      <span className="cc__icon" style={{ background: categoryColor }}>
        {icon ?? '◻'}
      </span>
      <span className="cc__main">
        <span className="cc__top">
          <span className="cc__name">{name}</span>
          <StatusBadge status={status} size="sm" />
        </span>
        <span className="cc__role" style={{ color: categoryColor }}>
          {role}
        </span>
        <FieldList fields={fields} />
      </span>
      <span className="cc__chevron">›</span>
    </button>
  )
}

/** Compact card — lists & small summaries. */
export function CompactCard({
  name,
  primary,
  secondary,
  categoryColor,
  onClick,
}: {
  name: string
  primary: string
  secondary?: string
  categoryColor: string
  onClick?: () => void
}) {
  return (
    <button className="cc cc--compact" onClick={onClick}>
      <span className="cc__dot" style={{ background: categoryColor }} />
      <span className="cc__compact-name">{name}</span>
      <span className="cc__compact-val">
        {primary}
        {secondary && <span className="muted"> · {secondary}</span>}
      </span>
    </button>
  )
}
