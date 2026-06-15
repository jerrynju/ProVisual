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

/** A single node in a workflow's flow / topology list. */
data class FlowNode(
    val id: String,
    val index: Int,
    val name: String,
    val typeLabel: String,
    val value: String,
    val valuePositive: Boolean,
    val icon: ImageVector,
    val accent: Color,
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
