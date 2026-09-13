package com.example.icons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IconPackType

enum class AppIconGlyph {
    BACKSPACE,
    SEND,
    SHIFT,
    ATTACHMENT,
    EMOJI,
    STICKER,
    GIF,
    MIC,
    CAMERA,
    GALLERY,
    DOCUMENT,
    LOCATION,
    CONTACT,
    POLL,
    PAYMENT,
    CLIPBOARD,
    TRANSLATE,
    TEXT_FORMAT,
    SETTINGS,
    SEARCH,
    THEME,
    SWITCH_STUDIO,
    SOUND_ON,
    SOUND_OFF,
    CHECK_SINGLE,
    CHECK_DOUBLE,
    LOCK_ENCRYPTED,
    GLOBE,
    CLEAR,
    COPY,
    FORMAT_BOLD,
    FORMAT_ITALIC,
    FORMAT_CODE,
    FORMAT_QUOTE,
    REACTION_HEART,
    REACTION_LAUGH,
    REACTION_THUMBS_UP,
    REACTION_FIRE,
    REACTION_WOW,
    REACTION_SAD,
    REACTION_PRAY,
    PUZZLE,
    FLOPPY_DISK,
    HEX_NUT,
    MASCOT_ROBOT,
    CHEVRON_DOWN,
    ENTER_ARROW
}

@Composable
fun AppIcon(
    glyph: AppIconGlyph,
    packType: IconPackType,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    size: Dp = 22.dp
) {
    val actualTint = if (tint == Color.Unspecified) MaterialTheme.colorScheme.onSurface else tint

    when (packType) {
        IconPackType.RETRO_PIXEL_95 -> {
            RetroPixelIcon(glyph = glyph, tint = actualTint, size = size, modifier = modifier)
        }
        IconPackType.WHATSAPP_EXPRESSIVE -> {
            WhatsAppIcon(glyph = glyph, tint = actualTint, size = size, modifier = modifier)
        }
        IconPackType.ANDROID_17 -> {
            Android17Icon(glyph = glyph, tint = actualTint, size = size, modifier = modifier)
        }
        IconPackType.IOS_SF -> {
            IosSfIcon(glyph = glyph, tint = actualTint, size = size, modifier = modifier)
        }
    }
}

@Composable
private fun RetroPixelIcon(
    glyph: AppIconGlyph,
    tint: Color,
    size: Dp,
    modifier: Modifier = Modifier
) {
    when (glyph) {
        AppIconGlyph.MASCOT_ROBOT -> {
            // Pixel yellow mascot/robot head matching reference image
            Box(
                modifier = modifier
                    .size(size)
                    .background(Color(0xFFFFD700), RoundedCornerShape(2.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "•‿•",
                    color = Color.Black,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = (size.value * 0.45f).sp
                )
            }
        }
        AppIconGlyph.FLOPPY_DISK -> {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Floppy Disk",
                tint = tint,
                modifier = modifier.size(size)
            )
        }
        AppIconGlyph.PUZZLE -> {
            Icon(
                imageVector = Icons.Default.Extension,
                contentDescription = "Puzzle",
                tint = tint,
                modifier = modifier.size(size)
            )
        }
        AppIconGlyph.HEX_NUT -> {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings Nut",
                tint = tint,
                modifier = modifier.size(size)
            )
        }
        AppIconGlyph.CHEVRON_DOWN -> {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Dropdown",
                tint = tint,
                modifier = modifier.size(size)
            )
        }
        AppIconGlyph.ENTER_ARROW -> {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardReturn,
                contentDescription = "Return",
                tint = tint,
                modifier = modifier.size(size)
            )
        }
        AppIconGlyph.BACKSPACE -> {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Backspace",
                tint = tint,
                modifier = modifier.size(size)
            )
        }
        AppIconGlyph.SHIFT -> {
            Icon(
                imageVector = Icons.Default.North,
                contentDescription = "Shift",
                tint = tint,
                modifier = modifier.size(size)
            )
        }
        AppIconGlyph.SEND -> {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = tint,
                modifier = modifier.size(size)
            )
        }
        AppIconGlyph.MIC -> {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Microphone",
                tint = tint,
                modifier = modifier.size(size)
            )
        }
        AppIconGlyph.SEARCH -> {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = tint,
                modifier = modifier.size(size)
            )
        }
        AppIconGlyph.EMOJI -> {
            Icon(
                imageVector = Icons.Default.SentimentSatisfiedAlt,
                contentDescription = "Emoji",
                tint = tint,
                modifier = modifier.size(size)
            )
        }
        AppIconGlyph.ATTACHMENT -> {
            Icon(
                imageVector = Icons.Default.AttachFile,
                contentDescription = "Attachment",
                tint = tint,
                modifier = modifier.size(size)
            )
        }
        else -> {
            WhatsAppIcon(glyph = glyph, tint = tint, size = size, modifier = modifier)
        }
    }
}

