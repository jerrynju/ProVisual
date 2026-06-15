/*
 * RF domain category colors (§4.4: 分类色由具体 App 提供).
 * These are injected into the shared `--category-*` tokens at render time.
 */
import type { Category } from '../../shared/domain'

export const RF_CATEGORIES: Category[] = [
  { id: 'source',   label: '信号源', primary: '#2563EB', secondary: '#7CA2F2', accent: '#1D4ED8' },
  { id: 'active',   label: '有源',   primary: '#16A34A', secondary: '#7BC99A', accent: '#15803D' },
  { id: 'passive',  label: '无源',   primary: '#F59E0B', secondary: '#F6C36B', accent: '#B45309' },
  { id: 'antenna',  label: '天线',   primary: '#8B5CF6', secondary: '#B79EF0', accent: '#6D28D9' },
  { id: 'receiver', label: '接收',   primary: '#0EA5A5', secondary: '#6FC9C9', accent: '#0F766E' },
  { id: 'measure',  label: '测量',   primary: '#667085', secondary: '#98A2B3', accent: '#475467' },
]

const byId = new Map(RF_CATEGORIES.map((c) => [c.id, c]))
export function rfCategory(id: string): Category {
  return byId.get(id) ?? RF_CATEGORIES[RF_CATEGORIES.length - 1]
}
