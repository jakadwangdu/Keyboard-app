package com.example.engine

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.min

data class GlideCandidate(
    val word: String,
    val score: Float
)

object GlideTypingEngine {

    // Normalized key center coordinates (row 0: Q-P, row 1: A-L, row 2: Z-M)
    // X is 0.0 to 1.0, Y is 0.0 to 1.0 (relative to keyboard bounds)
    private val keyCoordinates: Map<Char, Offset> = mapOf(
        // Row 1 (Q to P, 10 keys)
        'q' to Offset(0.05f, 0.16f),
        'w' to Offset(0.15f, 0.16f),
        'e' to Offset(0.25f, 0.16f),
        'r' to Offset(0.35f, 0.16f),
        't' to Offset(0.45f, 0.16f),
        'y' to Offset(0.55f, 0.16f),
        'u' to Offset(0.65f, 0.16f),
        'i' to Offset(0.75f, 0.16f),
        'o' to Offset(0.85f, 0.16f),
        'p' to Offset(0.95f, 0.16f),

        // Row 2 (A to L, 9 keys, indented)
        'a' to Offset(0.10f, 0.50f),
        's' to Offset(0.20f, 0.50f),
        'd' to Offset(0.30f, 0.50f),
        'f' to Offset(0.40f, 0.50f),
        'g' to Offset(0.50f, 0.50f),
        'h' to Offset(0.60f, 0.50f),
        'j' to Offset(0.70f, 0.50f),
        'k' to Offset(0.80f, 0.50f),
        'l' to Offset(0.90f, 0.50f),

        // Row 3 (Z to M, 7 keys, indented between shift and backspace)
        'z' to Offset(0.20f, 0.84f),
        'x' to Offset(0.30f, 0.84f),
        'c' to Offset(0.40f, 0.84f),
        'v' to Offset(0.50f, 0.84f),
        'b' to Offset(0.60f, 0.84f),
        'n' to Offset(0.70f, 0.84f),
        'm' to Offset(0.80f, 0.84f)
    )

    // Rich vocabulary for swipe recognition
    val glideVocabulary: List<String> by lazy {
        listOf(
            "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
            "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
            "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
            "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
            "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
            "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
            "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
            "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
            "back", "after", "use", "two", "how", "our", "work", "first", "well", "way",
            "even", "new", "want", "because", "any", "these", "give", "day", "most", "us",
            "hello", "thanks", "thank", "please", "yes", "cool", "great", "awesome",
            "keyboard", "mechanical", "switch", "switches", "sound", "thock", "clack",
            "glide", "typing", "swipe", "smooth", "minimal", "fast", "speed", "test",
            "today", "tomorrow", "tonight", "morning", "night", "meeting", "message",
            "call", "chat", "love", "friend", "happy", "excited", "ready", "super",
            "android", "gboard", "whatsapp", "retro", "sound", "acoustics", "tactile",
            "linear", "clicky", "space", "home", "work", "office", "quick", "easy",
            "better", "best", "world", "play", "music", "view", "edit", "clear", "copy",
            "paste", "send", "share", "write", "read", "speak", "voice", "clean",
            "screen", "display", "color", "dark", "light", "amber", "blue", "red"
        ).distinct()
    }

    /**
     * Finds closest key char for normalized coordinate (0..1, 0..1)
     */
    fun findClosestKey(normalizedPoint: Offset): Char? {
        var minDistance = Float.MAX_VALUE
        var closestKey: Char? = null
        for ((char, pos) in keyCoordinates) {
            val dist = hypot(normalizedPoint.x - pos.x, normalizedPoint.y - pos.y)
            if (dist < minDistance) {
                minDistance = dist
                closestKey = char
            }
        }
        return if (minDistance < 0.22f) closestKey else null
    }

    /**
     * Decodes a glide path (points in normalized 0..1 space) into candidate words
     */
    fun recognizeGlidePath(points: List<Offset>): List<GlideCandidate> {
        if (points.size < 3) return emptyList()

        // 1. Identify start, intermediate waypoints, and end keys
        val startPoint = points.first()
        val endPoint = points.last()

        val startChar = findClosestKey(startPoint) ?: return emptyList()
        val endChar = findClosestKey(endPoint) ?: return emptyList()

        // Resample path to 15 equidistant points for uniform comparison
        val resampled = resamplePath(points, 15)

        val candidates = mutableListOf<GlideCandidate>()

        for (word in glideVocabulary) {
            val w = word.lowercase()
            if (w.isEmpty()) continue

            // Must start with same first letter (or adjacent)
            if (w.first() != startChar) {
                val startPos = keyCoordinates[w.first()] ?: continue
                if (hypot(startPoint.x - startPos.x, startPoint.y - startPos.y) > 0.16f) {
                    continue
                }
            }

            // Must end with close last letter
            val lastChar = w.last()
            val endPos = keyCoordinates[lastChar]
            if (lastChar != endChar && endPos != null) {
                if (hypot(endPoint.x - endPos.x, endPoint.y - endPos.y) > 0.18f) {
                    continue
                }
            }

            // Check if characters appear in order along the gesture
            val score = calculatePathScore(w, resampled)
            if (score > 0f) {
                candidates.add(GlideCandidate(word, score))
            }
        }

        return candidates.sortedByDescending { it.score }.take(5)
    }

    private fun calculatePathScore(word: String, path: List<Offset>): Float {
        var wordIdx = 0
        var totalDistance = 0f

        val wordPoints = word.mapNotNull { keyCoordinates[it] }
        if (wordPoints.size != word.length) return 0f

        // Check character matching sequence
        for (pt in path) {
            if (wordIdx < wordPoints.size) {
                val targetKey = wordPoints[wordIdx]
                val d = hypot(pt.x - targetKey.x, pt.y - targetKey.y)
                if (d < 0.14f) {
                    wordIdx++
                }
            }
        }

        // Did we hit most characters in the word?
        val matchRatio = wordIdx.toFloat() / word.length.toFloat()
        if (matchRatio < 0.70f && word.length > 3) return 0f

        // Length bonus for exact length matches
        val lengthScore = 1.0f - (abs(word.length - (path.size / 2)) * 0.05f).coerceIn(0f, 0.5f)

        return (matchRatio * 0.7f + lengthScore * 0.3f)
    }

    private fun resamplePath(points: List<Offset>, targetCount: Int): List<Offset> {
        if (points.size <= targetCount) return points
        val step = points.size.toFloat() / targetCount.toFloat()
        val result = mutableListOf<Offset>()
        for (i in 0 until targetCount) {
            val idx = (i * step).toInt().coerceIn(0, points.size - 1)
            result.add(points[idx])
        }
        return result
    }
}
