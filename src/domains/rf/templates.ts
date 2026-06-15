/*
 * RF templates (§6.9). Each creates a runnable design, not a static example.
 */
import type { Template } from '../../shared/domain'
import { linearDesign } from './factory'

export const RF_TEMPLATES: Template[] = [
  {
    id: 'simple-link',
    name: '简单链路',
    description: '信号源 → 放大器 → 接收机，最小可运行链路。',
    appliesTo: '快速验证一条增益链路的余量。',
    assumptions: ['理想匹配', '常温工作'],
    build: () =>
      linearDesign([
        ['rf.source', '信号源', { power: -10, freq: 2.4 }],
        ['rf.amplifier', '低噪放', { gain: 20, nf: 1.5, p1db: 15 }],
        ['rf.receiver', '接收机', { nf: 5, bandwidth: 20, reqSnr: 10 }],
      ]),
  },
  {
    id: 'rx-chain',
    name: '接收链路',
    description: '天线 → 低噪放 → 滤波器 → 接收机，强调前级噪声。',
    appliesTo: '评估接收灵敏度与系统噪声系数。',
    assumptions: ['天线增益已含', '带内分析'],
    build: () =>
      linearDesign([
        ['rf.source', '入射信号', { power: -85, freq: 2.4 }],
        ['rf.antenna', '接收天线', { gain: 8 }],
        ['rf.amplifier', '低噪放', { gain: 22, nf: 1.2, p1db: 10 }],
        ['rf.filter', '带通滤波器', { loss: 1.5, bandwidth: 40 }],
        ['rf.cable', '馈线', { loss: 2, length: 8 }],
        ['rf.receiver', '接收机', { nf: 6, bandwidth: 20, reqSnr: 12 }],
      ]),
  },
  {
    id: 'tx-chain',
    name: '发射链路',
    description: '信号源 → 驱动 → 功放 → 滤波器 → 天线，关注输出功率与压缩。',
    appliesTo: '评估发射输出功率与压缩风险。',
    assumptions: ['线性区分析', '不含数字预失真'],
    build: () =>
      linearDesign([
        ['rf.source', '基带源', { power: -5, freq: 5.8 }],
        ['rf.amplifier', '驱动放大', { gain: 15, nf: 4, p1db: 10 }],
        ['rf.amplifier', '功率放大', { gain: 25, nf: 6, p1db: 33 }],
        ['rf.filter', '谐波滤波', { loss: 1, bandwidth: 200 }],
        ['rf.antenna', '发射天线', { gain: 12 }],
      ]),
  },
  {
    id: 'link-budget',
    name: '链路预算',
    description: '发射到接收的完整预算：发射链 + 路径损耗 + 接收链。',
    appliesTo: '端到端无线链路余量分析。',
    assumptions: ['自由空间路径损耗近似为固定值', '常温'],
    build: () =>
      linearDesign([
        ['rf.source', '发射源', { power: 0, freq: 2.4 }],
        ['rf.amplifier', '功放', { gain: 20, nf: 6, p1db: 30 }],
        ['rf.antenna', '发射天线', { gain: 10 }],
        ['rf.cable', '自由空间路径', { loss: 90, length: 0 }],
        ['rf.antenna', '接收天线', { gain: 10 }],
        ['rf.amplifier', '低噪放', { gain: 22, nf: 1.5, p1db: 12 }],
        ['rf.receiver', '接收机', { nf: 5, bandwidth: 10, reqSnr: 12 }],
      ]),
  },
  {
    id: 'measurement',
    name: '测量配置',
    description: '信号源 → 衰减器 → 测量点，用于台架电平测量。',
    appliesTo: '仪表测量与电平标定。',
    assumptions: ['仪表输入匹配'],
    build: () =>
      linearDesign([
        ['rf.source', '信号发生器', { power: 0, freq: 1 }],
        ['rf.attenuator', '程控衰减', { loss: 10 }],
        ['rf.measure', '功率计'],
      ]),
  },
  {
    id: 'blank',
    name: '空白设计',
    description: '从一个信号源开始，自行搭建链路。',
    appliesTo: '完全自定义的链路。',
    build: () => linearDesign([['rf.source', '信号源', { power: -10, freq: 2.4 }]]),
  },
]
