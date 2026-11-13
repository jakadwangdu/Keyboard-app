package app.aura.keyboard

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import app.aura.keyboard.logic.ClipboardStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ClipboardStoreTest {

    private lateinit var store: ClipboardStore

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences(ClipboardStore.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        store = ClipboardStore(context)
    }

    @Test
    fun enforcesMaxTwentyClipsAndPreservesPinned() {
        assertTrue(store.recordClip("keep-me", isPasswordField = false, nowMs = 1L))
        val firstId = store.getClips().first().id
        store.togglePin(firstId)

        for (i in 1..25) {
            assertTrue(store.recordClip("clip-$i", isPasswordField = false, nowMs = 100L + i))
        }

        val clips = store.getClips()
        assertEquals(ClipboardStore.MAX_CLIPS, clips.size)
        assertTrue(clips.first().pinned)
        assertEquals("keep-me", clips.first().text)
    }

    @Test
    fun skipsPasswordFieldsAndSensitiveClips() {
        assertFalse(store.recordClip("secret123", isPasswordField = true))
        assertFalse(store.recordClip("otp-4421", isPasswordField = false, isSensitive = true))
        assertFalse(store.recordClip("   ", isPasswordField = false))
        assertTrue(store.getClips().isEmpty())
    }

    @Test
    fun clearUnpinnedKeepsPinnedClipsOnly() {
        store.recordClip("pinned-clip", isPasswordField = false, nowMs = 10L)
        val pinnedId = store.getClips().first().id
        store.togglePin(pinnedId)
        store.recordClip("temp-clip", isPasswordField = false, nowMs = 20L)

        val remaining = store.clearUnpinned()
        assertEquals(1, remaining.size)
        assertEquals("pinned-clip", remaining.first().text)
    }
}
