package com.prorf.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prorf.app.ui.components.IconBadge
import com.prorf.app.ui.components.ProCard
import com.prorf.app.ui.components.ProTopBar
import com.prorf.app.ui.components.rememberToast
import com.prorf.app.ui.theme.ProColors

private data class SettingEntry(
    val label: String,
    val icon: ImageVector,
    val accent: Color,
    val trailing: String? = null,
)

@Composable
fun SettingsScreen(nav: NavController) {
    val toast = rememberToast()
    val entries = listOf(
        SettingEntry("通用设置", Icons.Filled.Tune, ProColors.Primary),
        SettingEntry("单位与量纲", Icons.Filled.Straighten, ProColors.Cyan),
        SettingEntry("主题设置", Icons.Filled.Palette, ProColors.Purple, trailing = "浅色模式"),
        SettingEntry("计算设置", Icons.Filled.Calculate, ProColors.Orange),
        SettingEntry("数据管理", Icons.Filled.Storage, ProColors.Green),
        SettingEntry("备份与恢复", Icons.Filled.Backup, ProColors.Cyan),
        SettingEntry("关于 ProRF", Icons.Filled.Info, ProColors.TextSecondary, trailing = "版本 1.0.0"),
    )

    Column(Modifier.fillMaxSize().background(ProColors.Background)) {
        ProTopBar(title = "设置", subtitle = "系统设置与个性化", onBack = { nav.popBackStack() })
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                ProCard(padding = PaddingValues(vertical = 4.dp)) {
                    Column {
                        entries.forEachIndexed { i, e ->
                            SettingRow(e) { toast(e.label) }
                            if (i != entries.lastIndex) {
                                Box(Modifier.fillMaxWidth().padding(start = 64.dp).height(1.dp).background(ProColors.Divider))
                            }
                        }
                    }
                }
            }
            item {
                ProCard(onClick = { toast("退出登录") }) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null, tint = ProColors.Red, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("退出登录", style = MaterialTheme.typography.titleSmall, color = ProColors.Red, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingRow(e: SettingEntry, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconBadge(e.icon, e.accent, size = 36, corner = 10)
        Spacer(Modifier.width(12.dp))
        Text(e.label, style = MaterialTheme.typography.bodyLarge, color = ProColors.TextPrimary, modifier = Modifier.weight(1f))
        if (e.trailing != null) {
            Text(e.trailing, style = MaterialTheme.typography.bodySmall, color = ProColors.TextTertiary)
            Spacer(Modifier.width(8.dp))
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, tint = ProColors.TextTertiary, modifier = Modifier.size(14.dp))
    }
}
