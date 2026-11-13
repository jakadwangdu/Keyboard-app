package app.aura.keyboard.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import app.aura.keyboard.model.KeySizePreset
import app.aura.keyboard.model.ThemeMode
import org.json.JSONArray

class KeyboardPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_SOUND_ENABLED, value) }

    var hapticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_HAPTICS_ENABLED, value) }

    var keySizePreset: KeySizePreset
        get() = KeySizePreset.fromLabel(prefs.getString(KEY_SIZE_PRESET, KeySizePreset.MEDIUM.label))
        set(value) = prefs.edit { putString(KEY_SIZE_PRESET, value.label) }

    var themeMode: ThemeMode
        get() {
            val raw = prefs.getString(KEY_THEME_MODE, ThemeMode.AUTO.name)
            return ThemeMode.entries.firstOrNull { it.name == raw } ?: ThemeMode.AUTO
        }
        set(value) = prefs.edit { putString(KEY_THEME_MODE, value.name) }

    fun getRecentEmojis(): List<String> {
        val raw = prefs.getString(KEY_RECENT_EMOJIS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            buildList(arr.length()) {
                for (i in 0 until arr.length()) {
                    val item = arr.optString(i, "")
                    if (item.isNotEmpty()) add(item)
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun recordRecentEmoji(emoji: String) {
        if (emoji.isEmpty()) return
        val current = getRecentEmojis().toMutableList()
        current.remove(emoji)
        current.add(0, emoji)
        val trimmed = current.take(MAX_RECENT_EMOJIS)
        val arr = JSONArray()
        trimmed.forEach { arr.put(it) }
        prefs.edit { putString(KEY_RECENT_EMOJIS, arr.toString()) }
    }

    companion object {
        const val PREFS_NAME = "aura_keyboard_prefs"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_HAPTICS_ENABLED = "haptics_enabled"
        private const val KEY_SIZE_PRESET = "key_size_preset"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_RECENT_EMOJIS = "recent_emojis"
        private const val MAX_RECENT_EMOJIS = 16
    }
}
