package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

/**
 * Keymold Keyboard Design Tokens (UI1.md spec).
 * Minimal, classy, quiet. Fast and smooth like Apple's keyboard.
 * True AMOLED black by default, user-selectable accents, zero drop shadows.
 */

val SyneFontFamily = FontFamily(
    Font(R.font.syne, FontWeight.Bold)
)

enum class BackgroundTheme(
    val id: String,
    val displayName: String,
    val bgHex: Long,
    val screenHex: Long,
    val keyboardHex: Long,
    val keyHex: Long,
    val keySecondaryHex: Long,
    val textHex: Long,
    val mutedHex: Long,
    val hairlineAlpha: Float,
    val isDark: Boolean
) {
    AMOLED(
        id = "amoled",
        displayName = "AMOLED",
        bgHex = 0xFF000000,
        screenHex = 0xFF000000,
        keyboardHex = 0xFF000000,
        keyHex = 0xFF161616,
        keySecondaryHex = 0xFF262626,
        textHex = 0xFFF5F5F5,
        mutedHex = 0xFF8A8A8A,
        hairlineAlpha = 0.10f,
        isDark = true
    ),
    DIM(
        id = "dim",
        displayName = "Dim",
        bgHex = 0xFF0B0A09,
        screenHex = 0xFF141210,
        keyboardHex = 0xFF1B1917,
        keyHex = 0xFF2B2825,
        keySecondaryHex = 0xFF201E1B,
        textHex = 0xFFF3EDE4,
        mutedHex = 0xFF8D857A,
        hairlineAlpha = 0.07f,
        isDark = true
    ),
    LIGHT(
        id = "light",
        displayName = "Light",
        bgHex = 0xFFE9E5DE,
        screenHex = 0xFFFAF8F4,
        keyboardHex = 0xFFE4E0D9,
        keyHex = 0xFFFFFFFF,
        keySecondaryHex = 0xFFCFCAC1,
        textHex = 0xFF1D1A16,
        mutedHex = 0xFF7A7268,
        hairlineAlpha = 0.08f,
        isDark = false
    )
}

enum class AccentColor(
    val title: String,
    val accentHex: Long,
    val onAccentHex: Long
) {
    AMBER("Amber", 0xFFF0A93B, 0xFF1A1204),
    BLUE("Blue", 0xFF4F9DFF, 0xFF04101F),
    GREEN("Green", 0xFF3DDC97, 0xFF03150D),
    ROSE("Rose", 0xFFFF6B8B, 0xFF200610),
    VIOLET("Violet", 0xFFA78BFA, 0xFF0E0720),
    WHITE("White", 0xFFFFFFFF, 0xFF000000)
}

enum class KeySizeOption(
    val title: String,
    val heightDp: Int
) {
    COMPACT("Compact", 48),
    FITTED("Fitted", 54), // Default
    ROOMY("Roomy", 60);

    val dp: Dp get() = heightDp.dp
}

enum class EmojiStyleOption(val title: String) {
    ANDROID_NOTO("Android (Noto)"),
    SYSTEM("System")
}

enum class AgePack(val title: String) {
    EVERYONE("Everyone"),
    KIDS("Kids"),
    TEENS("Teens"),
    ADULTS("Adults"),
    SENIORS("Seniors")
}

@androidx.compose.runtime.Immutable
data class KeymoldThemeTokens(
    val backgroundTheme: BackgroundTheme = BackgroundTheme.AMOLED,
    val accentColor: AccentColor = AccentColor.AMBER,
    val keySizeOption: KeySizeOption = KeySizeOption.FITTED,
    val emojiStyleOption: EmojiStyleOption = EmojiStyleOption.ANDROID_NOTO
) {
    val bg: Color = Color(backgroundTheme.bgHex)
    val screen: Color = Color(backgroundTheme.screenHex)
    val keyboard: Color = Color(backgroundTheme.keyboardHex)
    val key: Color = Color(backgroundTheme.keyHex)
    val keySecondary: Color = Color(backgroundTheme.keySecondaryHex)
    val text: Color = Color(backgroundTheme.textHex)
    val muted: Color = Color(backgroundTheme.mutedHex)
    val hairline: Color = if (backgroundTheme.isDark) {
        Color.White.copy(alpha = backgroundTheme.hairlineAlpha)
    } else {
        Color.Black.copy(alpha = backgroundTheme.hairlineAlpha)
    }
    val accent: Color = Color(accentColor.accentHex)
    val textOnAccent: Color = Color(accentColor.onAccentHex)
    val isDark: Boolean = backgroundTheme.isDark

    val keyHeight: Dp = keySizeOption.dp

    // Exact total height specified in UI1.md:
    // Total height = 40 + 4 × (key height + 8) + 22px, identical in every mode so nothing jumps!
    val totalKeyboardHeight: Dp = 40.dp + (keyHeight + 8.dp) * 4 + 22.dp
    val bodyHeight: Dp = (keyHeight + 8.dp) * 4 + 22.dp
}
