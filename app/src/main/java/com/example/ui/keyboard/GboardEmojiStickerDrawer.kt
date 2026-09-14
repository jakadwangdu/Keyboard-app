package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import com.example.model.EmojiDatabase
import com.example.model.IconPackType
import com.example.model.KeyboardThemeType

@Composable
fun GboardEmojiStickerDrawer(
    iconPackType: IconPackType,
    theme: KeyboardThemeType,
    onEmojiSelected: (String) -> Unit,
    onBackspace: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    var selectedCategoryId by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }

    val emojiKitchenStickers = remember {
        listOf(
            "✨🔔🔔✨", "👑💖", "💯🥰", "🕶️😎", "🫠🔥", "🚀✨", "🎉🥳", "🤖⚡",
            "🩷✨", "🫨⚡", "🫧💖", "🍋‍🟩🍹", "🫶❤️", "🪩🕺", "🪿⚡", "🫡🔥"
        )
    }

    val kaomojiList = remember {
        listOf(
            "(⁠・⁠∀⁠・⁠)", "(⁠^⁠^⁠)", "(⁠≧⁠▽⁠≦⁠)", "(⁠人⁠ ⁠•͈⁠ᴗ⁠•͈⁠)", "(⁠ʘ⁠ᴗ⁠ʘ⁠✿⁠)",
            "(⁠◕⁠ᴗ⁠◕⁠✿⁠)", "(⁠ʘ⁠д⁠ʘ⁠╬⁠)", "(⁠눈⁠‸⁠눈⁠)", "(⁠ノ⁠ಠ⁠益⁠ಠ⁠)⁠ノ", "¯\\_(ツ)_/¯",
            "(⁠づ⁠｡⁠◕⁠‿⁠‿⁠◕⁠｡⁠)⁠づ", "ʕ⁠っ⁠•⁠ᴥ⁠•⁠ʔ⁠っ", "(⁠つ⁠✧⁠ω⁠✧⁠)⁠つ", "(⁠⊃⁠｡⁠•́⁠‿⁠•̀⁠｡⁠)⁠⊃"
        )
    }

    val bgColor = Color(theme.surfaceHex)
    val cardBg = Color(theme.keyCapHex)
    val accentColor = Color(theme.accentHex)
    val textColor = Color(theme.keyTextHex)

    // Filter emojis if search query is active
    val searchResults = remember(searchQuery) {
        if (searchQuery.isBlank()) emptyList()
        else {
            val q = searchQuery.trim().lowercase()
            EmojiDatabase.allCategories
                .filter { it.title.lowercase().contains(q) || it.id.contains(q) }
                .flatMap { it.emojis }
                .distinct()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(bgColor)
            .testTag("gboard_emoji_drawer")
    ) {
        // 1. Emoji Kitchen / Mashup Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor.copy(alpha = 0.95f))
                .padding(horizontal = 8.dp, vertical = 5.dp)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Close / Return to ABC Button
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = cardBg,
                modifier = Modifier
                    .height(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onCloseDrawer() }
            ) {
                Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), contentAlignment = Alignment.Center) {
                    Text("ABC", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accentColor)
                }
            }

            emojiKitchenStickers.forEach { sticker ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = cardBg,
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onEmojiSelected(sticker) }
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = sticker, fontSize = 16.sp)
                    }
                }
            }
        }

        // 2. Category Tabs & Backspace
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBg.copy(alpha = 0.5f))
                .padding(horizontal = 6.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "All" tab
                val isAllSelected = selectedCategoryId == "all"
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isAllSelected) accentColor.copy(alpha = 0.2f) else Color.Transparent,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { selectedCategoryId = "all"; searchQuery = "" }
                ) {
                    Text(
                        text = "All Emojis",
                        fontSize = 11.5.sp,
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isAllSelected) accentColor else textColor.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Database categories
                EmojiDatabase.allCategories.forEach { category ->
                    val isSelected = selectedCategoryId == category.id
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) accentColor.copy(alpha = 0.2f) else Color.Transparent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedCategoryId = category.id; searchQuery = "" }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = category.emojis.firstOrNull() ?: "😃",
                                fontSize = 13.sp
                            )
                            Text(
                                text = category.title.split(" ").first(),
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) accentColor else textColor.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Kaomoji Tab
                val isKaomoji = selectedCategoryId == "kaomoji"
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isKaomoji) accentColor.copy(alpha = 0.2f) else Color.Transparent,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { selectedCategoryId = "kaomoji"; searchQuery = "" }
                ) {
                    Text(
                        text = "(⁠^⁠^⁠) Kaomoji",
                        fontSize = 11.5.sp,
                        fontWeight = if (isKaomoji) FontWeight.Bold else FontWeight.Medium,
                        color = if (isKaomoji) accentColor else textColor.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Quick backspace in emoji drawer
            IconButton(
                onClick = onBackspace,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = textColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        HorizontalDivider(color = textColor.copy(alpha = 0.08f), thickness = 0.5.dp)

        // 3. Emojis Grid Viewport
        if (searchQuery.isNotBlank()) {
            // Search Results Grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 40.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(searchResults) { emoji ->
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onEmojiSelected(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                    }
                }
            }
        } else if (selectedCategoryId == "kaomoji") {
            // Kaomoji Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(kaomojiList) { kaomoji ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = cardBg,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onEmojiSelected(kaomoji) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = kaomoji, fontSize = 13.sp, color = textColor)
                        }
                    }
                }
            }
        } else if (selectedCategoryId == "all") {
            // All Categories in a Continuous Smooth Scrolling List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EmojiDatabase.allCategories.forEach { category ->
                    item(key = category.id) {
                        Column {
                            Text(
                                text = category.title,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor.copy(alpha = 0.6f),
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 4.dp)
                            )
                            // Flow of emojis
                            val rows = category.emojis.chunked(8)
                            rows.forEach { rowEmojis ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    rowEmojis.forEach { emoji ->
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { onEmojiSelected(emoji) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = emoji, fontSize = 23.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Single Category Grid
            val currentCategory = EmojiDatabase.allCategories.find { it.id == selectedCategoryId }
            val emojis = currentCategory?.emojis ?: emptyList()
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 38.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(emojis) { emoji ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onEmojiSelected(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}
