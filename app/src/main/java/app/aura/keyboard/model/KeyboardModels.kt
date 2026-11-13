package app.aura.keyboard.model

enum class KeyboardLayer {
    LETTERS,
    NUMBERS,
    SYMBOLS,
    NUMBER_PAD
}

enum class ShiftState {
    OFF,
    SHIFTED,
    CAPS_LOCK
}

enum class KeySizePreset(
    val label: String,
    val keyHeightDp: Float,
    val labelSp: Float
) {
    SMALL("14", 44f, 16f),
    MEDIUM("16", 49f, 18f),
    LARGE("18", 54f, 20f);

    companion object {
        fun fromLabel(value: String?): KeySizePreset {
            return entries.firstOrNull { it.label == value } ?: MEDIUM
        }
    }
}

enum class ThemeMode {
    AUTO,
    DARK,
    LIGHT
}

enum class PanelMode {
    KEYBOARD,
    CLIPBOARD,
    EMOJI
}

enum class ImeActionKind(val defaultLabel: String) {
    RETURN("Return"),
    SEND("Send"),
    SEARCH("Search"),
    GO("Go"),
    NEXT("Next"),
    DONE("Done")
}

enum class KeyKind {
    CHARACTER,
    SHIFT,
    BACKSPACE,
    LAYER_NUMBERS,
    LAYER_LETTERS,
    LAYER_SYMBOLS,
    EMOJI,
    SPACE,
    RETURN
}

data class KeySpec(
    val id: String,
    val kind: KeyKind,
    val primaryChar: String = "",
    val shiftedChar: String = primaryChar,
    val displayLabel: String = primaryChar,
    val row: Int,
    val isModifier: Boolean = false,
    val isAccent: Boolean = false
)

data class KeyboardPalette(
    val isDark: Boolean,
    val cardTop: Int,
    val cardBottom: Int,
    val cardRim: Int,
    val keyFill: Int,
    val keyPressed: Int,
    val modifierFill: Int,
    val modifierPressed: Int,
    val accentFill: Int,
    val accentPressed: Int,
    val textOnAccent: Int,
    val keyText: Int,
    val mutedText: Int,
    val emojiAccent: Int,
    val toolbarCircle: Int,
    val cardBorder: Int
) {
    companion object {
        val DARK_ESPRESSO = KeyboardPalette(
            isDark = true,
            cardTop = 0xFF211E1B.toInt(),
            cardBottom = 0xFF171513.toInt(),
            cardRim = 0x2EEFE7DA,
            keyFill = 0xFF2C2824.toInt(),
            keyPressed = 0xFF423C36.toInt(),
            modifierFill = 0xFF25211E.toInt(),
            modifierPressed = 0xFF39332D.toInt(),
            accentFill = 0xFFEFE7DA.toInt(),
            accentPressed = 0xFFD9CFC0.toInt(),
            textOnAccent = 0xFF171513.toInt(),
            keyText = 0xFFEFE7DA.toInt(),
            mutedText = 0xFF9E9488.toInt(),
            emojiAccent = 0xFFD9B98C.toInt(),
            toolbarCircle = 0xFF3A3530.toInt(),
            cardBorder = 0xFF332E29.toInt()
        )

        val LIGHT_CREAM = KeyboardPalette(
            isDark = false,
            cardTop = 0xFFF4EFE8.toInt(),
            cardBottom = 0xFFE8E0D4.toInt(),
            cardRim = 0x33211E1B,
            keyFill = 0xFFFFFCF7.toInt(),
            keyPressed = 0xFFE2D9CB.toInt(),
            modifierFill = 0xFFE2D9CC.toInt(),
            modifierPressed = 0xFFD0C5B5.toInt(),
            accentFill = 0xFF211E1B.toInt(),
            accentPressed = 0xFF3A342E.toInt(),
            textOnAccent = 0xFFEFE7DA.toInt(),
            keyText = 0xFF211E1B.toInt(),
            mutedText = 0xFF786E62.toInt(),
            emojiAccent = 0xFFB07D38.toInt(),
            toolbarCircle = 0xFFDCD2C4.toInt(),
            cardBorder = 0xFFD0C5B5.toInt()
        )
    }
}
