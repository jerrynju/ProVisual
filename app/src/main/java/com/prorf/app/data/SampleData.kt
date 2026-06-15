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

    val flowNodes = listOf(
        FlowNode(
            "tx", 1, "发射机", "信号源", "TX", null, "+25.0 dBm", true,
            Icons.Filled.Podcasts, ProColors.Primary,
            params = listOf(ParamKV("Pout", "+20.0 dBm"), ParamKV("频率", "2.4 GHz"), ParamKV("模式", "CW")),
        ),
        FlowNode(
            "pa", 2, "功率放大器", "有源器件", "AMP", null, "+43.0 dB", true,
            Icons.Filled.Bolt, ProColors.Orange,
            params = listOf(ParamKV("增益", "+23.0 dB"), ParamKV("NF", "2.0 dB"), ParamKV("P1dB", "+33.0 dB")),
        ),
        FlowNode(
            "coup", 3, "定向耦合器", "无源器件", "COUP", "2dB", "-1.5 dB", false,
            Icons.Filled.CallSplit, ProColors.Purple,
            params = listOf(ParamKV("插损", "-20.0 dB"), ParamKV("插入损耗", "-9.8 dB")),
            branch = listOf(ParamKV("返增辐", "-0.5 dB", false), ParamKV("耦合端", "-20.8 dB", false)),
        ),
        FlowNode(
            "ant1", 4, "发射天线", "天线", "ANT", null, "+15.0 dBi", true,
            Icons.Filled.SettingsInputAntenna, ProColors.Cyan,
            params = listOf(ParamKV("增益", "+15.0 dBi"), ParamKV("方向性", "12.0 dB")),
        ),
        FlowNode(
            "fspl", 5, "自由空间传播", "信道", "PATH", "1km", "-161.2 dB", false,
            Icons.Filled.Waves, ProColors.Red,
            params = listOf(ParamKV("频率", "2.4 GHz"), ParamKV("距离", "1.0 km")),
        ),
        FlowNode(
            "ant2", 6, "接收天线", "天线", "ANT", null, "+15.0 dBi", true,
            Icons.Filled.SettingsInputAntenna, ProColors.Cyan,
            params = listOf(ParamKV("增益", "+15.0 dBi"), ParamKV("方向性", "12.0 dB")),
        ),
        FlowNode(
            "lna", 7, "低噪声放大器", "有源器件", "LNA", null, "+18.0 dB", true,
            Icons.Filled.GraphicEq, ProColors.Green,
            params = listOf(ParamKV("增益", "+18.0 dB"), ParamKV("NF", "1.2 dB")),
        ),
        FlowNode(
            "rx", 8, "接收机", "信号端", "RX", null, "-84.2 dBm", false,
            Icons.Filled.CenterFocusStrong, ProColors.Primary,
            params = listOf(ParamKV("NF", "3.0 dB"), ParamKV("灵敏度", "-146.0 dBm")),
        ),
    )

    /** Topology graph: node boxes positioned on the canvas (dp) + connections. */
    val graphNodes = listOf(
        GraphNode(
            "tx", "发射机", "TX", Icons.Filled.Podcasts, ProColors.Primary, x = 24, y = 24,
            outputs = listOf(GraphPort("Pout"), GraphPort("Freq")),
        ),
        GraphNode(
            "pa", "功率放大器", "AMP", Icons.Filled.Bolt, ProColors.Orange, x = 24, y = 180,
            inputs = listOf(GraphPort("Pin")), outputs = listOf(GraphPort("Gain")),
        ),
        GraphNode(
            "coup", "定向耦合器", "COUP", Icons.Filled.CallSplit, ProColors.Purple, x = 24, y = 336,
            inputs = listOf(GraphPort("Input")),
            outputs = listOf(GraphPort("Through", highlight = true), GraphPort("Coupled", highlight = true)),
        ),
        GraphNode(
            "ant1", "发射天线", "ANT", Icons.Filled.SettingsInputAntenna, ProColors.Cyan, x = 230, y = 336,
            inputs = listOf(GraphPort("Input")), outputs = listOf(GraphPort("EIRP")),
        ),
        GraphNode(
            "fspl", "自由空间传播", "PATH", Icons.Filled.Waves, ProColors.Red, x = 24, y = 492,
            inputs = listOf(GraphPort("EIRP")), outputs = listOf(GraphPort("Prx")),
        ),
        GraphNode(
            "ant2", "接收天线", "ANT", Icons.Filled.SettingsInputAntenna, ProColors.Cyan, x = 230, y = 492,
            inputs = listOf(GraphPort("Prx")), outputs = listOf(GraphPort("Pout")),
        ),
        GraphNode(
            "rx", "接收机", "RX", Icons.Filled.CenterFocusStrong, ProColors.Primary, x = 130, y = 648,
            inputs = listOf(GraphPort("Pin"), GraphPort("NF")),
        ),
    )

    val graphEdges = listOf(
        GraphEdge("tx", 0, "pa", 0, ProColors.Primary),
        GraphEdge("pa", 0, "coup", 0, ProColors.Orange),
        GraphEdge("coup", 0, "ant1", 0, ProColors.Green),
        GraphEdge("ant1", 0, "fspl", 0, ProColors.Cyan),
        GraphEdge("fspl", 0, "ant2", 0, ProColors.Red),
        GraphEdge("ant2", 0, "rx", 0, ProColors.Cyan),
        GraphEdge("coup", 1, "rx", 1, ProColors.Purple),
    )

    // Flow summary strip
    val flowSummary = listOf(
        Metric("链路增益", "+57.0", "dBm", ProColors.Green),
        Metric("系统余量", "+52.8", "dB", ProColors.Primary),
    )

    val nodeParams = listOf(
        NodeParam("增益 (Gain)", "43.0", "dB", highlight = true),
        NodeParam("噪声系数 (NF)", "2.0", "dB"),
        NodeParam("P1dB", "33.0", "dBm"),
        NodeParam("IP3", "45.0", "dBm"),
        NodeParam("工作频率", "2.4", "GHz"),
        NodeParam("温度", "25", "°C"),
    )

    val resultMetrics = listOf(
        Metric("EIRP", "+57.0", "dBm", ProColors.Primary),
        Metric("接收功率", "-94.2", "dBm", ProColors.Red),
        Metric("系统增益", "+52.8", "dB", ProColors.Green),
    )

    val gainBars = listOf(
        Bar("TX", 25f, ProColors.ChartBlue),
        Bar("AMP", 43f, ProColors.ChartOrange),
        Bar("COUP", -1.5f, ProColors.ChartGreen),
        Bar("ANT", 15f, ProColors.ChartBlue),
        Bar("PATH", -161.2f, ProColors.ChartRed),
        Bar("ANT", 15f, ProColors.ChartGreen),
        Bar("LNA", 18f, ProColors.ChartPurple),
        Bar("RX", -84.2f, ProColors.ChartPurple),
    )

    val noiseSummary = listOf(
        Metric("噪声系数", "3.1", "dB", ProColors.Primary),
        Metric("灵敏度", "-146", "dBm", ProColors.Cyan),
        Metric("链路余量", "52.8", "dB", ProColors.Green),
    )

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
