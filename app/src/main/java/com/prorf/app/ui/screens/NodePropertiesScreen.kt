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
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.prorf.app.data.NodeParam
import com.prorf.app.data.SampleData
import com.prorf.app.ui.components.IconBadge
import com.prorf.app.ui.components.ProCard
import com.prorf.app.ui.components.ProTopBar
import com.prorf.app.ui.components.SectionHeader
import com.prorf.app.ui.components.SegmentTabs
import com.prorf.app.ui.components.rememberToast
import com.prorf.app.ui.theme.ProColors

@Composable
fun NodePropertiesScreen(nav: NavController, id: String?) {
    val node = SampleData.flowNodes.firstOrNull { it.id == id }
        ?: SampleData.flowNodes.first { it.id == "pa" }
    val toast = rememberToast()
    var tab by remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize().background(ProColors.Background)) {
        ProTopBar(
            title = node.name,
            subtitle = "节点参数配置",
            onBack = { nav.popBackStack() },
            actions = listOf(Icons.Filled.MoreHoriz to { toast("更多操作") }),
        )
        Spacer(Modifier.height(8.dp))
        SegmentTabs(
            tabs = listOf("参数", "输入输出", "规格", "图表"),
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
                        IconBadge(node.icon, node.accent, size = 48)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(node.name, style = MaterialTheme.typography.titleMedium, color = ProColors.TextPrimary)
                            Text(node.typeLabel, style = MaterialTheme.typography.bodySmall, color = ProColors.TextSecondary)
                        }
                        Box(
                            Modifier.background(node.accent.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(node.value, style = MaterialTheme.typography.labelMedium, color = node.accent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            when (tab) {
                2 -> item { SpecCard() }
                3 -> item { ChartHint() }
                else -> {
                    item {
                        ProCard {
                            Column {
                                SectionHeader("基本参数")
                                Spacer(Modifier.height(8.dp))
                                SampleData.nodeParams.take(4).forEach { ParamRow(it) }
                            }
                        }
                    }
                    item {
                        ProCard {
                            Column {
                                SectionHeader("高级参数")
                                Spacer(Modifier.height(8.dp))
                                SampleData.nodeParams.drop(4).forEach { ParamRow(it) }
                            }
                        }
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().background(ProColors.Surface).padding(16.dp)) {
            Button(
                onClick = { toast("正在运行节点：${node.name}") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ProColors.Primary),
            ) {
                Icon(Icons.Filled.PlayArrow, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("运行节点", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

@Composable
private fun ParamRow(p: NodeParam) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(p.label, style = MaterialTheme.typography.bodyMedium, color = ProColors.TextSecondary)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                p.value,
                style = MaterialTheme.typography.titleSmall,
                color = if (p.highlight) ProColors.Orange else ProColors.TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.width(4.dp))
            Text(p.unit, style = MaterialTheme.typography.labelSmall, color = ProColors.TextTertiary, modifier = Modifier.padding(bottom = 2.dp))
        }
    }
}

@Composable
private fun SpecCard() {
    ProCard {
        Column {
            SectionHeader("器件规格")
            Spacer(Modifier.height(8.dp))
            listOf(
                "封装" to "QFN-24",
                "供电电压" to "5.0 V",
                "静态电流" to "120 mA",
                "工作温度" to "-40 ~ +85 °C",
                "厂商" to "ProRF Labs",
            ).forEach { (k, v) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(k, style = MaterialTheme.typography.bodyMedium, color = ProColors.TextSecondary)
                    Text(v, style = MaterialTheme.typography.bodyMedium, color = ProColors.TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun ChartHint() {
    ProCard {
        Column(Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("增益 vs 频率响应曲线", style = MaterialTheme.typography.titleSmall, color = ProColors.TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text("图表数据将在节点运行后生成", style = MaterialTheme.typography.bodySmall, color = ProColors.TextSecondary)
        }
    }
}
