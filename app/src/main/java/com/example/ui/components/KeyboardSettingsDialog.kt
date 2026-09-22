package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.audio.MechanicalAudioEngine
import com.example.icons.AppIcon
import com.example.icons.AppIconGlyph
import com.example.model.IconPackType
import com.example.model.KeyboardThemeType
import com.example.model.SwitchType

enum class HeightPreset(val title: String, val heightDp: Float) {
    COMPACT("Compact", 42f),
    STANDARD("Default", 50f),
    TALL("Tall", 56f),
    EXTRA_TALL("Extra Tall", 62f)
}

@Composable
fun KeyboardSettingsDialog(
    currentKeyHeight: Dp,
    currentSwitch: SwitchType,
    currentTheme: KeyboardThemeType,
    currentIconPack: IconPackType,
    soundEngine: MechanicalAudioEngine,
    isSoundOn: Boolean,
    isHapticOn: Boolean,
    isAutocorrectOn: Boolean = false,
    isGlideTypingOn: Boolean = true,
    isImeEnabled: Boolean = false,
    isImeSelected: Boolean = false,
    onUpdateKeyHeight: (Dp) -> Unit,
    onSelectSwitch: (SwitchType) -> Unit,
    onSelectTheme: (KeyboardThemeType) -> Unit,
    onSelectIconPack: (IconPackType) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleHaptic: (Boolean) -> Unit,
    onToggleAutocorrect: (Boolean) -> Unit = {},
    onToggleGlideTyping: (Boolean) -> Unit = {},
    onUpdateVolume: (Float) -> Unit,
    onUpdateHapticStrength: (Float) -> Unit,
    onOpenDefaultKeyboardSetup: () -> Unit,
    onSaveAsDefaultPreset: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var selectedHeight by remember { mutableStateOf(currentKeyHeight.value) }
    var soundEnabled by remember { mutableStateOf(isSoundOn) }
    var hapticEnabled by remember { mutableStateOf(isHapticOn) }
    var autocorrectEnabled by remember { mutableStateOf(isAutocorrectOn) }
    var glideEnabled by remember { mutableStateOf(isGlideTypingOn) }
    var volume by remember { mutableStateOf(soundEngine.volumeLevel) }
    var hapticStrength by remember { mutableStateOf(soundEngine.hapticStrength) }

    val surfaceBg = Color(currentTheme.surfaceHex)
    val accentColor = Color(currentTheme.accentHex)
    val textColor = Color(currentTheme.keyTextHex)
    val cardBg = Color(currentTheme.keyCapHex).copy(alpha = 0.5f)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceBg),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .padding(vertical = 12.dp)
                .testTag("keyboard_settings_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = accentColor.copy(alpha = 0.18f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Keyboard Settings",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Text(
                                text = "Size, Switch Acoustics & Haptics",
                                fontSize = 12.sp,
                                color = textColor.copy(alpha = 0.6f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("close_settings_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = textColor.copy(alpha = 0.7f)
                        )
                    }
                }

                HorizontalDivider(
                    color = textColor.copy(alpha = 0.1f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Scrollable content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // SECTION 1: KEYBOARD SIZE / RESIZING
                    SettingsSection(
                        title = "Keyboard Size & Height",
                        icon = Icons.Default.Height,
                        accentColor = accentColor,
                        textColor = textColor
                    ) {
                        // Preset Height Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HeightPreset.values().forEach { preset ->
                                val isSelected = (selectedHeight - preset.heightDp) in -1.5f..1.5f
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) accentColor else cardBg,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            selectedHeight = preset.heightDp
                                            onUpdateKeyHeight(preset.heightDp.dp)
                                            soundEngine.playKeyPressSound(currentSwitch)
                                        }
                                        .testTag("height_preset_${preset.name.lowercase()}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = preset.title,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.Black else textColor
                                        )
                                        Text(
                                            text = "${preset.heightDp.toInt()}dp",
                                            fontSize = 10.sp,
                                            color = if (isSelected) Color.Black.copy(alpha = 0.7f) else textColor.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Smooth Height Slider
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Custom Key Height",
                                    fontSize = 12.sp,
                                    color = textColor.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "${selectedHeight.toInt()} dp",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor
                                )
                            }
                            Slider(
                                value = selectedHeight,
                                onValueChange = {
                                    selectedHeight = it
                                    onUpdateKeyHeight(it.dp)
                                },
                                onValueChangeFinished = {
                                    soundEngine.playKeyPressSound(currentSwitch)
                                },
                                valueRange = 40f..64f,
                                steps = 23,
                                colors = SliderDefaults.colors(
                                    thumbColor = accentColor,
                                    activeTrackColor = accentColor,
                                    inactiveTrackColor = textColor.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("keyboard_height_slider")
                            )
                        }
                    }

                    // SECTION 2: HAPTIC FEEDBACK (Independent of sound!)
                    SettingsSection(
                        title = "Tactile Haptic Feedback",
                        icon = Icons.Default.Vibration,
                        accentColor = accentColor,
                        textColor = textColor
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = cardBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Keypress Vibration",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textColor
                                        )
                                        Text(
                                            text = "Works even when sound is disabled",
                                            fontSize = 11.sp,
                                            color = Color(0xFF00E676)
                                        )
                                    }
                                    Switch(
                                        checked = hapticEnabled,
                                        onCheckedChange = {
                                            hapticEnabled = it
                                            onToggleHaptic(it)
                                            soundEngine.isHapticEnabled = it
                                            if (it) soundEngine.triggerHaptic(currentSwitch)
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = accentColor,
                                            checkedTrackColor = accentColor.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier.testTag("toggle_haptic_switch")
                                    )
                                }

                                AnimatedVisibility(visible = hapticEnabled) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Vibration Intensity",
                                                fontSize = 12.sp,
                                                color = textColor.copy(alpha = 0.7f)
                                            )
                                            Text(
                                                text = "${(hapticStrength * 100).toInt()}%",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = accentColor
                                            )
                                        }
                                        Slider(
                                            value = hapticStrength,
                                            onValueChange = {
                                                hapticStrength = it
                                                onUpdateHapticStrength(it)
                                                soundEngine.hapticStrength = it
                                            },
                                            onValueChangeFinished = {
                                                soundEngine.triggerHaptic(currentSwitch)
                                            },
                                            valueRange = 0.1f..1.0f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = accentColor,
                                                activeTrackColor = accentColor
                                            )
                                        )
                                        Button(
                                            onClick = { soundEngine.triggerHaptic(currentSwitch) },
                                            colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.2f)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "📳 Test Tactile Haptic Pulse",
                                                color = textColor,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECTION: WORD CORRECTOR & SPELL CHECK
                    SettingsSection(
                        title = "Word Corrector & Auto-Correction",
                        icon = Icons.Default.Spellcheck,
                        accentColor = accentColor,
                        textColor = textColor
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = cardBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "Auto-Correction",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = textColor
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (autocorrectEnabled) Color(0xFF00E676).copy(alpha = 0.18f) else textColor.copy(alpha = 0.1f)
                                            ) {
                                                Text(
                                                    text = if (autocorrectEnabled) "ON" else "OFF",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (autocorrectEnabled) Color(0xFF00E676) else textColor.copy(alpha = 0.5f),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Corrects typos (e.g. 'macanical' → 'mechanical', 'teh' → 'the') when pressing space",
                                            fontSize = 11.5.sp,
                                            color = textColor.copy(alpha = 0.65f),
                                            lineHeight = 15.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = autocorrectEnabled,
                                        onCheckedChange = {
                                            autocorrectEnabled = it
                                            onToggleAutocorrect(it)
                                            soundEngine.playKeyPressSound(currentSwitch)
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = accentColor,
                                            checkedTrackColor = accentColor.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier.testTag("toggle_autocorrect_switch")
                                    )
                                }

                                if (autocorrectEnabled) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = accentColor.copy(alpha = 0.08f),
                                        border = androidx.compose.foundation.BorderStroke(0.8.dp, accentColor.copy(alpha = 0.25f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(text = "✨", fontSize = 13.sp)
                                            Text(
                                                text = "Active: Top corrections are marked with ✨ in the suggestion strip and applied automatically when spacebar is tapped.",
                                                fontSize = 11.sp,
                                                color = textColor.copy(alpha = 0.85f),
                                                lineHeight = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECTION: GLIDE TYPING (Gesture Typing)
                    SettingsSection(
                        title = "Glide Typing (Gesture Input)",
                        icon = Icons.Default.Keyboard,
                        accentColor = accentColor,
                        textColor = textColor
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = cardBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "Continuous Gesture Input",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = textColor
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (glideEnabled) Color(0xFF00E676).copy(alpha = 0.18f) else textColor.copy(alpha = 0.1f)
                                            ) {
                                                Text(
                                                    text = if (glideEnabled) "ON" else "OFF",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (glideEnabled) Color(0xFF00E676) else textColor.copy(alpha = 0.5f),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Glide your finger through letters to type complete words with neon dynamic trail rendering",
                                            fontSize = 11.5.sp,
                                            color = textColor.copy(alpha = 0.65f),
                                            lineHeight = 15.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = glideEnabled,
                                        onCheckedChange = {
                                            glideEnabled = it
                                            onToggleGlideTyping(it)
                                            soundEngine.playKeyPressSound(currentSwitch)
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = accentColor,
                                            checkedTrackColor = accentColor.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier.testTag("toggle_glide_switch")
                                    )
                                }
                            }
                        }
                    }

                    // SECTION 3: SWITCH ACOUSTICS & SOUND
                    SettingsSection(
                        title = "Mechanical Switches & Sound",
                        icon = Icons.Default.VolumeUp,
                        accentColor = accentColor,
                        textColor = textColor
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = cardBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Switch Click Sound",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textColor
                                        )
                                        Text(
                                            text = "PCM synthesis acoustic profile",
                                            fontSize = 11.sp,
                                            color = textColor.copy(alpha = 0.6f)
                                        )
                                    }
                                    Switch(
                                        checked = soundEnabled,
                                        onCheckedChange = {
                                            soundEnabled = it
                                            onToggleSound(it)
                                            soundEngine.isSoundEnabled = it
                                            if (it) soundEngine.playKeyPressSound(currentSwitch)
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = accentColor,
                                            checkedTrackColor = accentColor.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier.testTag("toggle_sound_switch")
                                    )
                                }

                                if (soundEnabled) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Volume",
                                                fontSize = 12.sp,
                                                color = textColor.copy(alpha = 0.7f)
                                            )
                                            Text(
                                                text = "${(volume * 100).toInt()}%",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = accentColor
                                            )
                                        }
                                        Slider(
                                            value = volume,
                                            onValueChange = {
                                                volume = it
                                                onUpdateVolume(it)
                                                soundEngine.volumeLevel = it
                                            },
                                            onValueChangeFinished = {
                                                soundEngine.playKeyPressSound(currentSwitch)
                                            },
                                            valueRange = 0.05f..1.0f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = accentColor,
                                                activeTrackColor = accentColor
                                            )
                                        )
                                    }
                                }

                                // Switch Selector Cards
                                Text(
                                    text = "Select Switch Model",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = textColor.copy(alpha = 0.8f)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    SwitchType.values().forEach { switch ->
                                        val isSelected = switch == currentSwitch
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSelected) Color(switch.accentHex).copy(alpha = 0.25f) else Color(currentTheme.surfaceHex),
                                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(switch.accentHex)) else null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable {
                                                    onSelectSwitch(switch)
                                                    soundEngine.playKeyPressSound(switch)
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(12.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(switch.accentHex))
                                                    )
                                                    Column {
                                                        Text(
                                                            text = switch.title,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = textColor
                                                        )
                                                        Text(
                                                            text = switch.soundProfile,
                                                            fontSize = 10.sp,
                                                            color = textColor.copy(alpha = 0.6f)
                                                        )
                                                    }
                                                }
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color(switch.accentHex),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 4: KEYBOARD THEMES
                    SettingsSection(
                        title = "Keyboard Themes",
                        icon = Icons.Default.Palette,
                        accentColor = accentColor,
                        textColor = textColor
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(KeyboardThemeType.values()) { theme ->
                                val isSelected = theme == currentTheme
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(theme.surfaceHex),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(theme.accentHex)) else null,
                                    modifier = Modifier
                                        .width(130.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            onSelectTheme(theme)
                                            soundEngine.playKeyPressSound(currentSwitch)
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = theme.displayName.split(" ").firstOrNull() ?: theme.displayName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(theme.keyTextHex)
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color(theme.accentHex),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(theme.accentHex)))
                                            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(theme.keyCapHex)))
                                            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(theme.backgroundHex)))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 5: ICON PACKS
                    SettingsSection(
                        title = "Conversation Icon Pack",
                        icon = Icons.Default.Keyboard,
                        accentColor = accentColor,
                        textColor = textColor
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconPackType.values().forEach { pack ->
                                val isSelected = pack == currentIconPack
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) accentColor.copy(alpha = 0.2f) else cardBg,
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, accentColor) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            onSelectIconPack(pack)
                                            soundEngine.playKeyPressSound(currentSwitch)
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        AppIcon(
                                            glyph = AppIconGlyph.EMOJI,
                                            packType = pack,
                                            tint = if (isSelected) accentColor else textColor,
                                            size = 20.dp
                                        )
                                        Text(
                                            text = when (pack) {
                                                IconPackType.WHATSAPP_EXPRESSIVE -> "WhatsApp"
                                                IconPackType.ANDROID_17 -> "Android 17"
                                                IconPackType.IOS_SF -> "iOS SF"
                                                IconPackType.RETRO_PIXEL_95 -> "Retro 95"
                                            },
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = textColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 5.5: SAVE CURRENT PRESET AS DEFAULT
                    var isSaved by remember { mutableStateOf(false) }
                    Button(
                        onClick = {
                            onSaveAsDefaultPreset()
                            isSaved = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSaved) Color(0xFF00E676) else accentColor
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_default_preset_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isSaved) "✓ Default Preset Saved!" else "💾 Save as Default Preset",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSaved) Color.Black else Color.White
                            )
                        }
                    }

                    // SECTION 6: SYSTEM DEFAULT KEYBOARD SHORTCUT
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF00E676).copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onOpenDefaultKeyboardSetup()
                                onDismiss()
                            }
                            .testTag("setup_default_ime_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isImeEnabled && isImeSelected) Color(0xFF00E676) else Color(0xFFFFB300))
                                )
                                Column {
                                    Text(
                                        text = "System Default Keyboard",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                    Text(
                                        text = if (isImeEnabled && isImeSelected) "Active across all Android apps" else "Tap to enable in Android settings",
                                        fontSize = 11.sp,
                                        color = textColor.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    onOpenDefaultKeyboardSetup()
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isImeEnabled && isImeSelected) "Manage" else "Enable",
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    textColor: Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
        content()
    }
}
