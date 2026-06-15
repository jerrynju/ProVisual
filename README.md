# ProRF App — 界面原型（Android）

基于 **Pro Workflow UI System v2.0** 设计稿，使用现代 Android 技术栈复刻的射频链路预算工具界面原型。

> 本项目聚焦 **界面原型验证**：完整实现页面布局、按钮点击与页面切换等交互，**暂不包含底层计算逻辑与后台服务**（数据均为示例数据，运行/计算类按钮以 Toast 形式给出反馈）。

## 技术栈

- **Kotlin** + **Jetpack Compose**（声明式 UI）
- **Material 3** 设计系统（自定义 ProRF 浅色主题）
- **Navigation Compose**（页面路由与底部导航）
- 单 Activity 架构，`compileSdk 34` / `minSdk 26`

## 已实现页面（对应设计稿编号）

| 编号 | 页面 | 说明 |
| --- | --- | --- |
| 08 | 首页仪表盘 | 用户信息、统计卡片、快速操作、最近工作流 |
| 01 | 工作流列表 | 状态筛选 Tab、卡片列表、新建工作流 FAB |
| 02 | 工作流详情 | 节点/链接/阶段统计、描述、标签、底部操作栏 |
| 02 | 工作流编辑器 | 拓扑视图（信号链示意）/ 流程视图（节点列表）切换 |
| 03 | 节点属性 | 参数 / 输入输出 / 规格 / 图表 Tab，运行节点 |
| 04 | 结果总览 | EIRP / 接收功率 / 系统增益指标卡、增益分布柱状图 |
| 05 | 场景库 | 搜索、分类筛选、场景卡片列表 |
| 06 | 节点库 | 分类筛选、节点组件网格 |
| 07 | 模板库 | 工作流模板列表、导入模板 |
| 09 | 分析图表 | 图表 / 数据 / 设置 Tab，发散式柱状图 |
| 10 | 设置中心 | 分组设置项、退出登录 |

底部导航：**工作流 · 库 · 结果 · 场景 · 我的**（Material 3 NavigationBar），二级页面通过返回栈跳转；首页仪表盘可从「我的」进入。

### 拓扑视图 / 流程视图

- **拓扑视图**：基于 Canvas 的节点图编辑器 —— 节点卡片含输入/输出端口，端口间以贝塞尔曲线连线，点状网格背景，支持双指缩放与拖拽平移；顶部为编辑工具栏（选择/设置/运行/缩放），底部为「添加节点 / 添加连接 / 注释」操作条。
- **流程视图**：竖向时间轴 —— 渐变连接轴串联带编号的彩色节点标记，卡片展示类型标签、增益数值与子参数（如 Pout/频率/模式），定向耦合器额外展示分支（耦合端）参数。

## 交互说明

- **页面切换**：底部导航切换一级页面；列表项 / 卡片点击进入详情、编辑器、节点属性、结果等二级页面；返回按钮逐级回退。
- **按钮反馈**：新建、导入、运行、导出等尚未接入逻辑的操作，点击后以 Toast 提示，便于原型走查。
- **状态切换**：筛选 Tab、分段控件、开关等为真实可交互状态。

## 构建与运行

```bash
# 需要 Android SDK（platform 34、build-tools 34.0.0）
./gradlew :app:assembleDebug
# 产物：app/build/outputs/apk/debug/app-debug.apk
```

在 Android Studio 中直接打开工程并运行到设备 / 模拟器即可。

## 架构分层（对齐 ProRF Build Spec v1.0）

按 Spec 的分层原则在 app 内以包结构落地（详见 `docs/decisions/ADR-0001`）：

| 层 | 包 | 职责 |
| --- | --- | --- |
| L0 平台核心 | `platform` | Graph/Node/Port/Edge、DAG 执行引擎（拓扑排序+缓存+环检测）、节点注册表、CapabilityService。**不含任何 RF/UI 概念** |
| L1 工程基础 | `engineering` | `Quantity = 值 + 单位 + 量纲`，对数/线性处理，FSPL 公式 |
| L3 RF 领域 | `domain.rf` | RF 节点定义（纯执行器）、链路预算工作流模板、`RfLinkBudget.compute()`，仅依赖 platform+engineering |
| L2 UI | `ui` + `data.RfPresenter` | 渲染计算结果并叠加图标/配色，**不做任何 RF 计算** |
| L4 App Shell | `com.prorf.app` | MainActivity、CapabilityService 装配 |

关键改造：参数采用 `Quantity`（值+单位+量纲），杜绝裸 double 与字符串解析单位（§7）；链路预算由引擎**计算**、UI 仅展示（§6/§12）；Inspector 改为 **输入/参数/输出/诊断/图表**，节点卡片含 标题/参数摘要/状态/输出摘要（§9）；导出经 `CapabilityService.has()` 门控（§10）。

自验证：`./gradlew :app:testDebugUnitTest` —— 执行引擎拓扑/环检测、链路预算计算（EIRP/接收功率/余量）单元测试全部通过。

## 目录结构

```
app/src/main/java/com/prorf/app/
├── MainActivity.kt
├── data/                 # 数据模型与示例数据
├── ui/
│   ├── ProRFApp.kt       # NavHost + 底部导航
│   ├── nav/              # 路由定义
│   ├── theme/            # 颜色 / 字体 / 主题
│   ├── components/       # 通用组件（卡片、Tab、图表、顶栏等）
│   └── screens/          # 各页面
```
