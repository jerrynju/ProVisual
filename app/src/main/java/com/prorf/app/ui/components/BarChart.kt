package com.prorf.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prorf.app.data.Bar
import com.prorf.app.ui.theme.ProColors
import kotlin.math.abs

/**
 * A simple diverging bar chart with a baseline at zero. Positive bars grow up
 * from the centre, negative bars grow down. Heights are normalized to the
 * largest absolute value in the set.
 */
@Composable
fun BarChart(
    bars: List<Bar>,
    modifier: Modifier = Modifier,
    chartHeight: Int = 180,
) {
    val maxAbs = (bars.maxOfOrNull { abs(it.value) } ?: 1f).coerceAtLeast(1f)
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(chartHeight.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            bars.forEach { bar ->
                val frac = (abs(bar.value) / maxAbs).coerceIn(0.04f, 1f)
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                        if (bar.value >= 0) PositiveBar(bar.color, frac, formatValue(bar.value))
                    }
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                        if (bar.value < 0) NegativeBar(bar.color, frac, formatValue(bar.value))
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            bars.forEach { bar ->
                Text(
                    bar.label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = ProColors.TextTertiary,
                )
            }
        }
    }
}

@Composable
private fun PositiveBar(color: Color, frac: Float, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.SemiBold)
        Box(
            Modifier
                .padding(top = 2.dp)
                .width(18.dp)
                .fillMaxHeight(frac)
                .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                .background(Brush.verticalGradient(listOf(color, color.copy(alpha = 0.6f))))
        )
    }
}

@Composable
private fun NegativeBar(color: Color, frac: Float, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .width(18.dp)
                .fillMaxHeight(frac)
                .clip(RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp))
                .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.6f), color)))
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

private fun formatValue(v: Float): String {
    val s = if (v % 1f == 0f) v.toInt().toString() else String.format("%.1f", v)
    return if (v > 0) "+$s" else s
}
