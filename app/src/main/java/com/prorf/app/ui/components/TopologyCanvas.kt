package com.prorf.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prorf.app.data.GraphEdge
import com.prorf.app.data.GraphNode
import com.prorf.app.data.GraphPort
import com.prorf.app.ui.theme.ProColors

// Layout constants shared between the drawn nodes and the wire anchor maths.
private const val NODE_W = 156
private const val HEADER_H = 42
private const val PORTS_TOP = 6
private const val PORT_H = 24

/**
 * A pannable / zoomable node-graph canvas: node cards with input/output ports
 * connected by curved bezier wires, drawn over a dotted grid background.
 */
@Composable
fun TopologyCanvas(
    nodes: List<GraphNode>,
    edges: List<GraphEdge>,
    modifier: Modifier = Modifier,
    onNodeClick: (GraphNode) -> Unit = {},
) {
    var scale by remember { mutableFloatStateOf(0.92f) }
    var offset by remember { mutableStateOf(Offset(16f, 8f)) }
    val nodeById = remember(nodes) { nodes.associateBy { it.id } }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .background(ProColors.SurfaceAlt)
            .dottedGrid()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 2.2f)
                    offset += pan
                }
            },
    ) {
        Box(
            Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                    transformOrigin = TransformOrigin(0f, 0f)
                }
                .size(width = 440.dp, height = 820.dp),
        ) {
            // Wires under the nodes
            Canvas(Modifier.fillMaxSize()) {
                edges.forEach { e ->
                    val from = nodeById[e.from] ?: return@forEach
                    val to = nodeById[e.to] ?: return@forEach
                    val start = outputAnchor(from, e.fromPort)
                    val end = inputAnchor(to, e.toPort)
                    drawWire(start, end, e.color)
                }
            }
            nodes.forEach { node ->
                GraphNodeCard(
                    node = node,
                    modifier = Modifier
                        .offset(x = node.x.dp, y = node.y.dp)
                        .width(NODE_W.dp),
                    onClick = { onNodeClick(node) },
                )
            }
        }
    }
}

private fun DrawScope.outputAnchor(node: GraphNode, port: Int): Offset = Offset(
    x = (node.x + NODE_W).dp.toPx(),
    y = (node.y + HEADER_H + PORTS_TOP + PORT_H * port + PORT_H / 2).dp.toPx(),
)

private fun DrawScope.inputAnchor(node: GraphNode, port: Int): Offset = Offset(
    x = node.x.dp.toPx(),
    y = (node.y + HEADER_H + PORTS_TOP + PORT_H * port + PORT_H / 2).dp.toPx(),
)

private fun DrawScope.drawWire(start: Offset, end: Offset, color: Color) {
    val dx = (kotlin.math.abs(end.x - start.x) * 0.5f).coerceAtLeast(40.dp.toPx())
    val path = Path().apply {
        moveTo(start.x, start.y)
        cubicTo(start.x + dx, start.y, end.x - dx, end.y, end.x, end.y)
    }
    drawPath(path, color.copy(alpha = 0.55f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx()))
    drawCircle(color, radius = 3.dp.toPx(), center = start)
    drawCircle(color, radius = 3.dp.toPx(), center = end)
}

/** Subtle dotted-grid background. */
private fun Modifier.dottedGrid(): Modifier = this.drawBehind {
    val step = 22.dp.toPx()
    val r = 1.1.dp.toPx()
    var y = 0f
    while (y < size.height) {
        var x = 0f
        while (x < size.width) {
            drawCircle(ProColors.Border, radius = r, center = Offset(x, y))
            x += step
        }
        y += step
    }
}

@Composable
private fun GraphNodeCard(node: GraphNode, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = ProColors.Surface,
        shadowElevation = 3.dp,
        onClick = onClick,
    ) {
        Column {
            // Header
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(HEADER_H.dp)
                    .background(node.accent.copy(alpha = 0.10f))
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(node.icon, null, tint = node.accent, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    node.title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ProColors.TextPrimary,
                    maxLines = 1,
                )
            }
            Spacer(Modifier.height(PORTS_TOP.dp))
            val rows = maxOf(node.inputs.size, node.outputs.size)
            repeat(rows) { i ->
                Row(
                    Modifier.fillMaxWidth().height(PORT_H.dp).padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val input = node.inputs.getOrNull(i)
                    val output = node.outputs.getOrNull(i)
                    if (input != null) {
                        PortDot(node.accent)
                        Spacer(Modifier.width(5.dp))
                        Text(input.name, style = MaterialTheme.typography.labelSmall, color = ProColors.TextSecondary, maxLines = 1)
                    }
                    Spacer(Modifier.weight(1f))
                    if (output != null) {
                        Text(
                            output.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (output.highlight) ProColors.Green else ProColors.TextSecondary,
                            fontWeight = if (output.highlight) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1,
                        )
                        Spacer(Modifier.width(5.dp))
                        PortDot(if (output.highlight) ProColors.Green else node.accent)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PortDot(color: Color) {
    Box(
        Modifier
            .size(8.dp)
            .background(ProColors.Surface, CircleShape)
    ) {
        Box(Modifier.fillMaxSize().padding(1.dp).background(color, CircleShape))
    }
}
