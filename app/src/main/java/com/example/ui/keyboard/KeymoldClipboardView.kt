package com.example.ui.keyboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClipboardItem
import com.example.ui.theme.KeymoldThemeTokens

@Composable
fun KeymoldClipboardView(
    tokens: KeymoldThemeTokens = KeymoldThemeTokens(),
    clipboardList: List<ClipboardItem>,
    onPasteItem: (String) -> Unit,
    onAddClip: (String) -> Unit = {},
    onTogglePin: (Long) -> Unit = {},
    onDeleteItem: (Long) -> Unit = {},
    onClearUnpinned: () -> Unit = {},
    onSyncSystemClipboard: () -> Unit = {},
    onCloseDrawer: () -> Unit
) {
    var showAddInput by remember { mutableStateOf(false) }
    var newClipText by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    // Auto-sync when entering clipboard drawer
    LaunchedEffect(Unit) {
        onSyncSystemClipboard()
    }

    val filteredList = remember(clipboardList, searchQuery) {
        if (searchQuery.isBlank()) {
            clipboardList
        } else {
            clipboardList.filter { it.text.contains(searchQuery, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(tokens.bodyHeight)
            .background(tokens.keyboard)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("keymold_clipboard_drawer")
    ) {
        // 1. Top Header Row: ABC return | Title + count | Sync | Clear | Add
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Return to ABC button
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = tokens.keySecondary,
                modifier = Modifier
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onCloseDrawer() }
                    .testTag("clipboard_abc_btn")
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ABC",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = tokens.text
                    )
                }
            }

            // Title & Count
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = null,
                    tint = tokens.accent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Clipboard (${clipboardList.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.text
                )
            }

            // Action Buttons (Sync, Add, Clear)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Sync with Android system clipboard
                IconButton(
                    onClick = onSyncSystemClipboard,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sync",
                        tint = tokens.accent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Add new manual clip
                IconButton(
                    onClick = { showAddInput = !showAddInput },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (showAddInput) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Add Clip",
                        tint = tokens.text,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Clear unpinned items
                if (clipboardList.any { !it.isPinned }) {
                    IconButton(
                        onClick = onClearUnpinned,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Unpinned",
                            tint = tokens.muted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 2. Add Clip Input Bar (if open)
        if (showAddInput) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = tokens.keySecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BasicTextField(
                        value = newClipText,
                        onValueChange = { newClipText = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                            color = tokens.text
                        ),
                        cursorBrush = SolidColor(tokens.accent),
                        modifier = Modifier.weight(1f),
                        decorationBox = { inner ->
                            if (newClipText.isEmpty()) {
                                Text(
                                    text = "Type text to save to clipboard...",
                                    fontSize = 12.sp,
                                    color = tokens.muted
                                )
                            }
                            inner()
                        }
                    )

                    if (newClipText.isNotBlank()) {
                        Surface(
                            shape = CircleShape,
                            color = tokens.accent,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onAddClip(newClipText.trim())
                                    newClipText = ""
                                    showAddInput = false
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Save",
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 3. Clipboard Items List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPasteOff,
                        contentDescription = null,
                        tint = tokens.muted.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Clipboard is empty",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = tokens.muted
                    )
                    Text(
                        text = "Copied text will appear here ready to paste with one tap.",
                        fontSize = 11.sp,
                        color = tokens.muted.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onSyncSystemClipboard,
                        colors = ButtonDefaults.buttonColors(containerColor = tokens.keySecondary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = "Sync Phone Clipboard",
                            fontSize = 11.sp,
                            color = tokens.text
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (item.isPinned) tokens.accent.copy(alpha = 0.14f) else tokens.key,
                        border = if (item.isPinned) BorderStroke(1.dp, tokens.accent.copy(alpha = 0.45f)) else BorderStroke(0.5.dp, tokens.keySecondary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onPasteItem(item.text)
                            }
                            .testTag("clip_item_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.text,
                                    fontSize = 13.sp,
                                    color = tokens.text,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (item.isPinned) {
                                    Text(
                                        text = "PINNED",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = tokens.accent,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                // Pin Button
                                IconButton(
                                    onClick = { onTogglePin(item.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (item.isPinned) Icons.Default.PushPin else Icons.Default.OutlinedFlag,
                                        contentDescription = "Pin",
                                        tint = if (item.isPinned) tokens.accent else tokens.muted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }

                                // Delete Button
                                IconButton(
                                    onClick = { onDeleteItem(item.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = tokens.muted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
