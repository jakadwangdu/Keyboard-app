package com.example.model

import com.example.icons.AppIconGlyph
import com.example.ui.theme.AgePack

data class EmojiCategory(
    val id: String,
    val title: String,
    val iconGlyph: AppIconGlyph,
    val emojis: List<String>
)

object EmojiDatabase {

    val smileys = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😭", "🥲", "😊", "😇",
        "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋", "😛",
        "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🥸", "🤩", "🥳", "😏", "😒",
        "😞", "😔", "😟", "😕", "🙁", "☹️", "😣", "😖", "😫", "😩", "🥺", "😢",
        "😤", "😠", "😡", "🤬", "🤯", "😳", "🥵", "🥶", "😱", "😨", "😰", "😥",
        "😓", "🤗", "🤔", "🤫", "🤥", "😶", "😐", "😑", "😬", "🙄", "😯", "😦",
        "😧", "😮", "😲", "🥱", "😴", "🤤", "😪", "😵", "🤐", "🥴", "🤢", "🤮",
        "🤧", "😷", "🤒", "🤕", "🤑", "🤠", "😈", "👿", "👹", "👺", "🤡", "💩",
        "👻", "💀", "☠️", "👽", "👾", "🤖", "🫠", "🥹", "🫡", "🫨", "🫶", "🫰"
    )

    val people = listOf(
        "👋", "🤚", "🖐️", "✋", "🖖", "👌", "🤌", "🤏", "✌️", "🤞", "🤟", "🤘",
        "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "👍", "👎", "✊", "👊", "🤛",
        "🤜", "👏", "🙌", "👐", "🤲", "🤝", "🙏", "✍️", "💅", "🤳", "💪", "🦾",
        "🦿", "🦵", "🦶", "👂", "🦻", "👃", "🧠", "🫀", "🫁", "🦷", "🦴", "👀",
        "👁️", "👅", "👄", "🫦", "👶", "🧒", "👦", "👧", "🧑", "👱", "👨", "🧔",
        "👩", "🧓", "👴", "👵", "🙍", "🙎", "🙅", "🙆", "💁", "🙋", "🧏", "🙇"
    )

    val animals = listOf(
        "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮",
        "🐷", "🐸", "🐵", "🐔", "🐧", "🐦", "🐤", "🦆", "🦅", "🦉", "🦇", "🐺",
        "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐞", "🐜", "🪰", "🪲", "🪳",
        "🦟", "🦗", "🕷️", "🦂", "🐢", "🐍", "🦎", "🦖", "🦕", "🐙", "🦑", "🦐",
        "🦞", "🦀", "🐡", "🐠", "🐟", "🐬", "🐳", "🦈", "🦭", "🐊", "🐅", "🐆",
        "🌲", "🌳", "🌴", "🪵", "🌱", "🌿", "☘️", "🍀", "🎍", "🪴", "🎋", "🍃"
    )

    val food = listOf(
        "🥭", "🍎", "🍏", "🍐", "🍑", "🍒", "🍓", "🫐", "🥝", "🍅", "🫒", "🥥",
        "🍇", "🍈", "🍉", "🍊", "🍋", "🍌", "🍍", "🥑", "🍆", "🥔", "🥕", "🌽",
        "🌶️", "🫑", "🥒", "🥬", "🥦", "🧄", "🧅", "🥜", "🫘", "🌰", "🍞", "🥐",
        "🥖", "🫓", "🥨", "🥯", "🥞", "🧇", "🧀", "🍖", "🍗", "🥩", "🥓", "🍔",
        "🍟", "🍕", "🌭", "🥪", "🌮", "🌯", "🫔", "🥙", "🧆", "🥚", "🍳", "🥘",
        "🍲", "🫕", "🥣", "🥗", "🍿", "🧈", "🧂", "🍦", "🍧", "🍨", "🍩", "🍪",
        "🎂", "🍰", "🧁", "🥧", "🍫", "🍬", "🍭", "🍮", "🍯", "🍼", "🥛", "☕",
        "🫖", "🍵", "🍶", "🧋"
    )

    val travel = listOf(
        "🚗", "🚕", "🚙", "🚌", "🚎", "🏎️", "🚓", "🚑", "🚒", "🚐", "🛻", "🚚",
        "🚛", "🚜", "🛴", "🚲", "🛵", "🏍️", "🛺", "🚨", "✈️", "🛫", "🛬", "🛩️",
        "🚀", "🛸", "🚁", "🛶", "⛵", "🚤", "🛥️", "🛳️", "⛴️", "🚢", "⚓", "🛟",
        "🗽", "🗼", "🏰", "🏯", "🏟️", "🎡", "🎢", "🏖️", "🏝️", "🏜️", "🌋", "⛰️",
        "🏠", "🏡", "🏘️", "🏢", "🏬", "🏥", "🏦", "🏨", "🏫", "🏛️"
    )

    val activities = listOf(
        "⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🥏", "🎱", "🪀", "🏓", "🏸",
        "🏒", "🏑", "🏏", "🥊", "🥋", "🛹", "🛼", "⛸️", "🎿", "🏂", "🏆", "🥇",
        "🥈", "🥉", "🏅", "🎖️", "🎫", "🎟️", "🎪", "🎭", "🎨", "🎬", "🎤", "🎧",
        "🎼", "🎹", "🥁", "🎷", "🎺", "🎸", "🎻", "🎲", "🎯"
    )

    val objects = listOf(
        "💻", "🖥️", "🖨️", "⌨️", "🖱️", "📱", "📲", "☎️", "📞", "📺", "📻", "🎙️",
        "⏱️", "⏲️", "⏰", "🕰️", "⌛", "⏳", "📡", "🔋", "🔌", "💡", "🔦", "🕯️",
        "💸", "💵", "🪙", "💰", "💳", "💎", "⚖️", "🧰", "🔧", "🔨", "⚒️", "🛠️",
        "⚙️", "🔒", "🔓", "🔑", "🗝️", "📦", "📫", "✉️", "📧", "📝", "📁", "📂"
    )

    val symbols = listOf(
        "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔", "❤️‍🔥", "❤️‍🩹",
        "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "✨", "⭐", "🌟",
        "💫", "⚡", "💥", "🔥", "💨", "🌈", "☀️", "💯", "🔟", "0️⃣", "1️⃣", "2️⃣",
        "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "✅", "❌", "❓", "❗",
        "⭕", "🔴", "🟢", "🔵", "🟣", "⚫", "⚪"
    )

    fun getCategoryMap(): Map<String, EmojiCategory> {
        return mapOf(
            "smileys" to EmojiCategory("smileys", "Smileys", AppIconGlyph.EMOJI, smileys),
            "people" to EmojiCategory("people", "People", AppIconGlyph.REACTION_THUMBS_UP, people),
            "animals" to EmojiCategory("animals", "Animals", AppIconGlyph.MIC, animals),
            "food" to EmojiCategory("food", "Food", AppIconGlyph.SWITCH_STUDIO, food),
            "travel" to EmojiCategory("travel", "Travel", AppIconGlyph.ATTACHMENT, travel),
            "activities" to EmojiCategory("activities", "Activity", AppIconGlyph.STICKER, activities),
            "objects" to EmojiCategory("objects", "Objects", AppIconGlyph.SETTINGS, objects),
            "symbols" to EmojiCategory("symbols", "Symbols", AppIconGlyph.REACTION_HEART, symbols)
        )
    }

    /**
     * Category order strictly defined per Age Pack in UI1.md:
     * Everyone: Smileys, People, Animals, Food, Travel, Activity, Objects, Symbols
     * Kids: Animals, Food, Activity, Smileys, Travel, People, Symbols, Objects
     * Teens: Smileys, Symbols, Activity, People, Objects, Animals, Food, Travel
     * Adults: Objects, Travel, Food, People, Symbols, Smileys, Animals, Activity
     * Seniors: People, Symbols, Food, Animals, Travel, Smileys, Objects, Activity
     */
    fun getCategoriesForPack(pack: AgePack): List<EmojiCategory> {
        val catMap = getCategoryMap()
        val order = when (pack) {
            AgePack.EVERYONE -> listOf("smileys", "people", "animals", "food", "travel", "activities", "objects", "symbols")
            AgePack.KIDS -> listOf("animals", "food", "activities", "smileys", "travel", "people", "symbols", "objects")
            AgePack.TEENS -> listOf("smileys", "symbols", "activities", "people", "objects", "animals", "food", "travel")
            AgePack.ADULTS -> listOf("objects", "travel", "food", "people", "symbols", "smileys", "animals", "activities")
            AgePack.SENIORS -> listOf("people", "symbols", "food", "animals", "travel", "smileys", "objects", "activities")
        }
        return order.mapNotNull { catMap[it] }
    }

    val allCategories: List<EmojiCategory> by lazy {
        getCategoriesForPack(AgePack.EVERYONE)
    }

    // Default 40 popular emojis shown when search query is empty
    val top40Emojis: List<String> = listOf(
        "😂", "❤️", "👍", "🔥", "🙏", "😍", "✨", "🥰", "🎉", "🤣",
        "🥹", "😊", "💯", "👏", "😭", "🫶", "🥭", "☕", "💀", "💕",
        "😎", "🫡", "🍕", "💖", "🐶", "🚀", "😋", "🥳", "🌟", "😘",
        "👋", "😉", "🙌", "🍰", "🍔", "🤩", "🌈", "🐱", "🍦", "⚡"
    )

    // Comprehensive Emoji Keyword Index for instant search
    val emojiKeywordMap: Map<String, List<String>> = mapOf(
        // Explicit test criteria in UI1.md: "love", "lol", "mango"
        "love" to listOf("❤️", "💕", "😍", "🥰", "💖", "💗", "💓", "💘", "💝", "🫶", "😘", "💞"),
        "lol" to listOf("😂", "🤣", "😆", "💀", "😹", "😅"),
        "mango" to listOf("🥭"),

        // Additional common keywords
        "laugh" to listOf("😂", "🤣", "😆", "😄", "😃", "😀"),
        "funny" to listOf("😂", "🤣", "🤪", "🤡"),
        "cry" to listOf("😭", "😢", "😥", "🥺", "😿"),
        "tears" to listOf("😭", "😂", "🥹", "🥲"),
        "pray" to listOf("🙏", "🤲", "🛐", "✨"),
        "thanks" to listOf("🙏", "🤝", "🙌", "💖", "👍"),
        "thank" to listOf("🙏", "🤝", "🙌"),
        "please" to listOf("🙏", "🥺"),
        "heart" to listOf("❤️", "🩷", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔", "💖"),
        "fire" to listOf("🔥", "💥", "⚡", "❤️‍🔥"),
        "lit" to listOf("🔥", "⚡", "✨"),
        "hot" to listOf("🔥", "🥵", "🌶️"),
        "thumbs" to listOf("👍", "👎"),
        "good" to listOf("👍", "👌", "✅", "💯"),
        "yes" to listOf("👍", "✅", "👌"),
        "ok" to listOf("👌", "👍", "✅"),
        "no" to listOf("👎", "❌", "🚫"),
        "star" to listOf("⭐", "🌟", "✨", "💫"),
        "sparkle" to listOf("✨", "⭐", "🌟"),
        "magic" to listOf("✨", "🪄", "🔮"),
        "party" to listOf("🎉", "🥳", "🎊", "🍾"),
        "celebrate" to listOf("🎉", "🥳", "🍾", "🥂"),
        "congrats" to listOf("🎉", "👏", "🥳", "💐"),
        "clap" to listOf("👏", "🙌"),
        "smile" to listOf("😊", "😀", "🙂", "😄", "😁"),
        "happy" to listOf("😊", "😄", "😁", "😃", "🥳", "✨"),
        "sad" to listOf("😢", "😭", "😞", "😔", "🙁"),
        "angry" to listOf("😠", "😡", "🤬", "😤"),
        "cool" to listOf("😎", "🕶️", "🧊", "🔥"),
        "coffee" to listOf("☕", "🧋", "🫖"),
        "tea" to listOf("🍵", "🫖", "🧋"),
        "food" to listOf("🍕", "🍔", "🍟", "🌮", "🥭", "🍎", "🍣", "🍜"),
        "pizza" to listOf("🍕"),
        "burger" to listOf("🍔"),
        "fruit" to listOf("🥭", "🍎", "🍌", "🍇", "🍓", "🍉", "🍒", "🍑"),
        "apple" to listOf("🍎", "🍏"),
        "banana" to listOf("🍌"),
        "dog" to listOf("🐶", "🐕", "🦮", "🐩"),
        "puppy" to listOf("🐶"),
        "cat" to listOf("🐱", "🐈", "😸", "😻"),
        "kitty" to listOf("🐱"),
        "animal" to listOf("🐶", "🐱", "🐻", "🐼", "🦁", "🐯", "🦊", "🐰"),
        "bear" to listOf("🐻", "🐼", "🐻‍❄️"),
        "rocket" to listOf("🚀"),
        "fast" to listOf("🚀", "⚡", "🏎️", "💨"),
        "hundred" to listOf("💯"),
        "100" to listOf("💯"),
        "skull" to listOf("💀", "☠️"),
        "dead" to listOf("💀", "☠️", "⚰️"),
        "salute" to listOf("🫡"),
        "wave" to listOf("👋", "🌊"),
        "kiss" to listOf("😘", "😗", "😙", "😚", "💋"),
        "sleep" to listOf("😴", "🥱", "💤", "🛌"),
        "tired" to listOf("🥱", "😫", "😩", "😴"),
        "car" to listOf("🚗", "🏎️", "🚕", "🚙"),
        "music" to listOf("🎵", "🎶", "🎧", "🎤", "🎸", "🎹"),
        "money" to listOf("💸", "💵", "💰", "🪙", "💳"),
        "cash" to listOf("💵", "💰", "💸")
    )

    /**
     * Search emojis matching query.
     * Empty query -> returns top 40 emojis.
     * Query matching keywords -> returns matching emojis without duplicates.
     * If no match -> returns empty list.
     */
    fun searchEmojis(query: String): List<String> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return top40Emojis

        val results = linkedSetOf<String>()

        // 1. Direct keyword match
        emojiKeywordMap[q]?.let { results.addAll(it) }

        // 2. Keyword prefix / contains match
        emojiKeywordMap.forEach { (keyword, emojis) ->
            if (keyword.contains(q) || q.contains(keyword)) {
                results.addAll(emojis)
            }
        }

        // 3. Category search match
        if ("food".contains(q)) results.addAll(food.take(12))
        if ("animal".contains(q)) results.addAll(animals.take(12))
        if ("smile".contains(q)) results.addAll(smileys.take(12))
        if ("sport".contains(q) || "activity".contains(q)) results.addAll(activities.take(12))
        if ("travel".contains(q) || "car".contains(q)) results.addAll(travel.take(12))
        if ("heart".contains(q) || "symbol".contains(q)) results.addAll(symbols.take(12))

        return results.toList()
    }
}