@Composable
private fun WhatsAppIcon(
    glyph: AppIconGlyph,
    tint: Color,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val vector: ImageVector = when (glyph) {
        AppIconGlyph.BACKSPACE -> Icons.AutoMirrored.Filled.Backspace
        AppIconGlyph.SEND -> Icons.AutoMirrored.Filled.Send
        AppIconGlyph.SHIFT -> Icons.Default.ArrowUpward
        AppIconGlyph.ATTACHMENT -> Icons.Default.AttachFile
        AppIconGlyph.EMOJI -> Icons.Default.Mood
        AppIconGlyph.STICKER -> Icons.Default.StickyNote2
        AppIconGlyph.GIF -> Icons.Default.Gif
        AppIconGlyph.MIC -> Icons.Default.Mic
        AppIconGlyph.CAMERA -> Icons.Default.CameraAlt
        AppIconGlyph.GALLERY -> Icons.Default.Image
        AppIconGlyph.DOCUMENT -> Icons.Default.Description
        AppIconGlyph.LOCATION -> Icons.Default.LocationOn
        AppIconGlyph.CONTACT -> Icons.Default.Person
        AppIconGlyph.POLL -> Icons.Default.Poll
        AppIconGlyph.PAYMENT -> Icons.Default.Payments
        AppIconGlyph.CLIPBOARD -> Icons.Default.ContentPaste
        AppIconGlyph.TRANSLATE -> Icons.Default.Translate
        AppIconGlyph.TEXT_FORMAT -> Icons.Default.FormatColorText
        AppIconGlyph.SETTINGS -> Icons.Default.Tune
        AppIconGlyph.SEARCH -> Icons.Default.Search
        AppIconGlyph.THEME -> Icons.Default.Palette
        AppIconGlyph.SWITCH_STUDIO -> Icons.Default.GraphicEq
        AppIconGlyph.SOUND_ON -> Icons.Default.VolumeUp
        AppIconGlyph.SOUND_OFF -> Icons.Default.VolumeOff
        AppIconGlyph.CHECK_SINGLE -> Icons.Default.Check
        AppIconGlyph.CHECK_DOUBLE -> Icons.Default.DoneAll
        AppIconGlyph.LOCK_ENCRYPTED -> Icons.Default.Lock
        AppIconGlyph.GLOBE -> Icons.Default.Language
        AppIconGlyph.CLEAR -> Icons.Default.Close
        AppIconGlyph.COPY -> Icons.Default.ContentCopy
        AppIconGlyph.FORMAT_BOLD -> Icons.Default.FormatBold
        AppIconGlyph.FORMAT_ITALIC -> Icons.Default.FormatItalic
        AppIconGlyph.FORMAT_CODE -> Icons.Default.Code
        AppIconGlyph.FORMAT_QUOTE -> Icons.Default.FormatQuote
        AppIconGlyph.REACTION_HEART -> Icons.Default.Favorite
        AppIconGlyph.REACTION_LAUGH -> Icons.Default.SentimentVerySatisfied
        AppIconGlyph.REACTION_THUMBS_UP -> Icons.Default.ThumbUp
        AppIconGlyph.REACTION_FIRE -> Icons.Default.LocalFireDepartment
        AppIconGlyph.REACTION_WOW -> Icons.Default.SentimentNeutral
        AppIconGlyph.REACTION_SAD -> Icons.Default.SentimentDissatisfied
        AppIconGlyph.REACTION_PRAY -> Icons.Default.VolunteerActivism
        AppIconGlyph.PUZZLE -> Icons.Default.Extension
        AppIconGlyph.FLOPPY_DISK -> Icons.Default.Save
        AppIconGlyph.HEX_NUT -> Icons.Default.Settings
        AppIconGlyph.MASCOT_ROBOT -> Icons.Default.SmartToy
        AppIconGlyph.CHEVRON_DOWN -> Icons.Default.KeyboardArrowDown
        AppIconGlyph.ENTER_ARROW -> Icons.AutoMirrored.Filled.KeyboardReturn
    }

    Icon(
        imageVector = vector,
        contentDescription = glyph.name,
        tint = tint,
        modifier = modifier.size(size)
    )
}

