package com.provisual.rf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
fun IssuesScreen(onBack: () -> Unit) {
    var selectedFilter by remember { mutableIntStateOf(0) }
    val filters = listOf("全部 (3)", "错误 (1)", "警告 (1)", "信息 (1)")

    val filteredIssues = when (selectedFilter) {
        1 -> sampleIssues.filter { it.severity == IssueSeverity.ERROR }
        2 -> sampleIssues.filter { it.severity == IssueSeverity.WARNING }
        3 -> sampleIssues.filter { it.severity == IssueSeverity.INFO }
        else -> sampleIssues
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        ProAppBar(
            title = "问题中心",
            onBack = onBack,
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.FilterList, contentDescription = "筛选", tint = TextSecondary)
                }
            }
        )

        IssueSummaryBar()

        ScrollableTabRow(
            selectedTabIndex = selectedFilter,
            containerColor = Surface,
            contentColor = Primary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedFilter]),
                    height = 2.dp,
                    color = Primary
                )
            },
            divider = { HorizontalDivider(color = Border, thickness = 0.5.dp) }
        ) {
            filters.forEachIndexed { index, filter ->
                Tab(
                    selected = selectedFilter == index,
                    onClick = { selectedFilter = index },
                    text = {
                        Text(
                            text = filter,
                            fontSize = 13.sp,
                            color = if (selectedFilter == index) Primary else TextSecondary
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (filteredIssues.isEmpty()) {
                item { EmptyIssuesState() }
            } else {
                items(filteredIssues) { issue ->
                    IssueCard(issue = issue)
                }
            }
        }
    }
}

@Composable
private fun IssueSummaryBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IssueSummaryChip(count = 1, label = "错误", color = Error)
        IssueSummaryChip(count = 1, label = "警告", color = Warning)
        IssueSummaryChip(count = 1, label = "信息", color = Primary)
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = {}) {
            Text("一键修复", fontSize = 13.sp, color = Primary)
        }
    }
    HorizontalDivider(color = Border, thickness = 0.5.dp)
}

@Composable
private fun IssueSummaryChip(count: Int, label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text("$count", color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Text(label, fontSize = 12.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun EmptyIssuesState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Success,
                modifier = Modifier.size(48.dp)
            )
            Text("此类别没有问题", fontSize = 15.sp, color = TextSecondary)
        }
    }
}
