/*
 * More (§3.3): project settings, import/export, advanced tools, help.
 * Advanced tools are shared but hidden behind the default path (§3.11) and
 * each entry explains why a user might need it.
 */
import { useStore } from '../shared/store'
import { Card, SectionHeader } from '../shared/ui/primitives'

export function More() {
  const { project, design, plugin, dispatch } = useStore()

  const advancedTools = [
    { name: '自由画布编辑', why: '需要非线性拓扑或显式连接时使用。', go: () => design && dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'canvas' } }) },
    { name: '原始结果表', why: '查看每一级的精确电平与噪声数据。', go: () => design && dispatch({ type: 'NAV', ref: { tab: 'results', screen: 'results' } }) },
    { name: 'Schema 查看器', why: '检查组件 schema 的参数与端口定义。', go: () => alert('Schema 查看器（演示）：组件由 schema 驱动渲染。') },
  ]

  return (
    <div className="page fade-in">
      <h1 className="page__title">更多</h1>

      {project && (
        <>
          <SectionHeader title="项目" />
          <Card>
            <div className="kv-line"><span className="kv-line__label">名称</span><span className="kv-line__value">{project.name}</span></div>
            <div className="kv-line"><span className="kv-line__label">领域</span><span className="kv-line__value">{plugin.label}</span></div>
            <div className="kv-line"><span className="kv-line__label">设计数量</span><span className="kv-line__value">{project.designs.length}</span></div>
            <div className="kv-line"><span className="kv-line__label">上下文</span><span className="kv-line__value">{project.context ?? '—'}</span></div>
          </Card>
          <button className="btn btn--secondary btn--block" onClick={() => dispatch({ type: 'TAB', tab: 'design' })}>返回项目首页</button>
        </>
      )}

      <SectionHeader title="高级工具" />
      <p className="muted" style={{ marginTop: -8 }}>这些能力存在但不在默认路径上，仅在需要时进入。</p>
      <div className="stack">
        {advancedTools.map((t) => (
          <button key={t.name} className="list-row" onClick={t.go}>
            <span className="list-row__label">
              <span className="list-row__title">{t.name}</span>
              <span className="list-row__sub">{t.why}</span>
            </span>
            <span className="cc__chevron">›</span>
          </button>
        ))}
      </div>

      <SectionHeader title="项目列表" />
      <div className="stack">
        <button className="list-row" onClick={() => dispatch({ type: 'NAV', ref: { tab: 'design', screen: 'home' } })}>
          <span className="list-row__label"><span className="list-row__title">全部项目与模板</span><span className="list-row__sub">回到首页创建或打开项目</span></span>
          <span className="cc__chevron">›</span>
        </button>
        {project && (
          <button className="list-row" onClick={() => { if (confirm('删除当前项目？')) dispatch({ type: 'DELETE_PROJECT', projectId: project.id }) }}>
            <span className="list-row__label"><span className="list-row__title" style={{ color: 'var(--color-error)' }}>删除当前项目</span></span>
            <span className="cc__chevron">›</span>
          </button>
        )}
      </div>

      <SectionHeader title="关于" />
      <Card>
        <p style={{ margin: 0, lineHeight: 1.6 }}>
          {plugin.label} · 基于「Pro Workflow App Matrix 统一界面设计语言」构建。
          共享 App Shell 与 UI 保持领域中立，射频语义通过领域插件注入。
        </p>
      </Card>
    </div>
  )
}
