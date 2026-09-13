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
import com.example.model.IconPackType
import com.example.model.KeyboardThemeType

enum class EmojiCategoryTab(val title: String, val iconGlyph: AppIconGlyph) {
    ALL("All", AppIconGlyph.EMOJI),
    SMILEYS("Smileys", AppIconGlyph.EMOJI),
    GESTURES("Gestures", AppIconGlyph.REACTION_THUMBS_UP),
    HEARTS("Hearts", AppIconGlyph.REACTION_HEART),
    STICKERS("Stickers", AppIconGlyph.STICKER),
    KAOMOJI("Kaomoji", AppIconGlyph.TEXT_FORMAT)
}

@Composable
fun GboardEmojiStickerDrawer(
    iconPackType: IconPackType,
    theme: KeyboardThemeType,
    onEmojiSelected: (String) -> Unit,
    onBackspace: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    var activeTab by remember { mutableStateOf(EmojiCategoryTab.ALL) }

    // Emoji Kitchen / Special Mashup stickers (matching reference image)
    val emojiKitchenStickers = listOf(
        "✨🔔🔔✨", "👑💖", "💯🥰", "🕶️😎", "🫠🔥", "🚀✨", "🎉🥳", "🤖⚡"
    )

    val recentEmojis = listOf(
        "😀", "♊", "😉", "🫠", "😶‍🌫️", "❤️", "🔥", "👍", "🥹", "🫡"
    )

    val smileysEmojis = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😭", "🥲",
        "🥹", "🫠", "😶‍🌫️", "🫡", "🫣", "🫢", "🫨", "🫥", "🫤", "😊",
        "😇", "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙",
        "😚", "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎",
        "🥸", "🤩", "🥳", "😏", "😒", "😞", "😔", "😟", "😕", "🙁",
        "☹️", "😣", "😖", "😫", "😩", "🥺", "😢", "😤", "😠", "😡"
    )

    val gestureEmojis = listOf(
        "🫶", "🫰", "🫵", "🫱", "🫲", "🫳", "🫴", "👍", "👎", "👊",
        "✊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝", "🙏", "✍️",
        "💅", "🤳", "💪", "🦾", "🦿", "🦵", "🦶", "👂", "🦻", "👃",
        "🧠", "🫀", "🫁", "👋", "🤚", "🖐️", "✋", "🖖", "👌", "🤌"
    )

    val heartsEmojis = listOf(
        "🩷", "🩵", "🩶", "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤",
        "🤍", "🤎", "💔", "❤️‍🔥", "❤️‍🩹", "❣️", "💕", "💞", "💓", "💗",
        "💖", "💘", "💝", "💟", "💌", "💋", "🫶", "👩‍❤️‍👨", "👩‍❤️‍👩", "👨‍❤️‍👨"
    )

    val chatStickers = listOf(
        "⚡ Quick!", "🚀 Shipped", "☕ Coffee time", "🎉 Woohoo!", "🔥 On Fire",
        "✨ Magic", "🤖 Mech Clack", "⌨️ QWERTY", "💡 Idea!", "💯 100%",
        "👌 Perfect", "😴 Goodnight", "🌅 Morning", "🎧 In the zone", "🍕 Pizza time"
    )

    val kaomojiList = listOf(
        "(⁠・⁠∀⁠・⁠)", "(⁠^⁠^⁠)", "(⁠≧⁠▽⁠≦⁠)", "(⁠人⁠ ⁠•͈⁠ᴗ⁠•͈⁠)", "(⁠ʘ⁠ᴗ⁠ʘ⁠✿⁠)",
        "(⁠◕⁠ᴗ⁠◕⁠✿⁠)", "(⁠ʘ⁠д⁠ʘ⁠╬⁠)", "(⁠눈⁠‸⁠눈⁠)", "(⁠ノ⁠ಠ⁠益⁠ಠ⁠)⁠ノ", "¯\\_(ツ)_/¯",
        "(⁠づ⁠｡⁠◕⁠‿⁠‿⁠◕⁠｡⁠)⁠づ", "ʕ⁠っ⁠•⁠ᴥ⁠•⁠ʔ⁠っ", "(⁠つ⁠✧⁠ω⁠✧⁠)⁠つ", "(⁠⊃⁠｡⁠•́⁠‿⁠•̀⁠｡⁠)⁠⊃"
    )

    val bgColor = Color(0xFF000000)
    val cardBg = Color(0xFF14181F)
    val accentColor = Color(theme.accentHex)
    val textColor = Color.White

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp)
            .background(bgColor)
            .testTag("gboard_emoji_drawer")
    ) {
        // 1. Top Emoji Kitchen / Mashup Ribbon (as in user's image)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0C0F))
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            emojiKitchenStickers.forEach { sticker ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = cardBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26303C)),
                    modifier = Modifier
                        .height(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onEmojiSelected(sticker) }
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sticker,
                            fontSize = 20.sp
                        )
                    }
                }
            }

            // Green Next Arrow Button (like screenshot)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00A884))
                    .clickable { activeTab = EmojiCategoryTab.STICKERS },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "More Stickers",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // 2. Category Navigation Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF10141B))
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
                EmojiCategoryTab.values().forEach { tab ->
                    val isSelected = tab == activeTab
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) accentColor.copy(alpha = 0.25f) else Color.Transparent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { activeTab = tab }
                    ) {
                        Text(
                            text = tab.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) accentColor else Color(0xFF9EABB8),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Backspace inside emoji drawer
            IconButton(
                onClick = onBackspace,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        HorizontalDivider(color = Color(0xFF202630), thickness = 0.5.dp)

        // 3. Emojis Viewport
        if (activeTab == EmojiCategoryTab.ALL) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Recents",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8899A6),
                        modifier = Modifier.padding(top = 6.dp, bottom = 4.dp, start = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        recentEmojis.forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable { onEmojiSelected(emoji) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 24.sp)
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Smileys & Emotions",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8899A6),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 4.dp)
                    )
                }

                // Grid of Smileys
                items(smileysEmojis.chunked(7).size) { rowIndex ->
                    val rowItems = smileysEmojis.chunked(7)[rowIndex]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        rowItems.forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .clickable { onEmojiSelected(emoji) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 24.sp)
                            }
                        }
                    }
                }
            }
        } else {
            val currentItems = when (activeTab) {
                EmojiCategoryTab.SMILEYS -> smileysEmojis
                EmojiCategoryTab.GESTURES -> gestureEmojis
                EmojiCategoryTab.HEARTS -> heartsEmojis
                EmojiCategoryTab.STICKERS -> chatStickers
                EmojiCategoryTab.KAOMOJI -> kaomojiList
                else -> smileysEmojis
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(if (activeTab == EmojiCategoryTab.STICKERS || activeTab == EmojiCategoryTab.KAOMOJI) 3 else 7),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(currentItems) { item ->
                    if (activeTab == EmojiCategoryTab.STICKERS || activeTab == EmojiCategoryTab.KAOMOJI) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = cardBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26303C)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onEmojiSelected(item) }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .clickable { onEmojiSelected(item) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item,
                                fontSize = 24.sp
                            )
                        }
                    }
                }
            }
        }

        // Bottom Bar to return to ABC Keyboard
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D1016))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onCloseDrawer,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2632)),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "ABC",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Text(
                text = "Tap any emoji to insert",
                fontSize = 11.sp,
                color = Color(0xFF8899A6)
            )
        }
    }
}