@Composable
private fun Android17Icon(
    glyph: AppIconGlyph,
    tint: Color,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val vector: ImageVector = when (glyph) {
        AppIconGlyph.BACKSPACE -> Icons.AutoMirrored.Filled.Backspace
        AppIconGlyph.SEND -> Icons.AutoMirrored.Filled.Send
        AppIconGlyph.SHIFT -> Icons.Default.KeyboardCapslock
        AppIconGlyph.ATTACHMENT -> Icons.Default.AddCircleOutline
        AppIconGlyph.EMOJI -> Icons.Default.EmojiEmotions
        AppIconGlyph.STICKER -> Icons.Default.AutoAwesome
        AppIconGlyph.GIF -> Icons.Default.GifBox
        AppIconGlyph.MIC -> Icons.Default.MicNone
        AppIconGlyph.CAMERA -> Icons.Default.PhotoCamera
        AppIconGlyph.GALLERY -> Icons.Default.PhotoLibrary
        AppIconGlyph.DOCUMENT -> Icons.Default.SnippetFolder
        AppIconGlyph.LOCATION -> Icons.Default.NearMe
        AppIconGlyph.CONTACT -> Icons.Default.AccountCircle
        AppIconGlyph.POLL -> Icons.Default.BarChart
        AppIconGlyph.PAYMENT -> Icons.Default.AccountBalanceWallet
        AppIconGlyph.CLIPBOARD -> Icons.Default.Assignment
        AppIconGlyph.TRANSLATE -> Icons.Default.GTranslate
        AppIconGlyph.TEXT_FORMAT -> Icons.Default.FormatShapes
        AppIconGlyph.SETTINGS -> Icons.Default.SettingsSuggest
        AppIconGlyph.SEARCH -> Icons.Default.Search
        AppIconGlyph.THEME -> Icons.Default.ColorLens
        AppIconGlyph.SWITCH_STUDIO -> Icons.Default.SurroundSound
        AppIconGlyph.SOUND_ON -> Icons.Default.VolumeUp
        AppIconGlyph.SOUND_OFF -> Icons.Default.VolumeOff
        AppIconGlyph.CHECK_SINGLE -> Icons.Default.Check
        AppIconGlyph.CHECK_DOUBLE -> Icons.Default.DoneAll
        AppIconGlyph.LOCK_ENCRYPTED -> Icons.Default.VpnKey
        AppIconGlyph.GLOBE -> Icons.Default.Public
        AppIconGlyph.CLEAR -> Icons.Default.Cancel
        AppIconGlyph.COPY -> Icons.Default.CopyAll
        AppIconGlyph.FORMAT_BOLD -> Icons.Default.FormatBold
        AppIconGlyph.FORMAT_ITALIC -> Icons.Default.FormatItalic
        AppIconGlyph.FORMAT_CODE -> Icons.Default.IntegrationInstructions
        AppIconGlyph.FORMAT_QUOTE -> Icons.Default.FormatQuote
        AppIconGlyph.REACTION_HEART -> Icons.Default.FavoriteBorder
        AppIconGlyph.REACTION_LAUGH -> Icons.Default.SentimentSatisfiedAlt
        AppIconGlyph.REACTION_THUMBS_UP -> Icons.Default.ThumbUpOffAlt
        AppIconGlyph.REACTION_FIRE -> Icons.Default.Whatshot
        AppIconGlyph.REACTION_WOW -> Icons.Default.Celebration
        AppIconGlyph.REACTION_SAD -> Icons.Default.MoodBad
        AppIconGlyph.REACTION_PRAY -> Icons.Default.Spa
        AppIconGlyph.PUZZLE -> Icons.Default.Extension
        AppIconGlyph.FLOPPY_DISK -> Icons.Default.Save
        AppIconGlyph.HEX_NUT -> Icons.Default.SettingsSuggest
        AppIconGlyph.MASCOT_ROBOT -> Icons.Default.SmartToy
        AppIconGlyph.CHEVRON_DOWN -> Icons.Default.KeyboardArrowDown
        AppIconGlyph.ENTER_ARROW -> Icons.AutoMirrored.Filled.KeyboardReturn
    }

    Icon(
        imageVector = vector,
        contentDescription = glyph.name,
        tint = tint,
        modifier = modifier.size(size)
    )
}

