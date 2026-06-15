/*
 * RF validation (§2.6 先解释再暴露细节, §6.8 射频问题).
 * Every issue must say: what happened, why it matters, which element, what to do.
 */
import type { Design, Issue } from '../../shared/types'
import { RF_SCHEMAS } from './schemas'
import { computeLink } from './calculator'

const schemaById = new Map(RF_SCHEMAS.map((s) => [s.id, s]))

export function validateDesign(design: Design): Issue[] {
  const issues: Issue[] = []
  const active = design.components.filter((c) => !c.disabled)

  // Structural: needs a source
  if (!active.some((c) => c.schemaId === 'rf.source')) {
    issues.push({
      id: 'no-source',
      severity: 'warning',
      title: '缺少信号源',
      explanation: '链路没有信号源，起始功率默认为 0 dBm，结果可能不准确。',
      suggestedActions: [{ label: '从库添加信号源', intent: { type: 'open-run' } }],
    })
  }

  // Structural: needs a receiver to compute margin
  if (!active.some((c) => c.schemaId === 'rf.receiver')) {
    issues.push({
      id: 'no-receiver',
      severity: 'info',
      title: '缺少接收机',
      explanation: '没有接收机就无法计算灵敏度与链路余量，只能得到电平与增益。',
      suggestedActions: [{ label: '从库添加接收机', intent: { type: 'open-run' } }],
    })
  }

  // Parameter-level: missing required values + out-of-range
  for (const c of active) {
    const schema = schemaById.get(c.schemaId)
    if (!schema) continue
    for (const p of schema.parameters) {
      if (p.readOnly) continue
      const raw = c.params[p.key]
      const n = typeof raw === 'number' ? raw : parseFloat(String(raw))
      const missing = raw === undefined || raw === '' || Number.isNaN(n)
      if (missing && p.control !== 'select') {
        issues.push({
          id: `missing-${c.uid}-${p.key}`,
          severity: 'error',
          title: '缺少必要参数',
          affectedObject: c.uid,
          affectedLabel: c.name,
          explanation: `「${c.name}」未定义「${p.label}」，因此无法完成计算。`,
          suggestedActions: [{ label: `打开 ${c.name} 详情`, intent: { type: 'open-component', uid: c.uid } }],
          jumpTarget: { type: 'component', uid: c.uid },
        })
        continue
      }
      if (!missing && p.recommended) {
        const [lo, hi] = p.recommended
        if (n < lo || n > hi) {
          issues.push({
            id: `range-${c.uid}-${p.key}`,
            severity: 'warning',
            title: '参数超出推荐范围',
            affectedObject: c.uid,
            affectedLabel: c.name,
            explanation: `「${c.name}」的「${p.label}」为 ${n}${p.unit ?? ''}，超出推荐范围 ${lo}–${hi}${p.unit ?? ''}，可能不符合实际器件或带来风险。`,
            suggestedActions: [{ label: `调整 ${p.label}`, intent: { type: 'open-component', uid: c.uid } }],
            jumpTarget: { type: 'component', uid: c.uid },
          })
        }
      }
    }
  }

  // Derived risks from the calculation (compression, low/negative margin)
  const link = computeLink(design, design.activeScenarioId)
  for (const s of link.stages) {
    if (s.compressionRisk) {
      issues.push({
        id: `compression-${s.inst.uid}`,
        severity: 'warning',
        title: '输出电平接近压缩限制',
        affectedObject: s.inst.uid,
        affectedLabel: s.inst.name,
        explanation: `「${s.inst.name}」输出电平约 ${s.levelOut.toFixed(1)} dBm，接近其 P1dB 压缩点，可能降低线性度与可用余量。`,
        suggestedActions: [
          { label: '降低上游增益', intent: { type: 'open-component', uid: s.inst.uid } },
          { label: '在前级加入衰减' },
        ],
        jumpTarget: { type: 'component', uid: s.inst.uid },
      })
    }
  }

  if (Number.isFinite(link.margin)) {
    if (link.margin < 0) {
      issues.push({
        id: 'margin-negative',
        severity: 'error',
        title: '链路余量不足',
        affectedObject: link.limiting?.uid,
        affectedLabel: link.limiting?.label,
        explanation: `接收功率 ${link.outputPower.toFixed(1)} dBm 低于灵敏度 ${link.sensitivity.toFixed(1)} dBm，余量为 ${link.margin.toFixed(1)} dB，链路无法可靠工作。`,
        suggestedActions: [
          { label: '提高链路增益' },
          { label: '降低路径损耗' },
          ...(link.limiting ? [{ label: `查看 ${link.limiting.label}`, intent: { type: 'open-component' as const, uid: link.limiting.uid } }] : []),
        ],
        jumpTarget: link.limiting ? { type: 'component', uid: link.limiting.uid } : undefined,
      })
    } else if (link.margin < 3) {
      issues.push({
        id: 'margin-low',
        severity: 'warning',
        title: '链路余量偏低',
        affectedObject: link.limiting?.uid,
        affectedLabel: link.limiting?.label,
        explanation: `当前余量仅 ${link.margin.toFixed(1)} dB，低于建议的 3 dB，抗温度与器件波动能力较弱。`,
        suggestedActions: [
          { label: '增加增益或降低损耗' },
          ...(link.limiting ? [{ label: `查看 ${link.limiting.label}`, intent: { type: 'open-component' as const, uid: link.limiting.uid } }] : []),
        ],
        jumpTarget: link.limiting ? { type: 'component', uid: link.limiting.uid } : undefined,
      })
    }
  }

  return issues
}
