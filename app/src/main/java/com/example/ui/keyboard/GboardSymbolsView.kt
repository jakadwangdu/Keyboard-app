package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.icons.AppIconGlyph
import com.example.model.IconPackType
import com.example.model.KeyboardMode
import com.example.model.KeyboardThemeType
import com.example.ui.components.MechanicalKey

@Composable
fun GboardSymbolsView(
    isAltSymbols: Boolean,
    iconPackType: IconPackType,
    theme: KeyboardThemeType,
    onKeyPressed: (String) -> Unit,
    onBackspace: () -> Unit,
    onSendOrEnter: () -> Unit,
    onToggleAltSymbols: () -> Unit,
    onSwitchMode: (KeyboardMode) -> Unit
) {
    val row1 = if (!isAltSymbols) {
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    } else {
        listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆")
    }

    val row2 = if (!isAltSymbols) {
        listOf("@", "#", "$", "_", "&", "-", "+", "(", ")", "/")
    } else {
        listOf("£", "€", "¥", "¢", "^", "°", "=", "{", "}", "\\")
    }

    val row3 = if (!isAltSymbols) {
        listOf("*", "\"", "'", ":", ";", "!", "?")
    } else {
        listOf("%", "©", "®", "™", "✓", "[", "]")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(theme.backgroundHex))
            .padding(horizontal = 3.dp, vertical = 5.dp)
            .testTag("gboard_symbols_layout"),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Row 1: Numbers or Math/Alt symbols
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (key in row1) {
                MechanicalKey(
                    primaryText = key,
                    iconPackType = iconPackType,
                    theme = theme,
                    weight = 1f,
                    height = 50.dp,
                    onKeyTriggered = { onKeyPressed(key) }
                )
            }
        }

        // Row 2: Standard or Currency/Brackets symbols
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (key in row2) {
                MechanicalKey(
                    primaryText = key,
                    iconPackType = iconPackType,
                    theme = theme,
                    weight = 1f,
                    height = 50.dp,
                    onKeyTriggered = { onKeyPressed(key) }
                )
            }
        }

        // Row 3: [=\< or ?123] [Symbols...] [BACKSPACE]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            MechanicalKey(
                primaryText = if (!isAltSymbols) "=\\<" else "?123",
                iconPackType = iconPackType,
                theme = theme,
                isAlt = true,
                isAccent = isAltSymbols,
                weight = 1.45f,
                height = 50.dp,
                onKeyTriggered = onToggleAltSymbols
            )

            for (key in row3) {
                MechanicalKey(
                    primaryText = key,
                    iconPackType = iconPackType,
                    theme = theme,
                    weight = 1f,
                    height = 50.dp,
                    onKeyTriggered = { onKeyPressed(key) }
                )
            }

            MechanicalKey(
                iconGlyph = AppIconGlyph.BACKSPACE,
                iconPackType = iconPackType,
                theme = theme,
                isAlt = true,
                weight = 1.45f,
                height = 50.dp,
                onKeyTriggered = onBackspace
            )
        }

        // Row 4: [ABC] [,] [EMOJI] [SPACEBAR] [.] [SEND/ENTER]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            MechanicalKey(
                primaryText = "ABC",
                iconPackType = iconPackType,
                theme = theme,
                isAlt = true,
                weight = 1.35f,
                height = 50.dp,
                onKeyTriggered = { onSwitchMode(KeyboardMode.QWERTY) }
            )

            MechanicalKey(
                primaryText = "<",
                iconPackType = iconPackType,
                theme = theme,
                weight = 0.9f,
                height = 50.dp,
                onKeyTriggered = { onKeyPressed("<") }
            )

            MechanicalKey(
                iconGlyph = AppIconGlyph.EMOJI,
                iconPackType = iconPackType,
                theme = theme,
                isAlt = true,
                weight = 1.0f,
                height = 50.dp,
                onKeyTriggered = { onSwitchMode(KeyboardMode.EMOJI_DRAWER) }
            )

            MechanicalKey(
                primaryText = "English (US)",
                iconPackType = iconPackType,
                theme = theme,
                weight = 3.6f,
                height = 50.dp,
                onKeyTriggered = { onKeyPressed(" ") }
            )

            MechanicalKey(
                primaryText = ">",
                iconPackType = iconPackType,
                theme = theme,
                weight = 0.9f,
                height = 50.dp,
                onKeyTriggered = { onKeyPressed(">") }
            )

            MechanicalKey(
                iconGlyph = AppIconGlyph.SEND,
                iconPackType = iconPackType,
                theme = theme,
                isAccent = true,
                weight = 1.45f,
                height = 50.dp,
                onKeyTriggered = onSendOrEnter
            )
        }
    }
}
