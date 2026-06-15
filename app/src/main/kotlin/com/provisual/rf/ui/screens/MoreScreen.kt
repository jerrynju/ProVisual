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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.provisual.rf.ui.components.ProAppBar
import com.provisual.rf.ui.components.SectionHeader
import com.provisual.rf.ui.theme.*

@Composable
fun MoreScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        ProAppBar(title = "更多")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { UserProfileSection() }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { MoreSection("项目", listOf(
                MoreItem(Icons.Default.FolderOpen, "项目设置", "管理当前项目"),
                MoreItem(Icons.Default.ImportExport, "导入 / 导出", "JSON、CSV 格式"),
                MoreItem(Icons.Default.Share, "分享链路", "生成分享链接"),
            )) }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { MoreSection("高级工具", listOf(
                MoreItem(Icons.Default.AccountTree, "自由画布编辑", "非线性拓扑结构"),
                MoreItem(Icons.Default.BugReport, "断点与单步", "逐元素调试"),
                MoreItem(Icons.Default.TrackChanges, "信号监看", "实时观测各节点"),
                MoreItem(Icons.Default.TableChart, "原始数据查看器", "完整计算矩阵"),
                MoreItem(Icons.Default.Extension, "自定义元素", "定义新元素类型"),
                MoreItem(Icons.Default.Code, "Schema 查看器", "查看内部数据结构"),
            )) }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { MoreSection("帮助与支持", listOf(
                MoreItem(Icons.Default.MenuBook, "使用文档", "查看完整文档"),
                MoreItem(Icons.Default.Forum, "反馈问题", "报告 Bug 或建议"),
                MoreItem(Icons.Default.Info, "关于 ProRF", "版本 1.0.0"),
            )) }
        }
    }
}

data class MoreItem(val icon: ImageVector, val title: String, val subtitle: String)

@Composable
private fun UserProfileSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Text("J", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Jerry Chen", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("专业版用户", fontSize = 13.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatChip("4 个项目")
                    StatChip("12 条链路")
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Border, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun StatChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Background)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MoreSection(title: String, items: List<MoreItem>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader(title)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
        ) {
            items.forEachIndexed { index, item ->
                MoreItemRow(item = item)
                if (index < items.size - 1) {
                    HorizontalDivider(color = Border, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
private fun MoreItemRow(item: MoreItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(item.icon, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(item.subtitle, fontSize = 12.sp, color = TextSecondary)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Border, modifier = Modifier.size(18.dp))
    }
}
