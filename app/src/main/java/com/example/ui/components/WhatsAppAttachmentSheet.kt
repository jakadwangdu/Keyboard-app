package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.example.icons.AppIcon
import com.example.icons.AppIconGlyph
import com.example.model.AttachmentType
import com.example.model.IconPackType
import com.example.model.KeyboardThemeType

data class AttachmentTile(
    val type: AttachmentType,
    val title: String,
    val colorHex: Long,
    val iconGlyph: AppIconGlyph
)

@Composable
fun WhatsAppAttachmentSheet(
    iconPackType: IconPackType,
    theme: KeyboardThemeType,
    onSelectAttachment: (AttachmentType) -> Unit,
    onDismiss: () -> Unit
) {
    val attachmentTiles = listOf(
        AttachmentTile(AttachmentType.DOCUMENT, "Document", 0xFF7F66FF, AppIconGlyph.DOCUMENT),
        AttachmentTile(AttachmentType.CAMERA, "Camera", 0xFFFF2D55, AppIconGlyph.CAMERA),
        AttachmentTile(AttachmentType.PHOTO, "Gallery", 0xFFAC44CF, AppIconGlyph.GALLERY),
        AttachmentTile(AttachmentType.VOICE_NOTE, "Audio", 0xFFFF9500, AppIconGlyph.MIC),
        AttachmentTile(AttachmentType.LOCATION, "Location", 0xFF00A884, AppIconGlyph.LOCATION),
        AttachmentTile(AttachmentType.PAYMENT, "Payment", 0xFF00BFA5, AppIconGlyph.PAYMENT),
        AttachmentTile(AttachmentType.CONTACT, "Contact", 0xFF007AFF, AppIconGlyph.CONTACT),
        AttachmentTile(AttachmentType.POLL, "Poll", 0xFFFFCC00, AppIconGlyph.POLL)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(theme.surfaceHex)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("whatsapp_attachment_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Share Content",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(theme.keyTextHex)
                        )
                        Text(
                            text = "Latest conversation attachments",
                            fontSize = 11.sp,
                            color = Color(theme.keyTextHex).copy(alpha = 0.6f)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(theme.keyCapAltHex))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(theme.keyTextHex),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Divider(color = Color(theme.keyTextHex).copy(alpha = 0.1f))

                // 4x2 Grid of conversation attachment tiles
                val chunkedTiles = attachmentTiles.chunked(4)
                for (row in chunkedTiles) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (tile in row) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        onSelectAttachment(tile.type)
                                        onDismiss()
                                    }
                                    .padding(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(Color(tile.colorHex)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AppIcon(
                                        glyph = tile.iconGlyph,
                                        packType = iconPackType,
                                        tint = Color.White,
                                        size = 24.dp
                                    )
                                }

                                Text(
                                    text = tile.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(theme.keyTextHex)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
