package com.prorf.app.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prorf.app.data.SampleData
import com.prorf.app.data.WorkflowStatus
import com.prorf.app.ui.components.IconBadge
import com.prorf.app.ui.components.ProCard
import com.prorf.app.ui.components.SectionHeader
import com.prorf.app.ui.components.StatusPill
import com.prorf.app.ui.nav.Routes
import com.prorf.app.ui.nav.TopDest
import com.prorf.app.ui.theme.ProColors

@Composable
fun HomeDashboardScreen(nav: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(ProColors.Background),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { ProfileHeader() }
        item {
            Row(
                Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SampleData.dashboardStats.forEach { m ->
                    ProCard(modifier = Modifier.weight(1f), padding = PaddingValues(14.dp)) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(m.value, style = MaterialTheme.typography.headlineSmall, color = m.accent)
                                Spacer(Modifier.width(2.dp))
                                Text(m.unit, style = MaterialTheme.typography.bodySmall, color = ProColors.TextTertiary, modifier = Modifier.padding(bottom = 3.dp))
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(m.label, style = MaterialTheme.typography.bodySmall, color = ProColors.TextSecondary)
                        }
                    }
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 16.dp)) {
                SectionHeader("快速操作")
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickAction("新建工作流", Icons.Filled.Add, ProColors.Primary, Modifier.weight(1f)) {
                        nav.navigate(TopDest.Workflows.route)
                    }
                    QuickAction("导入模板", Icons.Filled.Download, ProColors.Green, Modifier.weight(1f)) {
                        nav.navigate(Routes.TEMPLATES)
                    }
                    QuickAction("分析图表", Icons.Filled.BarChart, ProColors.Orange, Modifier.weight(1f)) {
                        nav.navigate(Routes.ANALYSIS)
                    }
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 16.dp)) {
                SectionHeader("最近工作流", action = "查看全部") { nav.navigate(TopDest.Workflows.route) }
                Spacer(Modifier.height(12.dp))
            }
        }
        items(SampleData.workflows.take(3)) { wf ->
            Box(Modifier.padding(horizontal = 16.dp)) {
                ProCard(onClick = { nav.navigate(Routes.workflowDetail(wf.id)) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBadge(wf.icon, wf.accent)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(wf.title, style = MaterialTheme.typography.titleSmall, color = ProColors.TextPrimary)
                            Spacer(Modifier.height(2.dp))
                            Text(wf.subtitle, style = MaterialTheme.typography.bodySmall, color = ProColors.TextSecondary)
                        }
                        StatusPill(wf.status.label, wf.status.color, wf.status.soft)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(ProColors.Primary, ProColors.PrimaryDark))
            )
            .padding(20.dp)
            .padding(top = 12.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(48.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("A", style = MaterialTheme.typography.titleLarge, color = Color.White)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(SampleData.userName, style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Text(SampleData.userPlan, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                }
                Box(
                    Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Notifications, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Bolt, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "今日已完成 3 次链路计算，平均耗时 0.8s",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun QuickAction(label: String, icon: ImageVector, accent: Color, modifier: Modifier, onClick: () -> Unit) {
    ProCard(modifier = modifier, onClick = onClick, padding = PaddingValues(vertical = 16.dp)) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconBadge(icon, accent, size = 44)
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = ProColors.TextPrimary)
        }
    }
}
