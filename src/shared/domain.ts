/*
 * Domain plugin contract (§5.1 分层架构, §5.2 强制隔离).
 *
 * The shared App Shell and shared UI depend ONLY on this interface. A domain
 * plugin provides: schemas, templates, validators, a calculator and result
 * rendering metadata. The shared layer must never contain domain formulas,
 * units, categories, validation rules or chart definitions — those live here.
 */
import type {
  ComponentSchema,
  Design,
  Issue,
  RunResult,
} from './types'

/** A category provides the color tokens consumed by shared UI (§4.4). */
export interface Category {
  id: string
  label: string
  /** maps to --category-primary / --category-secondary / --category-accent */
  primary: string
  secondary: string
  accent: string
}

/** A template creates a runnable starting point, not a static example (§3.8/§6.9). */
export interface Template {
  id: string
  name: string
  description: string
  appliesTo: string          // "适用场景"
  assumptions?: string[]      // "已知假设"
  /** build a fresh design instance (without project wrapping) */
  build: () => Omit<Design, 'id' | 'name' | 'domainId'>
}

export interface LibraryEntry {
  schema: ComponentSchema
  /** recommended in the current design context (§3.8) */
  recommended?: boolean
}

export interface DomainPlugin {
  id: string
  /** domain content-area label; shared nav still uses generic names (§4.1) */
  label: string
  tagline: string

  categories: Category[]
  schemas: ComponentSchema[]
  templates: Template[]

  /** library entries, optionally context-aware */
  library: (design: Design | null) => LibraryEntry[]

  /** structural + parameter validation, before running (§2.6, §6.8) */
  validate: (design: Design) => Issue[]

  /** the calculator — the only place domain math lives (§5.2) */
  calculate: (design: Design, scenarioId: string) => RunResult

  /** lookup helpers used by the shell */
  getSchema: (schemaId: string) => ComponentSchema | undefined
}

export function makeGetSchema(schemas: ComponentSchema[]) {
  const byId = new Map(schemas.map((s) => [s.id, s]))
  return (schemaId: string) => byId.get(schemaId)
}
