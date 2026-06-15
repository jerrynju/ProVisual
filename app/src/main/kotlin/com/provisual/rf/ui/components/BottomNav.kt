package com.provisual.rf.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.provisual.rf.ui.theme.Border
import com.provisual.rf.ui.theme.Primary
import com.provisual.rf.ui.theme.Surface
import com.provisual.rf.ui.theme.TextSecondary

data class BottomNavEntry(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavEntry("home", "设计", Icons.Filled.Edit, Icons.Outlined.Edit),
    BottomNavEntry("run_settings", "运行", Icons.Filled.PlayArrow, Icons.Outlined.PlayArrow),
    BottomNavEntry("results", "结果", Icons.Filled.BarChart, Icons.Outlined.BarChart),
    BottomNavEntry("library", "库", Icons.Filled.Book, Icons.Outlined.Book),
    BottomNavEntry("more", "更多", Icons.Filled.MoreHoriz, Icons.Filled.MoreHoriz),
)

@Composable
fun ProBottomNav(navController: NavController) {
    val backstackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backstackEntry.value?.destination?.route

    val activeRoute = when {
        currentRoute == "home" || currentRoute == "design_overview" ||
        currentRoute == "flow_view" || currentRoute == "canvas_view" ||
        currentRoute?.startsWith("component_detail") == true -> "home"
        currentRoute == "run_settings" -> "run_settings"
        currentRoute == "results" -> "results"
        currentRoute == "library" -> "library"
        currentRoute == "issues" -> "home"
        else -> "home"
    }

    NavigationBar(
        containerColor = Surface,
        tonalElevation = 0.dp,
        modifier = Modifier.height(64.dp)
    ) {
        bottomNavItems.forEach { item ->
            val selected = activeRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = Primary.copy(alpha = 0.08f)
                )
            )
        }
    }
}
