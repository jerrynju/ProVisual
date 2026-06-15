/*
 * Canvas view (§3.4.4). Expert structural editing. Auto layout by default;
 * connections are created by "select source, then target" (not drag-only).
 * The canvas is never the only way to build a valid design.
 */
import { useState } from 'react'
import { useStore } from '../shared/store'
import { categoryColor, chainOrder } from './helpers'

export function CanvasView() {
  const { design, plugin, dispatch } = useStore()
  const [connectFrom, setConnectFrom] = useState<string | null>(null)
  if (!design) return null

  const ordered = chainOrder(design)
  // auto layout: a gentle zig-zag column
  const positions = new Map<string, { x: number; y: number }>()
  ordered.forEach((c, i) => {
    const pos = c.pos ?? { x: 50 + (i % 2 === 0 ? -14 : 14), y: 12 + i * 13 }
    positions.set(c.uid, pos)
  })

  const onNodeTap = (uid: string) => {
    if (connectFrom === null) {
      dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'component', params: { uid } } })
    } else if (connectFrom === uid) {
      setConnectFrom(null)
    } else {
      dispatch({ type: 'CONNECT', from: connectFrom, to: uid })
      setConnectFrom(null)
    }
  }

  return (
    <div className="page fade-in">
      <div className="spread">
        <h1 className="page__title">画布</h1>
        <button className="btn btn--ghost btn--sm" onClick={() => dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'flow' } })}>流程视图 ›</button>
      </div>
      <p className="muted" style={{ marginTop: -8 }}>
        专家级拓扑编辑。默认自动布局。{connectFrom ? '现在点击目标元件以创建连接。' : '点击「连接」后先选源、再选目标。'}
      </p>

      <div className="canvas">
        <svg viewBox="0 0 100 100" preserveAspectRatio="none" style={{ position: 'absolute', inset: 0, width: '100%', height: '100%' }}>
          {design.connections.map((c) => {
            const a = positions.get(c.from)
            const b = positions.get(c.to)
            if (!a || !b) return null
            return <line key={c.id} x1={a.x} y1={a.y} x2={b.x} y2={b.y} stroke="var(--color-primary)" strokeWidth={0.5} opacity={0.6} />
          })}
        </svg>
        {ordered.map((c) => {
          const p = positions.get(c.uid)!
          const schema = plugin.getSchema(c.schemaId)
          return (
            <button
              key={c.uid}
              className={`canvas__node ${connectFrom === c.uid ? 'is-selected' : ''}`}
              style={{ left: `${p.x}%`, top: `${p.y}%`, borderColor: connectFrom === c.uid ? undefined : categoryColor(plugin, c.schemaId) }}
              onClick={() => onNodeTap(c.uid)}
            >
              <div className="canvas__node-name">{schema?.icon} {c.name}</div>
              <div className="canvas__node-val">{schema?.role}</div>
            </button>
          )
        })}
      </div>

      <div className="grid-2">
        <button className={`btn ${connectFrom ? 'btn--primary' : 'btn--secondary'}`} onClick={() => setConnectFrom(connectFrom ? null : ordered[0]?.uid ?? null)}>
          {connectFrom ? '取消连接' : '连接'}
        </button>
        <button className="btn btn--secondary" onClick={() => dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'flow' } })}>返回流程</button>
      </div>
      <p className="meta">连接支持「选择源，再选择目标」，不依赖拖拽（§3.4.4）。</p>
    </div>
  )
}
