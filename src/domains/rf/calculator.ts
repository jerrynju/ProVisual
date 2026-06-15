/*
 * RF link-budget calculator (§5.2: 领域公式只允许出现在这里).
 *
 * Model: an ordered chain from a signal source to a receiver. We walk the
 * chain accumulating the power level (dBm), compute cascade noise figure with
 * the Friis formula, then derive noise floor, sensitivity and link margin.
 */
import type {
  ComponentInstance,
  Design,
  Issue,
  RunResult,
  Status,
  KeyValue,
  ChartSpec,
  RawRow,
} from '../../shared/types'
import { RF_SCHEMAS } from './schemas'
import { rfCategory } from './categories'

const schemaById = new Map(RF_SCHEMAS.map((s) => [s.id, s]))

const toLin = (db: number) => Math.pow(10, db / 10)
const toDb = (lin: number) => 10 * Math.log10(lin)
const fmt = (n: number, d = 1) => (Number.isFinite(n) ? n.toFixed(d) : '—')

function num(inst: ComponentInstance, key: string): number {
  const v = inst.params[key]
  const n = typeof v === 'number' ? v : parseFloat(String(v))
  return Number.isFinite(n) ? n : NaN
}

/** Resolve a scenario's parameter overrides onto component instances. */
export function resolveInstances(design: Design, scenarioId: string): ComponentInstance[] {
  const sc = design.scenarios.find((s) => s.id === scenarioId)
  return design.components.map((c) => {
    const ov = sc?.overrides[c.uid]
    return ov ? { ...c, params: { ...c.params, ...ov } } : c
  })
}

/** Order the chain by following connections from the head; fall back to array order. */
export function orderChain(design: Design, instances: ComponentInstance[]): ComponentInstance[] {
  const byUid = new Map(instances.map((c) => [c.uid, c]))
  const incoming = new Set(design.connections.map((c) => c.to))
  const nextOf = new Map(design.connections.map((c) => [c.from, c.to]))
  const head = instances.find((c) => !incoming.has(c.uid))
  if (!head || design.connections.length === 0) return instances
  const ordered: ComponentInstance[] = []
  const seen = new Set<string>()
  let cur: ComponentInstance | undefined = head
  while (cur && !seen.has(cur.uid)) {
    seen.add(cur.uid)
    ordered.push(cur)
    const nx = nextOf.get(cur.uid)
    cur = nx ? byUid.get(nx) : undefined
  }
  // append any components not reached (broken graph) in array order
  for (const c of instances) if (!seen.has(c.uid)) ordered.push(c)
  return ordered
}

interface StageCalc {
  inst: ComponentInstance
  role: string
  category: string
  gainDb: number      // signed contribution to level
  nfDb: number        // own noise figure (active/passive), 0 if N/A
  levelIn: number
  levelOut: number
  compressionRisk: boolean
  outputs: Record<string, number | string>
  status: Status
}

export interface LinkCalc {
  stages: StageCalc[]
  order: ComponentInstance[]
  sourcePower: number
  receiver?: ComponentInstance
  totalGain: number
  outputPower: number     // level reaching the receiver / chain end
  systemNf: number
  noiseFloor: number
  sensitivity: number
  snr: number
  margin: number
  limiting?: { uid: string; label: string; reason: string }
}

/**
 * The receive sub-chain over which cascade NF is meaningful: the receiver plus
 * all stages back to (and including) the RX antenna; or back to the source for
 * a purely conducted chain. TX stages and free-space path stay out of NF.
 */
export function receiveSubchain(stages: StageCalc[], receiverUid?: string): StageCalc[] {
  if (!receiverUid) return []
  const idx = stages.findIndex((s) => s.inst.uid === receiverUid)
  if (idx < 0) return []
  const chain: StageCalc[] = []
  for (let i = idx; i >= 0; i--) {
    const s = stages[i]
    if (s.inst.schemaId === 'rf.source') break
    chain.unshift(s)
    if (s.inst.schemaId === 'rf.antenna' && i !== idx) break // include RX antenna, then stop
  }
  return chain
}

