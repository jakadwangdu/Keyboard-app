package app.aura.keyboard

import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.test.core.app.ActivityScenario
import app.aura.keyboard.logic.InputConnectionTarget
import app.aura.keyboard.logic.InputController
import app.aura.keyboard.model.KeyKind
import app.aura.keyboard.model.KeySizePreset
import app.aura.keyboard.model.KeyboardLayer
import app.aura.keyboard.model.KeyboardPalette
import app.aura.keyboard.prefs.KeyboardPreferences
import app.aura.keyboard.ui.KeyboardView
import app.aura.keyboard.ui.SettingsActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class KeyboardUiRobolectricTest {

    @Test
    fun settingsActivity_andKeyboardViewTouchTypingIntoTextField() {
        val scenario = ActivityScenario.launch(SettingsActivity::class.java)
        scenario.onActivity { activity ->
            val enableBtn = activity.findViewById<Button>(R.id.btn_enable_keyboard)
            val chooseBtn = activity.findViewById<Button>(R.id.btn_choose_keyboard)
            val testField = activity.findViewById<EditText>(R.id.test_input_field)
            val soundSwitch = activity.findViewById<Switch>(R.id.switch_sound)
            val hapticsSwitch = activity.findViewById<Switch>(R.id.switch_haptics)

            assertNotNull(enableBtn)
            assertNotNull(chooseBtn)
            assertNotNull(testField)
            assertNotNull(soundSwitch)
            assertNotNull(hapticsSwitch)

            // Verify settings switches persist to SharedPreferences
            soundSwitch.isChecked = false
            hapticsSwitch.isChecked = false
            val prefs = KeyboardPreferences(activity)
            assertFalse(prefs.soundEnabled)
            assertFalse(prefs.hapticsEnabled)

            // Attach a live KeyboardView + InputController to SettingsActivity's EditText InputConnection
            testField.requestFocus()
            val editorInfo = EditorInfo()
            val inputConnection = testField.onCreateInputConnection(editorInfo)
            assertNotNull(inputConnection)

            val controller = InputController(InputConnectionTarget { testField.onCreateInputConnection(EditorInfo()) })
            controller.onStartInput(editorInfo)

            val keyboardView = KeyboardView(activity)
            fun syncKeyboard() {
                keyboardView.configure(
                    newPalette = KeyboardPalette.DARK_ESPRESSO,
                    newLayer = controller.layer,
                    newShiftState = controller.shiftState,
                    newImeActionKind = controller.imeActionKind,
                    newSizePreset = KeySizePreset.MEDIUM
                )
            }

            keyboardView.listener = object : KeyboardView.Listener {
                override fun onKeyPressedDown(kind: KeyKind) = Unit
                override fun onCharacterKey(charText: String, chordShiftHeld: Boolean) {
                    controller.commitCharacter(charText, chordShiftHeld)
                    syncKeyboard()
                }

                override fun onShiftTap(nowMs: Long) {
                    controller.onShiftTap(nowMs)
                    syncKeyboard()
                }

                override fun onShiftLongPress() {
                    controller.onShiftLongPress()
                    syncKeyboard()
                }

                override fun onShiftChordReleased() {
                    controller.onShiftChordReleased()
                    syncKeyboard()
                }

                override fun onBackspaceTap() {
                    controller.onBackspaceTap()
                    syncKeyboard()
                }

                override fun onDeleteWord() {
                    controller.onDeleteWord()
                    syncKeyboard()
                }

                override fun onSpaceTap(nowMs: Long) {
                    controller.onSpaceTap(nowMs)
                    syncKeyboard()
                }

                override fun onTrackpadStepHorizontal(steps: Int) {
                    controller.onTrackpadStepHorizontal(steps)
                    syncKeyboard()
                }

                override fun onTrackpadStepVertical(lines: Int) {
                    controller.onTrackpadStepVertical(lines)
                    syncKeyboard()
                }

                override fun onLayerSwitch(targetLayer: KeyboardLayer) {
                    controller.setLayer(targetLayer)
                    syncKeyboard()
                }

                override fun onEmojiKey() = Unit

                override fun onReturnKey() {
                    controller.onReturnTap()
                    syncKeyboard()
                }
            }

            syncKeyboard()
            val widthSpec = View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            keyboardView.measure(widthSpec, heightSpec)
            keyboardView.layout(0, 0, keyboardView.measuredWidth, keyboardView.measuredHeight)

            // Type "Hi" via real MotionEvent taps (testField uses TYPE_TEXT_FLAG_CAP_SENTENCES so first H is upper)
            var t = SystemClock.uptimeMillis()
            tapKey(keyboardView, "char_h", t)
            t += 40L
            tapKey(keyboardView, "char_i", t)
            assertEquals("Hi", testField.text.toString())

            // Double-tap space bar -> inserts ". "
            t += 40L
            tapKey(keyboardView, "space", t)
            t += 120L
            tapKey(keyboardView, "space", t)
            assertEquals("Hi. ", testField.text.toString())

            // Switch to 123 numbers layer and tap Rupee sign "₹"
            t += 40L
            tapKey(keyboardView, "layer_toggle", t)
            assertEquals(KeyboardLayer.NUMBERS, controller.layer)
            t += 40L
            tapKey(keyboardView, "num_₹", t)
            t += 40L
            tapKey(keyboardView, "num_5", t)
            assertEquals("Hi. ₹5", testField.text.toString())

            // Test swipe-left on Backspace deletes whole word "₹5"
            swipeLeftOnKey(keyboardView, "backspace", t + 50L, distancePx = 90f)
            assertEquals("Hi. ", testField.text.toString())
        }
        scenario.close()
    }

    private fun tapKey(view: KeyboardView, keyId: String, timeMs: Long) {
        val center = view.findKeyCenter(keyId)
        assertTrue("Expected key '$keyId' to exist on current layer", center != null)
        val (x, y) = center!!
        val down = MotionEvent.obtain(timeMs, timeMs, MotionEvent.ACTION_DOWN, x, y, 0)
        val up = MotionEvent.obtain(timeMs, timeMs + 20L, MotionEvent.ACTION_UP, x, y, 0)
        view.dispatchTouchEvent(down)
        view.dispatchTouchEvent(up)
        down.recycle()
        up.recycle()
    }

    private fun swipeLeftOnKey(view: KeyboardView, keyId: String, startMs: Long, distancePx: Float) {
        val center = view.findKeyCenter(keyId)!!
        val (x, y) = center
        val down = MotionEvent.obtain(startMs, startMs, MotionEvent.ACTION_DOWN, x, y, 0)
        val move = MotionEvent.obtain(startMs, startMs + 30L, MotionEvent.ACTION_MOVE, x - distancePx, y, 0)
        val up = MotionEvent.obtain(startMs, startMs + 50L, MotionEvent.ACTION_UP, x - distancePx, y, 0)
        view.dispatchTouchEvent(down)
        view.dispatchTouchEvent(move)
        view.dispatchTouchEvent(up)
        down.recycle()
        move.recycle()
        up.recycle()
    }
}
