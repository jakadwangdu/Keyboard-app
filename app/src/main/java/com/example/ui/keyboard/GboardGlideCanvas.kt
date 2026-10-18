package com.example.ui.keyboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.example.engine.GlideTypingEngine

/**
 * Self-contained Glide Typing gesture overlay.
 * Isolates drag pointer tracking to this overlay only, ensuring
 * that the 33 keyboard keys NEVER recompose on pointer moves!
 */
@Composable
fun GboardGlideOverlay(
    enabled: Boolean,
    accentColor: Color,
    isShiftActive: Boolean,
    isCapsLock: Boolean,
    onGlideWordCommitted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!enabled) return

    var layoutSize by remember { mutableStateOf(IntSize.Zero) }
    var trailPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var normalizedPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var isGlideGestureActive by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { layoutSize = it.size }
            .pointerInput(enabled, isShiftActive, isCapsLock) {
                detectDragGestures(
                    onDragStart = { startOffset ->
                        // Only activate glide typing above spacebar / bottom row (upper 72%)
                        if (layoutSize.width > 0 && layoutSize.height > 0 && startOffset.y < layoutSize.height * 0.72f) {
                            isGlideGestureActive = true
                            trailPoints = listOf(startOffset)
                            normalizedPoints = listOf(
                                Offset(
                                    startOffset.x / layoutSize.width.toFloat(),
                                    startOffset.y / layoutSize.height.toFloat()
                                )
                            )
                        } else {
                            isGlideGestureActive = false
                        }
                    },
                    onDrag = { change, _ ->
                        if (isGlideGestureActive && layoutSize.width > 0 && layoutSize.height > 0) {
                            val currentPos = change.position
                            trailPoints = (trailPoints + currentPos).takeLast(40)
                            normalizedPoints = (normalizedPoints + Offset(
                                currentPos.x / layoutSize.width.toFloat(),
                                currentPos.y / layoutSize.height.toFloat()
                            )).takeLast(40)
                        }
                    },
                    onDragEnd = {
                        if (isGlideGestureActive && normalizedPoints.size >= 3) {
                            val candidates = GlideTypingEngine.recognizeGlidePath(normalizedPoints)
                            if (candidates.isNotEmpty()) {
                                val best = candidates.first().word
                                val formatted = if (isShiftActive || isCapsLock) {
                                    best.replaceFirstChar { it.uppercase() }
                                } else best
                                onGlideWordCommitted(formatted)
                            }
                        }
                        isGlideGestureActive = false
                        trailPoints = emptyList()
                        normalizedPoints = emptyList()
                    },
                    onDragCancel = {
                        isGlideGestureActive = false
                        trailPoints = emptyList()
                        normalizedPoints = emptyList()
                    }
                )
            }
    ) {
        if (trailPoints.size >= 2) {
            GboardGlideCanvas(
                trailPoints = trailPoints,
                accentColor = accentColor
            )
        }
    }
}

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

        // Draw smooth quadratic bezier curve across points
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

        // Draw touch point tip circle
        drawCircle(
            color = Color.White,
            radius = 6f,
            center = last
        )
    }
}
