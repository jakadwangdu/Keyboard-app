package com.example.engine

import kotlin.math.abs
import kotlin.math.min

data class CorrectionCandidate(
    val word: String,
    val isAutoCorrect: Boolean = false,
    val confidence: Float = 1.0f
)

object AutocorrectEngine {

    // Common instant typo lookup map
    private val commonTypoMap = mapOf(
        "macanical" to "mechanical",
        "mechanic" to "mechanical",
        "teh" to "the",
        "adn" to "and",
        "taht" to "that",
        "waht" to "what",
        "thsi" to "this",
        "wiht" to "with",
        "hwllo" to "hello",
        "hlelo" to "hello",
        "helo" to "hello",
        "thx" to "thanks",
        "thansk" to "thanks",
        "thanx" to "thanks",
        "ketyboard" to "keyboard",
        "keybaord" to "keyboard",
        "keybord" to "keyboard",
        "goboard" to "gboard",
        "watsapp" to "whatsapp",
        "whatsap" to "whatsapp",
        "becuse" to "because",
        "becasue" to "because",
        "beacuse" to "because",
        "tomorow" to "tomorrow",
        "tommorrow" to "tomorrow",
        "yesterdy" to "yesterday",
        "writting" to "writing",
        "beleive" to "believe",
        "belive" to "believe",
        "recieve" to "receive",
        "recieved" to "received",
        "definately" to "definitely",
        "definitly" to "definitely",
        "seperate" to "separate",
        "untill" to "until",
        "freind" to "friend",
        "peopel" to "people",
        "shoudl" to "should",
        "woudl" to "would",
        "coudl" to "could",
        "alot" to "a lot",
        "wierd" to "weird",
        "truely" to "truly",
        "goverment" to "government",
        "neccessary" to "necessary",
        "necessery" to "necessary",
        "occured" to "occurred",
        "occuring" to "occurring",
        "realy" to "really",
        "allways" to "always",
        "alway" to "always",
        "acoustics" to "acoustics",
        "swicth" to "switch",
        "swich" to "switch",
        "thocky" to "thock",
        "miss" to "miss",
        "misstyped" to "mistyped",
        "spel" to "spell",
        "speling" to "spelling",
        "corect" to "correct",
        "corector" to "corrector",
        "android" to "android",
        "awsome" to "awesome",
        "perfct" to "perfect",
        "redy" to "ready",
        "meetng" to "meeting"
    )

    // Comprehensive vocabulary
    private val dictionary: Set<String> = setOf(
        // High frequency basic words
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
        
        // Conversational & Daily
        "hello", "hi", "hey", "thanks", "thank", "please", "yes", "yeah", "nope", "okay", "ok",
        "sure", "awesome", "great", "cool", "perfect", "ready", "love", "like", "favorite",
        "today", "tomorrow", "tonight", "yesterday", "morning", "afternoon", "evening", "night",
        "meeting", "message", "call", "chat", "talk", "listen", "hear", "friend", "family",
        "house", "home", "work", "office", "school", "coffee", "lunch", "dinner", "food",
        "water", "happy", "excited", "tired", "busy", "free", "weekend", "week", "month",
        
        // Mechanical keyboard, typing, and tech vocabulary
        "keyboard", "mechanical", "switch", "switches", "acoustics", "thock", "clack",
        "linear", "tactile", "clicky", "typing", "sound", "volume", "haptic", "vibration",
        "keycap", "keycaps", "spacebar", "backspace", "enter", "shift", "caps", "capslock",
        "layout", "qwerty", "symbols", "emoji", "emojis", "sticker", "stickers", "theme",
        "themes", "dark", "light", "minimal", "minimalist", "android", "whatsapp", "gboard",
        "retro", "cyberpunk", "cream", "alpacas", "holy", "panda", "zealios", "gat", "gateron",
        "cherry", "lubed", "film", "stabilizer", "plate", "housing", "pcb", "bluetooth",
        "wireless", "wired", "speed", "words", "minute", "spelling", "spell", "correct",
        "corrector", "autocorrect", "suggestion", "suggestions", "prediction", "predictive",
        "custom", "customize", "customization", "height", "profile", "audio", "feel",
        
        // Action verbs & Common adjectives
        "accept", "add", "allow", "ask", "become", "begin", "believe", "bring", "build",
        "buy", "call", "change", "check", "choose", "clean", "close", "continue", "create",
        "cut", "decide", "delete", "develop", "different", "difficult", "display", "early",
        "easy", "edit", "enable", "enjoy", "enough", "enter", "every", "example", "expect",
        "fast", "feel", "find", "finish", "follow", "forget", "found", "general", "group",
        "handle", "happen", "hard", "help", "high", "hold", "hope", "idea", "important",
        "include", "increase", "inform", "input", "install", "keep", "large", "last",
        "late", "launch", "learn", "leave", "left", "less", "letter", "level", "life",
        "light", "line", "list", "listen", "little", "live", "long", "main", "manage",
        "many", "matter", "mean", "might", "mind", "miss", "mistake", "mistyped", "move",
        "much", "must", "name", "near", "need", "never", "next", "nice", "night", "note",
        "number", "open", "order", "original", "part", "pass", "pay", "phone", "place",
        "plan", "play", "point", "possible", "post", "power", "press", "preview", "price",
        "problem", "product", "program", "project", "provide", "quick", "quiet", "quite",
        "read", "real", "really", "receive", "recent", "record", "remember", "remove",
        "report", "request", "require", "reset", "respond", "result", "return", "right",
        "room", "rule", "run", "same", "save", "search", "second", "send", "sense",
        "separate", "service", "set", "settings", "share", "short", "should", "show",
        "simple", "since", "small", "smooth", "soft", "solution", "sound", "space",
        "speak", "special", "start", "state", "status", "stay", "still", "stop", "story",
        "straight", "stream", "strong", "student", "style", "system", "table", "take",
        "taste", "teach", "team", "tell", "term", "test", "text", "thing", "think",
        "thought", "time", "today", "together", "told", "took", "tool", "top", "total",
        "touch", "track", "travel", "true", "truly", "trust", "try", "turn", "type",
        "under", "understand", "until", "update", "upload", "useful", "user", "value",
        "version", "view", "voice", "wait", "walk", "watch", "water", "wear", "weather",
        "welcome", "went", "where", "while", "white", "whole", "wide", "window", "wish",
        "without", "wonder", "word", "works", "world", "worry", "worth", "write", "writing",
        "wrong", "wrote", "young", "yourself"
    )

