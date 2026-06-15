package com.prorf.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prorf.app.data.LibraryItem
import com.prorf.app.data.SampleData
import com.prorf.app.ui.components.FilterChipsRow
import com.prorf.app.ui.components.IconBadge
import com.prorf.app.ui.components.ProCard
import com.prorf.app.ui.components.ProTopBar
import com.prorf.app.ui.components.SearchBar
import com.prorf.app.ui.components.rememberToast
import com.prorf.app.ui.theme.ProColors

@Composable
fun SceneLibraryScreen(nav: NavController) {
    val toast = rememberToast()
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("全部", "官方场景", "我的场景", "收藏")

    Scaffold(
        containerColor = ProColors.Background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { toast("新建场景") },
                containerColor = ProColors.Primary,
                contentColor = ProColors.Surface,
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text("新建场景") },
            )
        },
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            ProTopBar(title = "场景库", subtitle = "场景模板库 · 快速复刻工程场景")
            Spacer(Modifier.height(4.dp))
            SearchBar("搜索场景...", Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))
            FilterChipsRow(items = tabs, selected = tab, onSelected = { tab = it })
            Spacer(Modifier.height(12.dp))
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(SampleData.scenes) { item ->
                    LibraryRow(item) { toast("打开场景：${item.title}") }
                }
            }
        }
    }
}

/** Shared row used by the scene & template libraries. */
@Composable
fun LibraryRow(item: LibraryItem, onClick: () -> Unit) {
    ProCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(item.icon, item.accent, size = 44)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleSmall, color = ProColors.TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = ProColors.TextSecondary)
            }
            Text(
                item.trailing,
                style = MaterialTheme.typography.titleSmall,
                color = if (item.trailingPositive) ProColors.Green else ProColors.Red,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
