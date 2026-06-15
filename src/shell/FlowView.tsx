/*
 * Flow view (§3.4.3, §6.5). The default mobile editor: an ordered, readable,
 * tappable vertical sequence. Tap a component → details; tap an insert point →
 * add; per-component menu → replace / disable / delete / reorder.
 */
import { useState } from 'react'
import { useStore } from '../shared/store'
import { StandardCard } from '../shared/ui/ComponentCard'
import { Sheet } from '../shared/ui/Sheet'
import { categoryColor, chainOrder, componentStatus, resolveFields } from './helpers'
import { AddComponentSheet } from './AddComponentSheet'

export function FlowView() {
  const { design, plugin, result, dispatch } = useStore()
  const [insertAt, setInsertAt] = useState<number | null>(null)
  const [menuUid, setMenuUid] = useState<string | null>(null)
  const [replaceFor, setReplaceFor] = useState<string | null>(null)
  if (!design) return null

  const ordered = chainOrder(design)
  const menuComp = ordered.find((c) => c.uid === menuUid)

  const InsertButton = ({ at }: { at: number }) => (
    <div className="flow__insert">
      <button className="flow__insert-btn" onClick={() => setInsertAt(at)} aria-label="在此插入组件">
        ＋
      </button>
    </div>
  )

  return (
    <div className="page fade-in">
      <div className="spread">
        <h1 className="page__title">链路</h1>
        <button className="btn btn--ghost btn--sm" onClick={() => dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'canvas' } })}>
          画布视图 ›
        </button>
      </div>
      <p className="muted" style={{ marginTop: -8 }}>纵向链路，点击元件查看详情，点击 ＋ 插入。</p>

      <div className="flow">
        <InsertButton at={0} />
        {ordered.map((inst, i) => {
          const schema = plugin.getSchema(inst.schemaId)
          return (
            <div key={inst.uid}>
              <div style={{ position: 'relative' }}>
                <StandardCard
                  name={inst.name}
                  role={schema?.role ?? '组件'}
                  icon={schema?.icon}
                  categoryColor={categoryColor(plugin, inst.schemaId)}
                  status={componentStatus(plugin, inst, result)}
                  fields={resolveFields(plugin, inst, result)}
                  disabled={inst.disabled}
                  onClick={() => dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'component', params: { uid: inst.uid } } })}
                />
                <button className="flow__menu" onClick={() => setMenuUid(inst.uid)} aria-label="更多操作">
                  ⋯
                </button>
              </div>
              <InsertButton at={i + 1} />
            </div>
          )
        })}
        {ordered.length === 0 && <div className="empty">链路为空，点击 ＋ 添加第一个组件</div>}
      </div>

      <AddComponentSheet open={insertAt !== null} atIndex={insertAt ?? undefined} onClose={() => setInsertAt(null)} />

      {/* per-component action sheet (swipe/menu actions §3.4.3) */}
      <Sheet open={!!menuComp} title={menuComp?.name} onClose={() => setMenuUid(null)}>
        {menuComp && (
          <div className="stack">
            <button className="list-row" onClick={() => { dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'component', params: { uid: menuComp.uid } } }); setMenuUid(null) }}>
              <span className="list-row__label"><span className="list-row__title">编辑参数</span></span><span className="cc__chevron">›</span>
            </button>
            <div className="grid-2">
              <button className="btn btn--secondary" onClick={() => { dispatch({ type: 'MOVE_COMPONENT', compUid: menuComp.uid, dir: -1 }); }}>上移</button>
              <button className="btn btn--secondary" onClick={() => { dispatch({ type: 'MOVE_COMPONENT', compUid: menuComp.uid, dir: 1 }); }}>下移</button>
            </div>
            <button className="btn btn--secondary btn--block" onClick={() => { setReplaceFor(menuComp.uid); setMenuUid(null) }}>替换组件</button>
            <button className="btn btn--secondary btn--block" onClick={() => dispatch({ type: 'TOGGLE_DISABLE', compUid: menuComp.uid })}>
              {menuComp.disabled ? '启用' : '禁用'}
            </button>
            <button className="btn btn--danger btn--block" onClick={() => { dispatch({ type: 'REMOVE_COMPONENT', compUid: menuComp.uid }); setMenuUid(null) }}>删除</button>
          </div>
        )}
      </Sheet>

      {/* replace sheet reuses library list */}
      <ReplaceSheet open={!!replaceFor} forUid={replaceFor} onClose={() => setReplaceFor(null)} />
    </div>
  )
}

function ReplaceSheet({ open, forUid, onClose }: { open: boolean; forUid: string | null; onClose: () => void }) {
  const { plugin, design, dispatch } = useStore()
  return (
    <Sheet open={open} title="替换为" onClose={onClose}>
      <div className="stack">
        {plugin.library(design).map((e) => (
          <button
            key={e.schema.id}
            className="lib-row"
            onClick={() => { if (forUid) dispatch({ type: 'REPLACE_COMPONENT', compUid: forUid, schemaId: e.schema.id }); onClose() }}
          >
            <span className="cc__icon" style={{ background: categoryColor(plugin, e.schema.id), width: 36, height: 36, fontSize: 18 }}>{e.schema.icon ?? '◻'}</span>
            <span className="lib-row__body">
              <span className="lib-row__name">{e.schema.name}<span className="lib-row__role"> · {e.schema.role}</span></span>
              {e.schema.description && <span className="lib-row__desc">{e.schema.description}</span>}
            </span>
            <span className="lib-row__add">↔</span>
          </button>
        ))}
      </div>
    </Sheet>
  )
}
