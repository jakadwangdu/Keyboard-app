package app.aura.keyboard

import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import app.aura.keyboard.logic.InputConnectionTarget
import app.aura.keyboard.logic.InputController
import app.aura.keyboard.logic.InputTarget
import app.aura.keyboard.model.ImeActionKind
import app.aura.keyboard.model.KeyKind
import app.aura.keyboard.model.KeyboardLayer
import app.aura.keyboard.model.KeyboardLayoutBuilder
import app.aura.keyboard.model.ShiftState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class InputControllerTest {

    private class FakeInputTarget : InputTarget {
        val text = StringBuilder()
        var selectionStart = 0
        var selectionEnd = 0
        var lastPerformedAction: Int? = null
        val sentKeyCodes = mutableListOf<Int>()

        override fun commitText(text: CharSequence, newCursorPosition: Int): Boolean {
            if (selectionStart != selectionEnd) {
                this.text.replace(selectionStart, selectionEnd, text.toString())
                val pos = selectionStart + text.length
                selectionStart = pos
                selectionEnd = pos
            } else {
                this.text.insert(selectionStart, text)
                val pos = selectionStart + text.length
                selectionStart = pos
                selectionEnd = pos
            }
            return true
        }

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            val start = (selectionStart - beforeLength).coerceAtLeast(0)
            val end = (selectionEnd + afterLength).coerceAtMost(text.length)
            text.delete(start, selectionStart)
            val deletedBefore = selectionStart - start
            val adjustedEnd = (end - deletedBefore).coerceAtLeast(start)
            val adjustedSelEnd = (selectionEnd - deletedBefore).coerceAtLeast(start)
            text.delete(adjustedSelEnd, adjustedEnd)
            selectionStart = start
            selectionEnd = start
            return true
        }

        override fun getTextBeforeCursor(length: Int, flags: Int): CharSequence {
            val start = (selectionStart - length).coerceAtLeast(0)
            return text.substring(start, selectionStart)
        }

        override fun getSelectedText(flags: Int): CharSequence? {
            return if (selectionStart == selectionEnd) null else text.substring(selectionStart, selectionEnd)
        }

        override fun getCursorCapsMode(reqModes: Int): Int {
            if ((reqModes and InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS) != 0) {
                return InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            }
            val before = text.substring(0, selectionStart)
            if ((reqModes and InputType.TYPE_TEXT_FLAG_CAP_WORDS) != 0) {
                if (before.isEmpty() || before.last().isWhitespace()) {
                    return InputType.TYPE_TEXT_FLAG_CAP_WORDS
                }
            }
            if ((reqModes and InputType.TYPE_TEXT_FLAG_CAP_SENTENCES) != 0) {
                val trimmed = before.trimEnd { it == ' ' || it == '\t' }
                if (trimmed.isEmpty()) {
                    return if (before.isEmpty() || before.last() == ' ' || before.last() == '\n') {
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                    } else {
                        0
                    }
                }
                val lastPunct = trimmed.last()
                val hasTrailingSpace = before.length > trimmed.length
                if ((lastPunct == '.' || lastPunct == '!' || lastPunct == '?' || lastPunct == '\n') &&
                    (hasTrailingSpace || lastPunct == '\n')
                ) {
                    return InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                }
            }
            return 0
        }

        override fun performEditorAction(editorAction: Int): Boolean {
            lastPerformedAction = editorAction
            return true
        }

        override fun sendKeyEvent(event: KeyEvent): Boolean {
            if (event.action == KeyEvent.ACTION_DOWN) {
                sentKeyCodes.add(event.keyCode)
                if (event.keyCode == KeyEvent.KEYCODE_DPAD_LEFT && selectionStart > 0) {
                    selectionStart--
                    selectionEnd = selectionStart
                } else if (event.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && selectionStart < text.length) {
                    selectionStart++
                    selectionEnd = selectionStart
                } else if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    commitText("\n", 1)
                }
            }
            return true
        }

        override fun beginBatchEdit(): Boolean = true
        override fun endBatchEdit(): Boolean = true
    }

    @Test
    fun layoutBuilder_producesExpectedRowsAndRupeeSign() {
        val letterRows = KeyboardLayoutBuilder.buildRows(
            KeyboardLayer.LETTERS,
            ShiftState.OFF,
            ImeActionKind.RETURN
        )
        assertEquals(4, letterRows.size)
        assertEquals(10, letterRows[0].size) // Q-P
        assertEquals(9, letterRows[1].size)  // A-L
        assertEquals(9, letterRows[2].size)  // Shift + Z-M (7) + Backspace
        assertEquals(KeyKind.SHIFT, letterRows[2].first().kind)
        assertEquals(KeyKind.BACKSPACE, letterRows[2].last().kind)
        assertEquals(4, letterRows[3].size)  // 123, Emoji, Space, Return

        val numberRows = KeyboardLayoutBuilder.buildRows(
            KeyboardLayer.NUMBERS,
            ShiftState.OFF,
            ImeActionKind.SEND
        )
        assertTrue(numberRows.flatten().any { it.primaryChar == "₹" })
        assertEquals("Send", numberRows[3].last().displayLabel)

        val symbolRows = KeyboardLayoutBuilder.buildRows(
            KeyboardLayer.SYMBOLS,
            ShiftState.OFF,
            ImeActionKind.SEARCH
        )
        assertTrue(symbolRows.flatten().any { it.primaryChar == "₹" })
        assertEquals("Search", symbolRows[3].last().displayLabel)
    }

    @Test
    fun shift_singleTapDoubleTapCapsLockAndAutoReset() {
        val target = FakeInputTarget()
        val controller = InputController(target)
        controller.onStartInput(EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT
        })

        assertEquals(ShiftState.OFF, controller.shiftState)

        // Single shift tap -> uppercase once, then resets
        controller.onShiftTap(100L)
        assertEquals(ShiftState.SHIFTED, controller.shiftState)
        controller.commitCharacter("a")
        assertEquals("A", target.text.toString())
        assertEquals(ShiftState.OFF, controller.shiftState)

        // Double tap shift -> CAPS_LOCK stays active across letters
        controller.onShiftTap(200L)
        controller.onShiftTap(380L)
        assertEquals(ShiftState.CAPS_LOCK, controller.shiftState)
        controller.commitCharacter("b")
        controller.commitCharacter("c")
        assertEquals("ABC", target.text.toString())
        assertEquals(ShiftState.CAPS_LOCK, controller.shiftState)

        // Long press exits CAPS_LOCK
        controller.onShiftLongPress()
        assertEquals(ShiftState.OFF, controller.shiftState)
    }

    @Test
    fun autoCapitalization_followsEditorInfoSentences() {
        val target = FakeInputTarget()
        val controller = InputController(target)
        controller.onStartInput(EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        })

        assertEquals(ShiftState.SHIFTED, controller.shiftState)
        controller.commitCharacter("h")
        controller.commitCharacter("i")
        assertEquals("Hi", target.text.toString())
        assertEquals(ShiftState.OFF, controller.shiftState)
    }

    @Test
    fun doubleSpacePeriod_convertsTrailingSpaceAfterWord() {
        val target = FakeInputTarget()
        val controller = InputController(target)
        controller.onStartInput(EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        })

        "hello".forEach { controller.commitCharacter(it.toString()) }
        assertFalse(controller.onSpaceTap(1000L))
        assertEquals("Hello ", target.text.toString())

        assertTrue(controller.onSpaceTap(1200L))
        assertEquals("Hello. ", target.text.toString())
        // Re-capitalizes new sentence automatically
        assertEquals(ShiftState.SHIFTED, controller.shiftState)

        // Second double tap immediately after period should NOT insert ".. "
        assertFalse(controller.onSpaceTap(1300L))
        assertFalse(controller.onSpaceTap(1400L))
        assertEquals("Hello.   ", target.text.toString())
    }

    @Test
    fun doubleSpacePeriod_disabledBeyondTimeoutAndInPasswordFields() {
        val target = FakeInputTarget()
        val controller = InputController(target)
        controller.onStartInput(EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        })

        assertTrue(controller.isPasswordField)
        assertEquals(ShiftState.OFF, controller.shiftState)
        "pass".forEach { controller.commitCharacter(it.toString()) }
        assertFalse(controller.onSpaceTap(100L))
        assertFalse(controller.onSpaceTap(200L))
        assertEquals("pass  ", target.text.toString())

        // Regular field past timeout
        controller.onStartInput(EditorInfo().apply { inputType = InputType.TYPE_CLASS_TEXT })
        controller.commitCharacter("x")
        assertFalse(controller.onSpaceTap(100L))
        assertFalse(controller.onSpaceTap(700L))
        assertEquals("pass  x  ", target.text.toString())
    }

    @Test
    fun deleteWord_removesWholeWordsAndTrailingSpaces() {
        val target = FakeInputTarget()
        val controller = InputController(target)
        controller.onStartInput(EditorInfo().apply { inputType = InputType.TYPE_CLASS_TEXT })

        controller.commitDirectText("Aura keyboard rocks  ")
        controller.onDeleteWord()
        assertEquals("Aura keyboard ", target.text.toString())

        controller.onDeleteWord()
        assertEquals("Aura ", target.text.toString())

        controller.onDeleteWord()
        assertEquals("", target.text.toString())
    }

    @Test
    fun backspace_handlesEmojiSurrogatePairsCleanly() {
        val target = FakeInputTarget()
        val controller = InputController(target)
        controller.onStartInput(EditorInfo().apply { inputType = InputType.TYPE_CLASS_TEXT })

        controller.commitDirectText("hi\uD83D\uDE04")
        controller.onBackspaceTap()
        assertEquals("hi", target.text.toString())
        controller.onBackspaceTap()
        assertEquals("h", target.text.toString())
    }

    @Test
    fun editorInfo_respectsNumericLayerAndImeActions() {
        val target = FakeInputTarget()
        val controller = InputController(target)

        controller.onStartInput(EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            imeOptions = EditorInfo.IME_ACTION_GO
        })
        assertTrue(controller.isNumericField)
        assertEquals(KeyboardLayer.NUMBER_PAD, controller.layer)
        assertEquals(ImeActionKind.GO, controller.imeActionKind)

        controller.onReturnTap()
        assertEquals(EditorInfo.IME_ACTION_GO, target.lastPerformedAction)
    }

    @Test
    fun nullInputConnection_isSafeAcrossAllOperations() {
        val controller = InputController(InputConnectionTarget { null })
        controller.onStartInput(EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        })
        controller.onShiftTap(100L)
        controller.commitCharacter("a")
        controller.commitDirectText("hello")
        controller.onSpaceTap(200L)
        controller.onSpaceTap(300L)
        controller.onBackspaceTap()
        controller.onDeleteWord()
        controller.onTrackpadStepHorizontal(-2)
        controller.onTrackpadStepVertical(1)
        controller.onReturnTap()
    }
}
