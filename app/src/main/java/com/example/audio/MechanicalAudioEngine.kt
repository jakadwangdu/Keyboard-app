package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.model.SwitchType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
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

    // Ultra-low latency Android SoundPool for rapid concurrent key clicks
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(12)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    // Map of SwitchType to SoundPool sound ID
    private val soundIdMap = ConcurrentHashMap<SwitchType, Int>()

    var isSoundEnabled: Boolean = true
    var isHapticEnabled: Boolean = true
    var volumeLevel: Float = 0.85f // 0.0 to 1.0
    var hapticStrength: Float = 0.8f // 0.0 to 1.0
    var currentSwitch: SwitchType = SwitchType.CREAM_THOCK

    init {
        // Pre-generate and cache sound files in background thread for instant hardware-accelerated playback
        scope.launch {
            try {
                for (switch in SwitchType.values()) {
                    val pcm = generateSwitchPcm(switch)
                    val soundFile = File(context.cacheDir, "mech_snd_${switch.name.lowercase()}.wav")
                    writeWavFile(soundFile, pcm, sampleRate)
                    val soundId = soundPool.load(soundFile.absolutePath, 1)
                    soundIdMap[switch] = soundId
                }
            } catch (_: Exception) {
                // Ignore initialization issues
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

    private fun writeWavFile(file: File, pcmData: ShortArray, sampleRate: Int) {
        val totalAudioLen = (pcmData.size * 2).toLong()
        val totalDataLen = totalAudioLen + 36
        val channels = 1
        val byteRate = (sampleRate * 2 * channels).toLong()

        FileOutputStream(file).use { fos ->
            val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
            header.put("RIFF".toByteArray())
            header.putInt(totalDataLen.toInt())
            header.put("WAVE".toByteArray())
            header.put("fmt ".toByteArray())
            header.putInt(16) // Subchunk1Size
            header.putShort(1.toShort()) // AudioFormat (PCM = 1)
            header.putShort(channels.toShort()) // NumChannels
            header.putInt(sampleRate)
            header.putInt(byteRate.toInt())
            header.putShort((channels * 2).toShort()) // BlockAlign
            header.putShort(16.toShort()) // BitsPerSample
            header.put("data".toByteArray())
            header.putInt(totalAudioLen.toInt())
            fos.write(header.array())

            val buffer = ByteBuffer.allocate(pcmData.size * 2).order(ByteOrder.LITTLE_ENDIAN)
            for (sample in pcmData) {
                buffer.putShort(sample)
            }
            fos.write(buffer.array())
        }
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
            val soundId = soundIdMap[switch]
            if (soundId != null && soundId > 0) {
                val effectivePitch = (pitchShift * (0.98f + Random.nextFloat() * 0.04f)).coerceIn(0.5f, 2.0f)
                soundPool.play(soundId, volumeLevel, volumeLevel, 1, 0, effectivePitch)
            }
        }

        if (isHapticEnabled && hapticStrength > 0.05f) {
            triggerHaptic(switch)
        }
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
}
