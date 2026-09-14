package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.MechanicalAudioEngine
import com.example.engine.AutocorrectEngine
import com.example.engine.CorrectionCandidate
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class KeyboardViewModel(application: Application) : AndroidViewModel(application) {

    val audioEngine = MechanicalAudioEngine(application)

    private val _activeText = MutableStateFlow("")
    val activeText: StateFlow<String> = _activeText.asStateFlow()

    private val _keyboardMode = MutableStateFlow(KeyboardMode.QWERTY)
    val keyboardMode: StateFlow<KeyboardMode> = _keyboardMode.asStateFlow()

    private val _iconPackType = MutableStateFlow(IconPackType.WHATSAPP_EXPRESSIVE)
    val iconPackType: StateFlow<IconPackType> = _iconPackType.asStateFlow()

    private val _keyboardTheme = MutableStateFlow(KeyboardThemeType.AMOLED_BLACK)
    val keyboardTheme: StateFlow<KeyboardThemeType> = _keyboardTheme.asStateFlow()

    private val _keyHeight = MutableStateFlow(50.dp)
    val keyHeight: StateFlow<Dp> = _keyHeight.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showImeSetupDialog = MutableStateFlow(false)
    val showImeSetupDialog: StateFlow<Boolean> = _showImeSetupDialog.asStateFlow()

    private val _currentSwitch = MutableStateFlow(SwitchType.CREAM_THOCK)
    val currentSwitch: StateFlow<SwitchType> = _currentSwitch.asStateFlow()

    private val _isShiftActive = MutableStateFlow(false)
    val isShiftActive: StateFlow<Boolean> = _isShiftActive.asStateFlow()

    private val _isCapsLock = MutableStateFlow(false)
    val isCapsLock: StateFlow<Boolean> = _isCapsLock.asStateFlow()

    private val _isAltSymbols = MutableStateFlow(false)
    val isAltSymbols: StateFlow<Boolean> = _isAltSymbols.asStateFlow()

    private val _isVoiceTyping = MutableStateFlow(false)
    val isVoiceTyping: StateFlow<Boolean> = _isVoiceTyping.asStateFlow()

    private val _isSoundOn = MutableStateFlow(true)
    val isSoundOn: StateFlow<Boolean> = _isSoundOn.asStateFlow()

    private val _isHapticOn = MutableStateFlow(true)
    val isHapticOn: StateFlow<Boolean> = _isHapticOn.asStateFlow()

    private val _isAutocorrectOn = MutableStateFlow(false)
    val isAutocorrectOn: StateFlow<Boolean> = _isAutocorrectOn.asStateFlow()

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    private val _candidates = MutableStateFlow<List<CorrectionCandidate>>(emptyList())
    val candidates: StateFlow<List<CorrectionCandidate>> = _candidates.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _clipboardHistory = MutableStateFlow<List<ClipboardItem>>(emptyList())
    val clipboardHistory: StateFlow<List<ClipboardItem>> = _clipboardHistory.asStateFlow()

    // Common English word vocabulary for Gboard predictive strip
    private val dictionary = listOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "I",
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
        "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
        "back", "after", "use", "two", "how", "our", "work", "first", "well", "way",
        "even", "new", "want", "because", "any", "these", "give", "day", "most", "us",
        "keyboard", "gboard", "whatsapp", "android", "mechanical", "switch", "sound", "thock",
        "typing", "awesome", "meeting", "thanks", "hello", "perfect", "great", "ready"
    )

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    init {
        // Preload sample conversation with Alex
        _messages.value = listOf(
            ChatMessage(
                id = 1001L,
                text = "Hey! Have you tried the new Gboard Mechanical Keyboard? ⌨️",
                isMe = false,
                senderName = "Alex",
                formattedTime = "12:48 PM",
                status = MessageStatus.READ
            ),
            ChatMessage(
                id = 1002L,
                text = "Yes! The NovelKeys Cream thock acoustics are incredible.",
                isMe = true,
                senderName = "You",
                formattedTime = "12:49 PM",
                status = MessageStatus.READ
            ),
            ChatMessage(
                id = 1003L,
                text = "And check out the preloaded WhatsApp and Android 17 conversation icon packs! Try sending attachments or emojis below 🔥",
                isMe = false,
                senderName = "Alex",
                formattedTime = "12:50 PM",
                status = MessageStatus.READ,
                reactions = listOf("🔥", "❤️")
            )
        )

        // Preload sample clipboard
        _clipboardHistory.value = listOf(
            ClipboardItem(1L, "MechBoard typing test in progress ✨", isPinned = true),
            ClipboardItem(2L, "Let's meet at 3:00 PM for the mech keyboard meetup! ☕", isPinned = false),
            ClipboardItem(3L, "https://github.com/aistudio/mechboard", isPinned = false)
        )
    }

    fun typeKey(key: String) {
        val current = _activeText.value

        // If user tapped Spacebar, check if autocorrect is enabled and the current word has an autocorrect suggestion
        if (key == " ") {
            val words = current.split(" ").toMutableList()
            val lastWord = words.lastOrNull()?.trim() ?: ""
            val activeCandidates = _candidates.value
            val autoCorrectMatch = activeCandidates.firstOrNull { it.isAutoCorrect }

            if (_isAutocorrectOn.value && lastWord.isNotEmpty() && autoCorrectMatch != null && !lastWord.equals(autoCorrectMatch.word, ignoreCase = true)) {
                // Auto-correct misspelled word on spacebar tap!
                words[words.size - 1] = autoCorrectMatch.word
                _activeText.value = words.joinToString(" ") + " "
                _suggestions.value = emptyList()
                _candidates.value = emptyList()
                audioEngine.playKeyPressSound(_currentSwitch.value, pitchShift = 1.15f)
                return
            }

            val updated = current + " "
            _activeText.value = updated
            _suggestions.value = emptyList()
            _candidates.value = emptyList()
            audioEngine.playKeyPressSound(_currentSwitch.value, pitchShift = 0.95f)
            return
        }

        val updated = current + key
        _activeText.value = updated

        // Lowercase shift if not caps lock
        if (_isShiftActive.value && !_isCapsLock.value) {
            _isShiftActive.value = false
        }

        // Trigger mechanical audio
        audioEngine.playKeyPressSound(_currentSwitch.value)

        // Compute predictive suggestions & spell checking
        updateSuggestions(updated)
    }

    fun backspace() {
        val current = _activeText.value
        if (current.isNotEmpty()) {
            val updated = current.dropLast(1)
            _activeText.value = updated
            audioEngine.playKeyPressSound(_currentSwitch.value, pitchShift = 0.88f)
            updateSuggestions(updated)
        }
    }

    fun updateActiveText(newText: String) {
        _activeText.value = newText
        updateSuggestions(newText)
    }

    private fun updateSuggestions(text: String) {
        if (text.isBlank()) {
            _suggestions.value = emptyList()
            _candidates.value = emptyList()
            return
        }
        val lastWord = text.split(" ").lastOrNull() ?: ""
        if (lastWord.isBlank()) {
            _suggestions.value = listOf("the", "you", "thanks", "sounds great", "let's go")
            _candidates.value = emptyList()
            return
        }

        val rawCandidates = AutocorrectEngine.getCorrections(lastWord)
        val candidatesList = if (_isAutocorrectOn.value) {
            rawCandidates
        } else {
            // When autocorrect is turned off, do not flag words for auto-correction on space
            rawCandidates.map { it.copy(isAutoCorrect = false) }
        }
        _candidates.value = candidatesList
        _suggestions.value = candidatesList.map { it.word }
    }

    fun applySuggestion(word: String) {
        val current = _activeText.value
        val words = current.split(" ").toMutableList()
        if (words.isNotEmpty()) {
            words[words.size - 1] = word
        } else {
            words.add(word)
        }
        _activeText.value = words.joinToString(" ") + " "
        audioEngine.playKeyPressSound(_currentSwitch.value, pitchShift = 1.15f)
        _suggestions.value = emptyList()
        _candidates.value = emptyList()
    }

    fun sendMessage() {
        val text = _activeText.value.trim()
        if (text.isEmpty()) return

        val now = System.currentTimeMillis()
        val timeStr = timeFormat.format(Date(now))

        val newMsg = ChatMessage(
            id = now,
            text = text,
            isMe = true,
            senderName = "You",
            formattedTime = timeStr,
            status = MessageStatus.READ
        )

        _messages.value = _messages.value + newMsg
        _activeText.value = ""
        _suggestions.value = emptyList()

        // Copy to clipboard history as recent message
        addClipboardItem(text)

        audioEngine.playKeyPressSound(_currentSwitch.value, pitchShift = 1.25f)

        // Simulate interactive contact response
        viewModelScope.launch {
            delay(1200)
            val replies = listOf(
                "Awesome message! That mechanical acoustic feel is super satisfying ⌨️",
                "Got it! Loving the WhatsApp conversation styling & icons ✨",
                "Nice! Try switching to IBM Buckling Spring or Cherry MX Blue in the switch studio! 🎵",
                "Sounds great! The typing experience feels completely immersive."
            )
            val replyText = replies.random()
            val replyMsg = ChatMessage(
                id = System.currentTimeMillis(),
                text = replyText,
                isMe = false,
                senderName = "Alex",
                formattedTime = timeFormat.format(Date()),
                status = MessageStatus.READ,
                reactions = listOf("👍")
            )
            _messages.value = _messages.value + replyMsg
            audioEngine.playKeyPressSound(_currentSwitch.value, pitchShift = 1.1f)
        }
    }

    fun sendAttachment(type: AttachmentType) {
        val now = System.currentTimeMillis()
        val timeStr = timeFormat.format(Date(now))

        val (textDesc, dataDesc) = when (type) {
            AttachmentType.PHOTO -> Pair("Shared 2 photos from Gallery", "IMG_2026_MECH.JPG (3.4 MB)")
            AttachmentType.CAMERA -> Pair("Live photo captured", "CAM_2026_SNAPSHOT.JPG")
            AttachmentType.VOICE_NOTE -> Pair("Voice Note (0:18)", "▶ ılıılılı 0:18 • High Quality")
            AttachmentType.DOCUMENT -> Pair("Keymap_Specification.pdf", "PDF Document • 1.2 MB")
            AttachmentType.LOCATION -> Pair("Live Location Shared", "Silicon Valley Mechanical Lab • 1 hr")
            AttachmentType.CONTACT -> Pair("Mechanical Artisan Contacts", "+1 (555) 019-2831")
            AttachmentType.POLL -> Pair("Poll: Favorite Switch Type?", "3 votes • NovelKeys Cream leading")
            AttachmentType.PAYMENT -> Pair("Quick Transfer", "$25.00 for Artisan Keycap")
            AttachmentType.STICKER -> Pair("Sent Sticker", "🚀 Shipped")
            AttachmentType.NONE -> Pair("", "")
        }

        val msg = ChatMessage(
            id = now,
            text = textDesc,
            isMe = true,
            senderName = "You",
            formattedTime = timeStr,
            status = MessageStatus.READ,
            attachmentType = type,
            attachmentData = dataDesc
        )

        _messages.value = _messages.value + msg
        audioEngine.playKeyPressSound(_currentSwitch.value, pitchShift = 1.2f)
    }

    fun addReaction(messageId: Long, reaction: String) {
        _messages.value = _messages.value.map { msg ->
            if (msg.id == messageId) {
                val currentReactions = msg.reactions.toMutableList()
                if (currentReactions.contains(reaction)) {
                    currentReactions.remove(reaction)
                } else {
                    currentReactions.add(reaction)
                }
                msg.copy(reactions = currentReactions)
            } else msg
        }
        audioEngine.playKeyPressSound(_currentSwitch.value, pitchShift = 1.3f)
    }

    fun formatText(wrapper: String) {
        val current = _activeText.value
        if (current.isNotEmpty()) {
            _activeText.value = "$wrapper$current$wrapper"
        } else {
            _activeText.value = "$wrapper$wrapper"
        }
        audioEngine.playKeyPressSound(_currentSwitch.value)
    }

    fun toggleShift() {
        if (!_isShiftActive.value && !_isCapsLock.value) {
            _isShiftActive.value = true
        } else if (_isShiftActive.value && !_isCapsLock.value) {
            _isCapsLock.value = true
            _isShiftActive.value = false
        } else {
            _isCapsLock.value = false
            _isShiftActive.value = false
        }
        audioEngine.playKeyPressSound(_currentSwitch.value, pitchShift = 0.95f)
    }

    fun toggleAltSymbols() {
        _isAltSymbols.value = !_isAltSymbols.value
        audioEngine.playKeyPressSound(_currentSwitch.value, pitchShift = 0.95f)
    }

    fun setMode(mode: KeyboardMode) {
        _keyboardMode.value = mode
        audioEngine.playKeyPressSound(_currentSwitch.value, pitchShift = 1.05f)
    }

    fun setIconPack(pack: IconPackType) {
        _iconPackType.value = pack
        audioEngine.playKeyPressSound(_currentSwitch.value)
    }

    fun setTheme(theme: KeyboardThemeType) {
        _keyboardTheme.value = theme
        audioEngine.playKeyPressSound(_currentSwitch.value)
    }

    fun setSwitch(switch: SwitchType) {
        _currentSwitch.value = switch
        audioEngine.currentSwitch = switch
        audioEngine.playKeyPressSound(switch)
    }

    fun setKeyHeight(height: Dp) {
        _keyHeight.value = height
    }

    fun setShowSettingsDialog(show: Boolean) {
        _showSettingsDialog.value = show
    }

    fun setHapticEnabled(enabled: Boolean) {
        _isHapticOn.value = enabled
        audioEngine.isHapticEnabled = enabled
    }

    fun toggleHaptic() {
        val next = !_isHapticOn.value
        _isHapticOn.value = next
        audioEngine.isHapticEnabled = next
    }

    fun setAutocorrectEnabled(enabled: Boolean) {
        _isAutocorrectOn.value = enabled
        updateSuggestions(_activeText.value)
    }

    fun toggleAutocorrect() {
        val next = !_isAutocorrectOn.value
        _isAutocorrectOn.value = next
        updateSuggestions(_activeText.value)
    }

    fun setSoundEnabled(enabled: Boolean) {
        _isSoundOn.value = enabled
        audioEngine.isSoundEnabled = enabled
    }

    fun setSoundVolume(volume: Float) {
        audioEngine.volumeLevel = volume
    }

    fun setHapticStrength(strength: Float) {
        audioEngine.hapticStrength = strength
    }

    fun toggleSound() {
        val next = !_isSoundOn.value
        _isSoundOn.value = next
        audioEngine.isSoundEnabled = next
    }

    fun toggleVoiceTyping() {
        val next = !_isVoiceTyping.value
        _isVoiceTyping.value = next
        if (next) {
            viewModelScope.launch {
                delay(1500)
                typeKey("Hello from voice dictation! ")
                _isVoiceTyping.value = false
            }
        }
    }

    fun clearChat() {
        _messages.value = emptyList()
        audioEngine.playKeyPressSound(_currentSwitch.value)
    }

    fun addClipboardItem(text: String) {
        if (text.isBlank()) return
        val current = _clipboardHistory.value.toMutableList()
        current.removeAll { it.text == text }
        current.add(0, ClipboardItem(text = text))
        _clipboardHistory.value = current.take(15)
    }

    fun togglePinClipboard(id: Long) {
        _clipboardHistory.value = _clipboardHistory.value.map {
            if (it.id == id) it.copy(isPinned = !it.isPinned) else it
        }
    }

    fun setShowImeSetupDialog(show: Boolean) {
        _showImeSetupDialog.value = show
    }

    fun isImeEnabled(): Boolean {
        return try {
            val imm = getApplication<Application>().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            val enabledMethods = imm?.enabledInputMethodList ?: emptyList()
            val packageName = getApplication<Application>().packageName
            enabledMethods.any { it.packageName == packageName }
        } catch (e: Exception) {
            false
        }
    }

    fun isImeSelected(): Boolean {
        return try {
            val currentIme = Settings.Secure.getString(
                getApplication<Application>().contentResolver,
                Settings.Secure.DEFAULT_INPUT_METHOD
            )
            val packageName = getApplication<Application>().packageName
            currentIme?.contains(packageName) == true
        } catch (e: Exception) {
            false
        }
    }
}

