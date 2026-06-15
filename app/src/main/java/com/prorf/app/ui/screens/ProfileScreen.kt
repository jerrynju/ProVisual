package com.prorf.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prorf.app.data.SampleData
import com.prorf.app.ui.components.IconBadge
import com.prorf.app.ui.components.ProCard
import com.prorf.app.ui.components.rememberToast
import com.prorf.app.ui.nav.Routes
import com.prorf.app.ui.theme.ProColors

@Composable
fun ProfileScreen(nav: NavController) {
    val toast = rememberToast()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(ProColors.Background),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { Header() }
        item {
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SampleData.dashboardStats.forEach { m ->
                    ProCard(modifier = Modifier.weight(1f), padding = PaddingValues(14.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(m.value, style = MaterialTheme.typography.titleLarge, color = m.accent)
                            Text(m.label, style = MaterialTheme.typography.labelSmall, color = ProColors.TextSecondary)
                        }
                    }
                }
            }
        }
        item {
            Box(Modifier.padding(horizontal = 16.dp)) {
                ProCard(padding = PaddingValues(vertical = 4.dp)) {
                    Column {
                        MenuRow(Icons.Filled.Dashboard, ProColors.Primary, "工作流模板库") { nav.navigate(Routes.TEMPLATES) }
                        Divider()
                        MenuRow(Icons.Filled.BarChart, ProColors.Orange, "分析图表") { nav.navigate(Routes.ANALYSIS) }
                        Divider()
                        MenuRow(Icons.Filled.Bookmark, ProColors.Green, "我的收藏") { toast("我的收藏") }
                        Divider()
                        MenuRow(Icons.Filled.ViewModule, ProColors.Purple, "节点组件管理") { toast("节点组件管理") }
                    }
                }
            }
        }
        item {
            Box(Modifier.padding(horizontal = 16.dp)) {
                ProCard(padding = PaddingValues(vertical = 4.dp)) {
                    Column {
                        MenuRow(Icons.Filled.Settings, ProColors.TextSecondary, "设置中心") { nav.navigate(Routes.SETTINGS) }
                        Divider()
                        MenuRow(Icons.Filled.HelpOutline, ProColors.Cyan, "帮助与反馈") { toast("帮助与反馈") }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(ProColors.Primary, ProColors.PrimaryDark)))
            .padding(20.dp)
            .padding(top = 12.dp, bottom = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(56.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Text("A", style = MaterialTheme.typography.headlineSmall, color = Color.White)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(SampleData.userName, style = MaterialTheme.typography.titleLarge, color = Color.White)
                Spacer(Modifier.height(2.dp))
                Text(SampleData.userPlan, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
            }
        }
    }
}

@Composable
private fun MenuRow(icon: ImageVector, accent: Color, label: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconBadge(icon, accent, size = 36, corner = 10)
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = ProColors.TextPrimary, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, tint = ProColors.TextTertiary, modifier = Modifier.size(14.dp))
    }
}

@Composable
private fun Divider() {
    Box(Modifier.fillMaxWidth().padding(start = 60.dp).height(1.dp).background(ProColors.Divider))
}
