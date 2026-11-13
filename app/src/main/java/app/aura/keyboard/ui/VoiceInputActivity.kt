package app.aura.keyboard.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import app.aura.keyboard.R
import java.util.Locale
import java.util.concurrent.atomic.AtomicReference

/**
 * Coordinates voice typing permission + RecognizerIntent handoff back to AuraKeyboardService.
 * Microphone permission is requested only when the user taps voice typing.
 */
object VoiceInputBus {
    private val pendingText = AtomicReference<String?>(null)
    @Volatile
    private var activeListener: ((String) -> Unit)? = null

    fun registerListener(listener: ((String) -> Unit)?) {
        activeListener = listener
        if (listener != null) {
            consumePending()?.let(listener)
        }
    }

    fun postResult(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val listener = activeListener
        if (listener != null) {
            listener.invoke(trimmed)
        } else {
            pendingText.set(trimmed)
        }
    }

    fun consumePending(): String? {
        return pendingText.getAndSet(null)
    }
}

class VoiceInputActivity : ComponentActivity() {

    private var launched = false

    private val requestAudioPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startSpeechRecognizer()
        } else {
            Toast.makeText(this, R.string.voice_permission_needed, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private val speechRecognizerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spoken = matches?.firstOrNull().orEmpty()
            if (spoken.isNotBlank()) {
                VoiceInputBus.postResult(spoken)
            }
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            launched = savedInstanceState.getBoolean(KEY_LAUNCHED, false)
        }
        if (!launched) {
            launched = true
            beginVoiceFlow()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_LAUNCHED, launched)
    }

    private fun beginVoiceFlow() {
        val hasMic = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasMic) {
            startSpeechRecognizer()
        } else {
            requestAudioPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startSpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_prompt))
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        try {
            speechRecognizerLauncher.launch(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.voice_unavailable, Toast.LENGTH_SHORT).show()
            finish()
        } catch (_: SecurityException) {
            Toast.makeText(this, R.string.voice_permission_needed, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object {
        private const val KEY_LAUNCHED = "voice_flow_launched"
    }
}
