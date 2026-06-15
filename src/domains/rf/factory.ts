/*
 * Instance + design construction helpers for the RF domain.
 */
import type { ComponentInstance, Connection, Design } from '../../shared/types'
import { RF_SCHEMAS } from './schemas'

const schemaById = new Map(RF_SCHEMAS.map((s) => [s.id, s]))

let counter = 0
export function uid(prefix = 'c'): string {
  counter += 1
  return `${prefix}_${Date.now().toString(36)}_${counter}`
}

export function makeInstance(
  schemaId: string,
  name?: string,
  overrides: Record<string, number | string> = {},
): ComponentInstance {
  const schema = schemaById.get(schemaId)
  const params: Record<string, number | string> = {}
  schema?.parameters.forEach((p) => {
    if (p.default !== undefined) params[p.key] = p.default
  })
  return {
    uid: uid(),
    schemaId,
    name: name ?? schema?.name ?? schemaId,
    params: { ...params, ...overrides },
  }
}

/** Build a linear design from an ordered list of [schemaId, name?, overrides?]. */
export function linearDesign(
  steps: Array<[string, string?, Record<string, number | string>?]>,
): Omit<Design, 'id' | 'name' | 'domainId'> {
  const components: ComponentInstance[] = steps.map(([sid, name, ov]) =>
    makeInstance(sid, name, ov ?? {}),
  )
  const connections: Connection[] = []
  for (let i = 0; i < components.length - 1; i++) {
    connections.push({ id: uid('conn'), from: components[i].uid, to: components[i + 1].uid })
  }
  const baselineId = uid('sc')
  return {
    components,
    connections,
    scenarios: [{ id: baselineId, name: '默认', baseline: true, overrides: {} }],
    activeScenarioId: baselineId,
  }
}
