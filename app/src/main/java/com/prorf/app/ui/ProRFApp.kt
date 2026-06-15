package com.prorf.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.prorf.app.ui.nav.Routes
import com.prorf.app.ui.nav.TopDest
import com.prorf.app.ui.screens.AnalysisChartScreen
import com.prorf.app.ui.screens.HomeDashboardScreen
import com.prorf.app.ui.screens.NodeLibraryScreen
import com.prorf.app.ui.screens.NodePropertiesScreen
import com.prorf.app.ui.screens.ProfileScreen
import com.prorf.app.ui.screens.ResultsOverviewScreen
import com.prorf.app.ui.screens.SceneLibraryScreen
import com.prorf.app.ui.screens.SettingsScreen
import com.prorf.app.ui.screens.TemplateLibraryScreen
import com.prorf.app.ui.screens.WorkflowDetailScreen
import com.prorf.app.ui.screens.WorkflowEditorScreen
import com.prorf.app.ui.screens.WorkflowListScreen
import com.prorf.app.ui.theme.ProColors

@Composable
fun ProRFApp() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = TopDest.entries.any { it.route == currentRoute }

    Scaffold(
        containerColor = ProColors.Background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = ProColors.Surface, tonalElevation = 0.dp) {
                    TopDest.entries.forEach { dest ->
                        val selected = currentRoute == dest.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(dest.route) {
                                        popUpTo(TopDest.Workflows.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ProColors.Primary,
                                selectedTextColor = ProColors.Primary,
                                unselectedIconColor = ProColors.TextTertiary,
                                unselectedTextColor = ProColors.TextTertiary,
                                indicatorColor = ProColors.PrimarySoft,
                            ),
                        )
                    }
                }
            }
        },
    ) { inner ->
        NavHost(
            navController = navController,
            startDestination = TopDest.Workflows.route,
            modifier = Modifier.padding(inner),
        ) {
            // Top-level destinations
            composable(TopDest.Workflows.route) { WorkflowListScreen(navController) }
            composable(TopDest.Nodes.route) { NodeLibraryScreen(navController) }
            composable(TopDest.Results.route) { ResultsOverviewScreen(navController, "rf-link", showBack = false) }
            composable(TopDest.Scenes.route) { SceneLibraryScreen(navController) }
            composable(TopDest.Profile.route) { ProfileScreen(navController) }
            composable("home") { HomeDashboardScreen(navController) }

            // Secondary destinations
            composable(
                "${Routes.WORKFLOW_DETAIL}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { WorkflowDetailScreen(navController, it.arguments?.getString("id")) }

            composable(
                "${Routes.EDITOR}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { WorkflowEditorScreen(navController, it.arguments?.getString("id")) }

            composable(
                "${Routes.NODE_PROPS}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { NodePropertiesScreen(navController, it.arguments?.getString("id")) }

            composable(
                "${Routes.RESULTS}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { ResultsOverviewScreen(navController, it.arguments?.getString("id")) }

            composable(Routes.TEMPLATES) { TemplateLibraryScreen(navController) }
            composable(Routes.ANALYSIS) { AnalysisChartScreen(navController) }
            composable(Routes.SETTINGS) { SettingsScreen(navController) }
        }
    }
}
