package app.aura.keyboard.service

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.aura.keyboard.R
import app.aura.keyboard.audio.FeedbackKind
import app.aura.keyboard.audio.FeedbackManager
import app.aura.keyboard.logic.ClipboardStore
import app.aura.keyboard.logic.InputConnectionTarget
import app.aura.keyboard.logic.InputController
import app.aura.keyboard.model.KeyKind
import app.aura.keyboard.model.KeySizePreset
import app.aura.keyboard.model.KeyboardLayer
import app.aura.keyboard.model.KeyboardPalette
import app.aura.keyboard.model.PanelMode
import app.aura.keyboard.model.ThemeMode
import app.aura.keyboard.prefs.KeyboardPreferences
import app.aura.keyboard.ui.AuraCardLayout
import app.aura.keyboard.ui.ClipboardPanelView
import app.aura.keyboard.ui.EmojiPanelView
import app.aura.keyboard.ui.KeyboardView
import app.aura.keyboard.ui.ToolbarView
import app.aura.keyboard.ui.VoiceInputActivity
import app.aura.keyboard.ui.VoiceInputBus

class AuraKeyboardService : InputMethodService() {

    private lateinit var prefs: KeyboardPreferences
    private lateinit var clipboardStore: ClipboardStore
    private lateinit var feedbackManager: FeedbackManager
    private lateinit var inputController: InputController

    private var clipboardManager: ClipboardManager? = null
    private var clipListenerRegistered = false

    private var cardLayout: AuraCardLayout? = null
    private var toolbarView: ToolbarView? = null
    private var keyboardView: KeyboardView? = null
    private var clipboardPanelView: ClipboardPanelView? = null
    private var emojiPanelView: EmojiPanelView? = null

    private var panelMode: PanelMode = PanelMode.KEYBOARD
    private var bottomInsetPx: Int = 0

