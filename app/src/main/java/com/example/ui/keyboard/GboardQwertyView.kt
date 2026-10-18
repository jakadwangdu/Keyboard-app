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
import com.example.ui.theme.KeymoldThemeTokens

/**
 * Keymold Letters Mode (QWERTY layout).
 * - Row 1: q w e r t y u i o p
 * - Row 2: a s d f g h j k l, with 5% side padding
 * - Row 3: Shift (1.45x) | z x c v b n m | Backspace (1.45x)
 * - Row 4: ?123 | Emoji | Space (5x width, label "Keymold", muted 12px) | . | Enter (1.45x in accent)
 * - Gap between keys 6px, 8px vertically, padding 6px sides, 10px bottom.
 */
@Composable
fun GboardQwertyView(
    iconPackType: IconPackType,
    theme: KeyboardThemeType,
    isShiftActive: Boolean,
    isCapsLock: Boolean,
    tokens: KeymoldThemeTokens = KeymoldThemeTokens(),
    glideTypingEnabled: Boolean = false,
    keyHeight: Dp = tokens.keyHeight,
    onKeyPressed: (String) -> Unit,
    onBackspace: () -> Unit,
    onSendOrEnter: () -> Unit,
    onToggleShift: () -> Unit,
    onSwitchMode: (KeyboardMode) -> Unit,
    onScrubCursor: (Int) -> Unit = {},
    onGlideWordCommitted: (String) -> Unit = {}
) {
    val row1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val row2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val row3 = listOf("z", "x", "c", "v", "b", "n", "m")

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(tokens.keyboard)
                .padding(start = 6.dp, end = 6.dp, bottom = 10.dp, top = 2.dp)
                .testTag("gboard_qwerty_layout"),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Row 1: q w e r t y u i o p
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (letter in row1) {
                    val displayLetter = if (isShiftActive || isCapsLock) letter.uppercase() else letter
                    MechanicalKey(
                        primaryText = displayLetter,
                        iconPackType = iconPackType,
                        theme = theme,
                        tokens = tokens,
                        weight = 1f,
                        height = keyHeight,
                        testTagId = "key_$letter",
                        onKeyTriggered = { onKeyPressed(displayLetter) }
                    )
                }
            }

            // Row 2: a s d f g h j k l (with 5% side padding)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                for (letter in row2) {
                    val displayLetter = if (isShiftActive || isCapsLock) letter.uppercase() else letter
                    MechanicalKey(
                        primaryText = displayLetter,
                        iconPackType = iconPackType,
                        theme = theme,
                        tokens = tokens,
                        weight = 1f,
                        height = keyHeight,
                        testTagId = "key_$letter",
                        onKeyTriggered = { onKeyPressed(displayLetter) }
                    )
                }
            }

            // Row 3: Shift (1.45x) | z x c v b n m | Backspace (1.45x)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                MechanicalKey(
                    iconGlyph = AppIconGlyph.SHIFT,
                    iconPackType = iconPackType,
                    theme = theme,
                    tokens = tokens,
                    isAlt = true,
                    isAccent = isShiftActive || isCapsLock,
                    weight = 1.45f,
                    height = keyHeight,
                    testTagId = "key_shift",
                    onKeyTriggered = onToggleShift
                )

                for (letter in row3) {
                    val displayLetter = if (isShiftActive || isCapsLock) letter.uppercase() else letter
                    MechanicalKey(
                        primaryText = displayLetter,
                        iconPackType = iconPackType,
                        theme = theme,
                        tokens = tokens,
                        weight = 1f,
                        height = keyHeight,
                        testTagId = "key_$letter",
                        onKeyTriggered = { onKeyPressed(displayLetter) }
                    )
                }

                MechanicalKey(
                    iconGlyph = AppIconGlyph.BACKSPACE,
                    iconPackType = iconPackType,
                    theme = theme,
                    tokens = tokens,
                    isAlt = true,
                    weight = 1.45f,
                    height = keyHeight,
                    isRepeatable = true,
                    testTagId = "key_backspace",
                    onKeyTriggered = onBackspace
                )
            }

            // Row 4: ?123 | Emoji | Space (5x width, label "Keymold", muted 12px) | . | Enter (1.45x in accent)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                MechanicalKey(
                    primaryText = "?123",
                    iconPackType = iconPackType,
                    theme = theme,
                    tokens = tokens,
                    isAlt = true,
                    weight = 1.45f,
                    height = keyHeight,
                    testTagId = "key_symbols_mode",
                    onKeyTriggered = { onSwitchMode(KeyboardMode.SYMBOLS_123) }
                )

                MechanicalKey(
                    iconGlyph = AppIconGlyph.EMOJI,
                    iconPackType = iconPackType,
                    theme = theme,
                    tokens = tokens,
                    isAlt = true,
                    weight = 1.0f,
                    height = keyHeight,
                    testTagId = "key_emoji_mode",
                    onKeyTriggered = { onSwitchMode(KeyboardMode.EMOJI_DRAWER) }
                )

                MechanicalKey(
                    iconPackType = iconPackType,
                    theme = theme,
                    tokens = tokens,
                    weight = 5.0f,
                    height = keyHeight,
                    testTagId = "key_spacebar",
                    onKeyTriggered = { onKeyPressed(" ") },
                    onHorizontalDrag = { dragDelta ->
                        if (dragDelta > 0) onScrubCursor(1) else onScrubCursor(-1)
                    }
                )

                MechanicalKey(
                    primaryText = ".",
                    iconPackType = iconPackType,
                    theme = theme,
                    tokens = tokens,
                    weight = 1.0f,
                    height = keyHeight,
                    onKeyTriggered = { onKeyPressed(".") }
                )

                // One bold element only: the accent-colored Enter key
                MechanicalKey(
                    iconGlyph = AppIconGlyph.ENTER_ARROW,
                    iconPackType = iconPackType,
                    theme = theme,
                    tokens = tokens,
                    isAccent = true,
                    weight = 1.45f,
                    height = keyHeight,
                    testTagId = "key_send_return",
                    onKeyTriggered = onSendOrEnter
                )
            }
        }

        // Isolated gesture glide overlay: draws smooth trail with zero key recomposition
        GboardGlideOverlay(
            enabled = glideTypingEnabled,
            accentColor = tokens.accent,
            isShiftActive = isShiftActive,
            isCapsLock = isCapsLock,
            onGlideWordCommitted = onGlideWordCommitted
        )
    }
}
