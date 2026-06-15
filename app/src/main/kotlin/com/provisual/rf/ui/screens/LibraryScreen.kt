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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.provisual.rf.ui.components.ProAppBar
import com.provisual.rf.ui.components.SectionHeader
import com.provisual.rf.ui.theme.*

data class LibraryEntry(
    val name: String,
    val category: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val isRecent: Boolean = false,
    val isRecommended: Boolean = false
)

val libraryEntries = listOf(
    LibraryEntry("低噪声放大器", "放大器", "用于接收链路前端，噪声系数 < 2 dB", Icons.Default.SignalCellular4Bar, CategoryGreen, isRecent = true, isRecommended = true),
    LibraryEntry("带通滤波器", "滤波器", "射频频段选择，插入损耗 1~3 dB", Icons.Default.FilterAlt, CategoryBlue, isRecent = true),
    LibraryEntry("功率放大器", "放大器", "发射链路末级放大，高功率输出", Icons.Default.SignalCellular4Bar, CategoryOrange, isRecommended = true),
    LibraryEntry("移相器", "移相", "相位可调，0~360°", Icons.Default.Rotate90DegreesCcw, CategoryPurple),
    LibraryEntry("定向耦合器", "耦合", "功率分配与监测，耦合度 10~30 dB", Icons.Default.CallSplit, CategoryBlue),
    LibraryEntry("隔离器", "隔离", "单向传输，防止反射", Icons.Default.ArrowForward, CategoryGreen),
    LibraryEntry("混频器", "变频", "射频信号上/下变频", Icons.Default.SwapHoriz, CategoryOrange),
    LibraryEntry("振荡器", "信号源", "本振信号生成，频率稳定", Icons.Default.Waves, CategoryPurple),
    LibraryEntry("衰减器", "衰减", "固定或可变功率衰减", Icons.Default.TrendingDown, CategoryRed),
    LibraryEntry("功率分配器", "分路", "威尔金森功分器，2路或4路", Icons.Default.AccountTree, CategoryBlue),
    LibraryEntry("天线 - 贴片", "天线", "低剖面定向辐射，常用于移动终端", Icons.Default.CellTower, CategoryBlue, isRecommended = true),
    LibraryEntry("天线 - 喇叭", "天线", "高增益定向天线，用于测量系统", Icons.Default.CellWifi, CategoryGreen),
)

val categories = listOf("全部", "放大器", "滤波器", "天线", "混频器", "分路", "信号源", "其他")

@Composable
fun LibraryScreen() {
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableIntStateOf(0) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("全部", "最近使用", "推荐", "模板")

    val filteredEntries = libraryEntries.filter { entry ->
        val matchesSearch = searchText.isEmpty() ||
            entry.name.contains(searchText, ignoreCase = true) ||
            entry.category.contains(searchText, ignoreCase = true) ||
            entry.description.contains(searchText, ignoreCase = true)
        val matchesCategory = selectedCategory == 0 || entry.category == categories[selectedCategory]
        val matchesTab = when (selectedTab) {
            1 -> entry.isRecent
            2 -> entry.isRecommended
            else -> true
        }
        matchesSearch && matchesCategory && matchesTab
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        ProAppBar(
            title = "元素库",
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Add, contentDescription = "自定义", tint = Primary)
                }
            }
        )

        Column(modifier = Modifier.background(Surface)) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("搜索元素或模板...", color = TextSecondary, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Border,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Background
                )
            )

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Surface,
                contentColor = Primary,
                divider = {}
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(tab, fontSize = 13.sp, color = if (selectedTab == index) Primary else TextSecondary)
                        }
                    )
                }
            }
            HorizontalDivider(color = Border, thickness = 0.5.dp)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (selectedTab == 0 || selectedTab == 1) {
                item {
                    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 4.dp)) {
                        SectionHeader("分类")
                    }
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories.indices.toList()) { index ->
                            FilterChip(
                                selected = selectedCategory == index,
                                onClick = { selectedCategory = index },
                                label = { Text(categories[index], fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryContainer,
                                    selectedLabelColor = Primary
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (selectedTab == 3) {
                item { TemplatesSection() }
            } else {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        SectionHeader("${filteredEntries.size} 个元素")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                items(filteredEntries) { entry ->
                    LibraryEntryCard(entry = entry)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun LibraryEntryCard(entry: LibraryEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable {},
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
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
                    .background(entry.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(entry.icon, contentDescription = null, tint = entry.color, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(entry.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    if (entry.isRecommended) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PrimaryContainer)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("推荐", fontSize = 10.sp, color = Primary, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(entry.category, fontSize = 11.sp, color = entry.color, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = entry.description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            FilledTonalButton(
                onClick = {},
                modifier = Modifier.height(34.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = PrimaryContainer,
                    contentColor = Primary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text("添加", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun TemplatesSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        SectionHeader("链路模板")
        Spacer(modifier = Modifier.height(10.dp))
        val templateItems = listOf(
            Triple("接收链路", "接收机灵敏度、动态范围分析", CategoryBlue),
            Triple("发射链路", "发射功率、效率、谐波分析", CategoryGreen),
            Triple("链路预算", "端到端链路余量完整计算", CategoryOrange),
            Triple("简单链路", "基础信号链路，快速上手", CategoryPurple),
            Triple("测量配置", "测量系统信号路径", CategoryRed),
            Triple("空白设计", "从零开始自定义链路", Disabled),
        )
        templateItems.forEach { (name, desc, color) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {},
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.GridView, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(desc, fontSize = 12.sp, color = TextSecondary)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Border, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
