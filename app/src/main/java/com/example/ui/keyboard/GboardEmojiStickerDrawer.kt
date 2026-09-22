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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.EmojiPredictionEngine
import com.example.engine.MediaType
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
    var mediaTypeFilter by remember { mutableStateOf<MediaType?>(null) }

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
    val altCardBg = Color(theme.keyCapAltHex)
    val accentColor = Color(theme.accentHex)
    val textColor = Color(theme.keyTextHex)

    // Unified media search results across Emojis, GIFs, Stickers
    val unifiedSearchResults = remember(searchQuery, mediaTypeFilter) {
        if (searchQuery.isBlank()) emptyList()
        else {
            val all = EmojiPredictionEngine.searchUnifiedMedia(searchQuery)
            if (mediaTypeFilter != null) all.filter { it.type == mediaTypeFilter } else all
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp)
            .background(bgColor)
            .testTag("gboard_emoji_drawer")
    ) {
        // 1. Unified Search Input Bar (Gboard Top Search)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBg.copy(alpha = 0.5f))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Return to ABC Button
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = cardBg,
                modifier = Modifier
                    .height(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onCloseDrawer() }
            ) {
                Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), contentAlignment = Alignment.Center) {
                    Text("ABC", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accentColor)
                }
            }

            // Search Bar Input Field
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = altCardBg,
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = textColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(
                            color = textColor,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(accentColor),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search Emojis, GIFs & Stickers (e.g. stress, pickle, fire)...",
                                    color = textColor.copy(alpha = 0.45f),
                                    fontSize = 11.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = textColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Backspace Key
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

        // 2. Category / Media Switcher Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBg.copy(alpha = 0.3f))
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Unicode 18.0 Tag
                val isU18 = selectedCategoryId == "unicode18"
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isU18) accentColor else accentColor.copy(alpha = 0.15f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { selectedCategoryId = "unicode18"; searchQuery = "" }
                ) {
                    Text(
                        text = "✨ Unicode 18.0",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isU18) Color.Black else accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // All Emojis Tab
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

                // GIFs Reaction Tab
                val isGifs = selectedCategoryId == "gifs"
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isGifs) accentColor.copy(alpha = 0.2f) else Color.Transparent,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { selectedCategoryId = "gifs"; searchQuery = "" }
                ) {
                    Text(
                        text = "GIFs",
                        fontSize = 11.5.sp,
                        fontWeight = if (isGifs) FontWeight.Bold else FontWeight.Medium,
                        color = if (isGifs) accentColor else textColor.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Database categories
                EmojiDatabase.allCategories.filter { it.id != "unicode18" }.forEach { category ->
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
        }

        HorizontalDivider(color = textColor.copy(alpha = 0.08f), thickness = 0.5.dp)

        // 3. Grid / Content Viewport
        if (searchQuery.isNotBlank()) {
            // Unified Search Results (Emojis + GIFs + Stickers)
            if (unifiedSearchResults.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No matching media found for '$searchQuery'", color = textColor.copy(alpha = 0.6f), fontSize = 13.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 75.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(unifiedSearchResults) { result ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = cardBg,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onEmojiSelected(result.content) }
                        ) {
                            Column(
                                modifier = Modifier.padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (result.type == MediaType.GIF) "GIF" else result.content,
                                    fontSize = if (result.type == MediaType.EMOJI) 24.sp else 14.sp
                                )
                                Text(
                                    text = result.title,
                                    fontSize = 9.sp,
                                    color = textColor.copy(alpha = 0.6f),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        } else if (selectedCategoryId == "gifs") {
            // GIFs reaction catalog
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(EmojiPredictionEngine.gifsCatalog) { gif ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = cardBg,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onEmojiSelected("[GIF: ${gif.title}]") }
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🎬 GIF", fontSize = 10.sp, color = accentColor, fontWeight = FontWeight.Bold)
                            Text(text = gif.title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = textColor)
                        }
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
        } else if (selectedCategoryId == "all" || selectedCategoryId == "unicode18") {
            // All Categories or Unicode 18
            val categoriesToShow = if (selectedCategoryId == "unicode18") {
                EmojiDatabase.allCategories.filter { it.id == "unicode18" }
            } else {
                EmojiDatabase.allCategories
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categoriesToShow.forEach { category ->
                    item(key = category.id) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 4.dp)
                            ) {
                                Text(
                                    text = category.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (category.id == "unicode18") accentColor else textColor.copy(alpha = 0.7f)
                                )
                                if (category.id == "unicode18") {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = accentColor.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "Unicode 18.0",
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = accentColor,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                        )
                                    }
                                }
                            }
                            // Clean grid flow with 7 items per row
                            val rows = category.emojis.chunked(7)
                            rows.forEach { rowEmojis ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    rowEmojis.forEach { emoji ->
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable { onEmojiSelected(emoji) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = emoji,
                                                fontSize = 24.sp
                                            )
                                        }
                                    }
                                    // Fill empty slots so items don't stretch
                                    if (rowEmojis.size < 7) {
                                        repeat(7 - rowEmojis.size) {
                                            Spacer(modifier = Modifier.size(44.dp))
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
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(emojis) { emoji ->
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onEmojiSelected(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }
}

