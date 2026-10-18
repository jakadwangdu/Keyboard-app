package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.icons.AppIcon
import com.example.icons.AppIconGlyph
import com.example.model.IconPackType
import com.example.model.KeyboardThemeType
import com.example.ui.theme.KeymoldThemeTokens
import com.example.ui.theme.SyneFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Keymold Minimal Key Component.
 * - Key press fires on pointer down, not on release.
 * - Visual: scale to 0.93 and swap to key-secondary fill, 60ms.
 * - No drop shadows on keys. Separation comes from fill color.
 * - Key radius: 10dp.
 * - High-speed zero-latency rendering.
 */
@Composable
fun RowScope.MechanicalKey(
    primaryText: String? = null,
    secondaryText: String? = null,
    iconGlyph: AppIconGlyph? = null,
    iconPackType: IconPackType = IconPackType.WHATSAPP_EXPRESSIVE,
    theme: KeyboardThemeType = KeyboardThemeType.AMOLED_BLACK,
    tokens: KeymoldThemeTokens = KeymoldThemeTokens(),
    isAccent: Boolean = false,
    isAlt: Boolean = false,
    weight: Float = 1f,
    height: Dp = tokens.keyHeight,
    testTagId: String? = null,
    isRepeatable: Boolean = false,
    onKeyTriggered: () -> Unit = {},
    onLongPress: (() -> Unit)? = null,
    onHorizontalDrag: ((Float) -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    var isScrubbingActive by remember { mutableStateOf(false) }
    val currentOnKeyTriggered by rememberUpdatedState(onKeyTriggered)
    val currentOnHorizontalDrag by rememberUpdatedState(onHorizontalDrag)
    val coroutineScope = rememberCoroutineScope()

    // 60ms fast scale transition to 0.93 on press, isolated to graphicsLayer
    val animScale = remember { androidx.compose.animation.core.Animatable(1.0f) }
    LaunchedEffect(isPressed) {
        animScale.animateTo(if (isPressed) 0.93f else 1.0f, animationSpec = tween(durationMillis = 60))
    }

    // Base fill color based on UI1.md tokens:
    // One bold element only: the accent-colored Enter key. Everything else stays neutral.
    // Active Shift: filled with text color, icon in background color.
    val isShiftKey = iconGlyph == AppIconGlyph.SHIFT
    val isActiveShift = isShiftKey && isAccent

    val fillColor = when {
        isActiveShift -> tokens.text
        isAccent -> tokens.accent
        isPressed -> tokens.keySecondary
        isAlt -> tokens.keySecondary
        else -> tokens.key
    }

    val contentColor = when {
        isActiveShift -> tokens.keyboard
        isAccent -> tokens.textOnAccent
        else -> tokens.text
    }

    Box(
        modifier = Modifier
            .weight(weight)
            .height(height)
            .padding(horizontal = 3.dp, vertical = 4.dp)
            .then(if (testTagId != null) Modifier.testTag(testTagId) else Modifier)
            .graphicsLayer {
                scaleX = animScale.value
                scaleY = animScale.value
            }
            .clip(RoundedCornerShape(10.dp))
            .background(fillColor)
            .pointerInput(isRepeatable, onHorizontalDrag != null) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    isPressed = true

                    if (onHorizontalDrag != null) {
                        // Spacebar: slide to scrub cursor
                        var isScrubbing = false
                        var accumulatedDelta = 0f
                        var lastX = down.position.x
                        val startTime = System.currentTimeMillis()
                        var totalMovedChars = 0

                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val change = event.changes.firstOrNull() ?: break
                            if (!change.pressed) break

                            val dx = change.position.x - lastX
                            lastX = change.position.x
                            accumulatedDelta += dx

                            val elapsed = System.currentTimeMillis() - startTime
                            if (!isScrubbing && (elapsed >= 140L || kotlin.math.abs(accumulatedDelta) >= 8f)) {
                                isScrubbing = true
                                isScrubbingActive = true
                            }

                            if (isScrubbing) {
                                val stepThreshold = 12f
                                while (accumulatedDelta >= stepThreshold) {
                                    currentOnHorizontalDrag?.invoke(stepThreshold)
                                    accumulatedDelta -= stepThreshold
                                    totalMovedChars++
                                }
                                while (accumulatedDelta <= -stepThreshold) {
                                    currentOnHorizontalDrag?.invoke(-stepThreshold)
                                    accumulatedDelta += stepThreshold
                                    totalMovedChars++
                                }
                            }
                        }

                        // If user tapped without scrubbing, trigger normal space
                        if (totalMovedChars == 0 && kotlin.math.abs(accumulatedDelta) < 12f) {
                            currentOnKeyTriggered()
                        }
                        isScrubbingActive = false
                    } else if (isRepeatable) {
                        currentOnKeyTriggered()
                        val repeatJob = coroutineScope.launch {
                            delay(300L)
                            while (isActive) {
                                currentOnKeyTriggered()
                                delay(45L)
                            }
                        }
                        waitForUpOrCancellation()
                        repeatJob.cancel()
                    } else {
                        // Key fires on pointer DOWN instantly!
                        currentOnKeyTriggered()
                        waitForUpOrCancellation()
                    }
                    isPressed = false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            // Spacebar special display
            onHorizontalDrag != null -> {
                if (isScrubbingActive) {
                    Text(
                        text = "◄  Slide Cursor  ►",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = tokens.accent,
                        textAlign = TextAlign.Center
                    )
                } else {
                    // Spacebar label "Keymold", muted, 12px in Syne font
                    Text(
                        text = "Keymold",
                        fontSize = 12.sp,
                        fontFamily = SyneFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = tokens.muted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            iconGlyph != null -> {
                AppIcon(
                    glyph = iconGlyph,
                    packType = iconPackType,
                    tint = contentColor,
                    size = if (iconGlyph == AppIconGlyph.ENTER_ARROW || iconGlyph == AppIconGlyph.SEND) 18.dp else 20.dp
                )
            }

            primaryText != null -> {
                val isFunctionLabel = primaryText == "?123" || primaryText == "ABC" || primaryText == "=<" || primaryText.length > 2
                Text(
                    text = primaryText,
                    fontSize = if (isFunctionLabel) 15.sp else 21.sp,
                    fontFamily = FontFamily.Default,
                    fontWeight = if (isFunctionLabel) FontWeight.Medium else FontWeight.Normal,
                    color = contentColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
