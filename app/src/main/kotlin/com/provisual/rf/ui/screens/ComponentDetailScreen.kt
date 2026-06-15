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
fun ComponentDetailScreen(componentId: String, onBack: () -> Unit) {
    val component = sampleComponents.find { it.id == componentId } ?: sampleComponents[2]
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("摘要", "参数", "输入", "输出", "图表", "问题", "高级")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        ProAppBar(
            title = component.name,
            onBack = onBack,
            actions = {
                StatusChip(status = component.status)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, contentDescription = "更多", tint = TextSecondary)
                }
            }
        )

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Surface,
            contentColor = Primary,
            edgePadding = 16.dp,
            divider = { HorizontalDivider(color = Border, thickness = 0.5.dp) }
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = tab,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTab == index) Primary else TextSecondary
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                0 -> item { SummaryTab(component) }
                1 -> item { ParametersTab(component) }
                2 -> item { InputOutputTab(isInput = true) }
                3 -> item { InputOutputTab(isInput = false) }
                4 -> item { ChartsTab() }
                5 -> item { IssuesTab() }
                6 -> item { AdvancedTab() }
            }
        }
    }
}

@Composable
private fun SummaryTab(component: RfComponent) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = component.categoryColor.copy(alpha = 0.06f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).background(component.categoryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(componentIcon(component.role), contentDescription = null, tint = component.categoryColor, modifier = Modifier.size(26.dp))
                }
                Column {
                    Text(component.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(component.role, fontSize = 13.sp, color = TextSecondary)
                }
            }
        }

        if (component.status == ComponentStatus.WARNING) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = WarningContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Warning, modifier = Modifier.size(18.dp))
                    Column {
                        Text("输出电平接近限制", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Warning)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("当前输出 +39.5 dBm 接近 1 dB 压缩点。建议降低上游信号功率。", fontSize = 12.sp, color = TextSecondary, lineHeight = 18.sp)
                    }
                }
            }
        }

        SectionHeader("关键指标")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(label = "增益", value = component.gain, modifier = Modifier.weight(1f))
            MetricCard(label = "输出功率", value = component.output, valueColor = if (component.status == ComponentStatus.WARNING) Warning else TextPrimary, modifier = Modifier.weight(1f))
        }
        if (component.noise != "—") {
            MetricCard(label = "噪声系数", value = component.noise, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ParametersTab(component: RfComponent) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("基本参数")
        ParameterField(label = "增益", value = "18", unit = "dB", hint = "推荐范围：10 ~ 25 dB")
        ParameterField(label = "输入功率", value = "21.5", unit = "dBm", hint = "由上游组件提供")
        ParameterField(label = "1 dB 压缩点", value = "40", unit = "dBm", hint = "线性工作上限")
        ParameterField(label = "噪声系数", value = "3.2", unit = "dB", hint = "推荐值 < 5 dB")
        ParameterField(label = "工作频率", value = "28", unit = "GHz", hint = "中心频率")

        SectionHeader("高级参数")
        ParameterField(label = "IP3 点", value = "52", unit = "dBm", hint = "三阶截断点")
        ParameterField(label = "直流功耗", value = "2.5", unit = "W", hint = "可选")
    }
}

@Composable
private fun ParameterField(label: String, value: String, unit: String, hint: String = "", isReadOnly: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = if (isReadOnly) Background else Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isReadOnly) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isReadOnly) TextSecondary else TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(unit, fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            }
        }
        if (hint.isNotEmpty()) {
            Text(hint, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun InputOutputTab(isInput: Boolean) {
    val title = if (isInput) "输入端口" else "输出端口"
    val ports = if (isInput) listOf(
        Triple("RF 输入", "+21.5 dBm", "来自带通滤波器"),
        Triple("偏置电压", "5 V", "DC 供电"),
    ) else listOf(
        Triple("RF 输出", "+39.5 dBm", "至天线"),
        Triple("监测口", "-20 dBm", "定向耦合 -60 dB"),
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(title)
        ports.forEach { (name, value, desc) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(if (isInput) CategoryBlue.copy(0.1f) else CategoryGreen.copy(0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isInput) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (isInput) CategoryBlue else CategoryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        Text(desc, fontSize = 12.sp, color = TextSecondary)
                    }
                    Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun ChartsTab() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("功率特性曲线")
        SimpleBarChart(
            title = "输入 vs 输出功率 (dBm)",
            bars = listOf(
                Pair("输入", 21.5f / 50f),
                Pair("输出", 39.5f / 50f),
                Pair("P1dB", 40f / 50f),
                Pair("IP3", 52f / 60f),
            )
        )
        SectionHeader("噪声贡献")
        SimpleBarChart(
            title = "各组件噪声系数 (dB)",
            bars = listOf(
                Pair("LNA", 0.3f),
                Pair("PA", 0.64f),
                Pair("滤波器", 0.06f),
                Pair("解调器", 1.0f),
            )
        )
    }
}

@Composable
private fun SimpleBarChart(title: String, bars: List<Pair<String, Float>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            bars.forEach { (label, ratio) ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(label, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.width(50.dp))
                    Box(
                        modifier = Modifier.weight(1f).height(24.dp).clip(RoundedCornerShape(4.dp)).background(Background)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxHeight().fillMaxWidth(ratio.coerceIn(0f, 1f))
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (ratio > 0.9f) Error.copy(0.8f)
                                    else if (ratio > 0.75f) Warning.copy(0.8f)
                                    else Primary.copy(0.7f)
                                )
                        )
                    }
                    Text("${(ratio * 100).toInt()}%", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(32.dp))
                }
            }
        }
    }
}

@Composable
private fun IssuesTab() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader("组件问题")
        IssueCard(issue = sampleIssues[0])
    }
}

@Composable
private fun AdvancedTab() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("高级设置", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                AdvancedRow("S 参数模型", "内置多项式")
                AdvancedRow("温度系数", "未配置")
                AdvancedRow("谐波模型", "关闭")
                AdvancedRow("组件 ID", "comp_003_pa")
                AdvancedRow("Schema 版本", "1.2.0")
            }
        }
        OutlinedActionButton(text = "查看原始 Schema", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun AdvancedRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = TextSecondary)
        Text(value, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
    HorizontalDivider(color = Border, thickness = 0.5.dp)
}