    private val clipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
        captureCurrentPrimaryClip()
    }

    override fun onCreate() {
        super.onCreate()
        prefs = KeyboardPreferences(this)
        clipboardStore = ClipboardStore(this)
        feedbackManager = FeedbackManager(this).apply {
            soundEnabled = prefs.soundEnabled
            hapticsEnabled = prefs.hapticsEnabled
        }
        inputController = InputController(InputConnectionTarget { currentInputConnection })

        clipboardManager = getSystemService(ClipboardManager::class.java)
        clipboardManager?.let { cm ->
            cm.addPrimaryClipChangedListener(clipChangedListener)
            clipListenerRegistered = true
        }

        VoiceInputBus.registerListener { spokenText ->
            inputController.commitDirectText("$spokenText ")
            syncUi()
        }
    }

    override fun onCreateInputView(): View {
        val density = resources.displayMetrics.density
        val sideInset = (4f * density).toInt()
        val topInset = (4f * density).toInt()

        val rootWrapper = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(sideInset, topInset, sideInset, 0)
        }

        val card = AuraCardLayout(this).also { cardLayout = it }

        val toolbar = ToolbarView(this).apply {
            listener = object : ToolbarView.Listener {
                override fun onClipboardClicked() {
                    feedbackManager.trigger(FeedbackKind.TAP)
                    panelMode = if (panelMode == PanelMode.CLIPBOARD) PanelMode.KEYBOARD else PanelMode.CLIPBOARD
                    if (panelMode == PanelMode.CLIPBOARD) {
                        captureCurrentPrimaryClip()
                    }
                    syncUi()
                }

                override fun onEmojiClicked() {
                    feedbackManager.trigger(FeedbackKind.TAP)
                    panelMode = if (panelMode == PanelMode.EMOJI) PanelMode.KEYBOARD else PanelMode.EMOJI
                    syncUi()
                }

                override fun onVoiceClicked() {
                    feedbackManager.trigger(FeedbackKind.TAP)
                    launchVoiceTyping()
                }

                override fun onSizeSelected(preset: KeySizePreset) {
                    feedbackManager.trigger(FeedbackKind.TAP)
                    prefs.keySizePreset = preset
                    syncUi()
                }
            }
        }.also { toolbarView = it }

        card.addView(
            toolbar,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val panelStack = FrameLayout(this)

        val kb = KeyboardView(this).apply {
            id = R.id.keyboard_view
            listener = object : KeyboardView.Listener {
                override fun onKeyPressedDown(kind: KeyKind) {
                    val fb = when (kind) {
                        KeyKind.SPACE -> FeedbackKind.SPACE
                        KeyKind.BACKSPACE -> FeedbackKind.DELETE
                        else -> FeedbackKind.TAP
                    }
                    feedbackManager.trigger(fb)
                }

                override fun onCharacterKey(charText: String, chordShiftHeld: Boolean) {
                    inputController.commitCharacter(charText, chordShiftHeld)
                    syncUi()
                }

                override fun onShiftTap(nowMs: Long) {
                    inputController.onShiftTap(nowMs)
                    syncUi()
                }

                override fun onShiftLongPress() {
                    feedbackManager.trigger(FeedbackKind.SPACE)
                    inputController.onShiftLongPress()
                    syncUi()
                }

                override fun onShiftChordReleased() {
                    inputController.onShiftChordReleased()
                    syncUi()
                }

                override fun onBackspaceTap() {
                    inputController.onBackspaceTap()
                    syncUi()
                }

                override fun onDeleteWord() {
                    feedbackManager.trigger(FeedbackKind.DELETE)
                    inputController.onDeleteWord()
                    syncUi()
                }

                override fun onSpaceTap(nowMs: Long) {
                    inputController.onSpaceTap(nowMs)
                    syncUi()
                }

                override fun onTrackpadStepHorizontal(steps: Int) {
                    feedbackManager.trigger(FeedbackKind.TRACKPAD_TICK)
                    inputController.onTrackpadStepHorizontal(steps)
                    syncUi()
                }

                override fun onTrackpadStepVertical(lines: Int) {
                    feedbackManager.trigger(FeedbackKind.TRACKPAD_TICK)
                    inputController.onTrackpadStepVertical(lines)
                    syncUi()
                }

                override fun onLayerSwitch(targetLayer: KeyboardLayer) {
                    inputController.setLayer(targetLayer)
                    syncUi()
                }

                override fun onEmojiKey() {
                    panelMode = PanelMode.EMOJI
                    syncUi()
                }

                override fun onReturnKey() {
                    inputController.onReturnTap()
                    syncUi()
                }
            }
        }.also { keyboardView = it }

        // Clipboard panel overlaps KeyboardView at its exact measured height
        val clipboardPanel = ClipboardPanelView(this).apply {
            isClickable = true
            visibility = View.GONE
            listener = object : ClipboardPanelView.Listener {
                override fun onPasteClip(text: String) {
                    feedbackManager.trigger(FeedbackKind.TAP)
                    inputController.commitDirectText(text)
                    panelMode = PanelMode.KEYBOARD
                    syncUi()
                }

                override fun onTogglePin(id: String) {
                    feedbackManager.trigger(FeedbackKind.TAP)
                    clipboardStore.togglePin(id)
                    syncUi()
                }

                override fun onDeleteClip(id: String) {
                    feedbackManager.trigger(FeedbackKind.DELETE)
                    clipboardStore.deleteClip(id)
                    syncUi()
                }

                override fun onClearUnpinned() {
                    feedbackManager.trigger(FeedbackKind.DELETE)
                    clipboardStore.clearUnpinned()
                    syncUi()
                }

                override fun onCloseClipboard() {
                    feedbackManager.trigger(FeedbackKind.TAP)
                    panelMode = PanelMode.KEYBOARD
                    syncUi()
                }
            }
        }.also { clipboardPanelView = it }

        val emojiPanel = EmojiPanelView(this).apply {
            isClickable = true
            visibility = View.GONE
            listener = object : EmojiPanelView.Listener {
                override fun onEmojiPicked(emoji: String) {
                    feedbackManager.trigger(FeedbackKind.TAP)
                    prefs.recordRecentEmoji(emoji)
                    inputController.commitDirectText(emoji)
                    syncUi()
                }

                override fun onBackspaceTap() {
                    feedbackManager.trigger(FeedbackKind.DELETE)
                    inputController.onBackspaceTap()
                    syncUi()
                }

                override fun onCloseEmoji() {
                    feedbackManager.trigger(FeedbackKind.TAP)
                    panelMode = PanelMode.KEYBOARD
                    syncUi()
                }
            }
        }.also { emojiPanelView = it }

        panelStack.addView(
            kb,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        )
        panelStack.addView(
            clipboardPanel,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        panelStack.addView(
            emojiPanel,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        card.addView(
            panelStack,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        ViewCompat.setOnApplyWindowInsetsListener(card) { v, insets ->
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            bottomInsetPx = navBars.bottom
            v.setPadding(0, 0, 0, bottomInsetPx)
            insets
        }

        rootWrapper.addView(
            card,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
        )

        syncUi()
        return rootWrapper
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        feedbackManager.soundEnabled = prefs.soundEnabled
        feedbackManager.hapticsEnabled = prefs.hapticsEnabled
        inputController.onStartInput(info)
        panelMode = PanelMode.KEYBOARD

        VoiceInputBus.consumePending()?.let { pendingVoice ->
            inputController.commitDirectText("$pendingVoice ")
        }

        syncUi()
    }

    override fun onWindowShown() {
        super.onWindowShown()
        feedbackManager.soundEnabled = prefs.soundEnabled
        feedbackManager.hapticsEnabled = prefs.hapticsEnabled
        VoiceInputBus.consumePending()?.let { pendingVoice ->
            inputController.commitDirectText("$pendingVoice ")
        }
        syncUi()
    }

    override fun onUpdateSelection(
        oldSelStart: Int,
        oldSelEnd: Int,
        newSelStart: Int,
        newSelEnd: Int,
        candidatesStart: Int,
        candidatesEnd: Int
    ) {
        super.onUpdateSelection(
            oldSelStart,
            oldSelEnd,
            newSelStart,
            newSelEnd,
            candidatesStart,
            candidatesEnd
        )
        inputController.refreshAutoCapitalization()
        syncUi()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        syncUi()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        panelMode = PanelMode.KEYBOARD
        inputController.cancelSpaceDoubleTap()
    }

    override fun onDestroy() {
        VoiceInputBus.registerListener(null)
        if (clipListenerRegistered) {
            clipboardManager?.removePrimaryClipChangedListener(clipChangedListener)
            clipListenerRegistered = false
        }
        feedbackManager.release()
        super.onDestroy()
    }

    private fun launchVoiceTyping() {
        val intent = Intent(this, VoiceInputActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            startActivity(intent)
        } catch (_: Exception) {
        }
    }

    private fun captureCurrentPrimaryClip() {
        if (inputController.isPasswordField) return
        val cm = clipboardManager ?: return
        val clip = try {
            cm.primaryClip
        } catch (_: SecurityException) {
            null
        } ?: return

        val desc = clip.description
        if (isSensitiveClip(desc)) return

        if (clip.itemCount <= 0) return
        val text = try {
            clip.getItemAt(0)?.coerceToText(this)?.toString()
        } catch (_: Exception) {
            null
        } ?: return

        clipboardStore.recordClip(
            text = text,
            isPasswordField = inputController.isPasswordField,
            isSensitive = false
        )
        if (panelMode == PanelMode.CLIPBOARD) {
            clipboardPanelView?.update(resolvePalette(), clipboardStore.getClips())
        }
    }

    private fun isSensitiveClip(description: ClipDescription?): Boolean {
        val extras = description?.extras ?: return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return extras.getBoolean(ClipDescription.EXTRA_IS_SENSITIVE, false)
        }
        return extras.getBoolean(LEGACY_EXTRA_IS_SENSITIVE, false)
    }

    private fun resolvePalette(): KeyboardPalette {
        return when (prefs.themeMode) {
            ThemeMode.DARK -> KeyboardPalette.DARK_ESPRESSO
            ThemeMode.LIGHT -> KeyboardPalette.LIGHT_CREAM
            ThemeMode.AUTO -> {
                val nightMask = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (nightMask == Configuration.UI_MODE_NIGHT_NO) {
                    KeyboardPalette.LIGHT_CREAM
                } else {
                    KeyboardPalette.DARK_ESPRESSO
                }
            }
        }
    }

    private fun syncUi() {
        val palette = resolvePalette()
        val preset = prefs.keySizePreset

        cardLayout?.applyPalette(palette)
        toolbarView?.updateState(palette, panelMode, preset)
        keyboardView?.configure(
            newPalette = palette,
            newLayer = inputController.layer,
            newShiftState = inputController.shiftState,
            newImeActionKind = inputController.imeActionKind,
            newSizePreset = preset
        )

        // Keep KeyboardView INVISIBLE when drawers are open so FrameLayout preserves exact card height
        keyboardView?.visibility = if (panelMode == PanelMode.KEYBOARD) View.VISIBLE else View.INVISIBLE
        clipboardPanelView?.visibility = if (panelMode == PanelMode.CLIPBOARD) View.VISIBLE else View.GONE
        emojiPanelView?.visibility = if (panelMode == PanelMode.EMOJI) View.VISIBLE else View.GONE

        if (panelMode == PanelMode.CLIPBOARD) {
            clipboardPanelView?.update(palette, clipboardStore.getClips())
        } else if (panelMode == PanelMode.EMOJI) {
            emojiPanelView?.update(palette, prefs.getRecentEmojis())
        }
    }

    companion object {
        private const val LEGACY_EXTRA_IS_SENSITIVE = "android.content.extra.IS_SENSITIVE"
    }
}
