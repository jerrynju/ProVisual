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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Timeline
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prorf.app.data.Metric
import com.prorf.app.data.SampleData
import com.prorf.app.ui.components.BarChart
import com.prorf.app.ui.components.ProCard
import com.prorf.app.ui.components.ProTopBar
import com.prorf.app.ui.components.SectionHeader
import com.prorf.app.ui.components.SegmentTabs
import com.prorf.app.ui.components.rememberToast
import com.prorf.app.ui.theme.ProColors

@Composable
fun ResultsOverviewScreen(nav: NavController, id: String?) {
    val wf = SampleData.workflow(id)
    val toast = rememberToast()
    var tab by remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize().background(ProColors.Background)) {
        ProTopBar(
            title = "结果总览",
            subtitle = wf.title,
            onBack = { nav.popBackStack() },
            actions = listOf(Icons.Filled.IosShare to { toast("导出结果报告") }),
        )
        Spacer(Modifier.height(8.dp))
        SegmentTabs(
            tabs = listOf("数据概要", "图表", "表格"),
            selected = tab,
            onSelected = { tab = it },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SampleData.resultMetrics.forEach { m ->
                        MetricCard(m, Modifier.weight(1f))
                    }
                }
            }
            item {
                ProCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.background(ProColors.PrimarySoft, RoundedCornerShape(10.dp)).padding(8.dp),
                        ) {
                            Icon(Icons.Filled.Timeline, null, tint = ProColors.Primary)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("自由空间链路损耗", style = MaterialTheme.typography.bodyMedium, color = ProColors.TextSecondary)
                            Text("FSPL @ 2.4 GHz · 10 km", style = MaterialTheme.typography.labelSmall, color = ProColors.TextTertiary)
                        }
                        Text("-151.2 dB", style = MaterialTheme.typography.titleMedium, color = ProColors.Red, fontWeight = FontWeight.Bold)
                    }
                }
            }
            item {
                ProCard {
                    Column {
                        SectionHeader("链路增益分布 (dB)", action = "详情") { nav.navigate(com.prorf.app.ui.nav.Routes.ANALYSIS) }
                        Spacer(Modifier.height(16.dp))
                        BarChart(bars = SampleData.gainBars)
                    }
                }
            }
            item {
                ProCard {
                    Column {
                        SectionHeader("噪声系数总览")
                        Spacer(Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SampleData.noiseSummary.forEach { m -> SmallMetric(m, Modifier.weight(1f)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(m: Metric, modifier: Modifier) {
    Box(
        modifier
            .background(m.accent.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(m.value, style = MaterialTheme.typography.headlineSmall, color = m.accent, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text("${m.label} · ${m.unit}", style = MaterialTheme.typography.labelSmall, color = ProColors.TextSecondary)
        }
    }
}

@Composable
private fun SmallMetric(m: Metric, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(m.value, style = MaterialTheme.typography.titleLarge, color = m.accent)
            Spacer(Modifier.width(2.dp))
            Text(m.unit, style = MaterialTheme.typography.labelSmall, color = ProColors.TextTertiary, modifier = Modifier.padding(bottom = 3.dp))
        }
        Text(m.label, style = MaterialTheme.typography.labelSmall, color = ProColors.TextSecondary)
    }
}
