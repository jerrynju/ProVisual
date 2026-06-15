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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prorf.app.data.SampleData
import com.prorf.app.ui.components.BarChart
import com.prorf.app.ui.components.ProCard
import com.prorf.app.ui.components.ProTopBar
import com.prorf.app.ui.components.SegmentTabs
import com.prorf.app.ui.components.rememberToast
import com.prorf.app.ui.theme.ProColors

@Composable
fun AnalysisChartScreen(nav: NavController) {
    val toast = rememberToast()
    var tab by remember { mutableIntStateOf(0) }
    var labelsOn by remember { mutableStateOf(true) }

    Column(Modifier.fillMaxSize().background(ProColors.Background)) {
        ProTopBar(
            title = "链路增益分布",
            subtitle = "专业级图表分析与可视化",
            onBack = { nav.popBackStack() },
            actions = listOf(Icons.Filled.MoreHoriz to { toast("图表设置") }),
        )
        Spacer(Modifier.height(8.dp))
        SegmentTabs(
            tabs = listOf("图表", "数据", "设置"),
            selected = tab,
            onSelected = { tab = it },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (tab) {
                1 -> items(SampleData.gainBars) { bar ->
                    ProCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(12.dp).clip(CircleShape).background(bar.color))
                            Spacer(Modifier.width(12.dp))
                            Text(bar.label, style = MaterialTheme.typography.titleSmall, color = ProColors.TextPrimary, modifier = Modifier.weight(1f))
                            Text(
                                (if (bar.value > 0) "+" else "") + (if (bar.value % 1f == 0f) bar.value.toInt().toString() else String.format("%.1f", bar.value)) + " dB",
                                style = MaterialTheme.typography.titleSmall,
                                color = bar.color,
                            )
                        }
                    }
                }
                2 -> {
                    item {
                        ProCard {
                            Column {
                                SettingToggle("显示数值标签", labelsOn) { labelsOn = it }
                                SettingToggle("启用网格线", true) {}
                                SettingToggle("平滑动画", true) {}
                            }
                        }
                    }
                }
                else -> {
                    item {
                        ProCard {
                            Column {
                                Text("链路增益分布 (dB)", style = MaterialTheme.typography.titleSmall, color = ProColors.TextPrimary)
                                Spacer(Modifier.height(20.dp))
                                BarChart(bars = SampleData.gainBars, chartHeight = 220)
                            }
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            LegendCard("类型", "增益瀑布图", Modifier.weight(1f))
                            LegendCard("单位", "dB", Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingToggle(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = ProColors.TextPrimary)
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ProColors.Surface,
                checkedTrackColor = ProColors.Green,
            ),
        )
    }
}

@Composable
private fun LegendCard(label: String, value: String, modifier: Modifier) {
    ProCard(modifier = modifier) {
        Column {
            Text(label, style = MaterialTheme.typography.bodySmall, color = ProColors.TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleSmall, color = ProColors.TextPrimary)
        }
    }
}
