package com.prorf.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prorf.app.ui.theme.ProColors

/** A rounded white card used throughout the app. */
@Composable
fun ProCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ProColors.Surface,
        border = BorderStroke(1.dp, ProColors.Border),
    ) {
        Box(
            Modifier
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
                .padding(padding)
        ) { content() }
    }
}

/** A small rounded square holding a tinted icon. */
@Composable
fun IconBadge(
    icon: ImageVector,
    accent: Color,
    size: Int = 40,
    corner: Int = 12,
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(corner.dp))
            .background(accent.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size((size * 0.5).dp))
    }
}

/** Colored status / value pill. */
@Composable
fun StatusPill(text: String, color: Color, soft: Color) {
    Surface(shape = RoundedCornerShape(8.dp), color = soft) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

/** Horizontal scrolling segmented filter chips with a selected index. */
@Composable
fun FilterChipsRow(
    items: List<String>,
    selected: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(items.size) { i ->
            val active = i == selected
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (active) ProColors.Primary else ProColors.Surface,
                border = BorderStroke(1.dp, if (active) ProColors.Primary else ProColors.Border),
                modifier = Modifier.clickable { onSelected(i) },
            ) {
                Text(
                    text = items[i],
                    style = MaterialTheme.typography.labelMedium,
                    color = if (active) ProColors.Surface else ProColors.TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }
    }
}

/** Read-only search field placeholder (prototype: tapping does nothing yet). */
@Composable
fun SearchBar(hint: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth().height(44.dp),
        shape = RoundedCornerShape(12.dp),
        color = ProColors.SurfaceAlt,
        border = BorderStroke(1.dp, ProColors.Border),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp),
        ) {
            Icon(Icons.Filled.Search, null, tint = ProColors.TextTertiary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(hint, color = ProColors.TextTertiary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/** Section title with optional trailing action label. */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = ProColors.TextPrimary)
        if (action != null) {
            Text(
                action,
                style = MaterialTheme.typography.labelMedium,
                color = ProColors.Primary,
                modifier = if (onAction != null) Modifier.clickable { onAction() } else Modifier,
            )
        }
    }
}

/** A tab row segmented control rendered inside content (not the bottom bar). */
@Composable
fun SegmentTabs(
    tabs: List<String>,
    selected: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = ProColors.SurfaceAlt,
    ) {
        Row(Modifier.padding(4.dp)) {
            tabs.forEachIndexed { i, t ->
                val active = i == selected
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelected(i) },
                    shape = RoundedCornerShape(9.dp),
                    color = if (active) ProColors.Surface else Color.Transparent,
                    shadowElevation = if (active) 1.dp else 0.dp,
                ) {
                    Text(
                        t,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (active) ProColors.Primary else ProColors.TextSecondary,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun VerticalSpace(height: Int) = Spacer(Modifier.height(height.dp))
