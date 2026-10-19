package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.model.SwitchType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

class MechanicalAudioEngine(private val context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default)

    // Pre-synthesized PCM audio buffers for each switch type (pure memory, no disk I/O, no Codec2)
    private val pcmCache = ConcurrentHashMap<SwitchType, ShortArray>()

    // Pool of static AudioTracks for instant playback with no media codec dependency
    private val trackPool = ConcurrentHashMap<SwitchType, List<AudioTrack>>()
    private val trackRoundRobin = ConcurrentHashMap<SwitchType, AtomicInteger>()

    var isSoundEnabled: Boolean = true
    var isHapticEnabled: Boolean = true
    var volumeLevel: Float = 0.85f // 0.0 to 1.0
    var hapticStrength: Float = 0.8f // 0.0 to 1.0
    var currentSwitch: SwitchType = SwitchType.CREAM_THOCK

    init {
        // Pre-generate raw PCM buffers and initialize direct AudioTracks in memory.
        // Bypasses SoundPool and MediaCodec / Codec2, eliminating
        // "Failed to query component interface for required system resources" errors.
        scope.launch {
            try {
                // Clean up any old cached .wav files from previous versions
                try {
                    context.cacheDir.listFiles { _, name -> name.startsWith("mech_snd_") }?.forEach {
                        it.delete()
                    }
                } catch (_: Throwable) {}

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                for (switch in SwitchType.entries) {
                    val pcm = generateSwitchPcm(switch)
                    pcmCache[switch] = pcm

                    // Create 2 static AudioTracks per switch for smooth rapid typing alternation
                    val tracks = mutableListOf<AudioTrack>()
                    for (i in 0 until 2) {
                        try {
                            val track = AudioTrack.Builder()
                                .setAudioAttributes(audioAttributes)
                                .setAudioFormat(audioFormat)
                                .setBufferSizeInBytes(pcm.size * 2)
                                .setTransferMode(AudioTrack.MODE_STATIC)
                                .build()
                            if (track.state == AudioTrack.STATE_INITIALIZED) {
                                track.write(pcm, 0, pcm.size)
                                tracks.add(track)
                            }
                        } catch (_: Throwable) {
                            // AudioTrack creation error on unsupported environments handled gracefully
                        }
                    }
                    if (tracks.isNotEmpty()) {
                        trackPool[switch] = tracks
                        trackRoundRobin[switch] = AtomicInteger(0)
                    }
                }
            } catch (_: Throwable) {
                // Ignore initialization issues, will fallback to system keypress sound
            }
        }
    }

    private fun generateSwitchPcm(switch: SwitchType): ShortArray {
        // Duration: between 40ms and 70ms
        val durationMs = when (switch) {
            SwitchType.MODEL_M_SPRING -> 70
            SwitchType.CREAM_THOCK -> 50
            SwitchType.BLUE_CLICKY -> 45
            SwitchType.BROWN_TACTILE -> 40
            SwitchType.RED_LINEAR -> 35
        }
        val totalSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(totalSamples)

        val clickFreq = switch.clickFrequency
        val thockFreq = switch.thockFrequency
        val damp = switch.damping

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / totalSamples

            // 1. Initial crisp mechanical switch transient
            val clickEnvelope = exp(-progress * 30.0)
            val clickSine = sin(2 * PI * clickFreq * t)
            val clickNoise = (Random.nextFloat() * 2f - 1f) * 0.3f
            val clickComponent = (clickSine * 0.7f + clickNoise) * clickEnvelope

            // 2. Bottom-out thock body / plate resonance
            val thockDelaySamples = (sampleRate * 0.0025).toInt()
            val thockT = if (i >= thockDelaySamples) (i - thockDelaySamples).toDouble() / sampleRate else 0.0
            val thockEnvelope = if (i >= thockDelaySamples) {
                exp(-((i - thockDelaySamples).toDouble() / totalSamples) * (15.0 * damp))
            } else 0.0
            val thockSine = sin(2 * PI * thockFreq * thockT + sin(2 * PI * (thockFreq * 0.5) * thockT) * 0.5)
            val thockComponent = thockSine * thockEnvelope * 0.95f

            // 3. Spring ping or leaf resonance
            val springComponent = when (switch) {
                SwitchType.MODEL_M_SPRING -> {
                    val springEnv = exp(-progress * 9.0)
                    sin(2 * PI * 4200 * t) * 0.22f * springEnv + sin(2 * PI * 2600 * t) * 0.15f * springEnv
                }
                SwitchType.BLUE_CLICKY -> {
                    val crispEnv = exp(-progress * 35.0)
                    sin(2 * PI * 5800 * t) * 0.2f * crispEnv
                }
                SwitchType.CREAM_THOCK -> {
                    val subEnv = exp(-progress * 12.0)
                    sin(2 * PI * 130 * t) * 0.3f * subEnv
                }
                else -> 0.0
            }

            val mixed = (clickComponent * 0.45 + thockComponent * 0.5 + springComponent * 0.25)
            val clamped = mixed.coerceIn(-1.0, 1.0)
            pcm[i] = (clamped * 32767.0 * 0.95).toInt().toShort()
        }
        return pcm
    }

    // Pre-cached VibrationEffects for zero-latency execution
    private val clickEffect: VibrationEffect? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        try { VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK) } catch (_: Exception) { null }
    } else null

    private val tickEffect: VibrationEffect? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        try { VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK) } catch (_: Exception) { null }
    } else null

    private val heavyEffect: VibrationEffect? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        try { VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK) } catch (_: Exception) { null }
    } else null

    private val doubleClickEffect: VibrationEffect? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        try { VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK) } catch (_: Exception) { null }
    } else null

    fun playKeyPressSound(switch: SwitchType = currentSwitch, pitchShift: Float = 1.0f) {
        if (isSoundEnabled && volumeLevel > 0.01f) {
            val tracks = trackPool[switch]
            if (!tracks.isNullOrEmpty()) {
                val rr = trackRoundRobin[switch]
                val idx = ((rr?.getAndIncrement() ?: 0) and 0x7FFFFFFF) % tracks.size
                val track = tracks[idx]
                try {
                    if (track.state == AudioTrack.STATE_INITIALIZED) {
                        if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                            track.stop()
                        }
                        track.reloadStaticData()
                        track.setVolume(volumeLevel)
                        val effectivePitch = (pitchShift * (0.98f + Random.nextFloat() * 0.04f)).coerceIn(0.5f, 2.0f)
                        val targetRate = (sampleRate * effectivePitch).toInt().coerceIn(4000, 96000)
                        try {
                            track.playbackRate = targetRate
                        } catch (_: Throwable) {}
                        track.play()
                    } else {
                        fallbackSystemSound()
                    }
                } catch (_: Throwable) {
                    fallbackSystemSound()
                }
            } else {
                fallbackSystemSound()
            }
        }

        if (isHapticEnabled && hapticStrength > 0.05f) {
            triggerHaptic(switch)
        }
    }

    private fun fallbackSystemSound() {
        try {
            audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, volumeLevel)
        } catch (_: Throwable) {}
    }

    /**
     * Ultra-responsive micro-haptic tick when dragging cursor over spacebar
     */
    fun triggerCursorTick() {
        if (!isHapticEnabled || hapticStrength <= 0.05f || vibrator == null) return
        scope.launch {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && tickEffect != null) {
                    vibrator.vibrate(tickEffect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(8L)
                }
            } catch (_: Exception) {}
        }
    }

    fun triggerHaptic(switch: SwitchType = currentSwitch) {
        if (vibrator == null || !isHapticEnabled || hapticStrength <= 0.05f) return
        scope.launch {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val effect = when (switch) {
                        SwitchType.BLUE_CLICKY -> clickEffect ?: tickEffect
                        SwitchType.BROWN_TACTILE -> tickEffect
                        SwitchType.RED_LINEAR -> tickEffect
                        SwitchType.CREAM_THOCK -> heavyEffect ?: clickEffect
                        SwitchType.MODEL_M_SPRING -> doubleClickEffect ?: clickEffect
                    }
                    if (effect != null) {
                        vibrator.vibrate(effect)
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate((12 * hapticStrength).toLong().coerceAtLeast(4))
                    }
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate((12 * hapticStrength).toLong().coerceAtLeast(4))
                }
            } catch (_: Exception) {
                // Ignore haptic exceptions
            }
        }
    }

    fun release() {
        trackPool.values.flatten().forEach { track ->
            try {
                if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    track.stop()
                }
                track.release()
            } catch (_: Throwable) {}
        }
        trackPool.clear()
        pcmCache.clear()
    }
}
