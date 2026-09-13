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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.IconPackType
import com.example.model.KeyboardThemeType

@Composable
fun ThemePickerSheet(
    currentIconPack: IconPackType,
    currentTheme: KeyboardThemeType,
    onSelectIconPack: (IconPackType) -> Unit,
    onSelectTheme: (KeyboardThemeType) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(currentTheme.surfaceHex)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("theme_picker_sheet")
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
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Themes",
                            tint = Color(currentTheme.accentHex),
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Packs & Themes",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(currentTheme.keyTextHex)
                            )
                            Text(
                                text = "Conversation Icon Packs & Mechanical Styles",
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

                // Icon Packs Section (WhatsApp vs Android 17 vs iOS SF)
                Text(
                    text = "PRELOADED CONVERSATION ICON PACKS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(currentTheme.keyTextHex).copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (pack in IconPackType.values()) {
                        val isSelected = pack == currentIconPack
                        val packAccent = Color(pack.brandColorHex)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) packAccent.copy(alpha = 0.15f) else Color(currentTheme.keyCapHex))
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.5.dp,
                                    color = if (isSelected) packAccent else Color(currentTheme.keyTextHex).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onSelectIconPack(pack) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(packAccent)
                                )
                                Column {
                                    Text(
                                        text = pack.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(currentTheme.keyTextHex)
                                    )
                                    Text(
                                        text = pack.subtitle,
                                        fontSize = 10.5.sp,
                                        color = Color(currentTheme.keyTextHex).copy(alpha = 0.6f)
                                    )
                                }
                            }

                            if (isSelected) {
                                Text(
                                    text = "Active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = packAccent
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Keyboard Theme Palettes Section
                Text(
                    text = "KEYBOARD THEMES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(currentTheme.keyTextHex).copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (theme in KeyboardThemeType.values()) {
                        val isSelected = theme == currentTheme
                        val themeAccent = Color(theme.accentHex)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) themeAccent.copy(alpha = 0.15f) else Color(currentTheme.keyCapHex))
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.5.dp,
                                    color = if (isSelected) themeAccent else Color(currentTheme.keyTextHex).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onSelectTheme(theme) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(Color(theme.backgroundHex))
                                            .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(Color(theme.keyCapHex))
                                            .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(themeAccent)
                                    )
                                }

                                Text(
                                    text = theme.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = Color(currentTheme.keyTextHex)
                                )
                            }

                            if (isSelected) {
                                Text(
                                    text = "Applied",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeAccent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
