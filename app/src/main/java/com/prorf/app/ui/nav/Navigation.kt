package com.prorf.app.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.ui.graphics.vector.ImageVector

/** Top-level routes reachable from the bottom navigation bar. */
enum class TopDest(val route: String, val label: String, val icon: ImageVector) {
    Workflows("workflows", "工作流", Icons.Filled.ViewKanban),
    Nodes("nodes", "库", Icons.Filled.GridView),
    Results("results_tab", "结果", Icons.Filled.BarChart),
    Scenes("scenes", "场景", Icons.Filled.Layers),
    Profile("profile", "我的", Icons.Filled.AccountCircle),
}

/** Secondary (pushed) routes. */
object Routes {
    const val WORKFLOW_DETAIL = "workflow_detail"
    const val EDITOR = "editor"
    const val NODE_PROPS = "node_props"
    const val RESULTS = "results"
    const val TEMPLATES = "templates"
    const val ANALYSIS = "analysis"
    const val SETTINGS = "settings"

    fun workflowDetail(id: String) = "$WORKFLOW_DETAIL/$id"
    fun editor(id: String) = "$EDITOR/$id"
    fun nodeProps(id: String) = "$NODE_PROPS/$id"
    fun results(id: String) = "$RESULTS/$id"
}
