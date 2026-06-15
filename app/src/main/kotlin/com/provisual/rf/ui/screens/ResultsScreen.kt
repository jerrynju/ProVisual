package com.provisual.rf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.provisual.rf.ui.components.*
import com.provisual.rf.ui.theme.*

@Composable
fun ResultsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        ProAppBar(
            title = "结果总览",
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Share, contentDescription = "分享", tint = TextSecondary)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Download, contentDescription = "导出", tint = TextSecondary)
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item { OverallStatusCard() }
            item { Spacer(modifier = Modifier.height(2.dp)) }
            item { KeyResultsSection() }
            item { PowerChartSection() }
            item { ScenarioComparisonSection() }
            item { NaturalLanguageSummary() }
            item { ResultsIssuesSection() }
            item { RawDataSection() }
        }
    }
}

@Composable
private fun OverallStatusCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WarningContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Warning, modifier = Modifier.size(28.dp))
                    Column {
                        Text("链路可用，存在风险", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("2 个问题需要关注", fontSize = 13.sp, color = Warning)
                    }
                }
                StatusChip(status = ComponentStatus.WARNING)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("5G NR 上行链路", fontSize = 13.sp, color = TextSecondary)
                Text("刚刚计算完成", fontSize = 13.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun KeyResultsSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader("关键结果")
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("总增益", "+37.5", "dB", valueColor = Success, modifier = Modifier.weight(1f))
            MetricCard("系统噪声", "4.8", "dB", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("接收功率", "-62.5", "dBm", modifier = Modifier.weight(1f))
            MetricCard("链路余量", "3.2", "dB", valueColor = Warning, modifier = Modifier.weight(1f))
            MetricCard("灵敏度", "-65.7", "dBm", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PowerChartSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader("链路功率分布")
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("各级功率电平 (dBm)", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(14.dp))
                val bars = listOf(
                    Triple("信号源", 0.46f, "+23"),
                    Triple("带通滤波", 0.43f, "+21.5"),
                    Triple("功率放大", 0.79f, "+39.5"),
                    Triple("天线发射", 1.0f, "+51.5"),
                    Triple("空间损耗", 0.18f, "-90.5"),
                    Triple("接收天线", 0.35f, "-82.5"),
                    Triple("低噪放", 0.75f, "-62.5"),
                    Triple("解调器", 0.75f, "-62.5"),
                )
                bars.forEach { (label, ratio, value) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(label, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(60.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f).height(20.dp).clip(RoundedCornerShape(4.dp)).background(Background)) {
                            val barColor = when {
                                ratio > 0.95f -> Error.copy(0.7f)
                                ratio > 0.8f -> Warning.copy(0.7f)
                                ratio < 0.3f -> TextSecondary.copy(0.4f)
                                else -> Primary.copy(0.6f)
                            }
                            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(ratio).clip(RoundedCornerShape(4.dp)).background(barColor))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(value, fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Medium, modifier = Modifier.width(48.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ScenarioComparisonSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader("场景对比")
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(0.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("指标", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1.5f))
                    Text("默认", fontSize = 12.sp, color = Primary, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text("高温", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                    Text("低温", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                }
                HorizontalDivider(color = Border, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
                listOf(
                    listOf("总增益", "+37.5 dB", "+36.2 dB", "+38.1 dB"),
                    listOf("接收功率", "-62.5 dBm", "-63.8 dBm", "-61.9 dBm"),
                    listOf("噪声系数", "4.8 dB", "5.6 dB", "4.3 dB"),
                    listOf("链路余量", "3.2 dB", "1.9 dB", "3.8 dB"),
                ).forEach { row ->
                    ScenarioCompareRow(row)
                    HorizontalDivider(color = Border.copy(0.5f), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ScenarioCompareRow(items: List<String>) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(items[0], fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1.5f))
        Text(items[1], fontSize = 12.sp, color = Primary, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Text(items[2], fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1f))
        Text(items[3], fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun NaturalLanguageSummary() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader("智能解读")
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                    Text("AI 链路分析", fontSize = 13.sp, color = Primary, fontWeight = FontWeight.SemiBold)
                }
                Text(
                    text = "当前链路在默认场景下可以正常工作，但余量偏低（3.2 dB）。功率放大器是主要风险点，输出电平已接近 1 dB 压缩点。在高温场景下，余量进一步降至 1.9 dB，可能不足以应对实际衰落。",
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 20.sp
                )
                Text(
                    text = "建议：降低信号源输出功率约 2 dB，或在功率放大器后添加可变衰减器，以获得更稳定的余量裕度。",
                    fontSize = 13.sp,
                    color = Primary,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ResultsIssuesSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader("结果问题", actionLabel = "查看全部") {}
        Spacer(modifier = Modifier.height(10.dp))
        sampleIssues.take(2).forEach { issue ->
            IssueCard(issue = issue)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RawDataSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader("原始数据")
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("查看完整数据表", fontSize = 14.sp, color = Primary)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
