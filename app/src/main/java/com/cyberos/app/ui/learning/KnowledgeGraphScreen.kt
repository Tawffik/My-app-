package com.cyberos.app.ui.learning

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberos.app.learning.CyberCurriculum
import com.cyberos.app.ui.lang.Lang

@Composable
fun KnowledgeGraphScreen(onBack: () -> Unit, onOpenTopic: (String) -> Unit) {
    val topics = remember { CyberCurriculum.paths.flatMap { it.topics } }
    val textMeasurer = rememberTextMeasurer()
    val nodeGreen = Color(0xFF10B981)
    val nodeBg = Color(0xFF09090B)
    val edgeColor = Color(0xFF2E3B4E)
    val labelColor = Color(0xFFA1A1AA)

    fun positions(w: Float, h: Float): Map<String, Offset> {
        val cols = 3
        val rows = (topics.size + cols - 1) / cols
        val map = mutableMapOf<String, Offset>()
        topics.forEachIndexed { i, t ->
            map[t.id] = Offset((i % cols + 0.5f) * w / cols, (i / cols + 0.5f) * h / maxOf(1, rows))
        }
        return map
    }

    Box(Modifier.fillMaxSize()) {
        Canvas(
            Modifier.fillMaxSize().pointerInput(Unit) {
                detectTapGestures { tap ->
                    val pos = positions(size.width.toFloat(), size.height.toFloat())
                    pos.forEach { (id, o) -> if ((tap - o).getDistance() < 70f) onOpenTopic(id) }
                }
            }
        ) {
            val pos = positions(size.width, size.height)
            topics.forEach { t ->
                t.related.forEach { rid ->
                    val a = pos[t.id]; val b = pos[rid]
                    if (a != null && b != null) drawLine(edgeColor, a, b, strokeWidth = 2f)
                }
            }
            pos.forEach { (id, o) ->
                drawCircle(nodeGreen, 15f, o)
                drawCircle(nodeBg, 11f, o)
                val t = topics.first { it.id == id }
                drawText(textMeasurer, t.title, topLeft = Offset(o.x - 52f, o.y + 20f), style = TextStyle(fontSize = 10.sp, color = labelColor))
            }
        }
        IconButton(onClick = onBack, modifier = Modifier.padding(8.dp)) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
    }
}
