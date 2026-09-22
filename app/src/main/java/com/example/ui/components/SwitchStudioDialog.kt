package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
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
import androidx.compose.ui.window.Dialog
import com.example.audio.MechanicalAudioEngine
import com.example.model.KeyboardThemeType
import com.example.model.SwitchType

@Composable
fun SwitchStudioDialog(
    currentSwitch: SwitchType,
    currentTheme: KeyboardThemeType,
    soundEngine: MechanicalAudioEngine,
    onSelectSwitch: (SwitchType) -> Unit,
    onSaveAsDefaultPreset: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var volume by remember { mutableStateOf(soundEngine.volumeLevel) }
    var hapticStrength by remember { mutableStateOf(soundEngine.hapticStrength) }
    var soundEnabled by remember { mutableStateOf(soundEngine.isSoundEnabled) }
    var hapticEnabled by remember { mutableStateOf(soundEngine.isHapticEnabled) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(currentTheme.surfaceHex)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("switch_studio_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Switch Studio",
                            tint = Color(currentSwitch.accentHex),
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Switch Sound Studio",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(currentTheme.keyTextHex)
                            )
                            Text(
                                text = "Acoustic Mechanical Typing Profiles",
                                fontSize = 11.sp,
                                color = Color(currentTheme.keyTextHex).copy(alpha = 0.6f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(currentTheme.keyCapAltHex))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(currentTheme.keyTextHex),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Divider(color = Color(currentTheme.keyTextHex).copy(alpha = 0.1f))

                // Switch Selection Cards
                Text(
                    text = "SELECT MECHANICAL SWITCH TYPE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(currentTheme.keyTextHex).copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (switch in SwitchType.values()) {
                        val isSelected = switch == currentSwitch
                        val accent = Color(switch.accentHex)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) accent.copy(alpha = 0.18f) else Color(currentTheme.keyCapHex))
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.5.dp,
                                    color = if (isSelected) accent else Color(currentTheme.keyTextHex).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    onSelectSwitch(switch)
                                    soundEngine.playKeyPressSound(switch)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(accent)
                                    )
                                    Text(
                                        text = switch.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(currentTheme.keyTextHex) else Color(currentTheme.keyTextHex).copy(alpha = 0.85f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${switch.feel} • ${switch.soundProfile}",
                                    fontSize = 11.sp,
                                    color = Color(currentTheme.keyTextHex).copy(alpha = 0.6f)
                                )
                            }

                            Button(
                                onClick = {
                                    onSelectSwitch(switch)
                                    soundEngine.playKeyPressSound(switch)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) accent else Color(currentTheme.keyCapAltHex)
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = if (isSelected) "Active" else "Play",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.Black else Color(currentTheme.keyTextHex)
                                )
                            }
                        }
                    }
                }

                // Interactive Test Typing Pad
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(currentTheme.keyCapHex)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Acoustic Test Keycaps (Tap to feel & hear)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(currentTheme.keyTextHex).copy(alpha = 0.7f)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val testKeys = listOf("A", "S", "D", "F", "SPACE")
                            for (k in testKeys) {
                                Button(
                                    onClick = {
                                        soundEngine.playKeyPressSound(currentSwitch)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(currentSwitch.accentHex).copy(alpha = 0.25f)
                                    ),
                                    modifier = Modifier
                                        .weight(if (k == "SPACE") 1.8f else 1f)
                                        .height(44.dp)
                                ) {
                                    Text(
                                        text = k,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(currentTheme.keyTextHex)
                                    )
                                }
                            }
                        }
                    }
                }

                // Audio & Haptic Sliders
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sound Volume: ${(volume * 100).toInt()}%",
                            fontSize = 13.sp,
                            color = Color(currentTheme.keyTextHex)
                        )
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = {
                                soundEnabled = it
                                soundEngine.isSoundEnabled = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(currentSwitch.accentHex),
                                checkedTrackColor = Color(currentSwitch.accentHex).copy(alpha = 0.4f)
                            )
                        )
                    }

                    if (soundEnabled) {
                        Slider(
                            value = volume,
                            onValueChange = {
                                volume = it
                                soundEngine.volumeLevel = it
                            },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(currentSwitch.accentHex),
                                activeTrackColor = Color(currentSwitch.accentHex)
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Haptic Vibration: ${(hapticStrength * 100).toInt()}%",
                            fontSize = 13.sp,
                            color = Color(currentTheme.keyTextHex)
                        )
                        Switch(
                            checked = hapticEnabled,
                            onCheckedChange = {
                                hapticEnabled = it
                                soundEngine.isHapticEnabled = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(currentSwitch.accentHex),
                                checkedTrackColor = Color(currentSwitch.accentHex).copy(alpha = 0.4f)
                            )
                        )
                    }

                    if (hapticEnabled) {
                        Slider(
                            value = hapticStrength,
                            onValueChange = {
                                hapticStrength = it
                                soundEngine.hapticStrength = it
                            },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(currentSwitch.accentHex),
                                activeTrackColor = Color(currentSwitch.accentHex)
                            )
                        )
                    }

                    var isPresetSaved by remember { mutableStateOf(false) }
                    Button(
                        onClick = {
                            onSaveAsDefaultPreset()
                            isPresetSaved = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPresetSaved) Color(0xFF00E676) else Color(currentSwitch.accentHex)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("save_switch_preset_button")
                    ) {
                        Text(
                            text = if (isPresetSaved) "✓ Default Acoustics Saved!" else "💾 Save as Default Preset",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPresetSaved) Color.Black else Color.White
                        )
                    }
                }
            }
        }
    }
}
