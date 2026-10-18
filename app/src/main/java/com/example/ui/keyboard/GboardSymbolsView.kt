package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.icons.AppIconGlyph
import com.example.model.IconPackType
import com.example.model.KeyboardMode
import com.example.model.KeyboardThemeType
import com.example.ui.components.MechanicalKey
import com.example.ui.theme.KeymoldThemeTokens

/**
 * Keymold Symbols Mode.
 * - Row 1: 1 2 3 4 5 6 7 8 9 0
 * - Row 2: @ # $ % & - + ( ) /
 * - Row 3: =< | * " ' : ; ! ? | Backspace
 * - Row 4: ABC | Emoji | Space (5x) | . | Enter (1.45x in accent)
 */
@Composable
fun GboardSymbolsView(
    isAltSymbols: Boolean,
    iconPackType: IconPackType,
    theme: KeyboardThemeType,
    tokens: KeymoldThemeTokens = KeymoldThemeTokens(),
    keyHeight: Dp = tokens.keyHeight,
    onKeyPressed: (String) -> Unit,
    onBackspace: () -> Unit,
    onSendOrEnter: () -> Unit,
    onToggleAltSymbols: () -> Unit,
    onSwitchMode: (KeyboardMode) -> Unit,
    onScrubCursor: (Int) -> Unit = {}
) {
    val row1 = if (!isAltSymbols) {
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    } else {
        listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆")
    }

    val row2 = if (!isAltSymbols) {
        listOf("@", "#", "$", "%", "&", "-", "+", "(", ")", "/")
    } else {
        listOf("£", "€", "¥", "¢", "^", "°", "=", "{", "}", "\\")
    }

    val row3 = if (!isAltSymbols) {
        listOf("*", "\"", "'", ":", ";", "!", "?")
    } else {
        listOf("_", "©", "®", "™", "✓", "[", "]")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(tokens.keyboard)
            .padding(start = 6.dp, end = 6.dp, bottom = 10.dp, top = 2.dp)
            .testTag("gboard_symbols_layout"),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Row 1: 1 2 3 4 5 6 7 8 9 0
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (key in row1) {
                MechanicalKey(
                    primaryText = key,
                    iconPackType = iconPackType,
                    theme = theme,
                    tokens = tokens,
                    weight = 1f,
                    height = keyHeight,
                    onKeyTriggered = { onKeyPressed(key) }
                )
            }
        }

        // Row 2: @ # $ % & - + ( ) /
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (key in row2) {
                MechanicalKey(
                    primaryText = key,
                    iconPackType = iconPackType,
                    theme = theme,
                    tokens = tokens,
                    weight = 1f,
                    height = keyHeight,
                    onKeyTriggered = { onKeyPressed(key) }
                )
            }
        }

        // Row 3: =< (1.45x) | * " ' : ; ! ? | Backspace (1.45x)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            MechanicalKey(
                primaryText = if (!isAltSymbols) "=\\<" else "?123",
                iconPackType = iconPackType,
                theme = theme,
                tokens = tokens,
                isAlt = true,
                isAccent = isAltSymbols,
                weight = 1.45f,
                height = keyHeight,
                onKeyTriggered = onToggleAltSymbols
            )

            for (key in row3) {
                MechanicalKey(
                    primaryText = key,
                    iconPackType = iconPackType,
                    theme = theme,
                    tokens = tokens,
                    weight = 1f,
                    height = keyHeight,
                    onKeyTriggered = { onKeyPressed(key) }
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
                onKeyTriggered = onBackspace
            )
        }

        // Row 4: ABC (1.45x) | Emoji (1.0x) | Space (5.0x) | . (1.0x) | Enter (1.45x in accent)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            MechanicalKey(
                primaryText = "ABC",
                iconPackType = iconPackType,
                theme = theme,
                tokens = tokens,
                isAlt = true,
                weight = 1.45f,
                height = keyHeight,
                onKeyTriggered = { onSwitchMode(KeyboardMode.QWERTY) }
            )

            MechanicalKey(
                iconGlyph = AppIconGlyph.EMOJI,
                iconPackType = iconPackType,
                theme = theme,
                tokens = tokens,
                isAlt = true,
                weight = 1.0f,
                height = keyHeight,
                onKeyTriggered = { onSwitchMode(KeyboardMode.EMOJI_DRAWER) }
            )

            MechanicalKey(
                iconPackType = iconPackType,
                theme = theme,
                tokens = tokens,
                weight = 5.0f,
                height = keyHeight,
                testTagId = "key_symbols_spacebar",
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

            MechanicalKey(
                iconGlyph = AppIconGlyph.ENTER_ARROW,
                iconPackType = iconPackType,
                theme = theme,
                tokens = tokens,
                isAccent = true,
                weight = 1.45f,
                height = keyHeight,
                onKeyTriggered = onSendOrEnter
            )
        }
    }
}
