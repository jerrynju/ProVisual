/*
 * Library tab (§3.8). Search, recommended, categories, templates, custom.
 * Templates create runnable starting points; library entries carry NL descriptions.
 */
import { useMemo, useState } from 'react'
import { useStore } from '../shared/store'
import { SectionHeader } from '../shared/ui/primitives'
import { categoryColor } from './helpers'

export function Library() {
  const { plugin, design, dispatch } = useStore()
  const [q, setQ] = useState('')
  const [cat, setCat] = useState<string>('all')

  const entries = useMemo(() => plugin.library(design), [plugin, design])
  const filtered = entries.filter((e) => {
    const matchQ = q.trim() === '' || e.schema.name.includes(q) || e.schema.role.includes(q)
    const matchCat = cat === 'all' || e.schema.category === cat
    return matchQ && matchCat
  })

  const addToDesign = (schemaId: string) => {
    if (!design) {
      dispatch({ type: 'NEW_FROM_TEMPLATE', templateId: 'blank' })
    }
    dispatch({ type: 'ADD_COMPONENT', schemaId })
    dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'flow' } })
  }

  return (
    <div className="page fade-in">
      <h1 className="page__title">库</h1>
      <input className="search" placeholder="搜索组件或模板" value={q} onChange={(e) => setQ(e.target.value)} />

      <SectionHeader title="模板" />
      <div className="stack">
        {plugin.templates.map((t) => (
          <button key={t.id} className="tpl" onClick={() => dispatch({ type: 'NEW_FROM_TEMPLATE', templateId: t.id })}>
            <div className="tpl__name">{t.name}<span style={{ color: 'var(--color-primary)', fontSize: 'var(--font-body)' }}>新建 ›</span></div>
            <p className="tpl__desc">{t.description}</p>
            <div className="tpl__meta">
              适用：{t.appliesTo}
              {t.assumptions && t.assumptions.length > 0 ? ` · 假设：${t.assumptions.join('、')}` : ''}
            </div>
          </button>
        ))}
      </div>

      <SectionHeader title="组件" />
      <div className="tabs">
        <button className={`tabs__item ${cat === 'all' ? 'is-active' : ''}`} onClick={() => setCat('all')}>全部</button>
        {plugin.categories.map((c) => (
          <button key={c.id} className={`tabs__item ${cat === c.id ? 'is-active' : ''}`} onClick={() => setCat(c.id)}>{c.label}</button>
        ))}
      </div>
      <div className="stack">
        {filtered.map((e) => (
          <button key={e.schema.id} className="lib-row" onClick={() => addToDesign(e.schema.id)}>
            <span className="cc__icon" style={{ background: categoryColor(plugin, e.schema.id), width: 36, height: 36, fontSize: 18 }}>{e.schema.icon ?? '◻'}</span>
            <span className="lib-row__body">
              <span className="lib-row__name">{e.schema.name}<span className="lib-row__role" style={{ color: categoryColor(plugin, e.schema.id) }}> · {e.schema.role}</span>{e.recommended && <span className="lib-row__rec">推荐</span>}</span>
              {e.schema.description && <span className="lib-row__desc">{e.schema.description}</span>}
            </span>
            <span className="lib-row__add">＋</span>
          </button>
        ))}
        {filtered.length === 0 && <div className="empty">未找到匹配的组件</div>}
      </div>
    </div>
  )
}
