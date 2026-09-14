package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import com.example.icons.AppIcon
import com.example.icons.AppIconGlyph
import com.example.model.*
import kotlinx.coroutines.launch

@Composable
fun ConversationChatView(
    messages: List<ChatMessage>,
    activeText: String,
    iconPackType: IconPackType,
    theme: KeyboardThemeType,
    currentSwitch: SwitchType,
    onSendMessage: () -> Unit,
    onOpenAttachments: () -> Unit,
    onOpenEmoji: () -> Unit,
    onClearChat: () -> Unit,
    onReactionClick: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
    onUpdateActiveText: ((String) -> Unit)? = null,
    onCopyMessage: ((String) -> Unit)? = null
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to latest message on update
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val chatBg = Color(theme.chatBackgroundHex)
    val userBubbleColor = Color(theme.userBubbleHex)
    val contactBubbleColor = Color(theme.contactBubbleHex)
    val textColor = Color(theme.keyTextHex)
    val accentColor = Color(theme.accentHex)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(chatBg)
            .testTag("conversation_chat_viewport")
    ) {
        // 1. WhatsApp / Retro Top Conversation App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(theme.surfaceHex))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Contact Avatar (Pixel Mascot or Switch Icon)
                if (theme == KeyboardThemeType.RETRO_95_PIXEL || iconPackType == IconPackType.RETRO_PIXEL_95) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFD700))
                            .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "•‿•",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(theme.accentHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⌨️",
                            fontSize = 18.sp
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (theme == KeyboardThemeType.RETRO_95_PIXEL) "Retro 1995 KeyBoard" else "Alex • Mech Club",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (theme == KeyboardThemeType.RETRO_95_PIXEL) Color.White else textColor
                        )
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF25D366))
                        )
                    }
                    Text(
                        text = "Typing with ${currentSwitch.title.split(" ").firstOrNull()} switches...",
                        fontSize = 11.sp,
                        color = accentColor
                    )
                }
            }

            // Quick Actions: Clear Chat, Icon Pack Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(
                        text = when (iconPackType) {
                            IconPackType.RETRO_PIXEL_95 -> "1995 Pixel"
                            IconPackType.WHATSAPP_EXPRESSIVE -> "WhatsApp"
                            IconPackType.ANDROID_17 -> "Android 17"
                            IconPackType.IOS_SF -> "iOS SF"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onClearChat,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear Chat",
                        tint = if (theme == KeyboardThemeType.RETRO_95_PIXEL) Color.White.copy(alpha = 0.8f) else textColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Divider(color = textColor.copy(alpha = 0.08f), thickness = 0.5.dp)

        // 2. Chat Messages Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // End-to-End Encryption Notice Banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(theme.keyCapHex).copy(alpha = 0.85f),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                AppIcon(
                                    glyph = AppIconGlyph.LOCK_ENCRYPTED,
                                    packType = iconPackType,
                                    tint = accentColor,
                                    size = 14.dp
                                )
                                Text(
                                    text = "Messages are end-to-end encrypted with Gboard Mech acoustics",
                                    fontSize = 10.sp,
                                    color = textColor.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                items(messages, key = { it.id }) { message ->
                    ChatMessageBubble(
                        message = message,
                        iconPackType = iconPackType,
                        theme = theme,
                        userBubbleColor = userBubbleColor,
                        contactBubbleColor = contactBubbleColor,
                        textColor = textColor,
                        accentColor = accentColor,
                        onReactionClick = { reaction -> onReactionClick(message.id, reaction) },
                        onCopyMessage = { if (message.text.isNotEmpty()) onCopyMessage?.invoke(message.text) }
                    )
                }
            }
        }

        // 3. WhatsApp-Style Active Conversation Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(theme.surfaceHex))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Input Container (Beveled Inset on Retro 95, Pill on modern themes)
            val isRetro95 = theme == KeyboardThemeType.RETRO_95_PIXEL
            Surface(
                shape = if (isRetro95) RoundedCornerShape(2.dp) else RoundedCornerShape(24.dp),
                color = if (isRetro95) Color.White else Color(theme.keyCapHex),
                border = if (isRetro95) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF808080)) else null,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Emoji Launcher Button
                    IconButton(
                        onClick = onOpenEmoji,
                        modifier = Modifier.size(28.dp)
                    ) {
                        AppIcon(
                            glyph = AppIconGlyph.EMOJI,
                            packType = iconPackType,
                            tint = if (isRetro95) Color.Black else textColor.copy(alpha = 0.7f),
                            size = 20.dp
                        )
                    }

                    // Live typing text viewport (Supports soft keyboard IME, physical typing, and mechanical keys)
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = activeText,
                            onValueChange = { onUpdateActiveText?.invoke(it) },
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = if (isRetro95) FontWeight.Medium else FontWeight.Normal,
                                color = if (isRetro95) Color.Black else textColor,
                                fontFamily = if (isRetro95) androidx.compose.ui.text.font.FontFamily.Monospace else androidx.compose.ui.text.font.FontFamily.Default
                            ),
                            cursorBrush = SolidColor(if (isRetro95) Color.Black else accentColor),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { onSendMessage() }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("chat_input_text_field"),
                            decorationBox = { innerTextField ->
                                if (activeText.isEmpty()) {
                                    Text(
                                        text = "Type a message...",
                                        fontSize = 14.sp,
                                        color = if (isRetro95) Color(0xFF707070) else textColor.copy(alpha = 0.45f),
                                        fontFamily = if (isRetro95) androidx.compose.ui.text.font.FontFamily.Monospace else androidx.compose.ui.text.font.FontFamily.Default
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                    // Attachments Button (Paperclip)
                    IconButton(
                        onClick = onOpenAttachments,
                        modifier = Modifier.size(28.dp)
                    ) {
                        AppIcon(
                            glyph = AppIconGlyph.ATTACHMENT,
                            packType = iconPackType,
                            tint = if (isRetro95) Color.Black else textColor.copy(alpha = 0.7f),
                            size = 20.dp
                        )
                    }

                    // Camera Icon
                    IconButton(
                        onClick = onOpenAttachments,
                        modifier = Modifier.size(28.dp)
                    ) {
                        AppIcon(
                            glyph = AppIconGlyph.CAMERA,
                            packType = iconPackType,
                            tint = if (isRetro95) Color.Black else textColor.copy(alpha = 0.7f),
                            size = 20.dp
                        )
                    }
                }
            }

            // Send / Mic Button (Retro beveled square on 95, circular on modern)
            if (isRetro95) {
                Button(
                    onClick = onSendMessage,
                    shape = RoundedCornerShape(2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD4D0C8),
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(0.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF404040)),
                    modifier = Modifier.size(44.dp)
                ) {
                    AppIcon(
                        glyph = if (activeText.isNotEmpty()) AppIconGlyph.SEND else AppIconGlyph.MIC,
                        packType = iconPackType,
                        tint = Color.Black,
                        size = 20.dp
                    )
                }
            } else {
                val fabContentColor = if (theme == KeyboardThemeType.WHATSAPP_LIGHT || theme == KeyboardThemeType.MINIMAL_LIGHT) Color.White else Color.White
                FloatingActionButton(
                    onClick = onSendMessage,
                    containerColor = accentColor,
                    contentColor = fabContentColor,
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp)
                ) {
                    if (activeText.isNotEmpty()) {
                        AppIcon(
                            glyph = AppIconGlyph.SEND,
                            packType = iconPackType,
                            tint = Color.White,
                            size = 20.dp
                        )
                    } else {
                        AppIcon(
                            glyph = AppIconGlyph.MIC,
                            packType = iconPackType,
                            tint = Color.White,
                            size = 20.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(
    message: ChatMessage,
    iconPackType: IconPackType,
    theme: KeyboardThemeType,
    userBubbleColor: Color,
    contactBubbleColor: Color,
    textColor: Color,
    accentColor: Color,
    onReactionClick: (String) -> Unit,
    onCopyMessage: () -> Unit
) {
    val bubbleColor = if (message.isMe) userBubbleColor else contactBubbleColor
    val bubbleShape = if (message.isMe) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    }

    var showReactionMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Surface(
                shape = bubbleShape,
                color = bubbleColor,
                modifier = Modifier
                    .clip(bubbleShape)
                    .clickable { showReactionMenu = !showReactionMenu }
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (!message.isMe) {
                        Text(
                            text = message.senderName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }

                    // Render Attachment Card if present
                    if (message.attachmentType != AttachmentType.NONE) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val icon = when (message.attachmentType) {
                                    AttachmentType.PHOTO -> AppIconGlyph.GALLERY
                                    AttachmentType.CAMERA -> AppIconGlyph.CAMERA
                                    AttachmentType.VOICE_NOTE -> AppIconGlyph.MIC
                                    AttachmentType.DOCUMENT -> AppIconGlyph.DOCUMENT
                                    AttachmentType.LOCATION -> AppIconGlyph.LOCATION
                                    AttachmentType.CONTACT -> AppIconGlyph.CONTACT
                                    AttachmentType.POLL -> AppIconGlyph.POLL
                                    AttachmentType.PAYMENT -> AppIconGlyph.PAYMENT
                                    AttachmentType.STICKER -> AppIconGlyph.STICKER
                                    AttachmentType.NONE -> AppIconGlyph.DOCUMENT
                                }
                                AppIcon(
                                    glyph = icon,
                                    packType = iconPackType,
                                    tint = accentColor,
                                    size = 22.dp
                                )
                                Column {
                                    Text(
                                        text = message.attachmentType.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                    if (message.attachmentData != null) {
                                        Text(
                                            text = message.attachmentData,
                                            fontSize = 10.sp,
                                            color = textColor.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Message text
                    if (message.text.isNotEmpty()) {
                        Text(
                            text = message.text,
                            fontSize = 14.sp,
                            color = textColor,
                            lineHeight = 18.sp
                        )
                    }

                    // Timestamp & Status ticks
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = message.formattedTime,
                            fontSize = 10.sp,
                            color = textColor.copy(alpha = 0.6f)
                        )

                        if (message.isMe) {
                            AppIcon(
                                glyph = when (message.status) {
                                    MessageStatus.READ -> AppIconGlyph.CHECK_DOUBLE
                                    MessageStatus.DELIVERED -> AppIconGlyph.CHECK_DOUBLE
                                    MessageStatus.SENT -> AppIconGlyph.CHECK_SINGLE
                                    MessageStatus.PENDING -> AppIconGlyph.CHECK_SINGLE
                                },
                                packType = iconPackType,
                                tint = if (message.status == MessageStatus.READ) Color(0xFF53BDEB) else textColor.copy(alpha = 0.6f),
                                size = 14.dp
                            )
                        }
                    }
                }
            }

            // Reactions Row Badge
            if (message.reactions.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.offset(y = (-6).dp).padding(horizontal = 6.dp)
                ) {
                    message.reactions.forEach { r ->
                        Surface(
                            shape = CircleShape,
                            color = Color(theme.surfaceHex),
                            border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = r,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Quick Reaction & Action Floating Picker (on click)
            if (showReactionMenu) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(theme.surfaceHex),
                    shadowElevation = 6.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.1f)),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("🫠", "😶‍🌫️", "❤️", "😂", "👍", "🔥", "🥹", "🫡").forEach { emoji ->
                            Text(
                                text = emoji,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        onReactionClick(emoji)
                                        showReactionMenu = false
                                    }
                                    .padding(4.dp)
                            )
                        }

                        if (message.text.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(theme.keyCapHex))
                                    .clickable {
                                        onCopyMessage()
                                        showReactionMenu = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Message",
                                    tint = accentColor,
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
