/*
 * RF component schemas — domain content (§5.3, §6.3).
 * Each schema is purely descriptive: parameters, ports and which values to
 * surface on a card. No math here; the calculator owns all formulas.
 */
import type { ComponentSchema } from '../../shared/types'

const SIGNAL_IN = { key: 'in', label: '信号输入', unit: 'dBm' }
const SIGNAL_OUT = { key: 'out', label: '信号输出', unit: 'dBm' }

export const RF_SCHEMAS: ComponentSchema[] = [
  {
    id: 'rf.source',
    name: '信号源',
    role: '信号源',
    category: 'source',
    icon: '◎',
    description: '链路的起点，定义输入功率与工作频率。',
    parameters: [
      { key: 'power', label: '输出功率', unit: 'dBm', control: 'slider', min: -40, max: 30, step: 0.5, default: -10, recommended: [-30, 10], hint: '链路起始功率电平。' },
      { key: 'freq', label: '工作频率', unit: 'GHz', control: 'number', min: 0.01, max: 100, step: 0.1, default: 2.4, hint: '用于电缆损耗等频率相关估算。' },
    ],
    inputs: [],
    outputs: [SIGNAL_OUT],
    display: [
      { key: 'power', label: '功率', unit: 'dBm', source: 'param' },
      { key: 'freq', label: '频率', unit: 'GHz', source: 'param' },
    ],
  },
  {
    id: 'rf.amplifier',
    name: '放大器',
    role: '增益级',
    category: 'active',
    icon: '△',
    description: '提供增益的有源元件，会引入噪声并存在压缩风险。',
    parameters: [
      { key: 'gain', label: '增益', unit: 'dB', control: 'slider', min: 0, max: 40, step: 0.5, default: 20, recommended: [10, 30], hint: '该级信号放大量。' },
      { key: 'nf', label: '噪声系数', unit: 'dB', control: 'number', min: 0, max: 15, step: 0.1, default: 2, recommended: [0.5, 6], hint: '越靠前的级对系统噪声影响越大。' },
      { key: 'p1db', label: '输出 P1dB', unit: 'dBm', control: 'number', min: -10, max: 40, step: 0.5, default: 18, advanced: true, hint: '输出 1dB 压缩点，用于评估压缩风险。' },
    ],
    inputs: [SIGNAL_IN],
    outputs: [SIGNAL_OUT],
    advanced: ['p1db'],
    display: [
      { key: 'gain', label: '增益', unit: 'dB', source: 'param' },
      { key: 'nf', label: 'NF', unit: 'dB', source: 'param' },
    ],
  },
  {
    id: 'rf.attenuator',
    name: '衰减器',
    role: '损耗级',
    category: 'passive',
    icon: '▽',
    description: '固定衰减，用于控制电平、保护下游或改善匹配。',
    parameters: [
      { key: 'loss', label: '衰减量', unit: 'dB', control: 'stepper', min: 0, max: 40, step: 0.5, default: 6, recommended: [0, 20], hint: '无源衰减，其噪声系数等于衰减量。' },
    ],
    inputs: [SIGNAL_IN],
    outputs: [SIGNAL_OUT],
    display: [{ key: 'loss', label: '衰减', unit: 'dB', source: 'param' }],
  },
  {
    id: 'rf.cable',
    name: '电缆/路径',
    role: '损耗级',
    category: 'passive',
    icon: '〰',
    description: '传输路径损耗，可由长度与单位损耗估算。',
    parameters: [
      { key: 'loss', label: '路径损耗', unit: 'dB', control: 'number', min: 0, max: 60, step: 0.1, default: 3, recommended: [0, 30], hint: '该段总损耗。' },
      { key: 'length', label: '长度', unit: 'm', control: 'number', min: 0, max: 1000, step: 0.5, default: 5, advanced: true, hint: '仅作记录，不直接参与计算。' },
    ],
    inputs: [SIGNAL_IN],
    outputs: [SIGNAL_OUT],
    advanced: ['length'],
    display: [{ key: 'loss', label: '损耗', unit: 'dB', source: 'param' }],
  },
  {
    id: 'rf.filter',
    name: '滤波器',
    role: '损耗级',
    category: 'passive',
    icon: '⊓',
    description: '带通/带阻滤波，带内引入插入损耗。',
    parameters: [
      { key: 'loss', label: '插入损耗', unit: 'dB', control: 'number', min: 0, max: 20, step: 0.1, default: 1.5, recommended: [0, 4], hint: '带内损耗，按无源 NF 计入噪声。' },
      { key: 'bandwidth', label: '带宽', unit: 'MHz', control: 'number', min: 0.1, max: 2000, step: 1, default: 100, advanced: true, hint: '通带宽度，仅作记录。' },
    ],
    inputs: [SIGNAL_IN],
    outputs: [SIGNAL_OUT],
    advanced: ['bandwidth'],
    display: [{ key: 'loss', label: '插损', unit: 'dB', source: 'param' }],
  },
  {
    id: 'rf.antenna',
    name: '天线',
    role: '天线',
    category: 'antenna',
    icon: '☰',
    description: '收发增益元件，发射端提升 EIRP，接收端提升接收电平。',
    parameters: [
      { key: 'gain', label: '天线增益', unit: 'dBi', control: 'slider', min: -10, max: 40, step: 0.5, default: 6, recommended: [0, 25], hint: '相对各向同性的增益。' },
    ],
    inputs: [SIGNAL_IN],
    outputs: [SIGNAL_OUT],
    display: [{ key: 'gain', label: '增益', unit: 'dBi', source: 'param' }],
  },
  {
    id: 'rf.receiver',
    name: '接收机',
    role: '接收机',
    category: 'receiver',
    icon: '◍',
    description: '链路终端，定义系统带宽、噪声系数与解调所需信噪比。',
    parameters: [
      { key: 'nf', label: '噪声系数', unit: 'dB', control: 'number', min: 0, max: 15, step: 0.1, default: 5, recommended: [1, 8], hint: '接收机自身噪声系数。' },
      { key: 'bandwidth', label: '带宽', unit: 'MHz', control: 'number', min: 0.001, max: 2000, step: 0.1, default: 20, recommended: [0.01, 200], hint: '决定本底噪声与灵敏度。' },
      { key: 'reqSnr', label: '所需信噪比', unit: 'dB', control: 'number', min: -10, max: 40, step: 0.5, default: 10, recommended: [3, 25], hint: '解调门限，用于计算余量。' },
    ],
    inputs: [SIGNAL_IN],
    outputs: [],
    display: [
      { key: 'nf', label: 'NF', unit: 'dB', source: 'param' },
      { key: 'bandwidth', label: 'BW', unit: 'MHz', source: 'param' },
    ],
  },
  {
    id: 'rf.measure',
    name: '测量点',
    role: '测量点',
    category: 'measure',
    icon: '⌖',
    description: '记录此处的功率电平，不改变链路。',
    parameters: [],
    inputs: [SIGNAL_IN],
    outputs: [SIGNAL_OUT],
    display: [{ key: 'levelOut', label: '电平', unit: 'dBm', source: 'output' }],
  },
]
