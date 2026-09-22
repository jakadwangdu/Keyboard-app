package com.example.ui.keyboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun GboardGlideCanvas(
    trailPoints: List<Offset>,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    if (trailPoints.size < 2) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val path = Path()
        val p0 = trailPoints.first()
        path.moveTo(p0.x, p0.y)

        // Draw smooth cubic or quadratic bezier line across points
        for (i in 1 until trailPoints.size) {
            val prev = trailPoints[i - 1]
            val curr = trailPoints[i]
            val midX = (prev.x + curr.x) / 2f
            val midY = (prev.y + curr.y) / 2f
            path.quadraticTo(prev.x, prev.y, midX, midY)
        }
        val last = trailPoints.last()
        path.lineTo(last.x, last.y)

        // Draw soft glowing outer aura
        drawPath(
            path = path,
            color = accentColor.copy(alpha = 0.25f),
            style = Stroke(
                width = 16f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw vibrant inner core stroke
        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.6f),
                    accentColor,
                    accentColor.copy(alpha = 0.9f)
                )
            ),
            style = Stroke(
                width = 7f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw trailing leading touch tip circle
        drawCircle(
            color = Color.White,
            radius = 6f,
            center = last
        )
    }
}
