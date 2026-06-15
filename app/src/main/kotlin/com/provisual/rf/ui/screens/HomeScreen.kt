package com.provisual.rf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.provisual.rf.ui.components.SectionHeader
import com.provisual.rf.ui.components.StatusChip
import com.provisual.rf.ui.theme.*

@Composable
fun HomeScreen(onProjectClick: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item { HomeAppBar() }
        item { ContinueCard(onProjectClick) }
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader(title = "最近项目", actionLabel = "全部") {}
            }
        }
        items(sampleProjects) { project ->
            ProjectCard(project = project, onClick = onProjectClick)
        }
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader(title = "快速开始", actionLabel = "全部模板") {}
                Spacer(modifier = Modifier.height(8.dp))
                TemplateRow()
            }
        }
    }
}

@Composable
private fun HomeAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "ProRF",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            Text(
                text = "射频链路计算",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.Search, contentDescription = "搜索", tint = TextSecondary)
        }
        IconButton(onClick = {}) {
            BadgedBox(badge = {
                Badge(containerColor = Error) { Text("2", fontSize = 10.sp) }
            }) {
                Icon(Icons.Default.Notifications, contentDescription = "通知", tint = TextSecondary)
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Primary),
            contentAlignment = Alignment.Center
        ) {
            Text("J", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
    HorizontalDivider(color = Border, thickness = 0.5.dp)
}

@Composable
private fun ContinueCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "继续上次工作",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "5G NR 上行链路",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "总增益 +37.5 dB",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "余量 3.2 dB",
                        fontSize = 13.sp,
                        color = Warning
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("⚠ 2 个警告", fontSize = 12.sp, color = Warning, fontWeight = FontWeight.Medium)
                }
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun ProjectCard(project: Project, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Hub,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = project.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    StatusChip(status = project.status)
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = project.description,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(12.dp), tint = TextSecondary)
                        Text(text = project.lastRun, fontSize = 11.sp, color = TextSecondary)
                    }
                    if (project.issueCount > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(12.dp), tint = Warning)
                            Text(text = "${project.issueCount} 个问题", fontSize = 11.sp, color = Warning)
                        }
                    }
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Border, modifier = Modifier.size(20.dp))
        }
    }
}

data class TemplateInfo(val name: String, val icon: ImageVector, val color: Color, val desc: String)

val templates = listOf(
    TemplateInfo("接收链路", Icons.Default.ArrowDownward, CategoryBlue, "接收机灵敏度分析"),
    TemplateInfo("发射链路", Icons.Default.ArrowUpward, CategoryGreen, "发射功率链路计算"),
    TemplateInfo("链路预算", Icons.Default.AccountBalance, CategoryOrange, "端到端链路余量"),
    TemplateInfo("简单链路", Icons.Default.LinearScale, CategoryPurple, "基础信号链路"),
)

@Composable
private fun TemplateRow() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(templates) { tmpl ->
            Card(
                modifier = Modifier
                    .width(140.dp)
                    .clickable {},
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(tmpl.color.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(tmpl.icon, contentDescription = null, tint = tmpl.color, modifier = Modifier.size(20.dp))
                    }
                    Text(text = tmpl.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(text = tmpl.desc, fontSize = 11.sp, color = TextSecondary, maxLines = 2)
                }
            }
        }
    }
}
