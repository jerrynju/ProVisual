/*
 * Component details (§3.4.5, §6.6). Single edit & inspect surface.
 * Tabs: 摘要 / 参数 / 输入 / 输出 / 图表 / 问题 / 高级. Summary first; advanced collapsed.
 */
import { useState } from 'react'
import { useStore } from '../shared/store'
import { ParamField } from '../shared/ui/ParamField'
import { IssueCard } from '../shared/ui/IssueCard'
import { StatusBadge } from '../shared/ui/primitives'
import { categoryColor, componentStatus, resolveFields } from './helpers'

type Tab = 'summary' | 'params' | 'inputs' | 'outputs' | 'issues' | 'advanced'
const TABS: { key: Tab; label: string }[] = [
  { key: 'summary', label: '摘要' },
  { key: 'params', label: '参数' },
  { key: 'inputs', label: '输入' },
  { key: 'outputs', label: '输出' },
  { key: 'issues', label: '问题' },
  { key: 'advanced', label: '高级' },
]

export function ComponentDetails({ uid }: { uid: string }) {
  const { design, plugin, result, dispatch } = useStore()
  const [tab, setTab] = useState<Tab>('summary')
  const inst = design?.components.find((c) => c.uid === uid)
  if (!design || !inst) return <div className="empty">组件不存在</div>
  const schema = plugin.getSchema(inst.schemaId)
  if (!schema) return <div className="empty">未知组件类型</div>

  const status = componentStatus(plugin, inst, result)
  const fields = resolveFields(plugin, inst, result)
  const outputs = result?.outputs[inst.uid]
  const issues = plugin.validate(design).filter((i) => i.affectedObject === inst.uid)
  const basic = schema.parameters.filter((p) => !p.advanced)
  const advanced = schema.parameters.filter((p) => p.advanced)
  const color = categoryColor(plugin, inst.schemaId)

  const setParam = (key: string, value: number | string) => dispatch({ type: 'SET_PARAM', compUid: inst.uid, key, value })

  return (
    <div className="page fade-in">
      <div className="row">
        <span className="cc__icon" style={{ background: color }}>{schema.icon ?? '◻'}</span>
        <div style={{ flex: 1, minWidth: 0 }}>
          <input
            className="title-input"
            value={inst.name}
            onChange={(e) => dispatch({ type: 'RENAME_COMPONENT', compUid: inst.uid, name: e.target.value })}
          />
          <div className="meta" style={{ color }}>{schema.role}</div>
        </div>
        <StatusBadge status={status} />
      </div>

      <div className="tabs">
        {TABS.map((t) => (
          <button key={t.key} className={`tabs__item ${tab === t.key ? 'is-active' : ''}`} onClick={() => setTab(t.key)}>
            {t.label}
            {t.key === 'issues' && issues.length > 0 ? ` (${issues.length})` : ''}
          </button>
        ))}
      </div>

      {tab === 'summary' && (
        <div className="stack">
          <p className="muted">{schema.description}</p>
          <div className="card">
            {fields.map((f, i) => (
              <div className="kv-line" key={i}>
                <span className="kv-line__label">{f.label}</span>
                <span className="kv-line__value">{f.value}{f.unit ? ` ${f.unit}` : ''}</span>
              </div>
            ))}
            {outputs && (
              <>
                <div className="kv-line"><span className="kv-line__label">输入电平</span><span className="kv-line__value">{outputs.levelIn} dBm</span></div>
                <div className="kv-line"><span className="kv-line__label">输出电平</span><span className="kv-line__value">{outputs.levelOut} dBm</span></div>
              </>
            )}
          </div>
          <button className="btn btn--secondary btn--block" onClick={() => setTab('params')}>编辑参数</button>
        </div>
      )}

      {tab === 'params' && (
        <div className="card">
          {basic.length === 0 && <div className="empty">该组件没有可配置参数</div>}
          {basic.map((p) => (
            <ParamField key={p.key} schema={p} value={inst.params[p.key]} onChange={(v) => setParam(p.key, v)} />
          ))}
        </div>
      )}

      {tab === 'inputs' && (
        <div className="card">
          {schema.inputs.length === 0 && <div className="empty">无输入端口</div>}
          {schema.inputs.map((p) => (
            <div className="kv-line" key={p.key}>
              <span className="kv-line__label">{p.label}</span>
              <span className="kv-line__value">{outputs?.levelIn ?? '—'}{p.unit ? ` ${p.unit}` : ''}</span>
            </div>
          ))}
        </div>
      )}

      {tab === 'outputs' && (
        <div className="card">
          {schema.outputs.length === 0 && <div className="empty">无输出端口</div>}
          {schema.outputs.map((p) => (
            <div className="kv-line" key={p.key}>
              <span className="kv-line__label">{p.label}</span>
              <span className="kv-line__value">{outputs?.levelOut ?? '—'}{p.unit ? ` ${p.unit}` : ''}</span>
            </div>
          ))}
          {outputs && (
            <div className="kv-line"><span className="kv-line__label">本级增益贡献</span><span className="kv-line__value">{outputs.gainDb} dB</span></div>
          )}
        </div>
      )}

      {tab === 'issues' && (
        <div className="stack">
          {issues.length === 0 && <div className="empty">该组件没有问题</div>}
          {issues.map((i) => (
            <IssueCard key={i.id} issue={i} />
          ))}
        </div>
      )}

      {tab === 'advanced' && (
        <div className="card">
          {advanced.length === 0 && <div className="empty">没有高级参数</div>}
          {advanced.map((p) => (
            <ParamField key={p.key} schema={p} value={inst.params[p.key]} onChange={(v) => setParam(p.key, v)} />
          ))}
        </div>
      )}
    </div>
  )
}
