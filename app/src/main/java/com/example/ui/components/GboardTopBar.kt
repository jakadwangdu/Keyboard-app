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
    onOpenTranslate: () -> Unit = {},
    onOpenTextEditing: () -> Unit = {},
    onCycleLayoutMode: () -> Unit = {},
    onOpenDefaultKeyboardSetup: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    var isToolbarExpanded by remember { mutableStateOf(false) }
    val barBg = Color(theme.surfaceHex)
    val accentColor = Color(theme.accentHex)
    val textColor = Color(theme.keyTextHex)

    // Contextual predicted emojis based on last typed word
    val lastWord = remember(activeText) {
        activeText.trim().split(" ").lastOrNull() ?: ""
    }
    val contextualEmojis = remember(lastWord) {
        com.example.engine.EmojiPredictionEngine.predictEmojis(lastWord)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(barBg)
            .testTag("gboard_top_bar")
    ) {
        // 1. Gboard Predictive Word Suggestion & Unicode 18 Strip (when not expanded into tools menu)
        if (!isToolbarExpanded && !isVoiceTyping) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expand / Collapse Chevron Button (">" toggle as specified in PRD 3.3)
                IconButton(
                    onClick = { isToolbarExpanded = !isToolbarExpanded },
                    modifier = Modifier.size(36.dp)
                ) {
                    AppIcon(
                        glyph = AppIconGlyph.CHEVRON_DOWN,
                        packType = iconPackType,
                        tint = accentColor,
                        size = 20.dp
                    )
                }

                // Suggestions & contextual emoji list
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState())
                        .padding(end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Contextual Emojis (e.g. stress -> 🫠, pickle -> 🥒)
                    contextualEmojis.forEach { emoji ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = accentColor.copy(alpha = 0.18f),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, accentColor.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSuggestionClick(emoji) }
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text(text = emoji, fontSize = 16.sp)
                            }
                        }
                    }

                    // Word suggestions & Autocorrect matches
                    if (suggestions.isEmpty() && contextualEmojis.isEmpty()) {
                        Text(
                            text = "Clacksy • Type naturally or swipe to glide",
                            fontSize = 12.sp,
                            color = textColor.copy(alpha = 0.45f),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    suggestions.forEachIndexed { index, word ->
                        val isAutocorrectMatch = index == 0 && lastWord.isNotBlank() && !word.equals(lastWord, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isAutocorrectMatch) accentColor.copy(alpha = 0.22f) else Color(theme.keyCapAltHex).copy(alpha = 0.7f),
                            border = if (isAutocorrectMatch) androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.6f)) else null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSuggestionClick(word) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                if (isAutocorrectMatch) {
                                    Text(
                                        text = "✨",
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = word,
                                    fontSize = 13.sp,
                                    fontWeight = if (isAutocorrectMatch) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isAutocorrectMatch) accentColor else textColor
                                )
                            }
                        }
                    }
                }

                // Voice Dictation quick mic on the far right of suggestion strip
                IconButton(
                    onClick = onToggleVoiceTyping,
                    modifier = Modifier.size(36.dp)
                ) {
                    AppIcon(
                        glyph = AppIconGlyph.MIC,
                        packType = iconPackType,
                        tint = textColor.copy(alpha = 0.8f),
                        size = 18.dp
                    )
                }
            }
            HorizontalDivider(color = textColor.copy(alpha = 0.08f), thickness = 0.5.dp)
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
                // 1995 Pixel Desktop Classic Utility Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .background(Color(0xFF000080))
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Collapse back to typing bar
                        IconButton(
                            onClick = { isToolbarExpanded = false },
                            modifier = Modifier.size(30.dp)
                        ) {
                            AppIcon(
                                glyph = AppIconGlyph.CHEVRON_DOWN,
                                packType = iconPackType,
                                tint = Color.White,
                                size = 18.dp
                            )
                        }

                        // Switch Studio
                        IconButton(
                            onClick = onOpenSwitchStudio,
                            modifier = Modifier.size(30.dp)
                        ) {
                            AppIcon(
                                glyph = AppIconGlyph.MASCOT_ROBOT,
                                packType = iconPackType,
                                tint = Color(0xFFFFD700),
                                size = 20.dp
                            )
                        }

                        // Clipboard
                        IconButton(
                            onClick = onOpenClipboard,
                            modifier = Modifier.size(30.dp)
                        ) {
                            AppIcon(
                                glyph = AppIconGlyph.FLOPPY_DISK,
                                packType = iconPackType,
                                tint = Color.White,
                                size = 18.dp
                            )
                        }

                        // Themes & Settings
                        IconButton(
                            onClick = onOpenThemePicker,
                            modifier = Modifier.size(30.dp)
                        ) {
                            AppIcon(
                                glyph = AppIconGlyph.THEME,
                                packType = iconPackType,
                                tint = Color(0xFF00FFFF),
                                size = 18.dp
                            )
                        }

                        // Text Edit
                        IconButton(
                            onClick = onOpenTextEditing,
                            modifier = Modifier.size(30.dp)
                        ) {
                            AppIcon(
                                glyph = AppIconGlyph.TEXT_EDIT,
                                packType = iconPackType,
                                tint = Color(0xFFC0C0C0),
                                size = 18.dp
                            )
                        }

                        // Translate
                        IconButton(
                            onClick = onOpenTranslate,
                            modifier = Modifier.size(30.dp)
                        ) {
                            AppIcon(
                                glyph = AppIconGlyph.TRANSLATE,
                                packType = iconPackType,
                                tint = Color(0xFFFFD700),
                                size = 18.dp
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Sound toggle
                        IconButton(
                            onClick = onToggleSound,
                            modifier = Modifier.size(30.dp)
                        ) {
                            AppIcon(
                                glyph = if (isSoundOn) AppIconGlyph.SOUND_ON else AppIconGlyph.SOUND_OFF,
                                packType = iconPackType,
                                tint = if (isSoundOn) Color(0xFF00FF00) else Color(0xFF808080),
                                size = 18.dp
                            )
                        }

                        // Settings
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.size(30.dp)
                        ) {
                            AppIcon(
                                glyph = AppIconGlyph.SETTINGS,
                                packType = iconPackType,
                                tint = Color.White,
                                size = 18.dp
                            )
                        }
                    }
                }
            } else {
                // 3. Clean Gboard Quick Access Toolbar Ribbon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Close / Return to Suggestion Bar
                    IconButton(
                        onClick = { isToolbarExpanded = false },
                        modifier = Modifier.size(32.dp)
                    ) {
                        AppIcon(
                            glyph = AppIconGlyph.CLEAR,
                            packType = iconPackType,
                            tint = textColor.copy(alpha = 0.65f),
                            size = 18.dp
                        )
                    }

                    // 1. Translate
                    ToolbarIconItem(
                        glyph = AppIconGlyph.TRANSLATE,
                        packType = iconPackType,
                        tint = if (keyboardMode == KeyboardMode.TRANSLATE) accentColor else textColor,
                        label = "Translate",
                        isActive = keyboardMode == KeyboardMode.TRANSLATE,
                        accentColor = accentColor,
                        onClick = onOpenTranslate
                    )

                    // 2. Text Editing Pad
                    ToolbarIconItem(
                        glyph = AppIconGlyph.TEXT_EDIT,
                        packType = iconPackType,
                        tint = if (keyboardMode == KeyboardMode.TEXT_EDITING) accentColor else textColor,
                        label = "Edit",
                        isActive = keyboardMode == KeyboardMode.TEXT_EDITING,
                        accentColor = accentColor,
                        onClick = onOpenTextEditing
                    )

                    // 3. Clipboard History
                    ToolbarIconItem(
                        glyph = AppIconGlyph.CLIPBOARD,
                        packType = iconPackType,
                        tint = if (keyboardMode == KeyboardMode.CLIPBOARD_DRAWER) accentColor else textColor,
                        label = "Clipboard",
                        isActive = keyboardMode == KeyboardMode.CLIPBOARD_DRAWER,
                        accentColor = accentColor,
                        onClick = onOpenClipboard
                    )

                    // 4. Themes
                    ToolbarIconItem(
                        glyph = AppIconGlyph.THEME,
                        packType = iconPackType,
                        tint = textColor,
                        label = "Themes",
                        isActive = false,
                        accentColor = accentColor,
                        onClick = onOpenThemePicker
                    )

                    // 5. Mechanical Switch Studio
                    ToolbarIconItem(
                        glyph = AppIconGlyph.SWITCH_STUDIO,
                        packType = iconPackType,
                        tint = accentColor,
                        label = currentSwitch.title.split(" ").firstOrNull() ?: "Switch",
                        isActive = false,
                        accentColor = accentColor,
                        onClick = onOpenSwitchStudio
                    )

                    // 6. One-Handed / Floating Layout
                    ToolbarIconItem(
                        glyph = AppIconGlyph.ONE_HANDED,
                        packType = iconPackType,
                        tint = textColor,
                        label = "Layout",
                        isActive = false,
                        accentColor = accentColor,
                        onClick = onCycleLayoutMode
                    )

                    // 7. Settings
                    ToolbarIconItem(
                        glyph = AppIconGlyph.SETTINGS,
                        packType = iconPackType,
                        tint = textColor,
                        label = "Settings",
                        isActive = false,
                        accentColor = accentColor,
                        onClick = onOpenSettings
                    )

                    // 8. Sound Toggle
                    ToolbarIconItem(
                        glyph = if (isSoundOn) AppIconGlyph.SOUND_ON else AppIconGlyph.SOUND_OFF,
                        packType = iconPackType,
                        tint = if (isSoundOn) accentColor else textColor.copy(alpha = 0.5f),
                        label = if (isSoundOn) "Sound On" else "Muted",
                        isActive = isSoundOn,
                        accentColor = accentColor,
                        onClick = onToggleSound
                    )

                    // 9. Voice Dictation
                    ToolbarIconItem(
                        glyph = AppIconGlyph.MIC,
                        packType = iconPackType,
                        tint = textColor,
                        label = "Voice",
                        isActive = isVoiceTyping,
                        accentColor = accentColor,
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
    label: String,
    isActive: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isActive) accentColor.copy(alpha = 0.18f) else Color.Transparent,
        border = if (isActive) androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)) else null,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            AppIcon(
                glyph = glyph,
                packType = packType,
                tint = if (isActive) accentColor else tint,
                size = 18.dp
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive) accentColor else tint
            )
        }
    }
}
