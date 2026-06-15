/*
 * RF domain plugin assembly. Implements the shared DomainPlugin contract.
 * This is the single seam where RF semantics enter the otherwise neutral app.
 */
import type { DomainPlugin, LibraryEntry } from '../../shared/domain'
import { makeGetSchema } from '../../shared/domain'
import type { Design } from '../../shared/types'
import { RF_CATEGORIES } from './categories'
import { RF_SCHEMAS } from './schemas'
import { RF_TEMPLATES } from './templates'
import { validateDesign } from './validators'
import { buildResult } from './calculator'

function rfLibrary(design: Design | null): LibraryEntry[] {
  const hasReceiver = !!design?.components.some((c) => c.schemaId === 'rf.receiver')
  const hasSource = !!design?.components.some((c) => c.schemaId === 'rf.source')
  return RF_SCHEMAS.map((schema) => {
    let recommended = false
    if (design) {
      if (schema.id === 'rf.receiver' && !hasReceiver) recommended = true
      if (schema.id === 'rf.source' && !hasSource) recommended = true
      if (schema.id === 'rf.amplifier' && hasSource && hasReceiver) recommended = true
    }
    return { schema, recommended }
  })
}

export const rfPlugin: DomainPlugin = {
  id: 'rf',
  label: '射频链路计算',
  tagline: '移动端原生的链路设计、计算与分析助手',
  categories: RF_CATEGORIES,
  schemas: RF_SCHEMAS,
  templates: RF_TEMPLATES,
  library: rfLibrary,
  validate: validateDesign,
  calculate: (design, scenarioId) => {
    const issues = validateDesign(design)
    return buildResult(design, scenarioId, issues)
  },
  getSchema: makeGetSchema(RF_SCHEMAS),
}
