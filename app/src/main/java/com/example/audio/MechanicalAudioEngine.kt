package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
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

    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default)

    // Pre-generated sound buffers for instant zero-latency playback
    private val pcmCache = ConcurrentHashMap<SwitchType, ShortArray>()

    var isSoundEnabled: Boolean = true
    var isHapticEnabled: Boolean = true
    var volumeLevel: Float = 0.85f // 0.0 to 1.0
    var hapticStrength: Float = 0.8f // 0.0 to 1.0
    var currentSwitch: SwitchType = SwitchType.CREAM_THOCK

    init {
        // Pre-compute sound waveforms for all switch types
        for (switch in SwitchType.values()) {
            pcmCache[switch] = generateSwitchPcm(switch)
        }
    }

    private fun generateSwitchPcm(switch: SwitchType): ShortArray {
        // Duration: between 45ms and 80ms
        val durationMs = when (switch) {
            SwitchType.MODEL_M_SPRING -> 75
            SwitchType.CREAM_THOCK -> 55
            SwitchType.BLUE_CLICKY -> 50
            SwitchType.BROWN_TACTILE -> 45
            SwitchType.RED_LINEAR -> 40
        }
        val totalSamples = (sampleRate * durationMs / 1000)
        val pcm = ShortArray(totalSamples)

        val clickFreq = switch.clickFrequency
        val thockFreq = switch.thockFrequency
        val damp = switch.damping

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / totalSamples

            // 1. Initial click / transient impulse (first 4-8 ms)
            val clickEnvelope = exp(-progress * 28.0)
            val clickSine = sin(2 * PI * clickFreq * t)
            val clickNoise = (Random.nextFloat() * 2f - 1f) * 0.35f
            val clickComponent = (clickSine * 0.65f + clickNoise) * clickEnvelope

            // 2. Bottom-out thock body / cavity resonance (starts slightly after click)
            val thockDelaySamples = (sampleRate * 0.003).toInt()
            val thockT = if (i >= thockDelaySamples) (i - thockDelaySamples).toDouble() / sampleRate else 0.0
            val thockEnvelope = if (i >= thockDelaySamples) {
                exp(-((i - thockDelaySamples).toDouble() / totalSamples) * (14.0 * damp))
            } else 0.0
            val thockSine = sin(2 * PI * thockFreq * thockT + sin(2 * PI * (thockFreq * 0.5) * thockT) * 0.5)
            val thockComponent = thockSine * thockEnvelope * 0.9f

            // 3. Spring ping for Model M or subtle metal leaf for Blue switch
            val springComponent = when (switch) {
                SwitchType.MODEL_M_SPRING -> {
                    val springEnv = exp(-progress * 9.0)
                    sin(2 * PI * 4400 * t) * 0.25f * springEnv + sin(2 * PI * 2800 * t) * 0.15f * springEnv
                }
                SwitchType.BLUE_CLICKY -> {
                    val crispEnv = exp(-progress * 35.0)
                    sin(2 * PI * 6000 * t) * 0.2f * crispEnv
                }
                SwitchType.CREAM_THOCK -> {
                    // Deep sub-bass resonance
                    val subEnv = exp(-progress * 12.0)
                    sin(2 * PI * 130 * t) * 0.3f * subEnv
                }
                else -> 0.0
            }

            val mixed = (clickComponent * 0.45 + thockComponent * 0.5 + springComponent * 0.3)
            val clamped = mixed.coerceIn(-1.0, 1.0)
            pcm[i] = (clamped * 32767.0 * 0.95).toInt().toShort()
        }
        return pcm
    }

    fun playKeyPressSound(switch: SwitchType = currentSwitch, pitchShift: Float = 1.0f) {
        if (isSoundEnabled && volumeLevel > 0.01f) {
            scope.launch {
                try {
                    val pcm = pcmCache[switch] ?: generateSwitchPcm(switch)
                    val effectiveSampleRate = (sampleRate * (pitchShift * (0.97f + Random.nextFloat() * 0.06f))).toInt()
                    
                    val audioTrack = AudioTrack.Builder()
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build()
                        )
                        .setAudioFormat(
                            AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(effectiveSampleRate)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build()
                        )
                        .setBufferSizeInBytes(pcm.size * 2)
                        .setTransferMode(AudioTrack.MODE_STATIC)
                        .build()

                    audioTrack.setVolume(volumeLevel)
                    audioTrack.write(pcm, 0, pcm.size)
                    audioTrack.play()

                    // Release track after playback completes
                    val playDurationMs = (pcm.size * 1000L) / effectiveSampleRate + 20
                    Thread.sleep(playDurationMs)
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {
                    // Fail gracefully
                }
            }
        }

        if (isHapticEnabled && hapticStrength > 0.05f) {
            triggerHaptic(switch)
        }
    }

    private fun triggerHaptic(switch: SwitchType) {
        try {
            if (vibrator == null || !vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val effect = when (switch) {
                    SwitchType.BLUE_CLICKY -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                    SwitchType.BROWN_TACTILE -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                    SwitchType.RED_LINEAR -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                    SwitchType.CREAM_THOCK -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                    SwitchType.MODEL_M_SPRING -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate((15 * hapticStrength).toLong().coerceAtLeast(5))
            }
        } catch (_: Exception) {
            // Ignore haptic exceptions
        }
    }
}
