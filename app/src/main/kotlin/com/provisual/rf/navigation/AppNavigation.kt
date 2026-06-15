package com.provisual.rf.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object DesignOverview : Screen("design_overview")
    object FlowView : Screen("flow_view")
    object CanvasView : Screen("canvas_view")
    object ComponentDetail : Screen("component_detail/{componentId}") {
        fun createRoute(componentId: String) = "component_detail/$componentId"
    }
    object RunSettings : Screen("run_settings")
    object Results : Screen("results")
    object Issues : Screen("issues")
    object Library : Screen("library")
}

enum class BottomNavItem(
    val route: String,
    val label: String,
    val iconName: String
) {
    DESIGN("home", "设计", "design"),
    RUN("run_settings", "运行", "play"),
    RESULTS("results", "结果", "chart"),
    LIBRARY("library", "库", "library"),
    MORE("more", "更多", "more")
}
