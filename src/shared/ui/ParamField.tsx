/*
 * Schema-driven parameter field (§3.7 参数输入规则).
 * Renders label + value+unit + hint, choosing the control from the schema.
 * Contains NO domain assumptions — only the generic control kinds.
 */
import type { ParamSchema } from '../types'

interface Props {
  schema: ParamSchema
  value: number | string | undefined
  onChange: (value: number | string) => void
}

export function ParamField({ schema, value, onChange }: Props) {
  const display = value === undefined ? '' : value
  const inRange =
    schema.recommended && typeof value === 'number'
      ? value >= schema.recommended[0] && value <= schema.recommended[1]
      : true

  return (
    <div className="field">
      <div className="field__top">
        <label className="field__label">{schema.label}</label>
        {schema.recommended && (
          <span className="field__rec">
            推荐 {schema.recommended[0]}–{schema.recommended[1]}
            {schema.unit ? ` ${schema.unit}` : ''}
          </span>
        )}
      </div>

      {schema.readOnly ? (
        <div className="field__readonly">
          {display}
          {schema.unit && <span className="field__unit"> {schema.unit}</span>}
        </div>
      ) : schema.control === 'slider' ? (
        <div className="field__slider">
          <input
            type="range"
            min={schema.min}
            max={schema.max}
            step={schema.step ?? 1}
            value={Number(value ?? schema.min ?? 0)}
            onChange={(e) => onChange(Number(e.target.value))}
          />
          <div className={`field__value ${inRange ? '' : 'field__value--warn'}`}>
            <input
              className="field__num"
              type="number"
              value={display}
              step={schema.step ?? 1}
              onChange={(e) => onChange(e.target.value === '' ? '' : Number(e.target.value))}
            />
            {schema.unit && <span className="field__unit">{schema.unit}</span>}
          </div>
        </div>
      ) : schema.control === 'stepper' ? (
        <div className="field__stepper">
          <button
            className="stepper__btn"
            onClick={() => onChange(round((Number(value ?? 0) - (schema.step ?? 1)), schema))}
            aria-label="减少"
          >
            −
          </button>
          <div className="stepper__display">
            {display}
            {schema.unit && <span className="field__unit"> {schema.unit}</span>}
          </div>
          <button
            className="stepper__btn"
            onClick={() => onChange(round((Number(value ?? 0) + (schema.step ?? 1)), schema))}
            aria-label="增加"
          >
            +
          </button>
        </div>
      ) : schema.control === 'select' ? (
        <select className="field__select" value={String(value ?? '')} onChange={(e) => onChange(e.target.value)}>
          {schema.options?.map((o) => (
            <option key={o.value} value={o.value}>
              {o.label}
            </option>
          ))}
        </select>
      ) : (
        <div className={`field__value field__value--box ${inRange ? '' : 'field__value--warn'}`}>
          <input
            className="field__num"
            type={schema.control === 'text' || schema.control === 'expression' ? 'text' : 'number'}
            value={display}
            step={schema.step ?? 1}
            onChange={(e) =>
              onChange(
                schema.control === 'text' || schema.control === 'expression'
                  ? e.target.value
                  : e.target.value === ''
                    ? ''
                    : Number(e.target.value),
              )
            }
          />
          {schema.unit && <span className="field__unit">{schema.unit}</span>}
        </div>
      )}

      {schema.hint && <p className="field__hint">{schema.hint}</p>}
      {!inRange && <p className="field__warn">超出推荐范围，请确认是否符合实际器件。</p>}
    </div>
  )
}

function round(v: number, schema: ParamSchema): number {
  let n = v
  if (schema.min !== undefined) n = Math.max(schema.min, n)
  if (schema.max !== undefined) n = Math.min(schema.max, n)
  return Math.round(n * 100) / 100
}
