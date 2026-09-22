package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IconPackType
import com.example.model.KeyboardMode
import com.example.model.KeyboardThemeType

@Composable
fun TextEditingView(
    activeText: String,
    cursorPosition: Int,
    isSelectionActive: Boolean,
    theme: KeyboardThemeType,
    iconPackType: IconPackType,
    keyHeight: Dp = 50.dp,
    onMoveCursorLeft: () -> Unit,
    onMoveCursorRight: () -> Unit,
    onMoveCursorStart: () -> Unit,
    onMoveCursorEnd: () -> Unit,
    onMoveCursorWordLeft: () -> Unit,
    onMoveCursorWordRight: () -> Unit,
    onToggleSelection: () -> Unit,
    onSelectAll: () -> Unit,
    onCut: () -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onBackspace: () -> Unit,
    onDeleteForward: () -> Unit,
    onClearAll: () -> Unit,
    onCloseEditingMode: () -> Unit
) {
    val bgColor = Color(theme.backgroundHex)
    val cardBg = Color(theme.keyCapHex)
    val altCardBg = Color(theme.keyCapAltHex)
    val accentColor = Color(theme.accentHex)
    val textColor = Color(theme.keyTextHex)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(8.dp)
            .testTag("text_editing_view"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header info bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = "Text Editing & Cursor Pad",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }

            // Quick Return to ABC Keyboard
            Button(
                onClick = onCloseEditingMode,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.25f)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text("Done", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accentColor)
            }
        }

        // Action Blocks (Select All, Cut, Copy, Paste, Select Toggle)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            EditingActionButton(
                title = "Select All",
                icon = Icons.Default.SelectAll,
                cardBg = if (isSelectionActive) accentColor.copy(alpha = 0.3f) else altCardBg,
                textColor = textColor,
                weight = 1f,
                onClick = onSelectAll
            )
            EditingActionButton(
                title = if (isSelectionActive) "Selecting..." else "Select",
                icon = Icons.Default.CropFree,
                cardBg = if (isSelectionActive) accentColor else altCardBg,
                textColor = if (isSelectionActive) Color.Black else textColor,
                weight = 1f,
                onClick = onToggleSelection
            )
            EditingActionButton(
                title = "Cut",
                icon = Icons.Default.ContentCut,
                cardBg = altCardBg,
                textColor = textColor,
                weight = 0.8f,
                onClick = onCut
            )
            EditingActionButton(
                title = "Copy",
                icon = Icons.Default.ContentCopy,
                cardBg = altCardBg,
                textColor = textColor,
                weight = 0.8f,
                onClick = onCopy
            )
            EditingActionButton(
                title = "Paste",
                icon = Icons.Default.ContentPaste,
                cardBg = altCardBg,
                textColor = textColor,
                weight = 0.8f,
                onClick = onPaste
            )
        }

        // Navigation D-Pad & Word Jump Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Left Column: Line & Word jumps
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                EditingNavigationKey(
                    label = "⇤ Start of Line",
                    cardBg = cardBg,
                    textColor = textColor,
                    onClick = onMoveCursorStart
                )
                EditingNavigationKey(
                    label = "← Word Left",
                    cardBg = cardBg,
                    textColor = textColor,
                    onClick = onMoveCursorWordLeft
                )
                EditingNavigationKey(
                    label = "Word Right →",
                    cardBg = cardBg,
                    textColor = textColor,
                    onClick = onMoveCursorWordRight
                )
                EditingNavigationKey(
                    label = "End of Line ⇥",
                    cardBg = cardBg,
                    textColor = textColor,
                    onClick = onMoveCursorEnd
                )
            }

            // Center Column: 4-Way Directional Cursor Pad
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = cardBg,
                modifier = Modifier
                    .weight(1.3f)
                    .height(168.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Up Arrow
                    IconButton(
                        onClick = onMoveCursorStart,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Cursor Up",
                            tint = accentColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Left Arrow
                    IconButton(
                        onClick = onMoveCursorLeft,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cursor Left",
                            tint = accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Center Cursor Indicator
                    Surface(
                        shape = CircleShape,
                        color = accentColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Cursor\n${cursorPosition.coerceAtLeast(0)}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                lineHeight = 11.sp
                            )
                        }
                    }

                    // Right Arrow
                    IconButton(
                        onClick = onMoveCursorRight,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Cursor Right",
                            tint = accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Down Arrow
                    IconButton(
                        onClick = onMoveCursorEnd,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Cursor Down",
                            tint = accentColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Right Column: Deletion Controls
            Column(
                modifier = Modifier.weight(0.9f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                EditingNavigationKey(
                    label = "⌫ Backspace",
                    cardBg = altCardBg,
                    textColor = textColor,
                    onClick = onBackspace
                )
                EditingNavigationKey(
                    label = "⌦ Delete",
                    cardBg = altCardBg,
                    textColor = textColor,
                    onClick = onDeleteForward
                )
                EditingNavigationKey(
                    label = "🗑️ Clear All",
                    cardBg = altCardBg,
                    textColor = Color(0xFFFF5252),
                    onClick = onClearAll
                )
            }
        }
    }
}

@Composable
private fun RowScope.EditingActionButton(
    title: String,
    icon: ImageVector,
    cardBg: Color,
    textColor: Color,
    weight: Float,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = cardBg,
        modifier = Modifier
            .weight(weight)
            .height(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
private fun EditingNavigationKey(
    label: String,
    cardBg: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = cardBg,
        modifier = Modifier
            .fillMaxWidth()
            .height(37.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}
