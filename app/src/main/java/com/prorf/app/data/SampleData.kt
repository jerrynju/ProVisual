package com.prorf.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Waves
import com.prorf.app.ui.theme.ProColors

/**
 * Static sample data backing the prototype. No business logic — these values
 * simply populate the screens so the navigation and interactions can be validated.
 */
object SampleData {

    val workflows = listOf(
        Workflow(
            id = "rf-link",
            title = "RF 链路预算示例",
            subtitle = "射频收发链路 · 12 节点",
            status = WorkflowStatus.Running,
            icon = Icons.Filled.Podcasts,
            accent = ProColors.Primary,
            nodeCount = 12, linkCount = 24, stageCount = 6,
            updated = "2024-05-20 16:30",
            tags = listOf("射频", "链路预算", "Sub-6"),
        ),
        Workflow(
            id = "satellite",
            title = "卫星通信链路分析",
            subtitle = "Ka 频段下行链路 · 14 节点",
            status = WorkflowStatus.Running,
            icon = Icons.Filled.SatelliteAlt,
            accent = ProColors.Cyan,
            nodeCount = 14, linkCount = 28, stageCount = 7,
            updated = "2024-05-19 09:12",
            tags = listOf("卫星", "Ka 频段", "下行"),
        ),
        Workflow(
            id = "5g-uplink",
            title = "5G NR 上行链路",
            subtitle = "n78 上行覆盖评估 · 10 节点",
            status = WorkflowStatus.Done,
            icon = Icons.Filled.CellTower,
            accent = ProColors.Purple,
            nodeCount = 10, linkCount = 20, stageCount = 5,
            updated = "2024-05-17 21:40",
            tags = listOf("5G", "NR", "上行"),
        ),
        Workflow(
            id = "radar",
            title = "雷达系统链路预算",
            subtitle = "X 波段相控阵 · 16 节点",
            status = WorkflowStatus.Done,
            icon = Icons.Filled.Radar,
            accent = ProColors.Orange,
            nodeCount = 16, linkCount = 32, stageCount = 8,
            updated = "2024-05-15 11:05",
            tags = listOf("雷达", "X 波段", "相控阵"),
        ),
        Workflow(
            id = "drone",
            title = "无人机链路分析",
            subtitle = "2.4G 图传链路 · 8 节点",
            status = WorkflowStatus.Draft,
            icon = Icons.Filled.Flight,
            accent = ProColors.Green,
            nodeCount = 8, linkCount = 14, stageCount = 4,
            updated = "2024-05-14 18:20",
            tags = listOf("无人机", "图传", "2.4G"),
        ),
        Workflow(
            id = "custom",
            title = "自定义链路方案",
            subtitle = "空白模板 · 0 节点",
            status = WorkflowStatus.Draft,
            icon = Icons.Filled.Tune,
            accent = ProColors.TextSecondary,
            nodeCount = 0, linkCount = 0, stageCount = 0,
            updated = "2024-05-12 08:00",
            tags = listOf("自定义"),
        ),
    )

    fun workflow(id: String?): Workflow = workflows.firstOrNull { it.id == id } ?: workflows.first()

    // ---- RF link-budget chain: computed by the domain engine, not hardcoded (§6/§7/§12) ----
    val flowNodes: List<FlowNode> get() = RfPresenter.flowNodes
    val graphNodes: List<GraphNode> get() = RfPresenter.graphNodes
    val graphEdges: List<GraphEdge> get() = RfPresenter.graphEdges
    val flowSummary: List<Metric> get() = RfPresenter.flowSummary
    val resultMetrics: List<Metric> get() = RfPresenter.resultMetrics
    val gainBars: List<Bar> get() = RfPresenter.gainBars
    val noiseSummary: List<Metric> get() = RfPresenter.noiseSummary
    val pathLoss: Metric get() = RfPresenter.pathLoss

    val scenes = listOf(
        LibraryItem("默认场景", "基础链路模板", "+52.0 dB", true, Icons.Filled.Hub, ProColors.Primary),
        LibraryItem("高增益天线场景", "大口径定向天线", "+72.6 dB", true, Icons.Filled.SettingsInputAntenna, ProColors.Green),
        LibraryItem("长距离链路场景", "远距离视距传输", "-8.8 dB", false, Icons.Filled.Timeline, ProColors.Orange),
        LibraryItem("低温环境场景", "低噪声接收前端", "+9.6 dB", true, Icons.Filled.Insights, ProColors.Cyan),
    )

    val templates = listOf(
        LibraryItem("RF 链路预算模板", "射频收发标准流程", "12 节点", true, Icons.Filled.Podcasts, ProColors.Primary),
        LibraryItem("卫星链路模板", "Ka 频段链路预算", "14 节点", true, Icons.Filled.SatelliteAlt, ProColors.Cyan),
        LibraryItem("5G 链路模板", "5G NR 链路预算", "10 节点", true, Icons.Filled.CellTower, ProColors.Purple),
        LibraryItem("雷达链路预算", "相控阵链路预算", "16 节点", true, Icons.Filled.Radar, ProColors.Orange),
    )

    val nodeCategories = listOf("全部", "链路源", "有源器件", "无源器件", "天线", "工具")

    val nodeComponents = listOf(
        NodeComponent("发射机", "链路源", Icons.Filled.Podcasts, ProColors.Primary),
        NodeComponent("功率放大器", "有源器件", Icons.Filled.Bolt, ProColors.Orange),
        NodeComponent("低噪声放大器", "有源器件", Icons.Filled.GraphicEq, ProColors.Green),
        NodeComponent("混频器", "有源器件", Icons.Filled.SwapHoriz, ProColors.Purple),
        NodeComponent("滤波器", "无源器件", Icons.Filled.FilterAlt, ProColors.Cyan),
        NodeComponent("接收机", "链路源", Icons.Filled.CenterFocusStrong, ProColors.Primary),
        NodeComponent("定向耦合器", "无源器件", Icons.Filled.CallSplit, ProColors.Purple),
        NodeComponent("衰减器", "无源器件", Icons.Filled.Tune, ProColors.Orange),
        NodeComponent("分配器", "无源器件", Icons.Filled.Hub, ProColors.Green),
        NodeComponent("开关", "工具", Icons.Filled.SettingsInputComponent, ProColors.Cyan),
        NodeComponent("天线", "天线", Icons.Filled.SettingsInputAntenna, ProColors.Primary),
        NodeComponent("自由空间传播", "工具", Icons.Filled.Waves, ProColors.Red),
        NodeComponent("移相器", "有源器件", Icons.Filled.ShowChart, ProColors.Purple),
        NodeComponent("解调器", "工具", Icons.Filled.Memory, ProColors.Orange),
        NodeComponent("测量仪表", "工具", Icons.Filled.Insights, ProColors.Cyan),
    )

    // Home dashboard
    const val userName = "Alex Chen"
    const val userPlan = "Pro 会员 · 2024.12.31 到期"
    val dashboardStats = listOf(
        Metric("活跃工作流", "8", "个", ProColors.Primary),
        Metric("成功率", "87", "%", ProColors.Green),
        Metric("节省工时", "3.2", "h", ProColors.Orange),
    )
}
