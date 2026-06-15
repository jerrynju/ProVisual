package com.prorf.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prorf.app.data.SampleData
import com.prorf.app.ui.components.FilterChipsRow
import com.prorf.app.ui.components.ProTopBar
import com.prorf.app.ui.components.SearchBar
import com.prorf.app.ui.components.rememberToast
import com.prorf.app.ui.theme.ProColors

@Composable
fun TemplateLibraryScreen(nav: NavController) {
    val toast = rememberToast()
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("全部", "官方模板", "我的模板")

    Scaffold(
        containerColor = ProColors.Background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { toast("导入模板") },
                containerColor = ProColors.Primary,
                contentColor = ProColors.Surface,
                icon = { Icon(Icons.Filled.FileDownload, null) },
                text = { Text("导入模板") },
            )
        },
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            ProTopBar(title = "模板库", subtitle = "工作流模板库 · 一键创建", onBack = { nav.popBackStack() })
            Spacer(Modifier.height(4.dp))
            SearchBar("搜索模板...", Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))
            FilterChipsRow(items = tabs, selected = tab, onSelected = { tab = it })
            Spacer(Modifier.height(12.dp))
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(SampleData.templates) { item ->
                    LibraryRow(item) { toast("使用模板：${item.title}") }
                }
            }
        }
    }
}