    /**
     * Finds spelling corrections and completions for a given raw word.
     * Returns a list of candidate suggestions, ordering the most likely autocorrect candidate first.
     */
    fun getCorrections(rawWord: String): List<CorrectionCandidate> {
        val word = rawWord.trim().lowercase()
        if (word.isEmpty()) return emptyList()

        val isCapitalized = rawWord.first().isUpperCase()

        // 1. Direct typo dictionary lookup (Instant 100% confidence fix)
        val typoMatch = commonTypoMap[word]
        if (typoMatch != null) {
            val formattedCorrection = if (isCapitalized) typoMatch.replaceFirstChar { it.uppercase() } else typoMatch
            val originalFormatted = rawWord
            return listOf(
                CorrectionCandidate(formattedCorrection, isAutoCorrect = true, confidence = 0.98f),
                CorrectionCandidate(originalFormatted, isAutoCorrect = false, confidence = 0.5f),
                CorrectionCandidate("${formattedCorrection}s", isAutoCorrect = false, confidence = 0.4f)
            )
        }

        // 2. Exact match in dictionary
        if (dictionary.contains(word)) {
            val prefixCompletions = dictionary
                .filter { it.startsWith(word) && it != word }
                .take(3)
                .map { if (isCapitalized) it.replaceFirstChar { c -> c.uppercase() } else it }

            val originalFormatted = rawWord
            val candidates = mutableListOf<CorrectionCandidate>()
            candidates.add(CorrectionCandidate(originalFormatted, isAutoCorrect = false, confidence = 1.0f))
            prefixCompletions.forEach { completion ->
                candidates.add(CorrectionCandidate(completion, isAutoCorrect = false, confidence = 0.7f))
            }
            return candidates
        }

        // 3. Prefix completions (user is currently typing a valid word)
        val prefixMatches = dictionary.filter { it.startsWith(word) }
        if (prefixMatches.isNotEmpty()) {
            val candidates = prefixMatches.take(4).mapIndexed { index, match ->
                val formatted = if (isCapitalized) match.replaceFirstChar { it.uppercase() } else match
                CorrectionCandidate(formatted, isAutoCorrect = (index == 0 && word.length >= 3), confidence = 0.85f - (index * 0.1f))
            }.toMutableList()
            if (!prefixMatches.contains(word)) {
                candidates.add(CorrectionCandidate(rawWord, isAutoCorrect = false, confidence = 0.3f))
            }
            return candidates
        }

        // 4. Misspelled word: Compute Levenshtein distance & keyboard proximity score
        val scoredWords = dictionary.mapNotNull { dictWord ->
            val dist = levenshteinDistance(word, dictWord)
            if (dist <= 2 && abs(word.length - dictWord.length) <= 2) {
                val score = 1.0f - (dist.toFloat() / maxOf(word.length, dictWord.length))
                dictWord to score
            } else null
        }.sortedByDescending { it.second }

        if (scoredWords.isNotEmpty()) {
            val best = scoredWords.first()
            val bestWord = if (isCapitalized) best.first.replaceFirstChar { it.uppercase() } else best.first
            val result = mutableListOf<CorrectionCandidate>()
            // Best autocorrect match marked true
            result.add(CorrectionCandidate(bestWord, isAutoCorrect = true, confidence = best.second))
            result.add(CorrectionCandidate(rawWord, isAutoCorrect = false, confidence = 0.2f))
            scoredWords.drop(1).take(2).forEach { (otherWord, score) ->
                val formatted = if (isCapitalized) otherWord.replaceFirstChar { it.uppercase() } else otherWord
                result.add(CorrectionCandidate(formatted, isAutoCorrect = false, confidence = score))
            }
            return result
        }

        // Fallback: return raw word
        return listOf(CorrectionCandidate(rawWord, isAutoCorrect = false, confidence = 1.0f))
    }

    /**
     * Compute Levenshtein Edit Distance between two strings.
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}
