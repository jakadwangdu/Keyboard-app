package com.example.ui.keyboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.KeymoldThemeTokens
import java.util.Locale

/**
 * Keymold Voice Input View (UI1.md spec).
 * - Sits in the keyboard body with exact tokens.bodyHeight (identical height so nothing jumps).
 * - 76px center mic button: key fill when idle, accent fill with pulse ring during live recording.
 * - Continuous recognition with real-time interim results.
 * - Smart voice "send" commands: "send", "enter", "bhej", "bhej de", "bhejo", etc.
 * - Clear, helpful error messages and instructions.
 */
@Composable
fun KeymoldVoiceInputView(
    tokens: KeymoldThemeTokens = KeymoldThemeTokens(),
    onTranscriptCommitted: (String) -> Unit,
    onSendMessage: () -> Unit,
    onCloseVoiceMode: () -> Unit
) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var interimTranscript by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("Tap the microphone and start speaking") }
    var soundLevel by remember { mutableStateOf(0f) }

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (!isGranted) {
            statusMessage = "Microphone permission required for voice typing"
        }
    }

    // Voice send triggers
    val sendCommands = remember {
        listOf(
            "bhej de", "bhej do", "bhejo", "bhej dena", "bhej",
            "send it", "send message", "send now", "send karo", "send",
            "enter karo", "enter"
        )
    }

    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else {
            null
        }
    }

    val processResult: (String) -> Unit = { text ->
        val trimmed = text.trim()
        if (trimmed.isNotEmpty()) {
            val lower = trimmed.lowercase(Locale.ROOT)
            var matchedCommand: String? = null
            for (cmd in sendCommands) {
                if (lower.endsWith(cmd)) {
                    matchedCommand = cmd
                    break
                }
            }

            if (matchedCommand != null) {
                // Strip the send command suffix and commit text, then send
                val endIndex = lower.lastIndexOf(matchedCommand)
                val content = trimmed.substring(0, endIndex).trim()
                if (content.isNotEmpty()) {
                    onTranscriptCommitted("$content ")
                }
                statusMessage = "Sent message via voice command ✓"
                onSendMessage()
            } else {
                onTranscriptCommitted("$trimmed ")
                statusMessage = "Listening... Speak naturally"
            }
        }
    }

    DisposableEffect(speechRecognizer) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                statusMessage = "Listening... Say your message"
            }

            override fun onBeginningOfSpeech() {
                statusMessage = "Hearing your voice..."
            }

            override fun onRmsChanged(rmsdB: Float) {
                soundLevel = (rmsdB.coerceIn(0f, 10f) / 10f)
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                isListening = false
                statusMessage = "Processing speech..."
            }

            override fun onError(error: Int) {
                isListening = false
                statusMessage = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Tap to try again"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timed out. Tap to speak"
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission needed"
                    SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network unavailable for voice typing"
                    else -> "Tap the microphone to speak"
                }
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val best = matches?.firstOrNull() ?: ""
                interimTranscript = ""
                if (best.isNotBlank()) {
                    processResult(best)
                } else {
                    statusMessage = "Tap the microphone and start speaking"
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val interim = matches?.firstOrNull() ?: ""
                if (interim.isNotBlank()) {
                    interimTranscript = interim
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        speechRecognizer?.setRecognitionListener(listener)

        onDispose {
            try {
                speechRecognizer?.stopListening()
                speechRecognizer?.destroy()
            } catch (_: Exception) {}
        }
    }

    val startListening = {
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else if (speechRecognizer != null) {
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                }
                speechRecognizer.startListening(intent)
                isListening = true
                interimTranscript = ""
                statusMessage = "Listening..."
            } catch (e: Exception) {
                statusMessage = "Failed to start speech recognition"
                isListening = false
            }
        } else {
            statusMessage = "Speech recognizer not available on this device"
        }
    }

    val stopListening = {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        isListening = false
    }

    // Auto-start on entering voice mode if permission is granted
    LaunchedEffect(Unit) {
        if (hasAudioPermission) {
            startListening()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Pulse animation for recording state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isListening) 1.22f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micPulse"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(tokens.bodyHeight)
            .background(tokens.keyboard)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("keymold_voice_input_panel"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top bar: Back to ABC | Status Hint | Clear
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = tokens.keySecondary,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        stopListening()
                        onCloseVoiceMode()
                    }
                    .testTag("voice_back_to_abc")
            ) {
                Text(
                    text = "ABC",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.text,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Text(
                text = "Voice Dictation",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = tokens.muted
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = tokens.keySecondary,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        interimTranscript = ""
                        statusMessage = "Tap mic to speak"
                    }
            ) {
                Text(
                    text = "Clear",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = tokens.muted,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        // 2. Transcript & Live Interim Display Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = tokens.key.copy(alpha = 0.7f),
            border = androidx.compose.foundation.BorderStroke(1.dp, tokens.hairline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .heightIn(min = 60.dp, max = 90.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (interimTranscript.isNotEmpty()) {
                    Text(
                        text = "“$interimTranscript”",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = tokens.text,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = statusMessage,
                        fontSize = 13.sp,
                        color = if (isListening) tokens.accent else tokens.muted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 3. Center 76px Mic Button with Pulse Ring
        Box(
            modifier = Modifier
                .size(92.dp),
            contentAlignment = Alignment.Center
        ) {
            // Pulse Ring when listening
            if (isListening) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(tokens.accent.copy(alpha = 0.25f))
                )
            }

            // 76px Mic Button
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(if (isListening) tokens.accent else tokens.key)
                    .clickable {
                        if (isListening) {
                            stopListening()
                        } else {
                            startListening()
                        }
                    }
                    .testTag("voice_mic_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = if (isListening) "Listening" else "Tap to Speak",
                    tint = if (isListening) tokens.textOnAccent else tokens.text,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // 4. Bottom Hint & Send Command Guide
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tip: Say \"...bhej de\" or \"...send\" to auto-send",
                fontSize = 11.sp,
                color = tokens.muted,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )

            // Manual Send Action Button
            Surface(
                shape = CircleShape,
                color = tokens.accent,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (interimTranscript.isNotEmpty()) {
                            onTranscriptCommitted("$interimTranscript ")
                            interimTranscript = ""
                        }
                        onSendMessage()
                        onCloseVoiceMode()
                    }
                    .testTag("voice_send_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = tokens.textOnAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
