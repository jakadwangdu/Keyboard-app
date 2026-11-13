package app.aura.keyboard.logic

import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import app.aura.keyboard.model.ImeActionKind
import app.aura.keyboard.model.KeyboardLayer
import app.aura.keyboard.model.ShiftState

/**
 * Abstraction over InputConnection so every call is null-safe, crash-proof, and unit-testable.
 */
interface InputTarget {
    fun commitText(text: CharSequence, newCursorPosition: Int): Boolean
    fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean
    fun getTextBeforeCursor(length: Int, flags: Int): CharSequence?
    fun getSelectedText(flags: Int): CharSequence?
    fun getCursorCapsMode(reqModes: Int): Int
    fun performEditorAction(editorAction: Int): Boolean
    fun sendKeyEvent(event: KeyEvent): Boolean
    fun beginBatchEdit(): Boolean
    fun endBatchEdit(): Boolean
}

class InputConnectionTarget(
    private val provider: () -> InputConnection?
) : InputTarget {

    override fun commitText(text: CharSequence, newCursorPosition: Int): Boolean {
        val ic = provider() ?: return false
        return try {
            ic.commitText(text, newCursorPosition)
        } catch (_: RuntimeException) {
            false
        }
    }

    override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
        val ic = provider() ?: return false
        return try {
            ic.deleteSurroundingText(beforeLength, afterLength)
        } catch (_: RuntimeException) {
            false
        }
    }

    override fun getTextBeforeCursor(length: Int, flags: Int): CharSequence? {
        val ic = provider() ?: return null
        return try {
            ic.getTextBeforeCursor(length, flags)
        } catch (_: RuntimeException) {
            null
        }
    }

    override fun getSelectedText(flags: Int): CharSequence? {
        val ic = provider() ?: return null
        return try {
            ic.getSelectedText(flags)
        } catch (_: RuntimeException) {
            null
        }
    }

    override fun getCursorCapsMode(reqModes: Int): Int {
        val ic = provider() ?: return 0
        return try {
            ic.getCursorCapsMode(reqModes)
        } catch (_: RuntimeException) {
            0
        }
    }

    override fun performEditorAction(editorAction: Int): Boolean {
        val ic = provider() ?: return false
        return try {
            ic.performEditorAction(editorAction)
        } catch (_: RuntimeException) {
            false
        }
    }

    override fun sendKeyEvent(event: KeyEvent): Boolean {
        val ic = provider() ?: return false
        return try {
            ic.sendKeyEvent(event)
        } catch (_: RuntimeException) {
            false
        }
    }

    override fun beginBatchEdit(): Boolean {
        val ic = provider() ?: return false
        return try {
            ic.beginBatchEdit()
        } catch (_: RuntimeException) {
            false
        }
    }

    override fun endBatchEdit(): Boolean {
        val ic = provider() ?: return false
        return try {
            ic.endBatchEdit()
        } catch (_: RuntimeException) {
            false
        }
    }
}

class InputController(private val target: InputTarget) {

    var layer: KeyboardLayer = KeyboardLayer.LETTERS
        private set

    var shiftState: ShiftState = ShiftState.OFF
        private set

    var imeActionKind: ImeActionKind = ImeActionKind.RETURN
        private set

    var isPasswordField: Boolean = false
        private set

    var isNumericField: Boolean = false
        private set

    private var autoCapMask: Int = 0
    private var editorActionId: Int = EditorInfo.IME_ACTION_NONE
    private var lastShiftTapMs: Long = 0L
    private var lastSpaceTapMs: Long = 0L
    private var isShiftChordUsed: Boolean = false

