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

        // Emotions & Faces
        "laugh" to listOf("😂", "🤣", "😆", "😄", "😃", "😀", "😹"),
        "haha" to listOf("😂", "🤣", "😆", "😹"),
        "funny" to listOf("😂", "🤣", "🤪", "🤡", "😆"),
        "smile" to listOf("😊", "😀", "🙂", "😄", "😁", "😃", "☺️"),
        "happy" to listOf("😊", "😄", "😁", "😃", "🥳", "✨", "🥰"),
        "joy" to listOf("😂", "🥹", "😊", "🥳", "✨"),
        "grin" to listOf("😁", "😀", "😃", "😄"),
        "cry" to listOf("😭", "😢", "😥", "🥺", "😿", "💧"),
        "tears" to listOf("😭", "😂", "🥹", "🥲", "😢"),
        "sob" to listOf("😭", "😢", "🥺"),
        "sad" to listOf("😢", "😭", "😞", "😔", "🙁", "☹️", "🥺"),
        "angry" to listOf("😠", "😡", "🤬", "😤", "👿"),
        "rage" to listOf("😡", "🤬", "👿", "🔥"),
        "mad" to listOf("😠", "😡", "😤", "👿"),
        "wink" to listOf("😉", "😜", "😏"),
        "smirk" to listOf("😏", "😼"),
        "blush" to listOf("😊", "☺️", "😳", "🥰"),
        "relieved" to listOf("😌", "😮‍💨"),
        "sweat" to listOf("😅", "😓", "😰", "😥"),
        "thinking" to listOf("🤔", "🧐"),
        "think" to listOf("🤔", "🧐", "💭"),
        "confused" to listOf("😕", "😟", "🤨", "🤔"),
        "upside down" to listOf("🙃"),
        "nerd" to listOf("🤓", "🧐"),
        "cool" to listOf("😎", "🕶️", "🧊", "🔥"),
        "sunglasses" to listOf("😎", "🕶️"),
        "party" to listOf("🎉", "🥳", "🎊", "🍾", "🥂"),
        "celebrate" to listOf("🎉", "🥳", "🍾", "🥂", "🎊"),
        "celebration" to listOf("🎉", "🥳", "🎊"),
        "congrats" to listOf("🎉", "👏", "🥳", "💐"),
        "scream" to listOf("😱", "🙀"),
        "scared" to listOf("😱", "😨", "😰", "🥶"),
        "fear" to listOf("😱", "😨", "😰"),
        "cold" to listOf("🥶", "❄️", "🧊", "⛄"),
        "hot" to listOf("🔥", "🥵", "🌶️", "☀️"),
        "sick" to listOf("🤒", "🤕", "🤢", "🤮", "😷", "🤧"),
        "vomit" to listOf("🤮", "🤢"),
        "mask" to listOf("😷"),
        "mindblown" to listOf("🤯"),
        "explode" to listOf("🤯", "💥"),
        "sleep" to listOf("😴", "🥱", "💤", "🛌"),
        "sleepy" to listOf("😴", "🥱", "💤"),
        "tired" to listOf("🥱", "😫", "😩", "😴"),
        "yawn" to listOf("🥱"),
        "skull" to listOf("💀", "☠️"),
        "dead" to listOf("💀", "☠️", "⚰️"),
        "ghost" to listOf("👻"),
        "robot" to listOf("🤖"),
        "alien" to listOf("👽", "👾"),
        "clown" to listOf("🤡"),
        "poop" to listOf("💩"),
        "devil" to listOf("😈", "👿"),
        "angel" to listOf("😇", "👼"),
        "hug" to listOf("🤗", "🫂"),
        "salute" to listOf("🫡"),
        "melting" to listOf("🫠"),
        "kiss" to listOf("😘", "😗", "😙", "😚", "💋", "😽"),

        // Gestures & Hands
        "thumbs" to listOf("👍", "👎"),
        "thumbsup" to listOf("👍"),
        "thumbsdown" to listOf("👎"),
        "good" to listOf("👍", "👌", "✅", "💯"),
        "yes" to listOf("👍", "✅", "👌"),
        "ok" to listOf("👌", "👍", "✅"),
        "no" to listOf("👎", "❌", "🚫"),
        "pray" to listOf("🙏", "🤲", "✨"),
        "prayer" to listOf("🙏", "🤲"),
        "thanks" to listOf("🙏", "🤝", "🙌", "💖", "👍"),
        "thank" to listOf("🙏", "🤝", "🙌"),
        "please" to listOf("🙏", "🥺"),
        "clap" to listOf("👏", "🙌"),
        "applause" to listOf("👏", "🙌"),
        "wave" to listOf("👋", "🌊"),
        "hi" to listOf("👋", "😊"),
        "hello" to listOf("👋", "✨"),
        "bye" to listOf("👋"),
        "hand" to listOf("👋", "✋", "🤚", "🖐️", "✋", "👍", "🤝"),
        "hands" to listOf("🙌", "👐", "🤲", "👏", "🤝", "🙏"),
        "punch" to listOf("👊", "✊", "🤛", "🤜"),
        "fist" to listOf("✊", "👊"),
        "muscle" to listOf("💪"),
        "strong" to listOf("💪", "🏋️"),
        "handshake" to listOf("🤝"),
        "peace" to listOf("✌️", "🕊️"),
        "fingers crossed" to listOf("🤞"),
        "point" to listOf("👉", "👈", "👆", "👇", "☝️"),
        "eye" to listOf("👀", "👁️"),
        "eyes" to listOf("👀", "👁️"),
        "heart" to listOf("❤️", "🩷", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔", "💖", "💕", "💞"),
        "broken heart" to listOf("💔"),

        // Nature, Sky & Weather
        "fire" to listOf("🔥", "💥", "⚡", "❤️‍🔥"),
        "flame" to listOf("🔥", "❤️‍🔥"),
        "lit" to listOf("🔥", "⚡", "✨"),
        "star" to listOf("⭐", "🌟", "✨", "💫"),
        "stars" to listOf("✨", "⭐", "🌟"),
        "sparkle" to listOf("✨", "⭐", "🌟"),
        "sparkles" to listOf("✨", "🌟"),
        "magic" to listOf("✨", "🪄", "🔮"),
        "sun" to listOf("☀️", "🌞", "🌅", "🌄"),
        "sunny" to listOf("☀️", "🌞"),
        "moon" to listOf("🌙", "🌕", "🌚", "🌛"),
        "cloud" to listOf("☁️", "⛅", "🌧️", "🌩️"),
        "rain" to listOf("🌧️", "💧", "☔", "⛈️"),
        "rainbow" to listOf("🌈"),
        "thunder" to listOf("⚡", "🌩️", "⛈️"),
        "lightning" to listOf("⚡", "🌩️"),
        "snow" to listOf("❄️", "☃️", "⛄", "🌨️"),
        "snowflake" to listOf("❄️"),
        "water" to listOf("💧", "🌊", "🚰"),
        "tree" to listOf("🌲", "🌳", "🌴", "🪵"),
        "flower" to listOf("🌸", "🌹", "🌺", "🌻", "🌼", "🌷", "💐"),
        "rose" to listOf("🌹"),
        "plant" to listOf("🌱", "🌿", "🪴", "🎍"),
        "leaf" to listOf("🍃", "🍂", "🍁", "☘️", "🍀"),
        "clover" to listOf("🍀", "☘️"),

        // Animals
        "dog" to listOf("🐶", "🐕", "🦮", "🐩"),
        "puppy" to listOf("🐶"),
        "cat" to listOf("🐱", "🐈", "😸", "😻"),
        "kitty" to listOf("🐱"),
        "bear" to listOf("🐻", "🐼", "🐻‍❄️"),
        "panda" to listOf("🐼"),
        "lion" to listOf("🦁"),
        "tiger" to listOf("🐯", "🐅"),
        "fox" to listOf("🦊"),
        "wolf" to listOf("🐺"),
        "rabbit" to listOf("🐰", "🐇"),
        "bunny" to listOf("🐰"),
        "monkey" to listOf("🐵", "🐒", "🙊", "🙉", "🙈"),
        "bird" to listOf("🐦", "🐤", "🦆", "🦅", "🦉", "🐧"),
        "chicken" to listOf("🐔", "🍗", "🐤"),
        "duck" to listOf("🦆"),
        "owl" to listOf("🦉"),
        "eagle" to listOf("🦅"),
        "bat" to listOf("🦇"),
        "horse" to listOf("🐴", "🐎"),
        "unicorn" to listOf("🦄"),
        "pig" to listOf("🐷", "🐽", "🐖"),
        "cow" to listOf("🐮", "🐄"),
        "frog" to listOf("🐸"),
        "snake" to listOf("🐍"),
        "turtle" to listOf("🐢"),
        "dinosaur" to listOf("🦖", "🦕"),
        "fish" to listOf("🐟", "🐠", "🐡", "🦈"),
        "shark" to listOf("🦈"),
        "whale" to listOf("🐳", "🐋"),
        "dolphin" to listOf("🐬"),
        "octopus" to listOf("🐙"),
        "crab" to listOf("🦀"),
        "butterfly" to listOf("🦋"),
        "bee" to listOf("🐝"),
        "spider" to listOf("🕷️", "🕸️"),
        "animal" to listOf("🐶", "🐱", "🐻", "🐼", "🦁", "🐯", "🦊", "🐰"),

        // Food & Drinks
        "food" to listOf("🍕", "🍔", "🍟", "🌮", "🥭", "🍎", "🍣", "🍜", "🍰", "🍩"),
        "fruit" to listOf("🥭", "🍎", "🍌", "🍇", "🍓", "🍉", "🍒", "🍑", "🍍"),
        "apple" to listOf("🍎", "🍏"),
        "banana" to listOf("🍌"),
        "watermelon" to listOf("🍉"),
        "grape" to listOf("🍇"),
        "strawberry" to listOf("🍓"),
        "cherry" to listOf("🍒"),
        "peach" to listOf("🍑"),
        "pineapple" to listOf("🍍"),
        "coconut" to listOf("🥥"),
        "avocado" to listOf("🥑"),
        "pizza" to listOf("🍕"),
        "burger" to listOf("🍔"),
        "hamburger" to listOf("🍔"),
        "fries" to listOf("🍟"),
        "hotdog" to listOf("🌭"),
        "sandwich" to listOf("🥪"),
        "taco" to listOf("🌮"),
        "burrito" to listOf("🌯"),
        "bread" to listOf("🍞", "🥐", "🥖"),
        "cheese" to listOf("🧀"),
        "meat" to listOf("🥩", "🍗", "🥓"),
        "egg" to listOf("🥚", "🍳"),
        "coffee" to listOf("☕", "🧋", "🫖"),
        "tea" to listOf("🍵", "🫖", "🧋"),
        "boba" to listOf("🧋"),
        "drink" to listOf("🥤", "🍺", "🍷", "🍹", "☕", "🧋"),
        "beer" to listOf("🍺", "🍻"),
        "wine" to listOf("🍷", "🍾", "🥂"),
        "cocktail" to listOf("🍸", "🍹"),
        "ice cream" to listOf("🍦", "🍨", "🍧"),
        "cake" to listOf("🎂", "🍰", "🧁"),
        "birthday" to listOf("🎂", "🎉", "🎈", "🎁"),
        "cookie" to listOf("🍪"),
        "chocolate" to listOf("🍫"),
        "candy" to listOf("🍬", "🍭"),
        "donut" to listOf("🍩"),
        "popcorn" to listOf("🍿"),

        // Sports, Games & Activities
        "sport" to listOf("⚽", "🏀", "🏈", "⚾", "🎾", "🏐"),
        "sports" to listOf("⚽", "🏀", "🏈", "⚾", "🎾", "🏐"),
        "ball" to listOf("⚽", "🏀", "🏈", "⚾", "🎾", "🏐", "🎱"),
        "soccer" to listOf("⚽"),
        "football" to listOf("⚽", "🏈"),
        "basketball" to listOf("🏀"),
        "baseball" to listOf("⚾"),
        "tennis" to listOf("🎾"),
        "game" to listOf("🎮", "🕹️", "🎲", "🎯"),
        "gaming" to listOf("🎮", "🕹️"),
        "music" to listOf("🎵", "🎶", "🎧", "🎤", "🎸", "🎹", "🥁"),
        "song" to listOf("🎵", "🎶"),
        "guitar" to listOf("🎸"),
        "piano" to listOf("🎹"),
        "drums" to listOf("🥁"),
        "headphones" to listOf("🎧"),
        "mic" to listOf("🎙️", "🎤"),
        "microphone" to listOf("🎤", "🎙️"),
        "trophy" to listOf("🏆", "🥇", "🥈", "🥉"),
        "winner" to listOf("🏆", "🥇", "👑"),
        "medal" to listOf("🥇", "🥈", "🥉", "🏅"),
        "movie" to listOf("🎬", "🍿", "🎥"),
        "cinema" to listOf("🎬", "🍿"),

        // Travel, Places & Vehicles
        "car" to listOf("🚗", "🏎️", "🚕", "🚙", "🚘"),
        "cars" to listOf("🚗", "🏎️", "🚕"),
        "taxi" to listOf("🚕"),
        "bus" to listOf("🚌", "🚎"),
        "truck" to listOf("🚚", "🚛", "🛻"),
        "bike" to listOf("🚲", "🏍️", "🛵"),
        "bicycle" to listOf("🚲"),
        "motorcycle" to listOf("🏍️", "🛵"),
        "plane" to listOf("✈️", "🛫", "🛬", "🛩️"),
        "airplane" to listOf("✈️", "🛫", "🛬"),
        "flight" to listOf("✈️", "🛫"),
        "rocket" to listOf("🚀"),
        "space" to listOf("🚀", "🛸", "🪐", "🌌", "⭐"),
        "fast" to listOf("🚀", "⚡", "🏎️", "💨"),
        "ship" to listOf("🚢", "🛳️", "⛴️", "⛵", "🚤"),
        "boat" to listOf("⛵", "🚤", "🛶", "🚢"),
        "house" to listOf("🏠", "🏡"),
        "home" to listOf("🏠", "🏡"),
        "building" to listOf("🏢", "🏬", "🏦", "🏨"),
        "beach" to listOf("🏖️", "🏝️", "🌊", "☀️"),
        "travel" to listOf("✈️", "🧳", "🏖️", "🚗", "🗺️"),

        // Objects, Tools & Tech
        "phone" to listOf("📱", "📲", "☎️", "📞"),
        "iphone" to listOf("📱"),
        "computer" to listOf("💻", "🖥️", "⌨️", "🖱️"),
        "laptop" to listOf("💻"),
        "keyboard" to listOf("⌨️"),
        "camera" to listOf("📷", "📸", "🎥"),
        "photo" to listOf("📷", "📸", "🖼️"),
        "tv" to listOf("📺"),
        "book" to listOf("📖", "📚", "📕", "📝"),
        "money" to listOf("💸", "💵", "💰", "🪙", "💳"),
        "cash" to listOf("💵", "💰", "💸"),
        "dollar" to listOf("💵", "💰", "💸"),
        "rich" to listOf("🤑", "💰", "💎"),
        "gem" to listOf("💎"),
        "diamond" to listOf("💎"),
        "clock" to listOf("⏰", "⏱️", "🕰️", "⏳"),
        "time" to listOf("⏰", "⏱️", "⏳", "⌛"),
        "watch" to listOf("⌚", "⏱️"),
        "battery" to listOf("🔋"),
        "light" to listOf("💡", "🔦", "🕯️"),
        "lightbulb" to listOf("💡"),
        "idea" to listOf("💡", "✨", "🧠"),
        "gift" to listOf("🎁", "🎀", "🎉"),
        "present" to listOf("🎁"),
        "box" to listOf("📦", "🎁"),
        "lock" to listOf("🔒", "🔐"),
        "key" to listOf("🔑", "🗝️"),
        "mail" to listOf("✉️", "📧", "📫", "📦"),
        "email" to listOf("📧", "✉️"),
        "letter" to listOf("✉️", "📝"),
        "check" to listOf("✅", "✔️", "☑️"),
        "tick" to listOf("✅", "✔️"),
        "cross" to listOf("❌", "✖️", "🚫"),
        "x" to listOf("❌", "✖️"),
        "question" to listOf("❓", "❔"),
        "warning" to listOf("⚠️", "🚨", "❗"),
        "100" to listOf("💯"),
        "hundred" to listOf("💯"),
        "crown" to listOf("👑"),
        "king" to listOf("👑", "🤴"),
        "queen" to listOf("👑", "👸"),
        "ring" to listOf("💍", "💎"),
        "bell" to listOf("🔔", "🛎️")
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

        // 2. Exact prefix matches (keywords that start with query)
        emojiKeywordMap.forEach { (keyword, emojis) ->
            if (keyword.startsWith(q)) {
                results.addAll(emojis)
            }
        }

        // 3. Substring keyword matches (keywords containing query or vice-versa)
        emojiKeywordMap.forEach { (keyword, emojis) ->
            if (keyword.contains(q) || q.contains(keyword)) {
                results.addAll(emojis)
            }
        }

        // 4. Category broad search
        if ("food".contains(q) || q.contains("food") || q.contains("eat")) results.addAll(food.take(16))
        if ("animal".contains(q) || q.contains("animal") || q.contains("pet")) results.addAll(animals.take(16))
        if ("smile".contains(q) || q.contains("face") || q.contains("emoji")) results.addAll(smileys.take(16))
        if ("people".contains(q) || q.contains("person") || q.contains("human")) results.addAll(people.take(16))
        if ("sport".contains(q) || "activity".contains(q) || q.contains("game")) results.addAll(activities.take(16))
        if ("travel".contains(q) || "car".contains(q) || q.contains("place")) results.addAll(travel.take(16))
        if ("heart".contains(q) || "symbol".contains(q) || q.contains("sign")) results.addAll(symbols.take(16))
        if ("object".contains(q) || "thing".contains(q) || q.contains("tool")) results.addAll(objects.take(16))

        return results.toList()
    }
}
