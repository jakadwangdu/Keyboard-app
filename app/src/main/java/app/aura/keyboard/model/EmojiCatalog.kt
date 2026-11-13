package app.aura.keyboard.model

data class EmojiGroup(
    val id: String,
    val title: String,
    val iconLabel: String,
    val emojis: List<String>
)

object EmojiCatalog {

    val groups: List<EmojiGroup> = listOf(
        EmojiGroup(
            id = "smileys",
            title = "Smileys",
            iconLabel = "☺",
            emojis = listOf(
                "😀", "😃", "😄", "😁", "😅", "😂", "🤣", "😊",
                "😇", "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘",
                "😋", "😛", "😜", "🤪", "🤨", "🧐", "🤓", "😎",
                "🤩", "🥳", "😏", "😒", "😞", "😔", "😟", "😕",
                "🥺", "😢", "😭", "😤", "😠", "😡", "🤯", "😳",
                "🥵", "🥶", "😱", "😨", "🤗", "🤔", "🫡", "🫠",
                "😴", "🥱", "🤤", "🤐", "🤢", "🤧", "🤠", "👻"
            )
        ),
        EmojiGroup(
            id = "gestures",
            title = "Hands",
            iconLabel = "👍",
            emojis = listOf(
                "👋", "🤚", "🖐️", "✋", "👌", "🤌", "🤏", "✌️",
                "🤞", "🫰", "🤟", "🤘", "🤙", "👈", "👉", "👆",
                "👇", "☝️", "👍", "👎", "✊", "👊", "👏", "🙌",
                "🫶", "👐", "🤲", "🤝", "🙏", "✍️", "💪", "👀"
            )
        ),
        EmojiGroup(
            id = "hearts",
            title = "Symbols",
            iconLabel = "♥",
            emojis = listOf(
                "❤️", "🧡", "💛", "💚", "💙", "💜", "🤎", "🖤",
                "🤍", "💔", "❤️‍🔥", "💕", "💞", "💓", "💗", "💖",
                "💘", "💝", "✨", "⭐", "🌟", "💫", "⚡", "💥",
                "🔥", "💯", "✅", "❌", "❗", "❓", "🎉", "🎊"
            )
        ),
        EmojiGroup(
            id = "nature",
            title = "Nature",
            iconLabel = "🌿",
            emojis = listOf(
                "🐶", "🐱", "🐭", "🐰", "🦊", "🐻", "🐼", "🐨",
                "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🐧", "🦉",
                "🦋", "🐝", "🐢", "🐬", "🐳", "🌸", "🌹", "🌻",
                "🌿", "🍀", "🍁", "🌈", "☀️", "🌙", "❄️", "🌊"
            )
        ),
        EmojiGroup(
            id = "food",
            title = "Food",
            iconLabel = "☕",
            emojis = listOf(
                "☕", "🍵", "🧋", "🥐", "🍞", "🧀", "🍕", "🍔",
                "🍟", "🌮", "🍣", "🍜", "🍎", "🥭", "🍓", "🍒",
                "🍌", "🍉", "🍇", "🥑", "🍦", "🍩", "🍪", "🎂",
                "🍰", "🍫", "🍿", "🍺", "🍷", "🥂", "🍽️", "🧁"
            )
        ),
        EmojiGroup(
            id = "objects",
            title = "Objects",
            iconLabel = "✦",
            emojis = listOf(
                "🚀", "✈️", "🚗", "🚲", "🏠", "📱", "💻", "⌨️",
                "🎧", "📷", "🎬", "🎮", "🎵", "🎸", "📚", "📝",
                "💡", "🔑", "🔒", "📦", "🎁", "🏆", "⚽", "🏀",
                "⏰", "📅", "📌", "💰", "💎", "🛒", "🧭", "⏳"
            )
        )
    )
}
