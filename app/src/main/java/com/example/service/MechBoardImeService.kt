package com.example.service

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.audio.MechanicalAudioEngine
import com.example.engine.AutocorrectEngine
import com.example.model.IconPackType
import com.example.model.KeyboardMode
import com.example.model.KeyboardThemeType
import com.example.model.SwitchType
import com.example.ui.keyboard.GboardEmojiStickerDrawer
import com.example.ui.keyboard.GboardQwertyView
import com.example.ui.keyboard.GboardSymbolsView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.components.GboardTopBar

class MechBoardImeService : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private lateinit var audioEngine: MechanicalAudioEngine

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val viewModelStore: ViewModelStore
        get() = store

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        try {
            savedStateRegistryController.performAttach()
        } catch (_: Exception) {}
        try {
            savedStateRegistryController.performRestore(null)
        } catch (_: Exception) {}
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        audioEngine = MechanicalAudioEngine(this)
    }

    override fun onEvaluateInputViewShown(): Boolean {
        return true
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        if (lifecycleRegistry.currentState != Lifecycle.State.RESUMED) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
        super.onFinishInputView(finishingInput)
    }

    private fun handleBackspace() {
        val ic = currentInputConnection ?: return
        val selected = ic.getSelectedText(0)
        if (!selected.isNullOrEmpty()) {
            ic.commitText("", 1)
        } else {
            val before = ic.getTextBeforeCursor(1, 0)
            if (before.isNullOrEmpty()) {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
            } else {
                ic.deleteSurroundingText(1, 0)
            }
        }
    }

    override fun onCreateInputView(): View {
        window?.window?.decorView?.let { decor ->
            decor.setViewTreeLifecycleOwner(this)
            decor.setViewTreeViewModelStoreOwner(this)
            decor.setViewTreeSavedStateRegistryOwner(this)
        }

        if (lifecycleRegistry.currentState != Lifecycle.State.RESUMED) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }

        val composeView = ComposeView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(this@MechBoardImeService))
            setViewTreeLifecycleOwner(this@MechBoardImeService)
            setViewTreeViewModelStoreOwner(this@MechBoardImeService)
            setViewTreeSavedStateRegistryOwner(this@MechBoardImeService)

            setContent {
                MaterialTheme(
                    colorScheme = darkColorScheme(
                        primary = Color(0xFF00E676),
                        background = Color(0xFF000000),
                        surface = Color(0xFF141A21)
                    )
                ) {
                    var currentMode by remember { mutableStateOf(KeyboardMode.QWERTY) }
                    var isShiftActive by remember { mutableStateOf(false) }
                    var isCapsLock by remember { mutableStateOf(false) }
                    var isAltSymbols by remember { mutableStateOf(false) }
                    var currentTheme by remember { mutableStateOf(KeyboardThemeType.AMOLED_BLACK) }
                    var currentSwitch by remember { mutableStateOf(SwitchType.CREAM_THOCK) }
                    var iconPackType by remember { mutableStateOf(IconPackType.WHATSAPP_EXPRESSIVE) }
                    var isSoundOn by remember { mutableStateOf(true) }
                    var isHapticOn by remember { mutableStateOf(true) }
                    var isAutocorrectOn by remember { mutableStateOf(true) }
                    var keyHeight by remember { mutableStateOf(50.dp) }
                    var showSettingsDialog by remember { mutableStateOf(false) }
                    var activeWord by remember { mutableStateOf("") }
                    val currentSuggestions = remember(activeWord, isAutocorrectOn) {
                        if (activeWord.isBlank()) {
                            listOf("the", "to", "and", "hello", "keyboard")
                        } else {
                            AutocorrectEngine.getCorrections(activeWord).map { it.word }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(currentTheme.backgroundHex))
                    ) {
                        GboardTopBar(
                            iconPackType = iconPackType,
                            theme = currentTheme,
                            currentSwitch = currentSwitch,
                            keyboardMode = currentMode,
                            isSoundOn = isSoundOn,
                            activeText = activeWord,
                            suggestions = currentSuggestions,
                            isVoiceTyping = false,
                            onSuggestionClick = { word ->
                                audioEngine.playKeyPressSound(currentSwitch)
                                if (activeWord.isNotEmpty()) {
                                    currentInputConnection?.deleteSurroundingText(activeWord.length, 0)
                                }
                                currentInputConnection?.commitText("$word ", 1)
                                activeWord = ""
                            },
                            onToggleIconPack = {
                                iconPackType = when (iconPackType) {
                                    IconPackType.WHATSAPP_EXPRESSIVE -> IconPackType.ANDROID_17
                                    IconPackType.ANDROID_17 -> IconPackType.IOS_SF
                                    IconPackType.IOS_SF -> IconPackType.RETRO_PIXEL_95
                                    IconPackType.RETRO_PIXEL_95 -> IconPackType.WHATSAPP_EXPRESSIVE
                                }
                            },
                            onOpenSwitchStudio = {
                                currentSwitch = when (currentSwitch) {
                                    SwitchType.CREAM_THOCK -> SwitchType.BLUE_CLICKY
                                    SwitchType.BLUE_CLICKY -> SwitchType.BROWN_TACTILE
                                    SwitchType.BROWN_TACTILE -> SwitchType.RED_LINEAR
                                    SwitchType.RED_LINEAR -> SwitchType.MODEL_M_SPRING
                                    SwitchType.MODEL_M_SPRING -> SwitchType.CREAM_THOCK
                                }
                                audioEngine.playKeyPressSound(currentSwitch)
                            },
                            onOpenThemePicker = {
                                val allThemes = KeyboardThemeType.values()
                                val nextIndex = (allThemes.indexOf(currentTheme) + 1) % allThemes.size
                                currentTheme = allThemes[nextIndex]
                            },
                            onToggleSound = {
                                isSoundOn = !isSoundOn
                                audioEngine.isSoundEnabled = isSoundOn
                            },
                            onOpenEmoji = {
                                currentMode = if (currentMode == KeyboardMode.EMOJI_DRAWER) KeyboardMode.QWERTY else KeyboardMode.EMOJI_DRAWER
                            },
                            onOpenAttachments = { },
                            onOpenClipboard = { },
                            onToggleVoiceTyping = { },
                            onFormatText = { wrapper ->
                                currentInputConnection?.commitText(wrapper, 1)
                            },
                            onOpenSettings = {
                                showSettingsDialog = true
                            }
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(currentTheme.backgroundHex))
                        ) {
                            when (currentMode) {
                                KeyboardMode.QWERTY -> {
                                    GboardQwertyView(
                                        iconPackType = iconPackType,
                                        theme = currentTheme,
                                        isShiftActive = isShiftActive,
                                        isCapsLock = isCapsLock,
                                        keyHeight = keyHeight,
                                        onKeyPressed = { key ->
                                            audioEngine.playKeyPressSound(currentSwitch)
                                            if (key == " ") {
                                                val candidates = AutocorrectEngine.getCorrections(activeWord)
                                                val autoCorrect = candidates.firstOrNull { it.isAutoCorrect }
                                                if (isAutocorrectOn && autoCorrect != null && activeWord.isNotEmpty() && !activeWord.equals(autoCorrect.word, ignoreCase = true)) {
                                                    currentInputConnection?.deleteSurroundingText(activeWord.length, 0)
                                                    currentInputConnection?.commitText("${autoCorrect.word} ", 1)
                                                } else {
                                                    currentInputConnection?.commitText(" ", 1)
                                                }
                                                activeWord = ""
                                            } else {
                                                activeWord += key
                                                currentInputConnection?.commitText(key, 1)
                                            }
                                            if (isShiftActive && !isCapsLock) {
                                                isShiftActive = false
                                            }
                                        },
                                        onBackspace = {
                                            audioEngine.playKeyPressSound(currentSwitch)
                                            if (activeWord.isNotEmpty()) {
                                                activeWord = activeWord.dropLast(1)
                                            }
                                            handleBackspace()
                                        },
                                        onSendOrEnter = {
                                            activeWord = ""
                                            audioEngine.playKeyPressSound(currentSwitch)
                                            currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                                            currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
                                        },
                                        onToggleShift = {
                                            if (isShiftActive) {
                                                if (!isCapsLock) isCapsLock = true else { isShiftActive = false; isCapsLock = false }
                                            } else {
                                                isShiftActive = true
                                            }
                                        },
                                        onSwitchMode = { currentMode = it }
                                    )
                                }
                                KeyboardMode.SYMBOLS_123, KeyboardMode.SYMBOLS_ALT -> {
                                    GboardSymbolsView(
                                        isAltSymbols = isAltSymbols,
                                        iconPackType = iconPackType,
                                        theme = currentTheme,
                                        keyHeight = keyHeight,
                                        onKeyPressed = { key ->
                                            audioEngine.playKeyPressSound(currentSwitch)
                                            currentInputConnection?.commitText(key, 1)
                                        },
                                        onBackspace = {
                                            audioEngine.playKeyPressSound(currentSwitch)
                                            handleBackspace()
                                        },
                                        onSendOrEnter = {
                                            audioEngine.playKeyPressSound(currentSwitch)
                                            currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                                            currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
                                        },
                                        onToggleAltSymbols = { isAltSymbols = !isAltSymbols },
                                        onSwitchMode = { currentMode = it }
                                    )
                                }
                                KeyboardMode.EMOJI_DRAWER, KeyboardMode.STICKERS_DRAWER -> {
                                    GboardEmojiStickerDrawer(
                                        iconPackType = iconPackType,
                                        theme = currentTheme,
                                        onEmojiSelected = { emoji ->
                                            audioEngine.playKeyPressSound(currentSwitch)
                                            currentInputConnection?.commitText(emoji, 1)
                                        },
                                        onBackspace = {
                                            audioEngine.playKeyPressSound(currentSwitch)
                                            handleBackspace()
                                        },
                                        onCloseDrawer = { currentMode = KeyboardMode.QWERTY }
                                    )
                                }
                                else -> {
                                    GboardQwertyView(
                                        iconPackType = iconPackType,
                                        theme = currentTheme,
                                        isShiftActive = isShiftActive,
                                        isCapsLock = isCapsLock,
                                        keyHeight = keyHeight,
                                        onKeyPressed = { key ->
                                            audioEngine.playKeyPressSound(currentSwitch)
                                            currentInputConnection?.commitText(key, 1)
                                        },
                                        onBackspace = {
                                            handleBackspace()
                                        },
                                        onSendOrEnter = {
                                            currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                                            currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
                                        },
                                        onToggleShift = { isShiftActive = !isShiftActive },
                                        onSwitchMode = { currentMode = it }
                                    )
                                }
                            }
                        }

                        if (showSettingsDialog) {
                            com.example.ui.components.KeyboardSettingsDialog(
                                currentKeyHeight = keyHeight,
                                currentSwitch = currentSwitch,
                                currentTheme = currentTheme,
                                currentIconPack = iconPackType,
                                soundEngine = audioEngine,
                                isSoundOn = isSoundOn,
                                isHapticOn = isHapticOn,
                                isAutocorrectOn = isAutocorrectOn,
                                isImeEnabled = true,
                                isImeSelected = true,
                                onUpdateKeyHeight = { keyHeight = it },
                                onSelectSwitch = { currentSwitch = it },
                                onSelectTheme = { currentTheme = it },
                                onSelectIconPack = { iconPackType = it },
                                onToggleSound = {
                                    isSoundOn = it
                                    audioEngine.isSoundEnabled = it
                                },
                                onToggleHaptic = {
                                    isHapticOn = it
                                    audioEngine.isHapticEnabled = it
                                },
                                onToggleAutocorrect = {
                                    isAutocorrectOn = it
                                },
                                onUpdateVolume = { audioEngine.volumeLevel = it },
                                onUpdateHapticStrength = { audioEngine.hapticStrength = it },
                                onOpenDefaultKeyboardSetup = { },
                                onDismiss = { showSettingsDialog = false }
                            )
                        }
                    }
                }
            }
        }
        return composeView
    }

    override fun onDestroy() {
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }
        store.clear()
        super.onDestroy()
    }
}
