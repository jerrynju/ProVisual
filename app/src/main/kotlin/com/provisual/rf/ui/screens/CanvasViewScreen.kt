package com.provisual.rf.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size as GeoSize
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.provisual.rf.ui.components.ProAppBar
import com.provisual.rf.ui.theme.*

data class CanvasNode(val id: String, val label: String, val role: String, val x: Float, val y: Float, val color: Color, val status: ComponentStatus)

val canvasNodes = listOf(
    CanvasNode("1", "信号源", "发射机", 60f, 80f, CategoryBlue, ComponentStatus.COMPLETED),
    CanvasNode("2", "带通滤波器", "滤波", 200f, 80f, CategoryGreen, ComponentStatus.COMPLETED),
    CanvasNode("3", "功率放大器", "增益", 340f, 80f, CategoryOrange, ComponentStatus.WARNING),
    CanvasNode("4", "天线", "辐射", 480f, 80f, CategoryBlue, ComponentStatus.COMPLETED),
    CanvasNode("5", "自由空间损耗", "传播", 480f, 220f, CategoryRed, ComponentStatus.COMPLETED),
    CanvasNode("6", "接收天线", "接收", 340f, 220f, CategoryBlue, ComponentStatus.COMPLETED),
    CanvasNode("7", "低噪声放大器", "增益", 200f, 220f, CategoryGreen, ComponentStatus.COMPLETED),
    CanvasNode("8", "解调器", "接收机", 60f, 220f, CategoryPurple, ComponentStatus.NEEDS_UPDATE),
)

val canvasEdges = listOf(
    Pair("1", "2"), Pair("2", "3"), Pair("3", "4"),
    Pair("4", "5"), Pair("5", "6"), Pair("6", "7"), Pair("7", "8")
)

@Composable
fun CanvasViewScreen(onBack: () -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(40f, 60f)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        ProAppBar(
            title = "画布视图",
            onBack = onBack,
            actions = {
                IconButton(onClick = { scale = 1f; offset = Offset(40f, 60f) }) {
                    Icon(Icons.Default.FitScreen, contentDescription = "适应屏幕", tint = TextSecondary)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.AutoFixHigh, contentDescription = "自动布局", tint = TextSecondary)
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            CanvasArea(scale = scale, offset = offset, onTransform = { scaleChange, panChange ->
                scale = (scale * scaleChange).coerceIn(0.3f, 3f)
                offset = offset + panChange
            })
            CanvasControls(
                scale = scale,
                onZoomIn = { scale = (scale * 1.2f).coerceAtMost(3f) },
                onZoomOut = { scale = (scale / 1.2f).coerceAtLeast(0.3f) },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            )
            CanvasLegend(modifier = Modifier.align(Alignment.TopStart).padding(start = 8.dp, top = 8.dp))
        }
    }
}

@Composable
private fun CanvasArea(scale: Float, offset: Offset, onTransform: (Float, Offset) -> Unit) {
    val textMeasurer = rememberTextMeasurer()
    val nodeW = 120f
    val nodeH = 56f

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    onTransform(zoom, pan)
                }
            }
    ) {
        val bgPrimary = Color(0xFF2563EB)
        val surfaceColor = Color.White
        val borderColor = Color(0xFFD9DEE7)

        translate(offset.x, offset.y) {
            drawGridDots(scale)

            canvasEdges.forEach { (fromId, toId) ->
                val from = canvasNodes.find { it.id == fromId } ?: return@forEach
                val to = canvasNodes.find { it.id == toId } ?: return@forEach
                val fx = from.x * scale + nodeW * scale / 2
                val fy = from.y * scale + nodeH * scale / 2
                val tx = to.x * scale + nodeW * scale / 2
                val ty = to.y * scale + nodeH * scale / 2
                drawLine(
                    color = borderColor,
                    start = Offset(fx, fy),
                    end = Offset(tx, ty),
                    strokeWidth = 2f * scale,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f * scale, 4f * scale))
                )
                val arrowX = fx + (tx - fx) * 0.6f
                val arrowY = fy + (ty - fy) * 0.6f
                drawCircle(color = bgPrimary, radius = 4f * scale, center = Offset(arrowX, arrowY))
            }

            canvasNodes.forEach { node ->
                val nx = node.x * scale
                val ny = node.y * scale
                val nw = nodeW * scale
                val nh = nodeH * scale
                val nodeColor = node.color
                val statusColor = node.status.color

                drawRoundRect(
                    color = surfaceColor,
                    topLeft = Offset(nx, ny),
                    size = GeoSize(nw, nh),
                    cornerRadius = CornerRadius(10f * scale),
                    style = Fill
                )
                drawRoundRect(
                    color = if (node.status == ComponentStatus.WARNING) Color(0xFFF59E0B).copy(0.6f)
                            else if (node.status == ComponentStatus.NEEDS_UPDATE) Color(0xFF8B5CF6).copy(0.5f)
                            else borderColor,
                    topLeft = Offset(nx, ny),
                    size = GeoSize(nw, nh),
                    cornerRadius = CornerRadius(10f * scale),
                    style = Stroke(width = 1.5f * scale)
                )
                drawRect(
                    color = nodeColor.copy(alpha = 0.15f),
                    topLeft = Offset(nx, ny),
                    size = GeoSize(6f * scale, nh)
                )

                val labelStyle = TextStyle(fontSize = (10 * scale).sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1D2433))
                val roleStyle = TextStyle(fontSize = (8 * scale).sp, color = Color(0xFF667085))

                val labelResult = textMeasurer.measure(node.label, labelStyle)
                val roleResult = textMeasurer.measure(node.role, roleStyle)

                drawText(
                    textLayoutResult = labelResult,
                    topLeft = Offset(nx + 12f * scale, ny + 10f * scale)
                )
                drawText(
                    textLayoutResult = roleResult,
                    topLeft = Offset(nx + 12f * scale, ny + 28f * scale)
                )

                drawCircle(
                    color = statusColor,
                    radius = 4f * scale,
                    center = Offset(nx + nw - 10f * scale, ny + 10f * scale)
                )
            }
        }
    }
}

fun DrawScope.drawGridDots(scale: Float) {
    val step = 30f * scale
    val dotColor = Color(0xFFD9DEE7)
    var x = 0f
    while (x < size.width) {
        var y = 0f
        while (y < size.height) {
            drawCircle(color = dotColor, radius = 1.5f, center = Offset(x, y))
            y += step
        }
        x += step
    }
}

@Composable
private fun CanvasControls(scale: Float, onZoomIn: () -> Unit, onZoomOut: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                IconButton(onClick = onZoomIn, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "放大", tint = TextPrimary, modifier = Modifier.size(18.dp))
                }
                HorizontalDivider(color = Border, thickness = 0.5.dp)
                IconButton(onClick = onZoomOut, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "缩小", tint = TextPrimary, modifier = Modifier.size(18.dp))
                }
            }
        }
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = "${(scale * 100).toInt()}%",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CanvasLegend(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.92f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("图例", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            listOf(
                ComponentStatus.COMPLETED to "已完成",
                ComponentStatus.WARNING to "警告",
                ComponentStatus.NEEDS_UPDATE to "需更新"
            ).forEach { (status, label) ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(status.color))
                    Text(label, fontSize = 10.sp, color = TextSecondary)
                }
            }
        }
    }
}
