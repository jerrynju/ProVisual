/*
 * Bottom sheet (§3.1 优先使用底部面板). Domain-neutral overlay container.
 */
import type { ReactNode } from 'react'
import { useEffect } from 'react'

interface Props {
  open: boolean
  title?: string
  onClose: () => void
  children: ReactNode
  footer?: ReactNode
}

export function Sheet({ open, title, onClose, children, footer }: Props) {
  useEffect(() => {
    if (!open) return
    const onKey = (e: KeyboardEvent) => e.key === 'Escape' && onClose()
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [open, onClose])

  if (!open) return null
  return (
    <div className="sheet-scrim" onClick={onClose}>
      <div className="sheet" onClick={(e) => e.stopPropagation()} role="dialog" aria-modal="true">
        <div className="sheet__grip" />
        {title && (
          <div className="sheet__head">
            <h3 className="sheet__title">{title}</h3>
            <button className="sheet__close" onClick={onClose} aria-label="关闭">
              ✕
            </button>
          </div>
        )}
        <div className="sheet__body">{children}</div>
        {footer && <div className="sheet__footer">{footer}</div>}
      </div>
    </div>
  )
}
