package app.aura.keyboard.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import app.aura.keyboard.R
import java.util.concurrent.ConcurrentHashMap

enum class FeedbackKind {
    TAP,
    SPACE,
    DELETE,
    TRACKPAD_TICK
}

class FeedbackManager(context: Context) {

    @Volatile
    var soundEnabled: Boolean = true

    @Volatile
    var hapticsEnabled: Boolean = true

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        context.getSystemService(Vibrator::class.java)
    }

    private val tickEffect: VibrationEffect? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
    } else {
        VibrationEffect.createOneShot(10L, VibrationEffect.DEFAULT_AMPLITUDE)
    }

    private val clickEffect: VibrationEffect? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
    } else {
        VibrationEffect.createOneShot(14L, VibrationEffect.DEFAULT_AMPLITUDE)
    }

    private val loadedSounds = ConcurrentHashMap<Int, Boolean>()

    private val soundPool: SoundPool? = try {
        SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
            .apply {
                setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        loadedSounds[sampleId] = true
                    }
                }
            }
    } catch (_: Exception) {
        null
    }

    private val tapSoundId: Int = loadSafe(context, R.raw.key_tap)
    private val spaceSoundId: Int = loadSafe(context, R.raw.key_space)
    private val deleteSoundId: Int = loadSafe(context, R.raw.key_delete)

    private fun loadSafe(context: Context, resId: Int): Int {
        val pool = soundPool ?: return 0
        return try {
            pool.load(context, resId, 1)
        } catch (_: Exception) {
            0
        }
    }

    fun trigger(kind: FeedbackKind) {
        if (soundEnabled && kind != FeedbackKind.TRACKPAD_TICK) {
            val id = when (kind) {
                FeedbackKind.TAP -> tapSoundId
                FeedbackKind.SPACE -> spaceSoundId
                FeedbackKind.DELETE -> deleteSoundId
                FeedbackKind.TRACKPAD_TICK -> 0
            }
            if (id > 0 && loadedSounds[id] == true) {
                try {
                    soundPool?.play(id, 0.55f, 0.55f, 1, 0, 1.0f)
                } catch (_: Exception) {
                }
            }
        }

        if (hapticsEnabled) {
            val vib = vibrator ?: return
            val effect = when (kind) {
                FeedbackKind.SPACE, FeedbackKind.DELETE -> clickEffect
                FeedbackKind.TAP, FeedbackKind.TRACKPAD_TICK -> tickEffect
            } ?: return
            try {
                if (vib.hasVibrator()) {
                    vib.vibrate(effect)
                }
            } catch (_: Exception) {
            }
        }
    }

    fun release() {
        loadedSounds.clear()
        try {
            soundPool?.release()
        } catch (_: Exception) {
        }
    }
}
