package com.provisual.rf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.provisual.rf.ui.components.ProBottomNav
import com.provisual.rf.ui.screens.*
import com.provisual.rf.ui.theme.ProVisualTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProVisualTheme {
                ProRFApp()
            }
        }
    }
}

@Composable
fun ProRFApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { ProBottomNav(navController) },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    onProjectClick = { navController.navigate("design_overview") }
                )
            }
            composable("design_overview") {
                DesignOverviewScreen(
                    onBack = { navController.popBackStack() },
                    onFlowView = { navController.navigate("flow_view") },
                    onCanvasView = { navController.navigate("canvas_view") },
                    onRunSettings = { navController.navigate("run_settings") },
                    onIssues = { navController.navigate("issues") }
                )
            }
            composable("flow_view") {
                FlowViewScreen(
                    onBack = { navController.popBackStack() },
                    onComponentClick = { id -> navController.navigate("component_detail/$id") }
                )
            }
            composable("canvas_view") {
                CanvasViewScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable("component_detail/{componentId}") { backStackEntry ->
                val componentId = backStackEntry.arguments?.getString("componentId") ?: "1"
                ComponentDetailScreen(
                    componentId = componentId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("run_settings") {
                RunSettingsScreen(
                    onRunComplete = { navController.navigate("results") }
                )
            }
            composable("results") {
                ResultsScreen()
            }
            composable("issues") {
                IssuesScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable("library") {
                LibraryScreen()
            }
            composable("more") {
                MoreScreen()
            }
        }
    }
}
