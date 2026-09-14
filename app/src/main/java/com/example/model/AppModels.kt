package com.example.model

enum class IconPackType(
    val displayName: String,
    val subtitle: String,
    val brandColorHex: Long
) {
    RETRO_PIXEL_95(
        displayName = "1995 Pixel Pack",
        subtitle = "Authentic 8-Bit Retro Windows 95 Desktop Icons",
        brandColorHex = 0xFFFFD700
    ),
    WHATSAPP_EXPRESSIVE(
        displayName = "WhatsApp Chat",
        subtitle = "Official WhatsApp Conversation & Action Icons",
        brandColorHex = 0xFF25D366
    ),
    ANDROID_17(
        displayName = "Android 17 Expressive",
        subtitle = "Material You Dynamic Squircles & Tonal Badges",
        brandColorHex = 0xFFD0BCFF
    ),
    IOS_SF(
        displayName = "iOS SF Symbols",
        subtitle = "Cupertino Frosted Glass Precision Vectors",
        brandColorHex = 0xFF007AFF
    )
}

enum class SwitchType(
    val title: String,
    val feel: String,
    val soundProfile: String,
    val accentHex: Long,
    val clickFrequency: Float,
    val thockFrequency: Float,
    val damping: Float
) {
    BLUE_CLICKY(
        title = "Cherry MX Blue",
        feel = "Clicky & Tactile",
        soundProfile = "Crisp high-pitch acoustic click",
        accentHex = 0xFF2196F3,
        clickFrequency = 3200f,
        thockFrequency = 750f,
        damping = 0.88f
    ),
    BROWN_TACTILE(
        title = "Gateron Brown",
        feel = "Tactile & Balanced",
        soundProfile = "Mellow bump & soft clack",
        accentHex = 0xFF8D6E63,
        clickFrequency = 1900f,
        thockFrequency = 520f,
        damping = 0.82f
    ),
    RED_LINEAR(
        title = "Cherry MX Red",
        feel = "Smooth Linear",
        soundProfile = "Quiet low-friction bottom-out",
        accentHex = 0xFFE53935,
        clickFrequency = 900f,
        thockFrequency = 380f,
        damping = 0.75f
    ),
    CREAM_THOCK(
        title = "NovelKeys Cream",
        feel = "Deep Thocky",
        soundProfile = "Rich warm POM thock resonance",
        accentHex = 0xFFFFD54F,
        clickFrequency = 700f,
        thockFrequency = 240f,
        damping = 0.65f
    ),
    MODEL_M_SPRING(
        title = "IBM Buckling Spring",
        feel = "Vintage Heavy",
        soundProfile = "Metallic snap & spring reverberation",
        accentHex = 0xFF78909C,
        clickFrequency = 4200f,
        thockFrequency = 620f,
        damping = 0.94f
    )
}