/**
 * Friis cascade noise figure over an ordered sub-chain. Antenna gain is treated
 * as noise-transparent (G=1, F=1): it raises wanted signal and sky noise
 * together, so it neither adds NF nor divides downstream noise.
 */
export function friisNf(chain: StageCalc[]): { nf: number; terms: { uid: string; term: number }[] } {
  let fTotal = 1
  let gCum = 1
  let started = false
  const terms: { uid: string; term: number }[] = []
  for (const s of chain) {
    const isAntenna = s.inst.schemaId === 'rf.antenna'
    const fLin = !isAntenna && s.nfDb > 0 ? toLin(s.nfDb) : 1
    const gLin = isAntenna ? 1 : toLin(s.gainDb)
    if (!started) {
      fTotal = fLin
      terms.push({ uid: s.inst.uid, term: fLin - 1 })
      started = true
    } else {
      const term = (fLin - 1) / gCum
      fTotal += term
      terms.push({ uid: s.inst.uid, term })
    }
    gCum *= gLin
  }
  return { nf: started ? toDb(fTotal) : NaN, terms }
}

export function computeLink(design: Design, scenarioId: string): LinkCalc {
  const instances = resolveInstances(design, scenarioId)
  const order = orderChain(design, instances).filter((c) => !c.disabled)

  const source = order.find((c) => c.schemaId === 'rf.source')
  const receiver = order.find((c) => c.schemaId === 'rf.receiver')
  const sourcePower = source ? num(source, 'power') : NaN

  let level = Number.isFinite(sourcePower) ? sourcePower : 0
  const stages: StageCalc[] = []

  for (const inst of order) {
    const schema = schemaById.get(inst.schemaId)
    const role = schema?.role ?? '组件'
    const category = schema?.category ?? 'measure'
    let gainDb = 0
    let nfDb = 0
    let compressionRisk = false
    const levelIn = level

    switch (inst.schemaId) {
      case 'rf.source':
        level = Number.isFinite(sourcePower) ? sourcePower : level
        break
      case 'rf.amplifier': {
        const g = num(inst, 'gain')
        gainDb = Number.isFinite(g) ? g : 0
        nfDb = num(inst, 'nf')
        level = levelIn + gainDb
        const p1db = num(inst, 'p1db')
        if (Number.isFinite(p1db) && level >= p1db - 1) compressionRisk = true
        break
      }
      case 'rf.antenna': {
        const g = num(inst, 'gain')
        gainDb = Number.isFinite(g) ? g : 0
        nfDb = 0
        level = levelIn + gainDb
        break
      }
      case 'rf.attenuator':
      case 'rf.cable':
      case 'rf.filter': {
        const loss = num(inst, 'loss')
        gainDb = Number.isFinite(loss) ? -loss : 0
        nfDb = Number.isFinite(loss) ? loss : 0 // passive NF = insertion loss
        level = levelIn + gainDb
        break
      }
      case 'rf.receiver':
        nfDb = num(inst, 'nf')
        // receiver does not change the through level; it terminates the chain
        break
      case 'rf.measure':
      default:
        break
    }

    stages.push({
      inst, role, category, gainDb, nfDb,
      levelIn, levelOut: level,
      compressionRisk,
      outputs: {
        levelIn: Number.isFinite(levelIn) ? +levelIn.toFixed(2) : '—',
        levelOut: Number.isFinite(level) ? +level.toFixed(2) : '—',
        gainDb: +gainDb.toFixed(2),
      },
      status: 'done',
    })
  }

  // Cascade noise figure (Friis), referenced to the RECEIVE front-end.
  // The cascade only runs over the receive sub-chain (receiver back to and
  // including its RX antenna, or to the source for a conducted chain), so a
  // transmit amplifier or free-space path loss does not pollute system NF.
  const rxStages = receiveSubchain(stages, receiver?.uid)
  const { nf: systemNf } = friisNf(rxStages)

  const outputPower = receiver
    ? (stages.find((s) => s.inst.uid === receiver.uid)?.levelIn ?? level)
    : level
  const totalGain = (Number.isFinite(sourcePower) ? outputPower - sourcePower : NaN)

  let noiseFloor = NaN
  let sensitivity = NaN
  let snr = NaN
  let margin = NaN
  if (receiver) {
    const bwMhz = num(receiver, 'bandwidth')
    const reqSnr = num(receiver, 'reqSnr')
    if (Number.isFinite(bwMhz) && bwMhz > 0 && Number.isFinite(systemNf)) {
      const bwHz = bwMhz * 1e6
      noiseFloor = -174 + 10 * Math.log10(bwHz) + systemNf
      snr = outputPower - noiseFloor
      if (Number.isFinite(reqSnr)) {
        sensitivity = noiseFloor + reqSnr
        margin = outputPower - sensitivity
      }
    }
  }

  // Limiting element: largest single loss, or any compression-risk stage.
  let limiting: LinkCalc['limiting']
  const comp = stages.find((s) => s.compressionRisk)
  if (comp) {
    limiting = { uid: comp.inst.uid, label: comp.inst.name, reason: '接近压缩点' }
  } else {
    const worstLoss = stages
      .filter((s) => s.gainDb < 0)
      .sort((a, b) => a.gainDb - b.gainDb)[0]
    if (worstLoss) limiting = { uid: worstLoss.inst.uid, label: worstLoss.inst.name, reason: `贡献最大损耗 ${fmt(-worstLoss.gainDb)} dB` }
  }

  return {
    stages, order, sourcePower, receiver,
    totalGain, outputPower, systemNf, noiseFloor, sensitivity, snr, margin, limiting,
  }
}

