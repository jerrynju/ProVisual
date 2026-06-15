/*
 * Bottom navigation (§3.3 主导航模型): 设计 / 运行 / 结果 / 库 / 更多.
 */
import type { TabKey } from '../store'

interface Item {
  key: TabKey
  label: string
  icon: string
}

const ITEMS: Item[] = [
  { key: 'design', label: '设计', icon: '◰' },
  { key: 'run', label: '运行', icon: '▷' },
  { key: 'results', label: '结果', icon: '◫' },
  { key: 'library', label: '库', icon: '⊞' },
  { key: 'more', label: '更多', icon: '⋯' },
]

export function BottomNav({
  active,
  issueCount,
  onSelect,
}: {
  active: TabKey
  issueCount: number
  onSelect: (tab: TabKey) => void
}) {
  return (
    <nav className="bottom-nav">
      {ITEMS.map((it) => {
        const showBadge = it.key === 'results' && issueCount > 0
        return (
          <button
            key={it.key}
            className={`bottom-nav__item ${active === it.key ? 'is-active' : ''}`}
            onClick={() => onSelect(it.key)}
          >
            <span className={`bottom-nav__icon ${showBadge ? 'bottom-nav__badge' : ''}`} data-count={issueCount}>
              {it.icon}
            </span>
            {it.label}
          </button>
        )
      })}
    </nav>
  )
}