enum class KeyboardThemeType(
    val displayName: String,
    val backgroundHex: Long,
    val surfaceHex: Long,
    val keyCapHex: Long,
    val keyCapAltHex: Long,
    val keyTextHex: Long,
    val accentHex: Long,
    val chatBackgroundHex: Long,
    val userBubbleHex: Long,
    val contactBubbleHex: Long,
    val isDark: Boolean
) {
    MINIMAL_DARK(
        displayName = "Minimal Slate Dark",
        backgroundHex = 0xFF121214,
        surfaceHex = 0xFF18181B,
        keyCapHex = 0xFF27272A,
        keyCapAltHex = 0xFF3F3F46,
        keyTextHex = 0xFFF4F4F5,
        accentHex = 0xFF00E676,
        chatBackgroundHex = 0xFF121214,
        userBubbleHex = 0xFF27272A,
        contactBubbleHex = 0xFF18181B,
        isDark = true
    ),
    MINIMAL_LIGHT(
        displayName = "Minimal Pure Light",
        backgroundHex = 0xFFF8F9FA,
        surfaceHex = 0xFFF1F3F5,
        keyCapHex = 0xFFFFFFFF,
        keyCapAltHex = 0xFFE9ECEF,
        keyTextHex = 0xFF212529,
        accentHex = 0xFF25D366,
        chatBackgroundHex = 0xFFF8F9FA,
        userBubbleHex = 0xFFE9ECEF,
        contactBubbleHex = 0xFFFFFFFF,
        isDark = false
    ),
    AMOLED_BLACK(
        displayName = "AMOLED Pitch Dark",
        backgroundHex = 0xFF000000,
        surfaceHex = 0xFF101216,
        keyCapHex = 0xFF1A1F26,
        keyCapAltHex = 0xFF242B35,
        keyTextHex = 0xFFFFFFFF,
        accentHex = 0xFF00E676,
        chatBackgroundHex = 0xFF000000,
        userBubbleHex = 0xFF005C4B,
        contactBubbleHex = 0xFF1A1F26,
        isDark = true
    ),
    WHATSAPP_DARK(
        displayName = "WhatsApp Dark Chat",
        backgroundHex = 0xFF0B141A,
        surfaceHex = 0xFF121B22,
        keyCapHex = 0xFF1F2C34,
        keyCapAltHex = 0xFF2A3942,
        keyTextHex = 0xFFE9EDEF,
        accentHex = 0xFF00A884,
        chatBackgroundHex = 0xFF0B141A,
        userBubbleHex = 0xFF005C4B,
        contactBubbleHex = 0xFF1F2C34,
        isDark = true
    ),
    WHATSAPP_LIGHT(
        displayName = "WhatsApp Light Chat",
        backgroundHex = 0xFFEFEAE2,
        surfaceHex = 0xFFF0F2F5,
        keyCapHex = 0xFFFFFFFF,
        keyCapAltHex = 0xFFE4E8EC,
        keyTextHex = 0xFF111B21,
        accentHex = 0xFF25D366,
        chatBackgroundHex = 0xFFEFEAE2,
        userBubbleHex = 0xFFD9FDD3,
        contactBubbleHex = 0xFFFFFFFF,
        isDark = false
    ),
    ANDROID_17_PILL(
        displayName = "Android 17 Expressive",
        backgroundHex = 0xFF141218,
        surfaceHex = 0xFF211F26,
        keyCapHex = 0xFF2B2930,
        keyCapAltHex = 0xFF36343B,
        keyTextHex = 0xFFE6E0E9,
        accentHex = 0xFFD0BCFF,
        chatBackgroundHex = 0xFF141218,
        userBubbleHex = 0xFF4F378B,
        contactBubbleHex = 0xFF2B2930,
        isDark = true
    ),
    IOS_FROSTED_DARK(
        displayName = "iOS SF Glass Dark",
        backgroundHex = 0xFF000000,
        surfaceHex = 0xFF1C1C1E,
        keyCapHex = 0xFF2C2C2E,
        keyCapAltHex = 0xFF3A3A3C,
        keyTextHex = 0xFFFFFFFF,
        accentHex = 0xFF007AFF,
        chatBackgroundHex = 0xFF000000,
        userBubbleHex = 0xFF007AFF,
        contactBubbleHex = 0xFF2C2C2E,
        isDark = true
    ),
    CYBERPUNK_MECH(
        displayName = "Cyberpunk Cyberboard",
        backgroundHex = 0xFF0A0E17,
        surfaceHex = 0xFF121B2A,
        keyCapHex = 0xFF1A263D,
        keyCapAltHex = 0xFF233554,
        keyTextHex = 0xFF00F0FF,
        accentHex = 0xFFFF0055,
        chatBackgroundHex = 0xFF0A0E17,
        userBubbleHex = 0xFF2B1055,
        contactBubbleHex = 0xFF1A263D,
        isDark = true
    ),
    RETRO_MODEL_M(
        displayName = "Retro 1984 Beige",
        backgroundHex = 0xFFD6C7A1,
        surfaceHex = 0xFFC9B78C,
        keyCapHex = 0xFFEFE4C8,
        keyCapAltHex = 0xFFC2B28B,
        keyTextHex = 0xFF2E2619,
        accentHex = 0xFF9E2A2B,
        chatBackgroundHex = 0xFFD6C7A1,
        userBubbleHex = 0xFFC2B28B,
        contactBubbleHex = 0xFFEFE4C8,
        isDark = false
    ),
    RETRO_95_PIXEL(
        displayName = "1995 Pixel Desktop",
        backgroundHex = 0xFF008080,
        surfaceHex = 0xFF000080,
        keyCapHex = 0xFFD4D0C8,
        keyCapAltHex = 0xFFB8B4AC,
        keyTextHex = 0xFF000000,
        accentHex = 0xFFFFD700,
        chatBackgroundHex = 0xFF008080,
        userBubbleHex = 0xFFFFFFFF,
        contactBubbleHex = 0xFFE0DFDB,
        isDark = false
    )
}

enum class KeyboardMode {
    QWERTY,
    SYMBOLS_123,
    SYMBOLS_ALT,
    EMOJI_DRAWER,
    STICKERS_DRAWER,
    ATTACHMENTS_SHEET,
    CLIPBOARD_DRAWER
}

enum class MessageStatus {
    PENDING,
    SENT,
    DELIVERED,
    READ
}

enum class AttachmentType(val title: String) {
    NONE("None"),
    PHOTO("Photo / Gallery"),
    CAMERA("Live Camera"),
    VOICE_NOTE("Voice Note"),
    DOCUMENT("Document File"),
    LOCATION("Live Location"),
    CONTACT("Shared Contact"),
    POLL("Chat Poll"),
    PAYMENT("Quick Transfer"),
    STICKER("Sticker Pack")
}

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String = "",
    val isMe: Boolean = true,
    val senderName: String = "You",
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTime: String = "12:54 PM",
    val status: MessageStatus = MessageStatus.READ,
    val attachmentType: AttachmentType = AttachmentType.NONE,
    val attachmentData: String? = null,
    val reactions: List<String> = emptyList()
)

data class ClipboardItem(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isPinned: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
