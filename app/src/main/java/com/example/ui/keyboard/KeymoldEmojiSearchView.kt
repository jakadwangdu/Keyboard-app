package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EmojiDatabase
import com.example.ui.theme.KeymoldThemeTokens

/**
 * Keymold Emoji Search View (UI1.md spec).
 * - Query bar with real editable text field and clear button
 * - Results row of emojis matching query in real time
 * - Compact letters & numbers keyboard (flex to fill)
 * - Bottom row with Back, Space, Backspace
 * - Results update on every key. Empty query shows first 40 emoji.
 * - Tapping a result inserts it into the message and keeps search open.
 */
@Composable
fun KeymoldEmojiSearchView(
    tokens: KeymoldThemeTokens = KeymoldThemeTokens(),
    onEmojiSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val searchResults = remember(searchQuery) {
        EmojiDatabase.searchEmojis(searchQuery)
    }

    val numbersRow = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val row1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val row2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val row3 = listOf("z", "x", "c", "v", "b", "n", "m")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(tokens.bodyHeight)
            .background(tokens.keyboard)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("keymold_emoji_search")
    ) {
        // 1. Query Bar (36px) - Real interactive BasicTextField
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = tokens.keySecondary,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = tokens.accent,
                    modifier = Modifier.size(16.dp)
                )

                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 13.sp,
                        color = tokens.text,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(tokens.accent),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("emoji_search_input_field"),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search emoji (e.g. smile, heart, pizza, cat)...",
                                fontSize = 12.sp,
                                color = tokens.muted
                            )
                        }
                        innerTextField()
                    }
                )

                if (searchQuery.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(tokens.key)
                            .clickable { searchQuery = "" },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = tokens.text,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 2. Horizontal row of results (44px)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(tokens.key.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            if (searchResults.isEmpty()) {
                Text(
                    text = "No matching emoji for \"$searchQuery\"",
                    fontSize = 12.sp,
                    color = tokens.muted,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    searchResults.forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(tokens.keySecondary.copy(alpha = 0.35f))
                                .clickable { onEmojiSelected(emoji) }
                                .testTag("emoji_result_$emoji"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 22.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 3. Compact Letters & Numbers Keyboard (flex rows to fill remaining height)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Numbers Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                numbersRow.forEach { num ->
                    CompactSearchKey(
                        text = num,
                        tokens = tokens,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 1.5.dp),
                        onClick = { searchQuery += num }
                    )
                }
            }

            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                row1.forEach { letter ->
                    CompactSearchKey(
                        text = letter,
                        tokens = tokens,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 1.5.dp),
                        onClick = { searchQuery += letter }
                    )
                }
            }

            // Row 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                row2.forEach { letter ->
                    CompactSearchKey(
                        text = letter,
                        tokens = tokens,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 1.5.dp),
                        onClick = { searchQuery += letter }
                    )
                }
            }

            // Row 3
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                row3.forEach { letter ->
                    CompactSearchKey(
                        text = letter,
                        tokens = tokens,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 1.5.dp),
                        onClick = { searchQuery += letter }
                    )
                }
            }

            // Bottom row with Back, Space, Backspace
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button to return to emoji drawer
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.keySecondary,
                    modifier = Modifier
                        .width(58.dp)
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onBack() }
                        .testTag("emoji_search_back")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Back",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = tokens.text
                        )
                    }
                }

                // Space button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.key,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { searchQuery += " " }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Space",
                            fontSize = 12.sp,
                            color = tokens.muted
                        )
                    }
                }

                // Backspace button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.keySecondary,
                    modifier = Modifier
                        .width(58.dp)
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            if (searchQuery.isNotEmpty()) {
                                searchQuery = searchQuery.dropLast(1)
                            }
                        }
                        .testTag("emoji_search_backspace")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = "Backspace",
                            tint = tokens.text,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactSearchKey(
    text: String,
    tokens: KeymoldThemeTokens,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(7.dp),
        color = tokens.key,
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(7.dp))
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 15.sp,
                color = tokens.text
            )
        }
    }
}
