package com.prorf.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Waves
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.prorf.app.domain.rf.RfDomain
import com.prorf.app.domain.rf.RfLinkBudget
import com.prorf.app.domain.rf.RfStage
import com.prorf.app.ui.theme.ProColors

/**
 * UI presenter: maps the (pure) RF domain result into the screens' view models,
 * decorating it with icons and colours. The domain stays free of UI concerns;
 * the UI never recomputes RF values — it only renders [RfLinkBudget.compute].
 */
object RfPresenter {

    val result = RfLinkBudget.compute()

    fun stage(id: String?): RfStage = result.stages.firstOrNull { it.id == id } ?: result.stages.first()

    fun accent(code: String): Color = when (code) {
        "TX" -> ProColors.Primary
        "AMP" -> ProColors.Orange
        "COUP" -> ProColors.Purple
        "ANT" -> ProColors.Cyan
        "PATH" -> ProColors.Red
        "LNA" -> ProColors.Green
        "RX" -> ProColors.Primary
        else -> ProColors.Primary
    }

    fun icon(code: String): ImageVector = when (code) {
        "TX" -> Icons.Filled.Podcasts
        "AMP" -> Icons.Filled.Bolt
        "COUP" -> Icons.Filled.CallSplit
        "ANT" -> Icons.Filled.SettingsInputAntenna
        "PATH" -> Icons.Filled.Waves
        "LNA" -> Icons.Filled.GraphicEq
        "RX" -> Icons.Filled.CenterFocusStrong
        else -> Icons.Filled.Podcasts
    }

    fun typeLabel(typeId: String): String = when (typeId) {
        "rf.source" -> "信号源"
        "rf.amplifier" -> "有源器件"
        "rf.coupler" -> "无源器件"
        "rf.antenna" -> "天线"
        "rf.fspl" -> "信道"
        "rf.receiver" -> "信号端"
        else -> "节点"
    }

    val flowNodes: List<FlowNode> = result.stages.mapIndexed { i, s ->
        FlowNode(
            id = s.id, index = i + 1, name = s.name, typeLabel = typeLabel(s.typeId),
            code = s.code, chipExtra = null,
            value = s.headline.display(), valuePositive = s.headline.value >= 0,
            icon = icon(s.code), accent = accent(s.code),
            params = s.params.map { ParamKV(it.label.substringBefore(" ("), it.quantity.display()) },
            branch = s.branch.map { ParamKV(it.label, it.quantity.display(), positive = false) },
        )
    }

    val graphNodes: List<GraphNode> = result.stages.map { s ->
        val def = RfDomain.definitions.first { d -> d.typeId == s.typeId }
        GraphNode(
            id = s.id, title = s.name, code = s.code, icon = icon(s.code), accent = accent(s.code),
            x = s.x, y = s.y,
            inputs = def.inputs.map { GraphPort(prettyPort(it.id)) },
            outputs = def.outputs.map { GraphPort(prettyPort(it.id), highlight = s.typeId == "rf.coupler") },
        )
    }

    val graphEdges: List<GraphEdge> = RfLinkBudget.graph().connections.map { c ->
        val fromCode = result.stages.first { it.id == c.fromNode }.code
        GraphEdge(c.fromNode, portIndex(c.fromNode, c.fromPort), c.toNode, portIndex(c.toNode, c.toPort), accent(fromCode))
    }

    val flowSummary: List<Metric> = listOf(
        Metric("EIRP", result.eirp.formattedValue(), "dBm", ProColors.Green),
        Metric("链路余量", result.margin.formattedValue(), "dB", ProColors.Primary),
    )

    val resultMetrics: List<Metric> = listOf(
        Metric("EIRP", result.eirp.formattedValue(), "dBm", ProColors.Primary),
        Metric("接收功率", result.receivedPower.formattedValue(), "dBm", ProColors.Red),
        Metric("系统增益", result.systemGain.formattedValue(), "dB", ProColors.Green),
    )

    val gainBars: List<Bar> = result.gainBars.map { (code, v) ->
        Bar(code, v.toFloat(), accent(code))
    }

    val noiseSummary: List<Metric> = listOf(
        Metric("噪声系数", result.noiseFigure.formattedValue(), "dB", ProColors.Primary),
        Metric("灵敏度", result.sensitivity.formattedValue(0), "dBm", ProColors.Cyan),
        Metric("链路余量", result.margin.formattedValue(), "dB", ProColors.Green),
    )

    val pathLoss: Metric = run {
        val stage = result.stages.first { it.typeId == "rf.fspl" }
        Metric("自由空间链路损耗", stage.headline.formattedValue(), "dB", ProColors.Red)
    }

    private fun prettyPort(id: String): String = id.replaceFirstChar { it.uppercase() }

    private fun portIndex(nodeId: String, portId: String): Int {
        val s = result.stages.first { it.id == nodeId }
        val def = RfDomain.definitions.first { it.typeId == s.typeId }
        // index within its own (input or output) list
        return def.inputs.indexOfFirst { it.id == portId }
            .takeIf { it >= 0 } ?: def.outputs.indexOfFirst { it.id == portId }
    }
}
