package com.provisual.rf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.provisual.rf.ui.components.ProAppBar
import com.provisual.rf.ui.components.StatusChip
import com.provisual.rf.ui.theme.*

@Composable
fun FlowViewScreen(onBack: () -> Unit, onComponentClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        ProAppBar(
            title = "流程视图",
            onBack = onBack,
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.FilterList, contentDescription = "筛选", tint = TextSecondary)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Add, contentDescription = "添加", tint = Primary)
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
        ) {
            item {
                FlowHeaderCard()
                Spacer(modifier = Modifier.height(8.dp))
            }

            itemsIndexed(sampleComponents) { index, component ->
                if (index > 0) {
                    FlowConnector(component.gain)
                }
                FlowComponentCard(
                    component = component,
                    onClick = { onComponentClick(component.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                AddComponentButton()
            }
        }
    }
}

@Composable
private fun FlowHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.06f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
            Text(
                text = "8 个元素 · 总增益 +37.5 dB · 2 个问题",
                fontSize = 12.sp,
                color = Primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun FlowComponentCard(component: RfComponent, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            width = if (component.status == ComponentStatus.WARNING || component.status == ComponentStatus.ERROR) 1.5.dp else 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(
                when (component.status) {
                    ComponentStatus.WARNING -> Warning.copy(alpha = 0.5f)
                    ComponentStatus.ERROR -> Error.copy(alpha = 0.5f)
                    ComponentStatus.NEEDS_UPDATE -> NeedUpdate.copy(alpha = 0.5f)
                    else -> Border
                }
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(component.categoryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    componentIcon(component.role),
                    contentDescription = null,
                    tint = component.categoryColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = component.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    StatusChip(status = component.status)
                }
                Text(text = component.role, fontSize = 12.sp, color = TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (component.gain != "—") {
                        RfValuePair("增益/损耗", component.gain)
                    }
                    if (component.noise != "—") {
                        RfValuePair("噪声系数", component.noise)
                    }
                    RfValuePair("输出", component.output)
                }
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Border,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun RfValuePair(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 10.sp, color = TextSecondary)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

@Composable
private fun FlowConnector(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(37.dp))
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(24.dp)
                .background(Border)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
    }
}

@Composable
private fun AddComponentButton() {
    OutlinedButton(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
    ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("添加元素", fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

fun componentIcon(role: String) = when {
    role.contains("发射") || role.contains("信号源") -> Icons.Default.Sensors
    role.contains("滤波") -> Icons.Default.FilterAlt
    role.contains("增益") || role.contains("放大") -> Icons.Default.SignalCellular4Bar
    role.contains("天线") || role.contains("辐射") || role.contains("接收") -> Icons.Default.CellTower
    role.contains("损耗") || role.contains("传播") -> Icons.Default.SignalCellularAlt
    role.contains("接收机") || role.contains("解调") -> Icons.Default.Radio
    else -> Icons.Default.Memory
}
