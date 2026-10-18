package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.icons.AppIcon
import com.example.icons.AppIconGlyph
import com.example.model.EmojiCategory
import com.example.model.EmojiDatabase
import com.example.model.IconPackType
import com.example.model.KeyboardThemeType
import com.example.ui.theme.AgePack
import com.example.ui.theme.KeymoldThemeTokens

/**
 * Keymold Emoji Panel (UI1.md spec).
 * - Top row of chips: Search | Everyone | Kids | Teens | Adults | Seniors. Active chip uses accent fill.
 * - Grid: 8 columns, 26px emoji, 46px cells. Seniors pack: 5 columns, 36px emoji, 62px cells.
 * - Bottom row: ABC | category tabs (icon only, 50% opacity, active 100% with key-secondary fill) | Backspace.
 * - A Recent tab appears first once user picked an emoji (max 16, newest first).
 * - Acts on tap so scrolling never types by accident.
 */
@Composable
fun GboardEmojiStickerDrawer(
    iconPackType: IconPackType,
    theme: KeyboardThemeType,
    tokens: KeymoldThemeTokens = KeymoldThemeTokens(),
    recentEmojis: List<String> = emptyList(),
    onEmojiSelected: (String) -> Unit,
    onBackspace: () -> Unit,
    onOpenSearch: () -> Unit = {},
    onCloseDrawer: () -> Unit
) {
    var selectedAgePack by remember { mutableStateOf(AgePack.EVERYONE) }
    var selectedCategoryIndex by remember { mutableStateOf(0) }

    val categories = remember(selectedAgePack, recentEmojis) {
        val packCategories = EmojiDatabase.getCategoriesForPack(selectedAgePack).toMutableList()
        if (recentEmojis.isNotEmpty()) {
            packCategories.add(
                0,
                EmojiCategory(
                    id = "recent",
                    title = "Recent",
                    iconGlyph = AppIconGlyph.REACTION_HEART,
                    emojis = recentEmojis.take(16)
                )
            )
        }
        packCategories
    }

    val activeCategory = categories.getOrNull(selectedCategoryIndex.coerceIn(0, categories.size - 1)) ?: categories.first()
    val isSeniors = selectedAgePack == AgePack.SENIORS

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(tokens.bodyHeight)
            .background(tokens.keyboard)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("keymold_emoji_panel")
    ) {
        // 1. Top row of chips: Search | Everyone | Kids | Teens | Adults | Seniors
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Chip
            Surface(
                shape = CircleShape,
                color = tokens.keySecondary,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onOpenSearch() }
                    .testTag("emoji_search_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = tokens.text,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Search",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = tokens.text
                    )
                }
            }

            // Age Pack Chips
            AgePack.values().forEach { pack ->
                val isSelected = selectedAgePack == pack
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) tokens.accent else tokens.keySecondary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            selectedAgePack = pack
                            selectedCategoryIndex = 0
                        }
                        .testTag("emoji_pack_${pack.name.lowercase()}")
                ) {
                    Text(
                        text = pack.title,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) tokens.textOnAccent else tokens.text,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 2. Emoji Grid (Scrollable area acts on TAP, not pointer down)
        val columns = if (isSeniors) 5 else 8
        val emojiSize = if (isSeniors) 36.sp else 26.sp
        val cellSize = if (isSeniors) 62.dp else 46.dp

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("emoji_grid"),
                contentPadding = PaddingValues(bottom = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(activeCategory.emojis, key = { it }) { emoji ->
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onEmojiSelected(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = emojiSize,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 3. Bottom Row: ABC | Category tabs (icon only, 50% opacity, active 100% with key-secondary fill) | Backspace
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ABC button
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = tokens.keySecondary,
                modifier = Modifier
                    .width(52.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onCloseDrawer() }
                    .testTag("emoji_abc_btn")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "ABC",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = tokens.text
                    )
                }
            }

            // Category Tabs
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                categories.forEachIndexed { index, cat ->
                    val isSelected = selectedCategoryIndex == index
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) tokens.keySecondary else Color.Transparent)
                            .clickable { selectedCategoryIndex = index },
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(
                            glyph = cat.iconGlyph,
                            packType = iconPackType,
                            tint = if (isSelected) tokens.text else tokens.text.copy(alpha = 0.50f),
                            size = 18.dp
                        )
                    }
                }
            }

            // Backspace button
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = tokens.keySecondary,
                modifier = Modifier
                    .width(52.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onBackspace() }
                    .testTag("emoji_backspace_btn")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Backspace",
                        tint = tokens.text,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}
