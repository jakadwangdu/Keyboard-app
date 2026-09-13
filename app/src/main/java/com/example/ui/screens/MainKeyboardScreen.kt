package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AttachmentType
import com.example.model.IconPackType
import com.example.model.KeyboardMode
import com.example.ui.components.*
import com.example.ui.keyboard.GboardEmojiStickerDrawer
import com.example.ui.keyboard.GboardQwertyView
import com.example.ui.keyboard.GboardSymbolsView
import com.example.viewmodel.KeyboardViewModel

@Composable
fun MainKeyboardScreen(
    viewModel: KeyboardViewModel,
    modifier: Modifier = Modifier
) {
    val activeText by viewModel.activeText.collectAsStateWithLifecycle()
    val keyboardMode by viewModel.keyboardMode.collectAsStateWithLifecycle()
    val iconPackType by viewModel.iconPackType.collectAsStateWithLifecycle()
    val theme by viewModel.keyboardTheme.collectAsStateWithLifecycle()
    val currentSwitch by viewModel.currentSwitch.collectAsStateWithLifecycle()
    val isShiftActive by viewModel.isShiftActive.collectAsStateWithLifecycle()
    val isCapsLock by viewModel.isCapsLock.collectAsStateWithLifecycle()
    val isAltSymbols by viewModel.isAltSymbols.collectAsStateWithLifecycle()
    val isVoiceTyping by viewModel.isVoiceTyping.collectAsStateWithLifecycle()
    val isSoundOn by viewModel.isSoundOn.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val clipboardHistory by viewModel.clipboardHistory.collectAsStateWithLifecycle()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showSwitchDialog by remember { mutableStateOf(false) }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showClipboardSheet by remember { mutableStateOf(false) }
    var showDefaultKeyboardDialog by remember { mutableStateOf(false) }

    val showImeDialogFromVm by viewModel.showImeSetupDialog.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(theme.backgroundHex),
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(theme.backgroundHex))
        ) {
            // 1. WhatsApp / Modern Conversation Chat Viewport
            ConversationChatView(
                messages = messages,
                activeText = activeText,
                iconPackType = iconPackType,
                theme = theme,
                currentSwitch = currentSwitch,
                onSendMessage = { viewModel.sendMessage() },
                onOpenAttachments = { showAttachmentSheet = true },
                onOpenEmoji = { viewModel.setMode(KeyboardMode.EMOJI_DRAWER) },
                onClearChat = { viewModel.clearChat() },
                onReactionClick = { msgId, reaction -> viewModel.addReaction(msgId, reaction) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            // 2. Gboard Quick-Action Toolbar (predictive word strip + icon actions)
            GboardTopBar(
                iconPackType = iconPackType,
                theme = theme,
                currentSwitch = currentSwitch,
                keyboardMode = keyboardMode,
                isSoundOn = isSoundOn,
                activeText = activeText,
                suggestions = suggestions,
                isVoiceTyping = isVoiceTyping,
                onSuggestionClick = { viewModel.applySuggestion(it) },
                onToggleIconPack = {
                    val nextPack = when (iconPackType) {
                        IconPackType.RETRO_PIXEL_95 -> IconPackType.WHATSAPP_EXPRESSIVE
                        IconPackType.WHATSAPP_EXPRESSIVE -> IconPackType.ANDROID_17
                        IconPackType.ANDROID_17 -> IconPackType.IOS_SF
                        IconPackType.IOS_SF -> IconPackType.WHATSAPP_EXPRESSIVE
                    }
                    viewModel.setIconPack(nextPack)
                },
                onOpenSwitchStudio = { showSwitchDialog = true },
                onOpenThemePicker = { showThemeDialog = true },
                onToggleSound = { viewModel.toggleSound() },
                onOpenEmoji = {
                    if (keyboardMode == KeyboardMode.EMOJI_DRAWER) {
                        viewModel.setMode(KeyboardMode.QWERTY)
                    } else {
                        viewModel.setMode(KeyboardMode.EMOJI_DRAWER)
                    }
                },
                onOpenAttachments = { showAttachmentSheet = true },
                onOpenClipboard = { showClipboardSheet = true },
                onToggleVoiceTyping = { viewModel.toggleVoiceTyping() },
                onFormatText = { wrapper -> viewModel.formatText(wrapper) },
                onOpenDefaultKeyboardSetup = { showDefaultKeyboardDialog = true }
            )

            // 3. Gboard Mechanical Keyboard Deck (QWERTY / Symbols / Emojis)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(theme.backgroundHex))
            ) {
                AnimatedContent(
                    targetState = keyboardMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "keyboard_view_transition"
                ) { mode ->
                    when (mode) {
                        KeyboardMode.QWERTY -> {
                            GboardQwertyView(
                                iconPackType = iconPackType,
                                theme = theme,
                                isShiftActive = isShiftActive,
                                isCapsLock = isCapsLock,
                                onKeyPressed = { viewModel.typeKey(it) },
                                onBackspace = { viewModel.backspace() },
                                onSendOrEnter = { viewModel.sendMessage() },
                                onToggleShift = { viewModel.toggleShift() },
                                onSwitchMode = { viewModel.setMode(it) }
                            )
                        }
                        KeyboardMode.SYMBOLS_123, KeyboardMode.SYMBOLS_ALT -> {
                            GboardSymbolsView(
                                isAltSymbols = isAltSymbols,
                                iconPackType = iconPackType,
                                theme = theme,
                                onKeyPressed = { viewModel.typeKey(it) },
                                onBackspace = { viewModel.backspace() },
                                onSendOrEnter = { viewModel.sendMessage() },
                                onToggleAltSymbols = { viewModel.toggleAltSymbols() },
                                onSwitchMode = { viewModel.setMode(it) }
                            )
                        }
                        KeyboardMode.EMOJI_DRAWER, KeyboardMode.STICKERS_DRAWER -> {
                            GboardEmojiStickerDrawer(
                                iconPackType = iconPackType,
                                theme = theme,
                                onEmojiSelected = { viewModel.typeKey(it) },
                                onBackspace = { viewModel.backspace() },
                                onCloseDrawer = { viewModel.setMode(KeyboardMode.QWERTY) }
                            )
                        }
                        else -> {
                            GboardQwertyView(
                                iconPackType = iconPackType,
                                theme = theme,
                                isShiftActive = isShiftActive,
                                isCapsLock = isCapsLock,
                                onKeyPressed = { viewModel.typeKey(it) },
                                onBackspace = { viewModel.backspace() },
                                onSendOrEnter = { viewModel.sendMessage() },
                                onToggleShift = { viewModel.toggleShift() },
                                onSwitchMode = { viewModel.setMode(it) }
                            )
                        }
                    }
                }
            }
        }

        // Modals & Dialogs
        if (showAttachmentSheet) {
            WhatsAppAttachmentSheet(
                iconPackType = iconPackType,
                theme = theme,
                onSelectAttachment = { type -> viewModel.sendAttachment(type) },
                onDismiss = { showAttachmentSheet = false }
            )
        }

        if (showSwitchDialog) {
            SwitchStudioDialog(
                currentSwitch = currentSwitch,
                currentTheme = theme,
                soundEngine = viewModel.audioEngine,
                onSelectSwitch = { viewModel.setSwitch(it) },
                onDismiss = { showSwitchDialog = false }
            )
        }

        if (showThemeDialog) {
            ThemePickerSheet(
                currentIconPack = iconPackType,
                currentTheme = theme,
                onSelectIconPack = { viewModel.setIconPack(it) },
                onSelectTheme = { viewModel.setTheme(it) },
                onDismiss = { showThemeDialog = false }
            )
        }

        if (showClipboardSheet) {
            ClipboardSheet(
                clipboardList = clipboardHistory,
                theme = theme,
                onPasteItem = { text -> viewModel.typeKey(text) },
                onTogglePin = { id -> viewModel.togglePinClipboard(id) },
                onClearClipboard = { /* clear */ },
                onDismiss = { showClipboardSheet = false }
            )
        }

        if (showDefaultKeyboardDialog || showImeDialogFromVm) {
            DefaultKeyboardSetupDialog(
                theme = theme,
                isImeEnabled = viewModel.isImeEnabled(),
                isImeSelected = viewModel.isImeSelected(),
                onDismiss = {
                    showDefaultKeyboardDialog = false
                    viewModel.setShowImeSetupDialog(false)
                }
            )
        }
    }
}
