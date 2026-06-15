/*
 * Shell helpers: resolve schema/result data into the neutral shapes the shared
 * UI consumes (card fields, category colors). The shell may read plugin
 * metadata; the shared UI components themselves stay domain-agnostic.
 */
import type { DomainPlugin } from '../shared/domain'
import type { ComponentInstance, Design, RunResult, Status } from '../shared/types'
import type { CardField } from '../shared/ui/ComponentCard'

export function categoryColor(plugin: DomainPlugin, schemaId: string): string {
  const schema = plugin.getSchema(schemaId)
  const cat = plugin.categories.find((c) => c.id === schema?.category)
  return cat?.primary ?? 'var(--color-disabled)'
}

export function resolveFields(
  plugin: DomainPlugin,
  inst: ComponentInstance,
  result: RunResult | null,
): CardField[] {
  const schema = plugin.getSchema(inst.schemaId)
  if (!schema) return []
  return schema.display.map((d) => {
    if (d.source === 'output') {
      const v = result?.outputs[inst.uid]?.[d.key]
      return { label: d.label, value: v ?? '—', unit: d.unit }
    }
    const v = inst.params[d.key]
    return { label: d.label, value: v ?? '—', unit: d.unit }
  })
}

export function componentStatus(
  plugin: DomainPlugin,
  inst: ComponentInstance,
  result: RunResult | null,
): Status {
  if (inst.disabled) return 'disabled'
  if (result?.componentStatus[inst.uid]) return result.componentStatus[inst.uid]
  // unconfigured if any required numeric param is missing
  const schema = plugin.getSchema(inst.schemaId)
  const missing = schema?.parameters.some((p) => {
    if (p.readOnly || p.control === 'select') return false
    const raw = inst.params[p.key]
    const n = typeof raw === 'number' ? raw : parseFloat(String(raw))
    return raw === undefined || raw === '' || Number.isNaN(n)
  })
  return missing ? 'unconfigured' : 'ready'
}

export function chainOrder(design: Design): ComponentInstance[] {
  const incoming = new Set(design.connections.map((c) => c.to))
  const nextOf = new Map(design.connections.map((c) => [c.from, c.to]))
  const byUid = new Map(design.components.map((c) => [c.uid, c]))
  const head = design.components.find((c) => !incoming.has(c.uid))
  if (!head || design.connections.length === 0) return design.components
  const out: ComponentInstance[] = []
  const seen = new Set<string>()
  let cur: ComponentInstance | undefined = head
  while (cur && !seen.has(cur.uid)) {
    seen.add(cur.uid)
    out.push(cur)
    const nx = nextOf.get(cur.uid)
    cur = nx ? byUid.get(nx) : undefined
  }
  for (const c of design.components) if (!seen.has(c.uid)) out.push(c)
  return out
}
