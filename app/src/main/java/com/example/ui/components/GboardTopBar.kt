package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.model.KeyboardMode
import com.example.model.KeyboardThemeType
import com.example.model.SwitchType

@Composable
fun GboardTopBar(
    iconPackType: IconPackType,
    theme: KeyboardThemeType,
    currentSwitch: SwitchType,
    keyboardMode: KeyboardMode,
    isSoundOn: Boolean,
    activeText: String,
    suggestions: List<String>,
    isVoiceTyping: Boolean,
    onSuggestionClick: (String) -> Unit,
    onToggleIconPack: () -> Unit,
    onOpenSwitchStudio: () -> Unit,
    onOpenThemePicker: () -> Unit,
    onToggleSound: () -> Unit,
    onOpenEmoji: () -> Unit,
    onOpenAttachments: () -> Unit,
    onOpenClipboard: () -> Unit,
    onToggleVoiceTyping: () -> Unit,
    onFormatText: (String) -> Unit,
    onOpenDefaultKeyboardSetup: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val barBg = Color(theme.surfaceHex)
    val accentColor = Color(theme.accentHex)
    val textColor = Color(theme.keyTextHex)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(barBg)
            .testTag("gboard_top_bar")
    ) {
        // 1. Gboard Predictive Word Suggestion Strip (if typing)
        if (suggestions.isNotEmpty() && !isVoiceTyping) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestions.forEach { word ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(theme.keyCapAltHex).copy(alpha = 0.7f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSuggestionClick(word) }
                    ) {
                        Text(
                            text = word,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            Divider(color = textColor.copy(alpha = 0.08f), thickness = 0.5.dp)
        }

        // 2. Interactive Voice Dictation Waveform Mode (if active)
        if (isVoiceTyping) {
            val infiniteTransition = rememberInfiniteTransition(label = "voice_wave")
            val bar1Height by infiniteTransition.animateFloat(
                initialValue = 8f, targetValue = 28f,
                animationSpec = infiniteRepeatable(tween(300, easing = LinearEasing), RepeatMode.Reverse),
                label = "b1"
            )
            val bar2Height by infiniteTransition.animateFloat(
                initialValue = 22f, targetValue = 10f,
                animationSpec = infiniteRepeatable(tween(250, easing = LinearEasing), RepeatMode.Reverse),
                label = "b2"
            )
            val bar3Height by infiniteTransition.animateFloat(
                initialValue = 12f, targetValue = 30f,
                animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
                label = "b3"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppIcon(
                        glyph = AppIconGlyph.MIC,
                        packType = iconPackType,
                        tint = accentColor,
                        size = 20.dp
                    )
                    Text(
                        text = "Listening... Speak naturally",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                }

                // Audio waveform bars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(3.dp).height(bar1Height.dp).clip(CircleShape).background(accentColor))
                    Box(modifier = Modifier.width(3.dp).height(bar3Height.dp).clip(CircleShape).background(accentColor))
                    Box(modifier = Modifier.width(3.dp).height(bar2Height.dp).clip(CircleShape).background(accentColor))
                    Box(modifier = Modifier.width(3.dp).height(bar1Height.dp).clip(CircleShape).background(accentColor))
                }

                IconButton(
                    onClick = onToggleVoiceTyping,
                    modifier = Modifier.size(32.dp)
                ) {
                    AppIcon(
                        glyph = AppIconGlyph.CLEAR,
                        packType = iconPackType,
                        tint = textColor.copy(alpha = 0.7f),
                        size = 18.dp
                    )
                }
            }
        } else {
            val isRetro95 = theme == KeyboardThemeType.RETRO_95_PIXEL || iconPackType == IconPackType.RETRO_PIXEL_95
            
            if (isRetro95) {
                // 1995 Pixel Desktop Classic Utility Bar (matching user's reference UI)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(Color(0xFF000080))
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 8-bit Yellow Robot Mascot Head (Switch Studio)
                        IconButton(
                            onClick = onOpenSwitchStudio,
                            modifier = Modifier.size(32.dp)
                        ) {
                            AppIcon(
                                glyph = AppIconGlyph.MASCOT_ROBOT,
                                packType = iconPackType,
                                tint = Color(0xFFFFD700),
                                size = 22.dp
                            )
                        }

                        // 8-bit Floppy Disk (Clipboard / Saved texts)
                        IconButton(
                            onClick = onOpenClipboard,
                            modifier = Modifier.size(32.dp)
                        ) {
                            AppIcon(
                                glyph = AppIconGlyph.FLOPPY_DISK,
                                packType = iconPackType,
                                tint = Color.White,
                                size = 20.dp
                            )
                        }

                        // 8-bit Jigsaw Puzzle (Icon Packs)
                        IconButton(
                            onClick = onToggleIconPack,
                            modifier = Modifier.size(32.dp)
                        ) {
                            AppIcon(
                                glyph = AppIconGlyph.PUZZLE,
                                packType = iconPackType,
                                tint = Color(0xFFFFD700),
                                size = 20.dp
                            )
                        }

                        // 8-bit Hex Bolt / Nut (Settings & Themes)
                        IconButton(
                            onClick = onOpenThemePicker,
                            modifier = Modifier.size(32.dp)
                        ) {
                            AppIcon(
                                glyph = AppIconGlyph.HEX_NUT,
                                packType = iconPackType,
                                tint = Color(0xFFC0C0C0),
                                size = 20.dp
                            )
                        }

                        // 8-bit Microphone (Voice dictation)
                        IconButton(
                            onClick = onToggleVoiceTyping,
                            modifier = Modifier.size(32.dp)
                        ) {
                            AppIcon(
                                glyph = AppIconGlyph.MIC,
                                packType = iconPackType,
                                tint = Color.White,
                                size = 20.dp
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 8-bit Search Magnifier (Emoji & Search)
                        IconButton(
                            onClick = onOpenEmoji,
                            modifier = Modifier.size(32.dp)
                        ) {
                            AppIcon(
                                glyph = AppIconGlyph.SEARCH,
                                packType = iconPackType,
                                tint = Color.White,
                                size = 20.dp
                            )
                        }

                        // 8-bit Chevron Down (Quick Settings)
                        IconButton(
                            onClick = onToggleIconPack,
                            modifier = Modifier.size(32.dp)
                        ) {
                            AppIcon(
                                glyph = AppIconGlyph.CHEVRON_DOWN,
                                packType = iconPackType,
                                tint = Color.White,
                                size = 20.dp
                            )
                        }
                    }
                }
            } else {
                // 3. Main Gboard Conversation Toolbar Ribbon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Keyboard Settings & Resize icon
                    ToolbarIconItem(
                        glyph = AppIconGlyph.SETTINGS,
                        packType = iconPackType,
                        tint = accentColor,
                        badgeText = null,
                        tooltip = "Keyboard Settings & Resizing",
                        onClick = onOpenSettings
                    )

                    // Set as Default Keyboard (System IME Setup)
                    ToolbarChipItem(
                        label = "⌨️ Set Default",
                        accentColor = Color(0xFF00E676),
                        textColor = Color.White,
                        onClick = onOpenDefaultKeyboardSetup
                    )

                    // WhatsApp-Style Attachments button (Paperclip)
                    ToolbarIconItem(
                        glyph = AppIconGlyph.ATTACHMENT,
                        packType = iconPackType,
                        tint = if (keyboardMode == KeyboardMode.ATTACHMENTS_SHEET) accentColor else textColor,
                        badgeText = null,
                        tooltip = "Attachments",
                        onClick = onOpenAttachments
                    )

                    // Conversation Emoji & Sticker Drawer
                    ToolbarIconItem(
                        glyph = AppIconGlyph.EMOJI,
                        packType = iconPackType,
                        tint = if (keyboardMode == KeyboardMode.EMOJI_DRAWER || keyboardMode == KeyboardMode.STICKERS_DRAWER) accentColor else textColor,
                        badgeText = null,
                        tooltip = "Emojis & Stickers",
                        onClick = onOpenEmoji
                    )

                    // Mechanical Switch Sound Studio
                    ToolbarIconItem(
                        glyph = AppIconGlyph.SWITCH_STUDIO,
                        packType = iconPackType,
                        tint = Color(currentSwitch.accentHex),
                        badgeText = currentSwitch.title.split(" ").firstOrNull(),
                        tooltip = "Switch Acoustics",
                        onClick = onOpenSwitchStudio
                    )

                    // Themes
                    ToolbarIconItem(
                        glyph = AppIconGlyph.THEME,
                        packType = iconPackType,
                        tint = textColor,
                        badgeText = null,
                        tooltip = "Themes",
                        onClick = onOpenThemePicker
                    )

                    // Icon Pack Selector (WhatsApp / Android 17 / iOS SF / 1995 Pixel)
                    ToolbarChipItem(
                        label = when (iconPackType) {
                            IconPackType.RETRO_PIXEL_95 -> "1995 Pixel"
                            IconPackType.WHATSAPP_EXPRESSIVE -> "WhatsApp Pack"
                            IconPackType.ANDROID_17 -> "Android 17"
                            IconPackType.IOS_SF -> "iOS SF"
                        },
                        accentColor = accentColor,
                        textColor = textColor,
                        onClick = onToggleIconPack
                    )

                    // Clipboard Manager
                    ToolbarIconItem(
                        glyph = AppIconGlyph.CLIPBOARD,
                        packType = iconPackType,
                        tint = if (keyboardMode == KeyboardMode.CLIPBOARD_DRAWER) accentColor else textColor,
                        badgeText = null,
                        tooltip = "Clipboard",
                        onClick = onOpenClipboard
                    )

                    // Text Formatting Shortcuts (Bold, Italic, Code, Quote)
                    ToolbarTextFormatGroup(
                        textColor = textColor,
                        theme = theme,
                        onFormat = onFormatText
                    )

                    // Sound On/Off Toggle
                    ToolbarIconItem(
                        glyph = if (isSoundOn) AppIconGlyph.SOUND_ON else AppIconGlyph.SOUND_OFF,
                        packType = iconPackType,
                        tint = if (isSoundOn) accentColor else textColor.copy(alpha = 0.5f),
                        badgeText = null,
                        tooltip = "Sound Toggle",
                        onClick = onToggleSound
                    )

                    // Voice Dictation
                    ToolbarIconItem(
                        glyph = AppIconGlyph.MIC,
                        packType = iconPackType,
                        tint = textColor,
                        badgeText = null,
                        tooltip = "Voice Typing",
                        onClick = onToggleVoiceTyping
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolbarIconItem(
    glyph: AppIconGlyph,
    packType: IconPackType,
    tint: Color,
    badgeText: String?,
    tooltip: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.Transparent,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AppIcon(
                glyph = glyph,
                packType = packType,
                tint = tint,
                size = 19.dp
            )
            if (badgeText != null) {
                Text(
                    text = badgeText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = tint
                )
            }
        }
    }
}

@Composable
private fun ToolbarChipItem(
    label: String,
    accentColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = accentColor.copy(alpha = 0.15f),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

@Composable
private fun ToolbarTextFormatGroup(
    textColor: Color,
    theme: KeyboardThemeType,
    onFormat: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(theme.keyCapAltHex).copy(alpha = 0.5f))
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onFormat("*") },
            modifier = Modifier.size(30.dp)
        ) {
            Text(text = "B", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = textColor)
        }
        IconButton(
            onClick = { onFormat("_") },
            modifier = Modifier.size(30.dp)
        ) {
            Text(text = "I", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontSize = 12.sp, color = textColor)
        }
        IconButton(
            onClick = { onFormat("~") },
            modifier = Modifier.size(30.dp)
        ) {
            Text(text = "S", fontSize = 12.sp, color = textColor)
        }
        IconButton(
            onClick = { onFormat("```") },
            modifier = Modifier.size(30.dp)
        ) {
            Text(text = "<>", fontSize = 11.sp, color = textColor)
        }
    }
}
