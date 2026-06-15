package com.prorf.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prorf.app.data.FlowNode
import com.prorf.app.data.ParamKV
import com.prorf.app.data.SampleData
import com.prorf.app.ui.components.ProTopBar
import com.prorf.app.ui.components.SegmentTabs
import com.prorf.app.ui.components.TopologyCanvas
import com.prorf.app.ui.components.TypeChip
import com.prorf.app.ui.components.rememberToast
import com.prorf.app.ui.nav.Routes
import com.prorf.app.ui.theme.ProColors

@Composable
fun WorkflowEditorScreen(nav: NavController, id: String?) {
    val wf = SampleData.workflow(id)
    val toast = rememberToast()
    var tab by remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize().background(ProColors.Background)) {
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
            TopologyTab(nav, toast)
        } else {
            FlowTab(nav)
        }
    }
}

/* ----------------------------- Topology ----------------------------- */

@Composable
private fun TopologyTab(nav: NavController, toast: (String) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        // Editor toolbar
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ToolIcon(Icons.Filled.NearMe, "选择工具", toast)
            ToolIcon(Icons.Filled.Tune, "画布设置", toast)
            Spacer(Modifier.width(4.dp))
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ProColors.Primary)
                    .clickable { nav.navigate(Routes.results("rf-link")) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.PlayArrow, null, tint = ProColors.Surface, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.weight(1f))
            ToolIcon(Icons.Filled.CenterFocusWeak, "适应画布", toast)
            Surface(shape = RoundedCornerShape(8.dp), color = ProColors.SurfaceAlt) {
                Text(
                    "100%",
                    style = MaterialTheme.typography.labelMedium,
                    color = ProColors.TextSecondary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = ProColors.SurfaceAlt,
            border = androidx.compose.foundation.BorderStroke(1.dp, ProColors.Border),
        ) {
            TopologyCanvas(
                nodes = SampleData.graphNodes,
                edges = SampleData.graphEdges,
                onNodeClick = { nav.navigate(Routes.nodeProps(it.id)) },
            )
        }
        // Bottom action chips
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ActionChip("添加节点", Icons.Filled.Add, Modifier.weight(1f)) { toast("添加节点") }
            ActionChip("添加连接", Icons.Filled.Hub, Modifier.weight(1f)) { toast("添加连接") }
            ActionChip("注释", Icons.Filled.AddComment, Modifier.weight(1f)) { toast("添加注释") }
        }
    }
}

@Composable
private fun ToolIcon(icon: ImageVector, desc: String, toast: (String) -> Unit) {
    Box(
        Modifier.size(40.dp).clip(CircleShape).clickable { toast(desc) },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, desc, tint = ProColors.TextSecondary, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun ActionChip(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = ProColors.PrimarySoft,
        onClick = onClick,
    ) {
        Row(
            Modifier.padding(vertical = 11.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, null, tint = ProColors.Primary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = ProColors.Primary)
        }
    }
}

/* ------------------------------- Flow ------------------------------- */

@Composable
private fun FlowTab(nav: NavController) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
    ) {
        item {
            // Summary strip
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ProColors.Surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ProColors.Border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(Modifier.padding(16.dp)) {
                    SampleData.flowSummary.forEachIndexed { i, m ->
                        Column(Modifier.weight(1f)) {
                            Text(m.label, style = MaterialTheme.typography.labelSmall, color = ProColors.TextSecondary)
                            Spacer(Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(m.value, style = MaterialTheme.typography.titleLarge, color = m.accent)
                                Spacer(Modifier.width(3.dp))
                                Text(m.unit, style = MaterialTheme.typography.labelSmall, color = ProColors.TextTertiary, modifier = Modifier.padding(bottom = 3.dp))
                            }
                        }
                        if (i == 0) Box(Modifier.width(1.dp).height(40.dp).background(ProColors.Divider))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        itemsIndexed(SampleData.flowNodes) { index, node ->
            FlowTimelineRow(
                node = node,
                isFirst = index == 0,
                isLast = index == SampleData.flowNodes.lastIndex,
                onClick = { nav.navigate(Routes.nodeProps(node.id)) },
            )
        }
    }
}

@Composable
private fun FlowTimelineRow(node: FlowNode, isFirst: Boolean, isLast: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // Timeline rail: top segment -> marker -> code -> bottom segment
        Column(
            Modifier.width(46.dp).fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.width(3.dp).height(14.dp).background(if (isFirst) Color.Transparent else ProColors.Border))
            Box(
                Modifier.size(30.dp).clip(CircleShape).background(node.accent),
                contentAlignment = Alignment.Center,
            ) {
                Text("${node.index}", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(2.dp))
            Text(node.code, style = MaterialTheme.typography.labelSmall, color = ProColors.TextTertiary, fontWeight = FontWeight.SemiBold)
            Box(Modifier.width(3.dp).weight(1f).background(if (isLast) Color.Transparent else ProColors.Border))
        }
        Spacer(Modifier.width(8.dp))
        // Card
        Box(Modifier.padding(vertical = 6.dp)) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = node.accent.copy(alpha = 0.05f),
                border = androidx.compose.foundation.BorderStroke(1.dp, node.accent.copy(alpha = 0.18f)),
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TypeChip(node.code + (node.chipExtra?.let { " $it" } ?: ""), node.accent)
                        Spacer(Modifier.width(8.dp))
                        Text(node.name, style = MaterialTheme.typography.titleSmall, color = ProColors.TextPrimary, modifier = Modifier.weight(1f))
                        Text(
                            node.value,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (node.valuePositive) ProColors.Green else ProColors.Red,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    ParamRow(node.params)
                    if (node.branch.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(14.dp).clip(RoundedCornerShape(bottomStart = 5.dp))
                                    .background(node.accent.copy(alpha = 0.25f))
                            )
                            Spacer(Modifier.width(8.dp))
                            Surface(shape = RoundedCornerShape(10.dp), color = node.accent.copy(alpha = 0.08f), modifier = Modifier.weight(1f)) {
                                Box(Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) { ParamRow(node.branch) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParamRow(params: List<ParamKV>) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        params.forEach { p ->
            Row {
                Text(p.label, style = MaterialTheme.typography.labelSmall, color = ProColors.TextTertiary)
                Spacer(Modifier.width(4.dp))
                Text(
                    p.value,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = when (p.positive) {
                        true -> ProColors.Green
                        false -> ProColors.Red
                        null -> ProColors.TextSecondary
                    },
                )
            }
        }
    }
}
