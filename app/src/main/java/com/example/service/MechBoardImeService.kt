package com.example.service

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import androidx.compose.ui.platform.ComposeView
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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        audioEngine = MechanicalAudioEngine(this)
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@MechBoardImeService)
            setViewTreeViewModelStoreOwner(this@MechBoardImeService)
            setViewTreeSavedStateRegistryOwner(this@MechBoardImeService)

            setContent {
                var currentMode by remember { mutableStateOf(KeyboardMode.QWERTY) }
                var isShiftActive by remember { mutableStateOf(false) }
                var isCapsLock by remember { mutableStateOf(false) }
                var isAltSymbols by remember { mutableStateOf(false) }
                val currentTheme: KeyboardThemeType = KeyboardThemeType.AMOLED_BLACK
                val currentSwitch: SwitchType = SwitchType.CREAM_THOCK
                val iconPackType: IconPackType = IconPackType.WHATSAPP_EXPRESSIVE
                var isSoundOn by remember { mutableStateOf(true) }

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
                        activeText = "",
                        suggestions = listOf("the", "to", "and", "hello", "keyboard"),
                        isVoiceTyping = false,
                        onSuggestionClick = { word ->
                            if (isSoundOn) audioEngine.playKeyPressSound(currentSwitch)
                            currentInputConnection?.commitText("$word ", 1)
                        },
                        onToggleIconPack = { },
                        onOpenSwitchStudio = { },
                        onOpenThemePicker = { },
                        onToggleSound = { isSoundOn = !isSoundOn },
                        onOpenEmoji = {
                            currentMode = if (currentMode == KeyboardMode.EMOJI_DRAWER) KeyboardMode.QWERTY else KeyboardMode.EMOJI_DRAWER
                        },
                        onOpenAttachments = { },
                        onOpenClipboard = { },
                        onToggleVoiceTyping = { },
                        onFormatText = { wrapper ->
                            currentInputConnection?.commitText(wrapper, 1)
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
                                    onKeyPressed = { key ->
                                        if (isSoundOn) audioEngine.playKeyPressSound(currentSwitch)
                                        currentInputConnection?.commitText(key, 1)
                                        if (isShiftActive && !isCapsLock) {
                                            isShiftActive = false
                                        }
                                    },
                                    onBackspace = {
                                        if (isSoundOn) audioEngine.playKeyPressSound(currentSwitch)
                                        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                                        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
                                    },
                                    onSendOrEnter = {
                                        if (isSoundOn) audioEngine.playKeyPressSound(currentSwitch)
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
                                    onKeyPressed = { key ->
                                        if (isSoundOn) audioEngine.playKeyPressSound(currentSwitch)
                                        currentInputConnection?.commitText(key, 1)
                                    },
                                    onBackspace = {
                                        if (isSoundOn) audioEngine.playKeyPressSound(currentSwitch)
                                        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                                        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
                                    },
                                    onSendOrEnter = {
                                        if (isSoundOn) audioEngine.playKeyPressSound(currentSwitch)
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
                                        if (isSoundOn) audioEngine.playKeyPressSound(currentSwitch)
                                        currentInputConnection?.commitText(emoji, 1)
                                    },
                                    onBackspace = {
                                        if (isSoundOn) audioEngine.playKeyPressSound(currentSwitch)
                                        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                                        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
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
                                    onKeyPressed = { key ->
                                        if (isSoundOn) audioEngine.playKeyPressSound(currentSwitch)
                                        currentInputConnection?.commitText(key, 1)
                                    },
                                    onBackspace = {
                                        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                                        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
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
                }
            }
        }
        return composeView
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
        super.onDestroy()
    }
}
