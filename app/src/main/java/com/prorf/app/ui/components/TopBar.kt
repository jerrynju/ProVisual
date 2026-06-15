package com.prorf.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.prorf.app.ui.theme.ProColors

/**
 * A lightweight top bar with an optional back affordance, a centered title and
 * up to two trailing icon actions. Kept flat to match the reference design.
 */
@Composable
fun ProTopBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: List<Pair<ImageVector, () -> Unit>> = emptyList(),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ProColors.Surface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            TopIcon(Icons.AutoMirrored.Filled.ArrowBack, onBack)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = if (onBack != null) 4.dp else 8.dp),
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = ProColors.TextPrimary,
                maxLines = 1,
            )
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = ProColors.TextSecondary, maxLines = 1)
            }
        }
        actions.forEach { (icon, action) -> TopIcon(icon, action) }
    }
}

@Composable
private fun TopIcon(icon: ImageVector, onClick: () -> Unit, tint: Color = ProColors.TextPrimary) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
    }
}

/** A thin divider line. */
@Composable
fun ThinDivider(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().height(1.dp).background(ProColors.Divider))
}