    fun onStartInput(editorInfo: EditorInfo?) {
        lastShiftTapMs = 0L
        lastSpaceTapMs = 0L
        isShiftChordUsed = false

        if (editorInfo == null) {
            isPasswordField = false
            isNumericField = false
            autoCapMask = 0
            editorActionId = EditorInfo.IME_ACTION_NONE
            imeActionKind = ImeActionKind.RETURN
            layer = KeyboardLayer.LETTERS
            shiftState = ShiftState.OFF
            return
        }

        val inputType = editorInfo.inputType
        val inputClass = inputType and InputType.TYPE_MASK_CLASS
        val variation = inputType and InputType.TYPE_MASK_VARIATION

        isPasswordField = when (inputClass) {
            InputType.TYPE_CLASS_TEXT -> {
                variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                    variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                    variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
            }
            InputType.TYPE_CLASS_NUMBER -> {
                variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD
            }
            else -> false
        }

        isNumericField = inputClass == InputType.TYPE_CLASS_NUMBER ||
            inputClass == InputType.TYPE_CLASS_PHONE ||
            inputClass == InputType.TYPE_CLASS_DATETIME

        autoCapMask = if (inputClass == InputType.TYPE_CLASS_TEXT && !isPasswordField) {
            inputType and (
                InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS or
                    InputType.TYPE_TEXT_FLAG_CAP_WORDS or
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                )
        } else {
            0
        }

        val noEnterAction = (editorInfo.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0
        val action = editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
        editorActionId = if (noEnterAction) EditorInfo.IME_ACTION_NONE else action
        imeActionKind = when (editorActionId) {
            EditorInfo.IME_ACTION_SEND -> ImeActionKind.SEND
            EditorInfo.IME_ACTION_SEARCH -> ImeActionKind.SEARCH
            EditorInfo.IME_ACTION_GO -> ImeActionKind.GO
            EditorInfo.IME_ACTION_NEXT -> ImeActionKind.NEXT
            EditorInfo.IME_ACTION_DONE -> ImeActionKind.DONE
            else -> ImeActionKind.RETURN
        }

        layer = if (isNumericField) KeyboardLayer.NUMBER_PAD else KeyboardLayer.LETTERS
        shiftState = ShiftState.OFF
        refreshAutoCapitalization()
    }

    fun setLayer(newLayer: KeyboardLayer) {
        layer = newLayer
        if (newLayer == KeyboardLayer.LETTERS) {
            refreshAutoCapitalization()
        }
    }

    fun onShiftTap(nowMs: Long) {
        if (layer != KeyboardLayer.LETTERS) return
        val isDoubleTap = (nowMs - lastShiftTapMs) in 1L..DOUBLE_TAP_TIMEOUT_MS
        shiftState = when (shiftState) {
            ShiftState.OFF -> ShiftState.SHIFTED
            ShiftState.SHIFTED -> if (isDoubleTap) ShiftState.CAPS_LOCK else ShiftState.OFF
            ShiftState.CAPS_LOCK -> ShiftState.OFF
        }
        lastShiftTapMs = if (shiftState == ShiftState.CAPS_LOCK) 0L else nowMs
    }

    fun onShiftLongPress() {
        if (layer != KeyboardLayer.LETTERS) return
        shiftState = if (shiftState == ShiftState.CAPS_LOCK) ShiftState.OFF else ShiftState.CAPS_LOCK
        lastShiftTapMs = 0L
    }

    fun markShiftChordUsed() {
        isShiftChordUsed = true
    }

    fun onShiftChordReleased() {
        if (isShiftChordUsed) {
            isShiftChordUsed = false
            if (shiftState == ShiftState.SHIFTED) {
                shiftState = ShiftState.OFF
                refreshAutoCapitalization()
            }
        }
    }

    fun commitCharacter(rawChar: String, chordShiftHeld: Boolean = false) {
        if (rawChar.isEmpty()) return
        lastSpaceTapMs = 0L
        val shouldUpper = layer == KeyboardLayer.LETTERS &&
            (shiftState != ShiftState.OFF || chordShiftHeld)
        val out = if (shouldUpper) rawChar.uppercase() else rawChar
        target.commitText(out, 1)

        if (chordShiftHeld) {
            isShiftChordUsed = true
        } else if (layer == KeyboardLayer.LETTERS && shiftState == ShiftState.SHIFTED) {
            shiftState = ShiftState.OFF
            refreshAutoCapitalization()
        } else if (layer == KeyboardLayer.LETTERS && shiftState != ShiftState.CAPS_LOCK) {
            refreshAutoCapitalization()
        }
    }

    fun commitDirectText(text: String) {
        if (text.isEmpty()) return
        lastSpaceTapMs = 0L
        target.commitText(text, 1)
        if (layer == KeyboardLayer.LETTERS && shiftState != ShiftState.CAPS_LOCK) {
            refreshAutoCapitalization()
        }
    }

    /**
     * Inserts a space or converts a double-tap space after a word into ". ".
     * Returns true if converted to ". ".
     */
    fun onSpaceTap(nowMs: Long): Boolean {
        if (!isPasswordField && !isNumericField &&
            lastSpaceTapMs > 0L && (nowMs - lastSpaceTapMs) <= DOUBLE_TAP_TIMEOUT_MS
        ) {
            val before = target.getTextBeforeCursor(3, 0)?.toString().orEmpty()
            if (before.length >= 2 && before.last() == ' ') {
                val charBeforeSpace = before[before.length - 2]
                if (!charBeforeSpace.isWhitespace() && charBeforeSpace != '.' &&
                    charBeforeSpace != '!' && charBeforeSpace != '?'
                ) {
                    target.beginBatchEdit()
                    target.deleteSurroundingText(1, 0)
                    target.commitText(". ", 1)
                    target.endBatchEdit()
                    lastSpaceTapMs = 0L
                    refreshAutoCapitalization()
                    return true
                }
            }
        }

        target.commitText(" ", 1)
        lastSpaceTapMs = nowMs
        refreshAutoCapitalization()
        return false
    }

    fun cancelSpaceDoubleTap() {
        lastSpaceTapMs = 0L
    }

    fun onBackspaceTap() {
        lastSpaceTapMs = 0L
        val selected = target.getSelectedText(0)
        if (!selected.isNullOrEmpty()) {
            target.commitText("", 1)
            refreshAutoCapitalization()
            return
        }

        val before = target.getTextBeforeCursor(4, 0)?.toString().orEmpty()
        if (before.isEmpty()) {
            target.deleteSurroundingText(1, 0)
            refreshAutoCapitalization()
            return
        }

        val deleteCount = graphemeDeleteLength(before)
        target.deleteSurroundingText(deleteCount, 0)
        refreshAutoCapitalization()
    }

    fun onDeleteWord() {
        lastSpaceTapMs = 0L
        val selected = target.getSelectedText(0)
        if (!selected.isNullOrEmpty()) {
            target.commitText("", 1)
            refreshAutoCapitalization()
            return
        }

        val before = target.getTextBeforeCursor(64, 0)?.toString().orEmpty()
        if (before.isEmpty()) return

        var idx = before.length
        while (idx > 0 && before[idx - 1].isWhitespace() && before[idx - 1] != '\n') {
            idx--
        }
        if (idx > 0 && before[idx - 1] == '\n') {
            if (idx == before.length) {
                idx--
            }
        } else {
            while (idx > 0 && !before[idx - 1].isWhitespace()) {
                idx--
            }
        }

        val deleteCount = (before.length - idx).coerceAtLeast(1)
        target.beginBatchEdit()
        target.deleteSurroundingText(deleteCount, 0)
        target.endBatchEdit()
        refreshAutoCapitalization()
    }

    fun onTrackpadStepHorizontal(steps: Int) {
        if (steps == 0) return
        lastSpaceTapMs = 0L
        val keyCode = if (steps > 0) KeyEvent.KEYCODE_DPAD_RIGHT else KeyEvent.KEYCODE_DPAD_LEFT
        val count = kotlin.math.abs(steps)
        for (i in 0 until count) {
            target.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            target.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
        }
        refreshAutoCapitalization()
    }

    fun onTrackpadStepVertical(lines: Int) {
        if (lines == 0) return
        lastSpaceTapMs = 0L
        val keyCode = if (lines > 0) KeyEvent.KEYCODE_DPAD_DOWN else KeyEvent.KEYCODE_DPAD_UP
        val count = kotlin.math.abs(lines)
        for (i in 0 until count) {
            target.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            target.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
        }
        refreshAutoCapitalization()
    }

    fun onReturnTap() {
        lastSpaceTapMs = 0L
        if (editorActionId != EditorInfo.IME_ACTION_NONE &&
            editorActionId != EditorInfo.IME_ACTION_UNSPECIFIED
        ) {
            val performed = target.performEditorAction(editorActionId)
            if (performed) return
        }
        target.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
        target.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        refreshAutoCapitalization()
    }

    fun refreshAutoCapitalization() {
        if (layer != KeyboardLayer.LETTERS || shiftState == ShiftState.CAPS_LOCK) return
        if (autoCapMask == 0 || isPasswordField) {
            shiftState = ShiftState.OFF
            return
        }
        val capsMode = target.getCursorCapsMode(autoCapMask)
        shiftState = if (capsMode != 0) ShiftState.SHIFTED else ShiftState.OFF
    }

    private fun graphemeDeleteLength(before: String): Int {
        val len = before.length
        if (len <= 1) return len
        val last = before[len - 1]
        // Handle variation selector (FE0F) preceded by BMP char or surrogate pair
        if (last == '\uFE0F' && len >= 2) {
            val prev = before[len - 2]
            return if (Character.isLowSurrogate(prev) && len >= 3 && Character.isHighSurrogate(before[len - 3])) {
                3
            } else {
                2
            }
        }
        if (Character.isLowSurrogate(last) && len >= 2 && Character.isHighSurrogate(before[len - 2])) {
            return 2
        }
        return 1
    }

    companion object {
        const val DOUBLE_TAP_TIMEOUT_MS = 350L
    }
}
