package app.aura.keyboard.logic

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class ClipEntry(
    val id: String,
    val text: String,
    val pinned: Boolean,
    val timestampMs: Long
)

/**
 * SharedPreferences-backed clipboard store holding up to 20 clips with pinning.
 * Never logs clipboard text and ignores clips when inside password fields or marked sensitive.
 */
class ClipboardStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getClips(): List<ClipEntry> {
        val raw = prefs.getString(KEY_CLIPS, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            val list = ArrayList<ClipEntry>(array.length())
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = obj.optString("id", "")
                val text = obj.optString("text", "")
                val pinned = obj.optBoolean("pinned", false)
                val ts = obj.optLong("ts", 0L)
                if (id.isNotEmpty() && text.isNotEmpty()) {
                    list.add(ClipEntry(id = id, text = text, pinned = pinned, timestampMs = ts))
                }
            }
            sortAndCap(list)
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun recordClip(
        text: String,
        isPasswordField: Boolean,
        isSensitive: Boolean = false,
        nowMs: Long = System.currentTimeMillis()
    ): Boolean {
        if (isPasswordField || isSensitive || text.isBlank()) {
            return false
        }
        val existing = getClips().toMutableList()
        val previousMatch = existing.firstOrNull { it.text == text }
        existing.removeAll { it.text == text }
        val entry = ClipEntry(
            id = previousMatch?.id ?: UUID.randomUUID().toString(),
            text = text,
            pinned = previousMatch?.pinned ?: false,
            timestampMs = nowMs
        )
        existing.add(0, entry)
        saveClips(sortAndCap(existing))
        return true
    }

    fun togglePin(id: String): List<ClipEntry> {
        val updated = getClips().map { entry ->
            if (entry.id == id) entry.copy(pinned = !entry.pinned) else entry
        }
        val capped = sortAndCap(updated)
        saveClips(capped)
        return capped
    }

    fun deleteClip(id: String): List<ClipEntry> {
        val updated = getClips().filterNot { it.id == id }
        saveClips(updated)
        return updated
    }

    fun clearUnpinned(): List<ClipEntry> {
        val kept = getClips().filter { it.pinned }
        saveClips(kept)
        return kept
    }

    private fun sortAndCap(items: List<ClipEntry>): List<ClipEntry> {
        val pinned = items.filter { it.pinned }.sortedByDescending { it.timestampMs }
        val unpinned = items.filterNot { it.pinned }.sortedByDescending { it.timestampMs }
        return (pinned + unpinned).take(MAX_CLIPS)
    }

    private fun saveClips(items: List<ClipEntry>) {
        val array = JSONArray()
        for (item in items) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("text", item.text)
            obj.put("pinned", item.pinned)
            obj.put("ts", item.timestampMs)
            array.put(obj)
        }
        prefs.edit { putString(KEY_CLIPS, array.toString()) }
    }

    companion object {
        const val PREFS_NAME = "aura_clipboard_prefs"
        private const val KEY_CLIPS = "clipboard_entries"
        const val MAX_CLIPS = 20
    }
}
