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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prorf.app.data.SampleData
import com.prorf.app.data.Workflow
import com.prorf.app.data.WorkflowStatus
import com.prorf.app.ui.components.FilterChipsRow
import com.prorf.app.ui.components.IconBadge
import com.prorf.app.ui.components.ProCard
import com.prorf.app.ui.components.ProTopBar
import com.prorf.app.ui.components.StatusPill
import com.prorf.app.ui.components.rememberToast
import com.prorf.app.ui.nav.Routes
import com.prorf.app.ui.theme.ProColors

private val tabs = listOf("全部", "进行中", "草稿", "已完成")

@Composable
fun WorkflowListScreen(nav: NavController) {
    var tab by remember { mutableIntStateOf(0) }
    val toast = rememberToast()

    val items = remember(tab) {
        when (tab) {
            1 -> SampleData.workflows.filter { it.status == WorkflowStatus.Running }
            2 -> SampleData.workflows.filter { it.status == WorkflowStatus.Draft }
            3 -> SampleData.workflows.filter { it.status == WorkflowStatus.Done }
            else -> SampleData.workflows
        }
    }

    Scaffold(
        containerColor = ProColors.Background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { toast("新建工作流") },
                containerColor = ProColors.Primary,
                contentColor = ProColors.Surface,
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text("新建工作流") },
            )
        },
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            ProTopBar(
                title = "工作流列表",
                subtitle = "列表视图 · 展示工作流集合",
                actions = listOf(
                    Icons.Filled.Search to { toast("搜索工作流") },
                    Icons.Filled.FilterList to { toast("筛选") },
                ),
            )
            Spacer(Modifier.height(8.dp))
            FilterChipsRow(items = tabs, selected = tab, onSelected = { tab = it })
            Spacer(Modifier.height(12.dp))
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items) { wf -> WorkflowRow(wf) { nav.navigate(Routes.workflowDetail(wf.id)) } }
            }
        }
    }
}

@Composable
private fun WorkflowRow(wf: Workflow, onClick: () -> Unit) {
    ProCard(onClick = onClick) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(wf.icon, wf.accent, size = 44)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(wf.title, style = MaterialTheme.typography.titleSmall, color = ProColors.TextPrimary)
                    Spacer(Modifier.height(2.dp))
                    Text(wf.subtitle, style = MaterialTheme.typography.bodySmall, color = ProColors.TextSecondary)
                }
                StatusPill(wf.status.label, wf.status.color, wf.status.soft)
            }
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(ProColors.Divider))
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                MiniStat("节点", wf.nodeCount.toString())
                Spacer(Modifier.width(20.dp))
                MiniStat("链接", wf.linkCount.toString())
                Spacer(Modifier.weight(1f))
                Text("更新于 ${wf.updated}", style = MaterialTheme.typography.labelSmall, color = ProColors.TextTertiary)
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(value, style = MaterialTheme.typography.titleSmall, color = ProColors.Primary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = ProColors.TextSecondary)
    }
}
