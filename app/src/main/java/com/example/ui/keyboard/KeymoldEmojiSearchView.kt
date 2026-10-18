package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EmojiDatabase
import com.example.ui.theme.KeymoldThemeTokens

/**
 * Keymold Emoji Search View (UI1.md spec).
 * - Query bar (36px)
 * - One horizontal row of results (44px)
 * - Compact letters keyboard (rows flex to fill)
 * - Bottom row with Back, Space, Backspace.
 * - Results update on every key. Empty query shows first 40 emoji. No match shows "No match".
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
        // 1. Query Bar (36px)
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
                    tint = tokens.muted,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = if (searchQuery.isEmpty()) "Search emoji (e.g. love, lol, mango)" else searchQuery,
                    fontSize = 13.sp,
                    color = if (searchQuery.isEmpty()) tokens.muted else tokens.text,
                    modifier = Modifier.weight(1f)
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

        // 2. One horizontal row of results (44px)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(tokens.key.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            if (searchResults.isEmpty()) {
                Text(
                    text = "No match",
                    fontSize = 13.sp,
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
                                .clickable { onEmojiSelected(emoji) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 3. Compact Letters Keyboard (Rows flex to fill remaining height)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
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
                            .padding(horizontal = 2.dp),
                        onClick = { searchQuery += letter }
                    )
                }
            }

            // Row 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                row2.forEach { letter ->
                    CompactSearchKey(
                        text = letter,
                        tokens = tokens,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp),
                        onClick = { searchQuery += letter }
                    )
                }
            }

            // Row 3
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                row3.forEach { letter ->
                    CompactSearchKey(
                        text = letter,
                        tokens = tokens,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp),
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
                // Back button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.keySecondary,
                    modifier = Modifier
                        .width(58.dp)
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onBack() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Back",
                            fontSize = 13.sp,
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
                        .height(38.dp)
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
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            if (searchQuery.isNotEmpty()) {
                                searchQuery = searchQuery.dropLast(1)
                            }
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = "Backspace",
                            tint = tokens.text,
                            modifier = Modifier.size(17.dp)
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
        shape = RoundedCornerShape(8.dp),
        color = tokens.key,
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 16.sp,
                color = tokens.text
            )
        }
    }
}
