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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prorf.app.data.RfPresenter
import com.prorf.app.domain.rf.Diagnostic
import com.prorf.app.domain.rf.LabeledQuantity
import com.prorf.app.ui.components.IconBadge
import com.prorf.app.ui.components.ProCard
import com.prorf.app.ui.components.ProTopBar
import com.prorf.app.ui.components.SectionHeader
import com.prorf.app.ui.components.SegmentTabs
import com.prorf.app.ui.components.rememberToast
import com.prorf.app.ui.theme.ProColors

/**
 * Inspector (§9): inputs / parameters / outputs / diagnostics / charts.
 * All values come from the computed [RfPresenter] — the UI performs no RF maths.
 */
@Composable
fun NodePropertiesScreen(nav: NavController, id: String?) {
    val stage = RfPresenter.stage(id)
    val accent = RfPresenter.accent(stage.code)
    val toast = rememberToast()
    var tab by remember { mutableIntStateOf(1) } // default to 参数

    Column(Modifier.fillMaxSize().background(ProColors.Background)) {
        ProTopBar(
            title = stage.name,
            subtitle = "节点属性 · ${RfPresenter.typeLabel(stage.typeId)}",
            onBack = { nav.popBackStack() },
            actions = listOf(Icons.Filled.MoreHoriz to { toast("更多操作") }),
        )
        Spacer(Modifier.height(8.dp))
        SegmentTabs(
            tabs = listOf("输入", "参数", "输出", "诊断", "图表"),
            selected = tab,
            onSelected = { tab = it },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                ProCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBadge(RfPresenter.icon(stage.code), accent, size = 48)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(stage.name, style = MaterialTheme.typography.titleMedium, color = ProColors.TextPrimary)
                            Text("类型 ${stage.typeId}", style = MaterialTheme.typography.bodySmall, color = ProColors.TextSecondary)
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = accent.copy(alpha = 0.12f)) {
                            Text(
                                stage.headline.display(),
                                style = MaterialTheme.typography.labelMedium,
                                color = accent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            )
                        }
                    }
                }
            }
            when (tab) {
                0 -> item { QuantityCard("输入端口", stage.inputs, accent, emptyHint = "该节点为信号源，无输入") }
                2 -> item { QuantityCard("输出端口", stage.outputs, accent) }
                3 -> item { DiagnosticsCard(stage.diagnostics) }
                4 -> item { ChartCard() }
                else -> {
                    item {
                        ProCard {
                            Column {
                                SectionHeader("参数")
                                Spacer(Modifier.height(8.dp))
                                stage.params.forEach { ParamField(it, accent) }
                            }
                        }
                    }
                    item { DiagnosticsInline(stage.diagnostics) }
                }
            }
        }
        Box(Modifier.fillMaxWidth().background(ProColors.Surface).padding(16.dp)) {
            Button(
                onClick = { toast("正在运行节点：${stage.name}") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ProColors.Primary),
            ) {
                Icon(Icons.Filled.PlayArrow, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("运行节点", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

/** A Material-style read-only parameter field: label + boxed value + unit. */
@Composable
private fun ParamField(p: LabeledQuantity, accent: Color) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(p.label, style = MaterialTheme.typography.bodyMedium, color = ProColors.TextSecondary, modifier = Modifier.weight(1f))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = ProColors.SurfaceAlt,
            border = androidx.compose.foundation.BorderStroke(1.dp, ProColors.Border),
        ) {
            Text(
                p.quantity.formattedValue(),
                style = MaterialTheme.typography.titleSmall,
                color = ProColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(p.quantity.unit.symbol, style = MaterialTheme.typography.labelSmall, color = ProColors.TextTertiary, modifier = Modifier.width(32.dp))
    }
}

@Composable
private fun QuantityCard(title: String, items: List<LabeledQuantity>, accent: Color, emptyHint: String? = null) {
    ProCard {
        Column {
            SectionHeader(title)
            Spacer(Modifier.height(8.dp))
            if (items.isEmpty()) {
                Text(emptyHint ?: "暂无数据", style = MaterialTheme.typography.bodyMedium, color = ProColors.TextTertiary)
            } else {
                items.forEach { q ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(accent))
                            Spacer(Modifier.width(10.dp))
                            Text(q.label, style = MaterialTheme.typography.bodyMedium, color = ProColors.TextSecondary)
                        }
                        Text(q.quantity.display(), style = MaterialTheme.typography.titleSmall, color = ProColors.TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagnosticsCard(diagnostics: List<Diagnostic>) {
    ProCard {
        Column {
            SectionHeader("诊断")
            Spacer(Modifier.height(8.dp))
            diagnostics.forEach { DiagnosticRow(it) }
        }
    }
}

@Composable
private fun DiagnosticsInline(diagnostics: List<Diagnostic>) {
    ProCard { Column { diagnostics.forEach { DiagnosticRow(it) } } }
}

@Composable
private fun DiagnosticRow(d: Diagnostic) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Filled.CheckCircle, null,
            tint = if (d.ok) ProColors.Green else ProColors.Orange,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(d.message, style = MaterialTheme.typography.bodyMedium, color = ProColors.TextSecondary)
    }
}

@Composable
private fun ChartCard() {
    ProCard {
        Column(Modifier.fillMaxWidth().padding(vertical = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.ShowChart, null, tint = ProColors.Primary, modifier = Modifier.size(36.dp))
            Spacer(Modifier.height(10.dp))
            Text("增益 vs 频率响应曲线", style = MaterialTheme.typography.titleSmall, color = ProColors.TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text("图表数据将在节点运行后生成", style = MaterialTheme.typography.bodySmall, color = ProColors.TextSecondary)
        }
    }
}
