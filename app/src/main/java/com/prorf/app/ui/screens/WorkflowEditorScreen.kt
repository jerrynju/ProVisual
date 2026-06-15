package com.prorf.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prorf.app.data.FlowNode
import com.prorf.app.data.SampleData
import com.prorf.app.ui.components.IconBadge
import com.prorf.app.ui.components.ProCard
import com.prorf.app.ui.components.ProTopBar
import com.prorf.app.ui.components.SegmentTabs
import com.prorf.app.ui.components.rememberToast
import com.prorf.app.ui.nav.Routes
import com.prorf.app.ui.theme.ProColors

@Composable
fun WorkflowEditorScreen(nav: NavController, id: String?) {
    val wf = SampleData.workflow(id)
    val toast = rememberToast()
    var tab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = ProColors.Background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { toast("添加节点") },
                containerColor = ProColors.Primary,
                contentColor = ProColors.Surface,
            ) { Icon(Icons.Filled.Add, null) }
        },
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            ProTopBar(
                title = wf.title,
                subtitle = "图形化编辑 · 拓扑与流程",
                onBack = { nav.popBackStack() },
                actions = listOf(
                    Icons.Filled.PlayArrow to { nav.navigate(Routes.results(wf.id)) },
                    Icons.Filled.MoreHoriz to { toast("更多操作") },
                ),
            )
            Spacer(Modifier.height(8.dp))
            SegmentTabs(
                tabs = listOf("拓扑视图", "流程视图"),
                selected = tab,
                onSelected = { tab = it },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(12.dp))
            if (tab == 0) {
                TopologyView(nav)
            } else {
                FlowView(nav)
            }
        }
    }
}

/** Topology view: a vertical signal-chain schematic with connector lines. */
@Composable
private fun TopologyView(nav: NavController) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
    ) {
        itemsIndexed(SampleData.flowNodes) { index, node ->
            ChainNode(
                node = node,
                isFirst = index == 0,
                isLast = index == SampleData.flowNodes.lastIndex,
                onClick = { nav.navigate(Routes.nodeProps(node.id)) },
            )
        }
    }
}

@Composable
private fun ChainNode(node: FlowNode, isFirst: Boolean, isLast: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // Connector rail
        Column(
            Modifier.width(40.dp).fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.width(2.dp).weight(1f).background(if (isFirst) Color.Transparent else ProColors.Border))
            Box(
                Modifier.size(12.dp).clip(CircleShape).background(node.accent),
            )
            Box(Modifier.width(2.dp).weight(1f).background(if (isLast) Color.Transparent else ProColors.Border))
        }
        Spacer(Modifier.width(8.dp))
        Box(Modifier.padding(vertical = 6.dp)) {
            ProCard(onClick = onClick) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBadge(node.icon, node.accent, size = 40)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(node.name, style = MaterialTheme.typography.titleSmall, color = ProColors.TextPrimary)
                        Text(node.typeLabel, style = MaterialTheme.typography.labelSmall, color = ProColors.TextSecondary)
                    }
                    Text(
                        node.value,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (node.valuePositive) ProColors.Green else ProColors.Red,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

/** Flow view: a numbered list of nodes in the signal chain. */
@Composable
private fun FlowView(nav: NavController) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(SampleData.flowNodes) { node ->
            ProCard(onClick = { nav.navigate(Routes.nodeProps(node.id)) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(26.dp).clip(CircleShape).background(node.accent.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("${node.index}", style = MaterialTheme.typography.labelMedium, color = node.accent, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    IconBadge(node.icon, node.accent, size = 38)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(node.name, style = MaterialTheme.typography.titleSmall, color = ProColors.TextPrimary)
                        Text(node.typeLabel, style = MaterialTheme.typography.labelSmall, color = ProColors.TextSecondary)
                    }
                    Text(
                        node.value,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (node.valuePositive) ProColors.Green else ProColors.Red,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
