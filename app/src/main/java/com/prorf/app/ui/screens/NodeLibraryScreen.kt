package com.prorf.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prorf.app.data.NodeComponent
import com.prorf.app.data.SampleData
import com.prorf.app.ui.components.FilterChipsRow
import com.prorf.app.ui.components.IconBadge
import com.prorf.app.ui.components.ProCard
import com.prorf.app.ui.components.ProTopBar
import com.prorf.app.ui.components.SearchBar
import com.prorf.app.ui.components.rememberToast
import com.prorf.app.ui.theme.ProColors

@Composable
fun NodeLibraryScreen(nav: NavController) {
    val toast = rememberToast()
    var tab by remember { mutableIntStateOf(0) }
    val categories = SampleData.nodeCategories
    val items = remember(tab) {
        if (tab == 0) SampleData.nodeComponents
        else SampleData.nodeComponents.filter { it.category == categories[tab] }
    }

    Column(Modifier.fillMaxSize()) {
        ProTopBar(title = "节点库", subtitle = "节点组件库 · 拖拽式构建系统")
        Spacer(Modifier.height(4.dp))
        SearchBar("搜索节点...", Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(12.dp))
        FilterChipsRow(items = categories, selected = tab, onSelected = { tab = it })
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items) { c -> NodeTile(c) { toast("添加节点：${c.name}") } }
        }
    }
}

@Composable
private fun NodeTile(c: NodeComponent, onClick: () -> Unit) {
    ProCard(onClick = onClick, padding = PaddingValues(vertical = 18.dp)) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconBadge(c.icon, c.accent, size = 46, corner = 14)
            Spacer(Modifier.height(10.dp))
            Text(
                c.name,
                style = MaterialTheme.typography.labelMedium,
                color = ProColors.TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}
