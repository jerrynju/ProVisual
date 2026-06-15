/*
 * Add-from-library sheet (§3.8). Search + recommended + categories. Adding a
 * component never requires drag — tapping inserts it (§3.8 规则).
 */
import { useMemo, useState } from 'react'
import { useStore } from '../shared/store'
import { Sheet } from '../shared/ui/Sheet'
import { categoryColor } from './helpers'

interface Props {
  open: boolean
  atIndex?: number
  onClose: () => void
}

export function AddComponentSheet({ open, atIndex, onClose }: Props) {
  const { plugin, design, dispatch } = useStore()
  const [q, setQ] = useState('')
  const entries = useMemo(() => plugin.library(design), [plugin, design])

  const filtered = entries.filter(
    (e) =>
      q.trim() === '' ||
      e.schema.name.includes(q) ||
      e.schema.role.includes(q) ||
      (e.schema.description ?? '').includes(q),
  )
  const recommended = filtered.filter((e) => e.recommended)

  const add = (schemaId: string) => {
    dispatch({ type: 'ADD_COMPONENT', schemaId, atIndex })
    onClose()
  }

  return (
    <Sheet open={open} title="添加组件" onClose={onClose}>
      <input className="search" placeholder="搜索组件、角色或说明" value={q} onChange={(e) => setQ(e.target.value)} />

      {recommended.length > 0 && q.trim() === '' && (
        <>
          <div className="lib-group">推荐</div>
          <div className="stack">
            {recommended.map((e) => (
              <LibRow key={`r-${e.schema.id}`} schemaId={e.schema.id} name={e.schema.name} role={e.schema.role} desc={e.schema.description} color={categoryColor(plugin, e.schema.id)} icon={e.schema.icon} onAdd={add} recommended />
            ))}
          </div>
        </>
      )}

      <div className="lib-group">全部组件</div>
      <div className="stack">
        {filtered.map((e) => (
          <LibRow key={e.schema.id} schemaId={e.schema.id} name={e.schema.name} role={e.schema.role} desc={e.schema.description} color={categoryColor(plugin, e.schema.id)} icon={e.schema.icon} onAdd={add} />
        ))}
        {filtered.length === 0 && <div className="empty">未找到匹配的组件</div>}
      </div>
    </Sheet>
  )
}

function LibRow({
  schemaId,
  name,
  role,
  desc,
  color,
  icon,
  onAdd,
  recommended,
}: {
  schemaId: string
  name: string
  role: string
  desc?: string
  color: string
  icon?: string
  onAdd: (id: string) => void
  recommended?: boolean
}) {
  return (
    <button className="lib-row" onClick={() => onAdd(schemaId)}>
      <span className="cc__icon" style={{ background: color, width: 36, height: 36, fontSize: 18 }}>
        {icon ?? '◻'}
      </span>
      <span className="lib-row__body">
        <span className="lib-row__name">
          {name}
          <span className="lib-row__role" style={{ color }}> · {role}</span>
          {recommended && <span className="lib-row__rec">推荐</span>}
        </span>
        {desc && <span className="lib-row__desc">{desc}</span>}
      </span>
      <span className="lib-row__add">＋</span>
    </button>
  )
}
