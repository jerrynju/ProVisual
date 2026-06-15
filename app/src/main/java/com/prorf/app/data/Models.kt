package com.prorf.app.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.prorf.app.ui.theme.ProColors

/** Status of a workflow, drives the colored badge in the list. */
enum class WorkflowStatus(val label: String, val color: Color, val soft: Color) {
    Running("进行中", ProColors.Green, ProColors.GreenSoft),
    Draft("草稿", ProColors.TextSecondary, ProColors.Background),
    Done("已完成", ProColors.Primary, ProColors.PrimarySoft),
}

data class Workflow(
    val id: String,
    val title: String,
    val subtitle: String,
    val status: WorkflowStatus,
    val icon: ImageVector,
    val accent: Color,
    val nodeCount: Int,
    val linkCount: Int,
    val stageCount: Int,
    val updated: String,
    val tags: List<String>,
)

/** A key/value sub-parameter shown inside a flow node card. */
data class ParamKV(
    val label: String,
    val value: String,
    /** null = neutral, true = green, false = red. */
    val positive: Boolean? = null,
)

/** A single node in a workflow's flow / topology list. */
data class FlowNode(
    val id: String,
    val index: Int,
    val name: String,
    val typeLabel: String,
    /** Short code shown on the timeline marker and the type chip, e.g. "TX". */
    val code: String,
    /** Optional extra appended to the chip, e.g. "2dB" / "1km". */
    val chipExtra: String? = null,
    val value: String,
    val valuePositive: Boolean,
    val icon: ImageVector,
    val accent: Color,
    val params: List<ParamKV> = emptyList(),
    /** Branch parameters (e.g. a coupler's tap output). */
    val branch: List<ParamKV> = emptyList(),
)

/** A port on a topology graph node. */
data class GraphPort(
    val name: String,
    val value: String? = null,
    val highlight: Boolean = false,
)

/** A node in the topology graph editor, positioned in dp on the canvas. */
data class GraphNode(
    val id: String,
    val title: String,
    val code: String,
    val icon: ImageVector,
    val accent: Color,
    val x: Int,
    val y: Int,
    val inputs: List<GraphPort> = emptyList(),
    val outputs: List<GraphPort> = emptyList(),
)

/** A directed connection between an output port and an input port. */
data class GraphEdge(
    val from: String,
    val fromPort: Int,
    val to: String,
    val toPort: Int,
    val color: Color,
)

/** A configurable parameter shown on the node-properties form. */
data class NodeParam(
    val label: String,
    val value: String,
    val unit: String,
    val highlight: Boolean = false,
)

/** A node component shown in the node library grid. */
data class NodeComponent(
    val name: String,
    val category: String,
    val icon: ImageVector,
    val accent: Color,
)

/** A reusable scene / template entry. */
data class LibraryItem(
    val title: String,
    val subtitle: String,
    val trailing: String,
    val trailingPositive: Boolean,
    val icon: ImageVector,
    val accent: Color,
)

/** A labelled value used by metric cards. */
data class Metric(
    val label: String,
    val value: String,
    val unit: String,
    val accent: Color,
)

/** A single bar in a bar chart. */
data class Bar(
    val label: String,
    val value: Float,
    val color: Color,
)
