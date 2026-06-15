package com.provisual.rf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
fun DesignOverviewScreen(
    onBack: () -> Unit,
    onFlowView: () -> Unit,
    onCanvasView: () -> Unit,
    onRunSettings: () -> Unit,
    onIssues: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        ProAppBar(
            title = "5G NR 上行链路",
            onBack = onBack,
            actions = {
                IconButton(onClick = onCanvasView) {
                    Icon(Icons.Default.AccountTree, contentDescription = "画布视图", tint = TextSecondary)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, contentDescription = "更多", tint = TextSecondary)
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item { DesignStatusSection(onRunSettings, onIssues) }
            item { KeyMetricsRow() }
            item { LinkSummarySection(onFlowView) }
        }
    }
}

@Composable
private fun DesignStatusSection(onRun: () -> Unit, onIssues: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("28 GHz · 毫米波接收链路", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    StatusChip(status = ComponentStatus.WARNING)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("最近运行", fontSize = 11.sp, color = TextSecondary)
                    Text("2分钟前", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(WarningContainer)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Warning, modifier = Modifier.size(18.dp))
                    Text("2 个问题需要处理", fontSize = 13.sp, color = Warning, fontWeight = FontWeight.Medium)
                }
                TextButton(onClick = onIssues) {
                    Text("查看", fontSize = 13.sp, color = Warning)
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Warning, modifier = Modifier.size(16.dp))
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PrimaryButton(
                    text = "运行",
                    onClick = onRun,
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                OutlinedActionButton(
                    text = "流程视图",
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun KeyMetricsRow() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader(title = "关键结果")
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(label = "总增益", value = "+37.5", unit = "dB", modifier = Modifier.weight(1f))
            MetricCard(label = "输出功率", value = "+39.5", unit = "dBm", valueColor = Warning, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(label = "链路余量", value = "3.2", unit = "dB", valueColor = Warning, modifier = Modifier.weight(1f))
            MetricCard(label = "噪声系数", value = "4.8", unit = "dB", modifier = Modifier.weight(1f))
            MetricCard(label = "接收功率", value = "-62.5", unit = "dBm", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun LinkSummarySection(onFlowView: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader(title = "链路结构 (${sampleComponents.size} 个元素)")
            TextButton(onClick = onFlowView) {
                Text("流程视图", fontSize = 13.sp, color = Primary)
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        sampleComponents.take(4).forEachIndexed { index, component ->
            CompactComponentRow(component = component, onClick = {})
            if (index < 3) {
                Box(
                    modifier = Modifier
                        .padding(start = 28.dp)
                        .width(2.dp)
                        .height(8.dp)
                        .background(Border)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onFlowView,
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("查看全部 ${sampleComponents.size} 个元素", fontSize = 13.sp, color = Primary)
        }
    }
}

@Composable
private fun CompactComponentRow(component: RfComponent, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Surface)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(component.categoryColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = null,
                tint = component.categoryColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = component.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(text = component.role, fontSize = 11.sp, color = TextSecondary)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = component.output, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(text = "输出", fontSize = 11.sp, color = TextSecondary)
        }
        StatusDot(status = component.status)
    }
}

@Composable
private fun StatusDot(status: ComponentStatus) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(status.color)
    )
}
