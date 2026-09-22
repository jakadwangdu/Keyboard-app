package com.example.engine

data class MediaSearchResult(
    val type: MediaType,
    val content: String,
    val title: String
)

enum class MediaType {
    EMOJI,
    GIF,
    STICKER
}

object EmojiPredictionEngine {

    // Contextual semantic keyword to emoji mapping (including Unicode 18.0)
    private val keywordToEmojis: Map<String, List<String>> = mapOf(
        "stress" to listOf("🫠", "🫨", "🫩", "🤯", "😫"),
        "pressure" to listOf("🫠", "🫨", "🥵", "💥"),
        "cracking" to listOf("🫠", "🫨", "💔", "💥"),
        "tired" to listOf("🫩", "🥱", "😴", "😮‍💨"),
        "exhausted" to listOf("🫩", "🫠", "🥱", "💀"),
        "pickle" to listOf("🥒", "🫙", "🥗"),
        "cucumber" to listOf("🥒", "🥗"),
        "sour" to listOf("🥒", "🍋‍🟩", "🍋"),
        "meteor" to listOf("☄️", "🌠", "🌌", "🚀"),
        "comet" to listOf("☄️", "🌠", "✨"),
        "space" to listOf("☄️", "🚀", "🪐", "🌌", "👽"),
        "lighthouse" to listOf("🏮", "🗼", "🌊", "🚢"),
        "beacon" to listOf("🏮", "🗼", "💡"),
        "coast" to listOf("🏮", "🌊", "🏖️"),
        "eraser" to listOf("🧼", "🧹", "🧽", "✨"),
        "clean" to listOf("🧼", "🫧", "✨", "🧹"),
        "butterfly" to listOf("🦋", "🪽", "🌸"),
        "monarch" to listOf("🦋", "👑", "🪽"),
        "net" to listOf("🥅", "🪤", "🎣"),
        "thumb" to listOf("🫲", "🫱", "👍", "👎"),
        "left" to listOf("🫲", "👈", "⬅️"),
        "right" to listOf("🫱", "👉", "➡️"),
        "harp" to listOf("🪕", "🎻", "🎶", "🎵"),
        "music" to listOf("🪕", "🎧", "🎵", "🎶", "🎹"),
        "shovel" to listOf("⛏️", "🪚", "⚒️"),
        "dig" to listOf("⛏️", "⚒️", "🕳️"),
        "splatter" to listOf("💥", "🫧", "🎨"),
        "fingerprint" to listOf("🪪", "🆔", "🔍"),
        "id" to listOf("🪪", "🆔", "💳"),
        "vegetable" to listOf("🫚", "🥕", "🥦", "🫛"),
        "ginger" to listOf("🫚", "🍵"),
        "lime" to listOf("🍋‍🟩", "🍹", "🍸"),
        "mushroom" to listOf("🍄‍🟫", "🍄"),
        "phoenix" to listOf("🐦‍🔥", "🔥", "🦅"),
        "chain" to listOf("⛓️‍💥", "⛓️"),
        "fire" to listOf("🔥", "❤️‍🔥", "💥"),
        "love" to listOf("❤️", "🩷", "💖", "🫶", "😍"),
        "heart" to listOf("🩷", "🩵", "🩶", "❤️", "💕"),
        "happy" to listOf("😀", "😄", "😊", "🥳", "✨"),
        "keyboard" to listOf("⌨️", "💻", "🖥️", "🤖"),
        "switch" to listOf("⌨️", "🔘", "✨"),
        "sound" to listOf("🔊", "🎧", "🎵", "🎙️"),
        "thock" to listOf("⌨️", "🪵", "✨"),
        "party" to listOf("🎉", "🥳", "🪩", "🍾"),
        "cool" to listOf("😎", "🕶️", "🔥", "🧊"),
        "wave" to listOf("👋", "🌊", "🫡"),
        "salute" to listOf("🫡", "🎖️", "🤝"),
        "bubble" to listOf("🫧", "🧼", "🛁")
    )

    // GIF animated reaction cards catalog
    val gifsCatalog = listOf(
        MediaSearchResult(MediaType.GIF, "https://media.giphy.com/media/mech_typing.gif", "⌨️ Mechanical Fast Typing"),
        MediaSearchResult(MediaType.GIF, "https://media.giphy.com/media/thock_sound.gif", "🔥 Deep Thock Resonance"),
        MediaSearchResult(MediaType.GIF, "https://media.giphy.com/media/dancing_cat.gif", "🐱 Vibing Cat"),
        MediaSearchResult(MediaType.GIF, "https://media.giphy.com/media/mind_blown.gif", "🤯 Mind Blown"),
        MediaSearchResult(MediaType.GIF, "https://media.giphy.com/media/high_five.gif", "🙌 High Five"),
        MediaSearchResult(MediaType.GIF, "https://media.giphy.com/media/happy_dance.gif", "🕺 Celebration Dance"),
        MediaSearchResult(MediaType.GIF, "https://media.giphy.com/media/typing_fast.gif", "⚡ 150 WPM Turbo Speed"),
        MediaSearchResult(MediaType.GIF, "https://media.giphy.com/media/cracking_stress.gif", "🫠 Cracking Face Stress Relieved")
    )

    // Sticker catalog
    val stickersCatalog = listOf(
        MediaSearchResult(MediaType.STICKER, "✨🔔🔔✨", "Golden Bell Sparkles"),
        MediaSearchResult(MediaType.STICKER, "👑💖", "Royal Love Crown"),
        MediaSearchResult(MediaType.STICKER, "💯🥰", "Pure 100% Love"),
        MediaSearchResult(MediaType.STICKER, "🕶️😎", "Chill Vibes Only"),
        MediaSearchResult(MediaType.STICKER, "🫠🔥", "Melting Fire Passion"),
        MediaSearchResult(MediaType.STICKER, "🚀✨", "To The Moon Speed"),
        MediaSearchResult(MediaType.STICKER, "🤖⚡", "Cyber Mech Bot"),
        MediaSearchResult(MediaType.STICKER, "🍋‍🟩🍹", "Fresh Lime Cocktail"),
        MediaSearchResult(MediaType.STICKER, "🪿⚡", "Peace Was Never An Option"),
        MediaSearchResult(MediaType.STICKER, "🩷✨", "Pink Glow Radiance")
    )

    /**
     * Gets contextually predicted emojis for a typed word
     */
    fun predictEmojis(word: String): List<String> {
        val clean = word.lowercase().trim().replace(Regex("[^a-zA-Z]"), "")
        if (clean.isEmpty()) return emptyList()
        return keywordToEmojis[clean] ?: emptyList()
    }

    /**
     * Performs unified media search across emojis, GIFs, and stickers
     */
    fun searchUnifiedMedia(query: String): List<MediaSearchResult> {
        val q = query.lowercase().trim()
        val results = mutableListOf<MediaSearchResult>()

        // 1. Search Emojis
        keywordToEmojis.forEach { (keyword, emojiList) ->
            if (keyword.contains(q) || q.contains(keyword)) {
                emojiList.forEach { emoji ->
                    results.add(MediaSearchResult(MediaType.EMOJI, emoji, keyword.replaceFirstChar { it.uppercase() }))
                }
            }
        }

        // 2. Search GIFs
        gifsCatalog.forEach { gif ->
            if (gif.title.lowercase().contains(q)) {
                results.add(gif)
            }
        }

        // 3. Search Stickers
        stickersCatalog.forEach { sticker ->
            if (sticker.title.lowercase().contains(q) || sticker.content.contains(q)) {
                results.add(sticker)
            }
        }

        return results.distinctBy { it.content }
    }
}