/** Build the full RunResult (key values, charts, raw rows) from a LinkCalc. */
export function buildResult(design: Design, scenarioId: string, issues: Issue[]): RunResult {
  const link = computeLink(design, scenarioId)
  const hasError = issues.some((i) => i.severity === 'error')
  const hasWarn = issues.some((i) => i.severity === 'warning')

  let status: Status = 'done'
  if (hasError) status = 'error'
  else if (hasWarn || (Number.isFinite(link.margin) && link.margin < 3)) status = 'warning'

  const marginStatus: Status = !Number.isFinite(link.margin)
    ? 'unconfigured'
    : link.margin < 0 ? 'error' : link.margin < 3 ? 'warning' : 'done'

  const keyValues: KeyValue[] = [
    { key: 'margin', label: '链路余量', value: fmt(link.margin), unit: 'dB', emphasis: true, status: marginStatus,
      hint: link.limiting ? `主要限制：${link.limiting.label}` : '相对接收灵敏度的裕量。' },
    { key: 'totalGain', label: '总增益', value: fmt(link.totalGain), unit: 'dB', hint: '源到接收机的净增益。' },
    { key: 'outputPower', label: '接收功率', value: fmt(link.outputPower), unit: 'dBm', hint: '到达接收机的信号电平。' },
    { key: 'systemNf', label: '系统噪声系数', value: fmt(link.systemNf), unit: 'dB', hint: '级联噪声系数（Friis）。' },
    { key: 'sensitivity', label: '接收灵敏度', value: fmt(link.sensitivity), unit: 'dBm', hint: '可解调的最小信号电平。' },
    { key: 'snr', label: '信噪比', value: fmt(link.snr), unit: 'dB', hint: '接收信号相对本底噪声。' },
  ]

  // Chart 1: power level along the chain
  const levelPoints = link.stages.map((s) => ({ x: s.inst.name, y: Number.isFinite(s.levelOut) ? +s.levelOut.toFixed(1) : 0 }))
  const charts: ChartSpec[] = []
  if (levelPoints.length > 1) {
    charts.push({
      id: 'power-chain',
      kind: 'line',
      title: '沿链路功率电平',
      yUnit: 'dBm',
      yLabel: '电平',
      series: [{ name: '电平', color: rfCategory('source').primary, points: levelPoints }],
      reference: Number.isFinite(link.sensitivity) ? { value: +link.sensitivity.toFixed(1), label: '灵敏度', color: '#DC2626' } : undefined,
      explanation: '展示信号从源到接收机的电平变化，越靠近灵敏度线余量越小。',
    })
  }

  // Chart 2: gain/loss contribution per stage
  const contribPoints = link.stages
    .filter((s) => s.inst.schemaId !== 'rf.source' && s.gainDb !== 0)
    .map((s) => ({ x: s.inst.name, y: +s.gainDb.toFixed(1) }))
  if (contribPoints.length) {
    charts.push({
      id: 'gain-contrib',
      kind: 'bar',
      title: '各级增益 / 损耗贡献',
      yUnit: 'dB',
      series: [{ name: '贡献', points: contribPoints }],
      explanation: '正值为增益，负值为损耗。损耗最大的元件通常是优化重点。',
    })
  }

  // Chart 3: noise contribution share over the receive sub-chain (Friis terms)
  const rxChain = receiveSubchain(link.stages, link.receiver?.uid)
  const { terms } = friisNf(rxChain)
  const denom = terms.reduce((a, t) => a + t.term, 0)
  const noiseShare: { x: string; y: number }[] = rxChain.map((s, i) => ({
    x: s.inst.name,
    y: denom > 0 ? +Math.max(0, (terms[i].term / denom) * 100).toFixed(0) : 0,
  }))
  if (noiseShare.some((p) => p.y > 0)) {
    charts.push({
      id: 'noise-contrib',
      kind: 'bar',
      title: '噪声贡献占比',
      yUnit: '%',
      series: [{ name: '占比', color: rfCategory('active').primary, points: noiseShare }],
      explanation: '越靠前的级对系统噪声影响越大，优先优化前级噪声系数。',
    })
  }

  const rawRows: RawRow[] = link.stages.map((s) => ({
    label: s.inst.name,
    values: [
      { key: 'role', value: s.role },
      { key: 'levelIn', value: `${fmt(s.levelIn)} dBm` },
      { key: 'levelOut', value: `${fmt(s.levelOut)} dBm` },
      { key: 'gain', value: `${s.gainDb >= 0 ? '+' : ''}${fmt(s.gainDb)} dB` },
      { key: 'nf', value: s.nfDb > 0 ? `${fmt(s.nfDb)} dB` : '—' },
    ],
  }))

  const summary = buildSummary(link, status)

  const outputs: Record<string, Record<string, number | string>> = {}
  const componentStatus: Record<string, Status> = {}
  for (const s of link.stages) {
    outputs[s.inst.uid] = s.outputs
    componentStatus[s.inst.uid] = s.compressionRisk ? 'warning' : 'done'
  }
  for (const c of design.components) {
    if (c.disabled) componentStatus[c.uid] = 'disabled'
    else if (!(c.uid in componentStatus)) componentStatus[c.uid] = 'ready'
  }

  const suggestedActions = []
  if (link.limiting) {
    suggestedActions.push({
      label: `查看 ${link.limiting.label}`,
      intent: { type: 'open-component' as const, uid: link.limiting.uid },
    })
  }
  if (issues.length) {
    suggestedActions.push({ label: '查看全部问题', intent: { type: 'open-issues' as const } })
  }

  return { status, summary, keyValues, charts, issues, rawRows, suggestedActions, outputs, componentStatus }
}

function buildSummary(link: LinkCalc, status: Status): string {
  if (!link.receiver) {
    return '链路尚未包含接收机，无法计算余量与灵敏度。添加接收机后即可得到完整结果。'
  }
  if (!Number.isFinite(link.margin)) {
    return '部分接收机参数缺失，无法完成余量计算。请补全带宽、噪声系数与所需信噪比。'
  }
  const m = link.margin
  const lim = link.limiting ? `主要限制元素为「${link.limiting.label}」（${link.limiting.reason}）。` : ''
  if (m < 0) {
    return `当前接收功率低于灵敏度，链路余量为 ${m.toFixed(1)} dB，无法可靠工作。${lim}建议提高增益或降低损耗。`
  }
  if (m < 3) {
    return `链路可工作但余量偏低（${m.toFixed(1)} dB），抗波动能力弱。${lim}建议增加余量到 3 dB 以上。`
  }
  return `链路状态良好，余量 ${m.toFixed(1)} dB，接收功率 ${link.outputPower.toFixed(1)} dBm，系统噪声系数 ${link.systemNf.toFixed(1)} dB。${lim}`
  void status
}
