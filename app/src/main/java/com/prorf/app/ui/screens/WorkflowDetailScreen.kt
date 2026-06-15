package com.prorf.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prorf.app.data.SampleData
import com.prorf.app.ui.components.IconBadge
import com.prorf.app.ui.components.ProCard
import com.prorf.app.ui.components.ProTopBar
import com.prorf.app.ui.components.SectionHeader
import com.prorf.app.ui.components.StatusPill
import com.prorf.app.ui.components.rememberToast
import com.prorf.app.ui.nav.Routes
import com.prorf.app.ui.theme.ProColors

@Composable
fun WorkflowDetailScreen(nav: NavController, id: String?) {
    val wf = SampleData.workflow(id)
    val toast = rememberToast()

    Column(Modifier.fillMaxSize().background(ProColors.Background)) {
        ProTopBar(
            title = "工作流详情",
            onBack = { nav.popBackStack() },
            actions = listOf(Icons.Filled.MoreHoriz to { toast("更多操作") }),
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                ProCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconBadge(wf.icon, wf.accent, size = 48)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(wf.title, style = MaterialTheme.typography.titleMedium, color = ProColors.TextPrimary)
                                Spacer(Modifier.height(2.dp))
                                Text(wf.subtitle, style = MaterialTheme.typography.bodySmall, color = ProColors.TextSecondary)
                            }
                            StatusPill(wf.status.label, wf.status.color, wf.status.soft)
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatTile("节点数", wf.nodeCount.toString(), ProColors.Primary, Modifier.weight(1f))
                            StatTile("链接数", wf.linkCount.toString(), ProColors.Green, Modifier.weight(1f))
                            StatTile("阶段数", wf.stageCount.toString(), ProColors.Orange, Modifier.weight(1f))
                        }
                    }
                }
            }
            item {
                ProCard {
                    Column {
                        SectionHeader("描述")
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "该工作流用于评估 ${wf.title} 的端到端链路预算，覆盖发射、传播与接收三个阶段，" +
                                "包含功率放大、天线增益与自由空间损耗等关键环节。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ProColors.TextSecondary,
                        )
                    }
                }
            }
            item {
                ProCard {
                    Column {
                        SectionHeader("标签")
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            wf.tags.forEach { TagChip(it) }
                        }
                        Spacer(Modifier.height(14.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(ProColors.Divider))
                        Spacer(Modifier.height(12.dp))
                        InfoRow("创建时间", "2024-05-12 08:00")
                        Spacer(Modifier.height(8.dp))
                        InfoRow("最近更新", wf.updated)
                    }
                }
            }
        }
        // Bottom action bar
        Row(
            Modifier
                .fillMaxWidth()
                .background(ProColors.Surface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = { nav.navigate(Routes.results(wf.id)) },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SolidColor(ProColors.Border)),
            ) {
                Icon(Icons.Filled.BarChart, null, tint = ProColors.Primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("查看结果", color = ProColors.TextPrimary)
            }
            Button(
                onClick = { nav.navigate(Routes.editor(wf.id)) },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ProColors.Primary),
            ) {
                Icon(Icons.Filled.AccountTree, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("打开编辑器")
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, accent: Color, modifier: Modifier) {
    Box(
        modifier
            .background(accent.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineSmall, color = accent)
            Spacer(Modifier.height(2.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = ProColors.TextSecondary)
        }
    }
}

@Composable
private fun TagChip(text: String) {
    Box(
        Modifier
            .background(ProColors.PrimarySoft, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = ProColors.Primary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = ProColors.TextSecondary)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = ProColors.TextPrimary)
    }
}