@Composable
private fun IosSfIcon(
    glyph: AppIconGlyph,
    tint: Color,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val vector: ImageVector = when (glyph) {
        AppIconGlyph.BACKSPACE -> Icons.AutoMirrored.Filled.Backspace
        AppIconGlyph.SEND -> Icons.AutoMirrored.Filled.ArrowForward
        AppIconGlyph.SHIFT -> Icons.Default.North
        AppIconGlyph.ATTACHMENT -> Icons.Default.Add
        AppIconGlyph.EMOJI -> Icons.Default.Face
        AppIconGlyph.STICKER -> Icons.Default.Layers
        AppIconGlyph.GIF -> Icons.Default.Slideshow
        AppIconGlyph.MIC -> Icons.Default.GraphicEq
        AppIconGlyph.CAMERA -> Icons.Default.Camera
        AppIconGlyph.GALLERY -> Icons.Default.Collections
        AppIconGlyph.DOCUMENT -> Icons.Default.FolderShared
        AppIconGlyph.LOCATION -> Icons.Default.Navigation
        AppIconGlyph.CONTACT -> Icons.Default.PermIdentity
        AppIconGlyph.POLL -> Icons.Default.Leaderboard
        AppIconGlyph.PAYMENT -> Icons.Default.CreditCard
        AppIconGlyph.CLIPBOARD -> Icons.Default.ContentPaste
        AppIconGlyph.TRANSLATE -> Icons.Default.Translate
        AppIconGlyph.TEXT_FORMAT -> Icons.Default.Title
        AppIconGlyph.SETTINGS -> Icons.Default.Settings
        AppIconGlyph.SEARCH -> Icons.Default.Search
        AppIconGlyph.THEME -> Icons.Default.Palette
        AppIconGlyph.SWITCH_STUDIO -> Icons.Default.Equalizer
        AppIconGlyph.SOUND_ON -> Icons.Default.VolumeMute
        AppIconGlyph.SOUND_OFF -> Icons.Default.VolumeOff
        AppIconGlyph.CHECK_SINGLE -> Icons.Default.Check
        AppIconGlyph.CHECK_DOUBLE -> Icons.Default.DoneAll
        AppIconGlyph.LOCK_ENCRYPTED -> Icons.Outlined.Lock
        AppIconGlyph.GLOBE -> Icons.Default.Language
        AppIconGlyph.CLEAR -> Icons.Default.Clear
        AppIconGlyph.COPY -> Icons.Default.ContentCopy
        AppIconGlyph.FORMAT_BOLD -> Icons.Default.FormatBold
        AppIconGlyph.FORMAT_ITALIC -> Icons.Default.FormatItalic
        AppIconGlyph.FORMAT_CODE -> Icons.Default.Code
        AppIconGlyph.FORMAT_QUOTE -> Icons.Default.FormatQuote
        AppIconGlyph.REACTION_HEART -> Icons.Default.Favorite
        AppIconGlyph.REACTION_LAUGH -> Icons.Default.Mood
        AppIconGlyph.REACTION_THUMBS_UP -> Icons.Default.ThumbUp
        AppIconGlyph.REACTION_FIRE -> Icons.Default.Whatshot
        AppIconGlyph.REACTION_WOW -> Icons.Default.Star
        AppIconGlyph.REACTION_SAD -> Icons.Default.SentimentDissatisfied
        AppIconGlyph.REACTION_PRAY -> Icons.Default.VolunteerActivism
        AppIconGlyph.PUZZLE -> Icons.Default.Extension
        AppIconGlyph.FLOPPY_DISK -> Icons.Default.Save
        AppIconGlyph.HEX_NUT -> Icons.Default.Settings
        AppIconGlyph.MASCOT_ROBOT -> Icons.Default.SmartToy
        AppIconGlyph.CHEVRON_DOWN -> Icons.Default.KeyboardArrowDown
        AppIconGlyph.ENTER_ARROW -> Icons.AutoMirrored.Filled.KeyboardReturn
    }

    Icon(
        imageVector = vector,
        contentDescription = glyph.name,
        tint = tint,
        modifier = modifier.size(size)
    )
}
