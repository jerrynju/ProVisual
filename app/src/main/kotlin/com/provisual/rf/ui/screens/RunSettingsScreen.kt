package com.provisual.rf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.delay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.provisual.rf.ui.components.*
import com.provisual.rf.ui.theme.*

@Composable
fun RunSettingsScreen(onRunComplete: () -> Unit) {
    var selectedScenario by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    val scenarios = listOf("默认参数", "高温场景 (+85°C)", "低温场景 (-40°C)", "最大功率")

    LaunchedEffect(isRunning) {
        if (isRunning) {
            for (i in 1..20) {
                delay(80)
                progress = i / 20f
            }
            isRunning = false
            progress = 0f
            onRunComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        ProAppBar(
            title = "运行设置",
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.History, contentDescription = "历史", tint = TextSecondary)
                }
            }
        )

        if (isRunning) {
            RunningOverlay(progress = progress)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { ValidationStatus() }
                item { ScenarioSection(selectedScenario, scenarios, onSelect = { selectedScenario = it }) }
                item { RunScopeSection() }
                item { RunOptionsSection() }
                item { HistorySection() }
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    PrimaryButton(
                        text = "开始运行",
                        onClick = { isRunning = true },
                        modifier = Modifier.fillMaxWidth(),
                        icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RunningOverlay(progress: Float) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(80.dp),
                color = Primary,
                strokeWidth = 6.dp
            )
            Text(
                text = "正在计算链路...",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = Primary,
                trackColor = Background
            )
            val steps = listOf("校验参数", "计算增益链", "计算噪声", "校验余量", "生成结果")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                steps.forEachIndexed { index, step ->
                    val stepProgress = index / 5f
                    val isDone = progress > stepProgress + 0.2f
                    val isCurrent = progress > stepProgress && !isDone
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier.size(20.dp).clip(CircleShape)
                                .background(if (isDone) Success else if (isCurrent) Primary else Border),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            } else if (isCurrent) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(12.dp), strokeWidth = 2.dp)
                            }
                        }
                        Text(
                            text = step,
                            fontSize = 13.sp,
                            color = if (isDone || isCurrent) TextPrimary else TextSecondary,
                            fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ValidationStatus() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("校验状态", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                StatusChip(status = ComponentStatus.WARNING)
            }
            HorizontalDivider(color = Border, thickness = 0.5.dp)
            ValidationRow("参数完整性", true, "8/8 元素已配置")
            ValidationRow("连接有效性", true, "7 条连接正常")
            ValidationRow("单位一致性", true, "无冲突")
            ValidationRow("余量检查", false, "链路余量 < 6 dB 阈值")
            ValidationRow("压缩检查", false, "PA 接近压缩点")
        }
    }
}

@Composable
private fun ValidationRow(label: String, passed: Boolean, detail: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            if (passed) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (passed) Success else Warning,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(label, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f))
        Text(detail, fontSize = 12.sp, color = if (passed) Success else Warning)
    }
}

@Composable
private fun ScenarioSection(selected: Int, scenarios: List<String>, onSelect: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            SectionHeader("运行场景")
            TextButton(onClick = {}) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = Primary)
                Spacer(modifier = Modifier.width(2.dp))
                Text("新建场景", fontSize = 12.sp, color = Primary)
            }
        }
        scenarios.forEachIndexed { index, name ->
            ScenarioRow(
                name = name,
                isSelected = index == selected,
                isBaseline = index == 0,
                onClick = { onSelect(index) }
            )
        }
    }
}

@Composable
private fun ScenarioRow(name: String, isSelected: Boolean, isBaseline: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) PrimaryContainer else Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            width = if (isSelected) 1.5.dp else 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) Primary else Border)
        )
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = isSelected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = Primary))
            Spacer(modifier = Modifier.width(4.dp))
            Text(name, fontSize = 14.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, color = if (isSelected) Primary else TextPrimary, modifier = Modifier.weight(1f))
            if (isBaseline) {
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Border).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text("基准", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun RunScopeSection() {
    var scope by remember { mutableIntStateOf(0) }
    val scopes = listOf("全链路", "发射段", "接收段", "自定义")
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader("执行范围")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            scopes.forEachIndexed { index, label ->
                FilterChip(
                    selected = scope == index,
                    onClick = { scope = index },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryContainer,
                        selectedLabelColor = Primary
                    )
                )
            }
        }
    }
}

@Composable
private fun RunOptionsSection() {
    var computeNoise by remember { mutableStateOf(true) }
    var computeMargin by remember { mutableStateOf(true) }
    var computeHarmonics by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader("运行选项")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                OptionRow("计算噪声系数", computeNoise, onToggle = { computeNoise = it })
                HorizontalDivider(color = Border, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                OptionRow("计算链路余量", computeMargin, onToggle = { computeMargin = it })
                HorizontalDivider(color = Border, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                OptionRow("谐波分析 (高级)", computeHarmonics, onToggle = { computeHarmonics = it })
            }
        }
    }
}

@Composable
private fun OptionRow(label: String, value: Boolean, onToggle: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 14.sp, color = TextPrimary, modifier = Modifier.weight(1f))
        Switch(
            checked = value,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Primary)
        )
    }
}

@Composable
private fun HistorySection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader("最近运行", actionLabel = "全部") {}
        listOf(
            Triple("默认参数", "2分钟前", true),
            Triple("高温场景", "昨天 14:23", false),
        ).forEach { (name, time, success) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (success) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = if (success) Success else Error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(name, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                    Text(time, fontSize = 12.sp, color = TextSecondary)
                }
            }
        }
    }
}
