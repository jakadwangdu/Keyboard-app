package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.icons.AppIcon
import com.example.icons.AppIconGlyph
import com.example.model.IconPackType
import com.example.model.KeyboardThemeType

/**
 * Standard One-Handed Mode Side Dock / Control Bar.
 * Displayed on the unused side of the keyboard when in One-Handed mode.
 * Provides controls to switch hand orientation, expand back to full width, and adjust settings.
 */
@Composable
fun OneHandedDock(
    isLeftDock: Boolean,
    theme: KeyboardThemeType,
    iconPackType: IconPackType,
    onSwitchSide: () -> Unit,
    onExpandFullWidth: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dockBg = Color(theme.surfaceHex)
    val accentColor = Color(theme.accentHex)
    val iconColor = Color(theme.keyTextHex).copy(alpha = 0.85f)
    val buttonBg = Color(theme.keyCapHex)

    Column(
        modifier = modifier
            .width(54.dp)
            .heightIn(min = 200.dp)
            .fillMaxHeight()
            .background(dockBg)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // 1. Switch Side Arrow Button (Points toward the other side)
        Surface(
            shape = CircleShape,
            color = buttonBg,
            shadowElevation = 2.dp,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .clickable { onSwitchSide() }
                .testTag("one_handed_switch_side_btn")
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                AppIcon(
                    glyph = if (isLeftDock) AppIconGlyph.ARROW_RIGHT else AppIconGlyph.ARROW_LEFT,
                    packType = iconPackType,
                    tint = accentColor,
                    size = 20.dp
                )
            }
        }

        // 2. Expand to Full Width Button
        Surface(
            shape = CircleShape,
            color = buttonBg,
            shadowElevation = 2.dp,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .clickable { onExpandFullWidth() }
                .testTag("one_handed_expand_full_btn")
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                AppIcon(
                    glyph = AppIconGlyph.EXPAND,
                    packType = iconPackType,
                    tint = iconColor,
                    size = 18.dp
                )
            }
        }

        // 3. Quick Settings / Mode Button
        Surface(
            shape = CircleShape,
            color = buttonBg,
            shadowElevation = 2.dp,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .clickable { onOpenSettings() }
                .testTag("one_handed_settings_btn")
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                AppIcon(
                    glyph = AppIconGlyph.SETTINGS,
                    packType = iconPackType,
                    tint = iconColor,
                    size = 18.dp
                )
            }
        }
    }
}
