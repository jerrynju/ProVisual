package com.provisual.rf.ui.theme

import androidx.compose.ui.graphics.Color

enum class ComponentStatus(val label: String, val color: Color) {
    UNCONFIGURED("未配置", Disabled),
    READY("就绪", Success),
    RUNNING("运行中", Primary),
    COMPLETED("已完成", Success),
    NEEDS_UPDATE("需更新", NeedUpdate),
    WARNING("警告", Warning),
    ERROR("错误", Error),
    DISABLED("已禁用", Disabled)
}

enum class IssueSeverity(val label: String, val color: Color, val bgColor: Color) {
    ERROR("错误", Error, ErrorContainer),
    WARNING("警告", Warning, WarningContainer),
    INFO("信息", Info, PrimaryContainer)
}

data class RfComponent(
    val id: String,
    val name: String,
    val role: String,
    val gain: String,
    val noise: String,
    val output: String,
    val status: ComponentStatus,
    val categoryColor: Color = CategoryBlue
)

data class IssueItem(
    val severity: IssueSeverity,
    val title: String,
    val affectedObject: String,
    val explanation: String,
    val suggestedAction: String
)

data class Project(
    val id: String,
    val name: String,
    val description: String,
    val status: ComponentStatus,
    val lastRun: String,
    val issueCount: Int
)

val sampleProjects = listOf(
    Project("1", "5G NR 上行链路", "28 GHz · 毫米波接收链路", ComponentStatus.WARNING, "2分钟前", 2),
    Project("2", "卫星下行链路", "Ka波段 · 地面站接收", ComponentStatus.COMPLETED, "昨天", 0),
    Project("3", "雷达发射链路", "X波段 · 脉冲发射系统", ComponentStatus.NEEDS_UPDATE, "3天前", 1),
    Project("4", "WiFi 6E 室内", "6 GHz · 室内覆盖分析", ComponentStatus.READY, "1周前", 0),
)

val sampleComponents = listOf(
    RfComponent("1", "信号源", "发射机", "+23 dBm", "—", "+23 dBm", ComponentStatus.COMPLETED, CategoryBlue),
    RfComponent("2", "带通滤波器", "滤波", "-1.5 dB", "—", "+21.5 dBm", ComponentStatus.COMPLETED, CategoryGreen),
    RfComponent("3", "功率放大器", "增益", "+18 dB", "3.2 dB", "+39.5 dBm", ComponentStatus.WARNING, CategoryOrange),
    RfComponent("4", "天线", "辐射", "+12 dBi", "—", "+51.5 dBm", ComponentStatus.COMPLETED, CategoryBlue),
    RfComponent("5", "自由空间损耗", "传播损耗", "-142 dB", "—", "-90.5 dBm", ComponentStatus.COMPLETED, CategoryRed),
    RfComponent("6", "接收天线", "接收", "+8 dBi", "—", "-82.5 dBm", ComponentStatus.COMPLETED, CategoryBlue),
    RfComponent("7", "低噪声放大器", "增益", "+20 dB", "1.5 dB", "-62.5 dBm", ComponentStatus.COMPLETED, CategoryGreen),
    RfComponent("8", "解调器", "接收机", "—", "6 dB", "-62.5 dBm", ComponentStatus.NEEDS_UPDATE, CategoryPurple),
)

val sampleIssues = listOf(
    IssueItem(
        IssueSeverity.WARNING,
        "输出电平接近限制",
        "功率放大器",
        "功率放大器当前输出 +39.5 dBm，接近 1 dB 压缩点 +40 dBm，可用余量不足 1 dB，存在非线性风险。",
        "降低上游信号源功率，或在功率放大器前添加可变衰减器。"
    ),
    IssueItem(
        IssueSeverity.ERROR,
        "缺少必要参数",
        "解调器",
        "解调器未配置最小可检测信号 (MDS) 参数，无法完成链路余量计算。",
        "打开解调器详情页，在参数标签页填写 MDS 值。"
    ),
    IssueItem(
        IssueSeverity.INFO,
        "链路余量偏低",
        "整体链路",
        "当前设计的链路余量为 3.2 dB，低于推荐的 6 dB，在衰落条件下可能影响通信质量。",
        "检查各级增益分配，或增加接收端天线增益。"
    ),
)
