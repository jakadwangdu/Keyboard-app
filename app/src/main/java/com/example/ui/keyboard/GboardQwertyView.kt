package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.engine.GlideTypingEngine
import com.example.icons.AppIconGlyph
import com.example.model.IconPackType
import com.example.model.KeyboardMode
import com.example.model.KeyboardThemeType
import com.example.ui.components.MechanicalKey

@Composable
fun GboardQwertyView(
    iconPackType: IconPackType,
    theme: KeyboardThemeType,
    isShiftActive: Boolean,
    isCapsLock: Boolean,
    glideTypingEnabled: Boolean = false,
    keyHeight: Dp = 50.dp,
    onKeyPressed: (String) -> Unit,
    onBackspace: () -> Unit,
    onSendOrEnter: () -> Unit,
    onToggleShift: () -> Unit,
    onSwitchMode: (KeyboardMode) -> Unit,
    onScrubCursor: (Int) -> Unit = {},
    onGlideWordCommitted: (String) -> Unit = {}
) {
    val row1 = listOf(
        Pair("q", "1"), Pair("w", "2"), Pair("e", "3"), Pair("r", "4"),
        Pair("t", "5"), Pair("y", "6"), Pair("u", "7"), Pair("i", "8"),
        Pair("o", "9"), Pair("p", "0")
    )

    val row2 = listOf(
        Pair("a", "@"), Pair("s", "#"), Pair("d", "$"), Pair("f", "_"),
        Pair("g", "&"), Pair("h", "-"), Pair("j", "+"), Pair("k", "("),
        Pair("l", ")")
    )

    val row3 = listOf(
        Pair("z", "*"), Pair("x", "\""), Pair("c", "'"), Pair("v", ":"),
        Pair("b", ";"), Pair("n", "!"), Pair("m", "?")
    )

    var layoutSize by remember { mutableStateOf(IntSize.Zero) }
    var trailPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var normalizedPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { layoutSize = it.size }
            .then(
                if (glideTypingEnabled) {
                    Modifier.pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { startOffset ->
                                if (layoutSize.width > 0 && layoutSize.height > 0) {
                                    trailPoints = listOf(startOffset)
                                    normalizedPoints = listOf(
                                        Offset(
                                            startOffset.x / layoutSize.width.toFloat(),
                                            startOffset.y / layoutSize.height.toFloat()
                                        )
                                    )
                                }
                            },
                            onDrag = { change, _ ->
                                if (layoutSize.width > 0 && layoutSize.height > 0) {
                                    val currentPos = change.position
                                    trailPoints = (trailPoints + currentPos).takeLast(40)
                                    normalizedPoints = (normalizedPoints + Offset(
                                        currentPos.x / layoutSize.width.toFloat(),
                                        currentPos.y / layoutSize.height.toFloat()
                                    )).takeLast(40)
                                }
                            },
                            onDragEnd = {
                                if (normalizedPoints.size >= 3) {
                                    val candidates = GlideTypingEngine.recognizeGlidePath(normalizedPoints)
                                    if (candidates.isNotEmpty()) {
                                        val best = candidates.first().word
                                        val formatted = if (isShiftActive || isCapsLock) {
                                            best.replaceFirstChar { it.uppercase() }
                                        } else best
                                        onGlideWordCommitted(formatted)
                                    }
                                }
                                trailPoints = emptyList()
                                normalizedPoints = emptyList()
                            },
                            onDragCancel = {
                                trailPoints = emptyList()
                                normalizedPoints = emptyList()
                            }
                        )
                    }
                } else Modifier
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(theme.backgroundHex))
                .padding(horizontal = 3.dp, vertical = 5.dp)
                .testTag("gboard_qwerty_layout"),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Row 1: Q W E R T Y U I O P (with numbers 1-0 secondary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for ((letter, number) in row1) {
                    val displayLetter = if (isShiftActive || isCapsLock) letter.uppercase() else letter
                    MechanicalKey(
                        primaryText = displayLetter,
                        secondaryText = number,
                        iconPackType = iconPackType,
                        theme = theme,
                        weight = 1f,
                        height = keyHeight,
                        testTagId = "key_$letter",
                        onKeyTriggered = { onKeyPressed(displayLetter) }
                    )
                }
            }

            // Row 2: A S D F G H J K L (with symbols secondary)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                for ((letter, symbol) in row2) {
                    val displayLetter = if (isShiftActive || isCapsLock) letter.uppercase() else letter
                    MechanicalKey(
                        primaryText = displayLetter,
                        secondaryText = symbol,
                        iconPackType = iconPackType,
                        theme = theme,
                        weight = 1f,
                        height = keyHeight,
                        testTagId = "key_$letter",
                        onKeyTriggered = { onKeyPressed(displayLetter) }
                    )
                }
            }

            // Row 3: [SHIFT] Z X C V B N M [BACKSPACE]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Shift / Caps Key
                MechanicalKey(
                    iconGlyph = AppIconGlyph.SHIFT,
                    iconPackType = iconPackType,
                    theme = theme,
                    isAlt = true,
                    isAccent = isShiftActive || isCapsLock,
                    weight = 1.45f,
                    height = keyHeight,
                    testTagId = "key_shift",
                    onKeyTriggered = onToggleShift
                )

                for ((letter, symbol) in row3) {
                    val displayLetter = if (isShiftActive || isCapsLock) letter.uppercase() else letter
                    MechanicalKey(
                        primaryText = displayLetter,
                        secondaryText = symbol,
                        iconPackType = iconPackType,
                        theme = theme,
                        weight = 1f,
                        height = keyHeight,
                        testTagId = "key_$letter",
                        onKeyTriggered = { onKeyPressed(displayLetter) }
                    )
                }

                // Backspace Key (with continuous hold-to-delete)
                MechanicalKey(
                    iconGlyph = AppIconGlyph.BACKSPACE,
                    iconPackType = iconPackType,
                    theme = theme,
                    isAlt = true,
                    weight = 1.45f,
                    height = keyHeight,
                    isRepeatable = true,
                    testTagId = "key_backspace",
                    onKeyTriggered = onBackspace
                )
            }

            // Row 4: [?123] [,] [EMOJI] [SPACEBAR] [.] [SEND/ENTER]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // ?123 Mode Key
                MechanicalKey(
                    primaryText = "?123",
                    iconPackType = iconPackType,
                    theme = theme,
                    isAlt = true,
                    weight = 1.35f,
                    height = keyHeight,
                    testTagId = "key_symbols_mode",
                    onKeyTriggered = { onSwitchMode(KeyboardMode.SYMBOLS_123) }
                )

                // Comma Key
                MechanicalKey(
                    primaryText = ",",
                    iconPackType = iconPackType,
                    theme = theme,
                    weight = 0.9f,
                    height = keyHeight,
                    onKeyTriggered = { onKeyPressed(",") }
                )

                // Emoji / Sticker drawer Key
                MechanicalKey(
                    iconGlyph = AppIconGlyph.EMOJI,
                    iconPackType = iconPackType,
                    theme = theme,
                    isAlt = true,
                    weight = 1.0f,
                    height = keyHeight,
                    testTagId = "key_emoji_mode",
                    onKeyTriggered = { onSwitchMode(KeyboardMode.EMOJI_DRAWER) }
                )

                // Spacebar with Gboard language indicator & Swipe cursor scrub
                MechanicalKey(
                    primaryText = "English (US)",
                    iconPackType = iconPackType,
                    theme = theme,
                    weight = 3.6f,
                    height = keyHeight,
                    testTagId = "key_spacebar",
                    onKeyTriggered = { onKeyPressed(" ") },
                    onHorizontalDrag = { dragDelta ->
                        if (dragDelta > 0) onScrubCursor(1) else onScrubCursor(-1)
                    }
                )

                // Period Key
                MechanicalKey(
                    primaryText = ".",
                    iconPackType = iconPackType,
                    theme = theme,
                    weight = 0.9f,
                    height = keyHeight,
                    onKeyTriggered = { onKeyPressed(".") }
                )

                // Send / Return Key (WhatsApp green accent or retro return arrow)
                val isRetro95 = theme == KeyboardThemeType.RETRO_95_PIXEL
                MechanicalKey(
                    iconGlyph = if (isRetro95) AppIconGlyph.ENTER_ARROW else AppIconGlyph.SEND,
                    primaryText = if (isRetro95) "↵" else null,
                    iconPackType = iconPackType,
                    theme = theme,
                    isAccent = !isRetro95,
                    isAlt = isRetro95,
                    weight = 1.45f,
                    height = keyHeight,
                    testTagId = "key_send_return",
                    onKeyTriggered = onSendOrEnter
                )
            }
        }

        // Gesture glide trail overlay
        if (trailPoints.isNotEmpty()) {
            GboardGlideCanvas(
                trailPoints = trailPoints,
                accentColor = Color(theme.accentHex)
            )
        }
    }
}
