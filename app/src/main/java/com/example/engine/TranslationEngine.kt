package com.example.engine

object TranslationEngine {

    // Common conversation phrasebook translations
    private val phrasebook: Map<Pair<String, String>, String> = mapOf(
        // English to Spanish
        Pair("hello", "es") to "hola",
        Pair("hi", "es") to "hola",
        Pair("how are you", "es") to "¿cómo estás?",
        Pair("good morning", "es") to "buenos días",
        Pair("good night", "es") to "buenas noches",
        Pair("thank you", "es") to "gracias",
        Pair("thanks", "es") to "gracias",
        Pair("please", "es") to "por favor",
        Pair("yes", "es") to "sí",
        Pair("no", "es") to "no",
        Pair("keyboard", "es") to "teclado",
        Pair("typing", "es") to "escribiendo",
        Pair("awesome", "es") to "increíble",
        Pair("see you tomorrow", "es") to "hasta mañana",
        Pair("i love this", "es") to "me encanta esto",
        Pair("sounds great", "es") to "suena genial",

        // English to French
        Pair("hello", "fr") to "bonjour",
        Pair("hi", "fr") to "salut",
        Pair("how are you", "fr") to "comment allez-vous?",
        Pair("good morning", "fr") to "bonjour",
        Pair("good night", "fr") to "bonne nuit",
        Pair("thank you", "fr") to "merci",
        Pair("thanks", "fr") to "merci beaucoup",
        Pair("please", "fr") to "s'il vous plaît",
        Pair("yes", "fr") to "oui",
        Pair("no", "fr") to "non",
        Pair("keyboard", "fr") to "clavier",
        Pair("typing", "fr") to "saisie",
        Pair("awesome", "fr") to "génial",
        Pair("sounds great", "fr") to "ça a l'air super",

        // English to German
        Pair("hello", "de") to "hallo",
        Pair("hi", "de") to "hallo",
        Pair("how are you", "de") to "wie geht es dir?",
        Pair("good morning", "de") to "guten morgen",
        Pair("good night", "de") to "gute nacht",
        Pair("thank you", "de") to "danke",
        Pair("thanks", "de") to "vielen dank",
        Pair("please", "de") to "bitte",
        Pair("yes", "de") to "ja",
        Pair("no", "de") to "nein",
        Pair("keyboard", "de") to "tastatur",
        Pair("awesome", "de") to "fantastisch",

        // English to Japanese
        Pair("hello", "ja") to "こんにちは",
        Pair("hi", "ja") to "やあ",
        Pair("how are you", "ja") to "お元気ですか？",
        Pair("good morning", "ja") to "おはようございます",
        Pair("good night", "ja") to "おやすみなさい",
        Pair("thank you", "ja") to "ありがとうございます",
        Pair("thanks", "ja") to "ありがとう",
        Pair("please", "ja") to "お願いします",
        Pair("keyboard", "ja") to "キーボード",
        Pair("awesome", "ja") to "素晴らしい",

        // English to Hindi
        Pair("hello", "hi") to "नमस्ते",
        Pair("hi", "hi") to "नमस्ते",
        Pair("how are you", "hi") to "आप कैसे हैं?",
        Pair("good morning", "hi") to "शुभ प्रभात",
        Pair("good night", "hi") to "शुभ रात्रि",
        Pair("thank you", "hi") to "धन्यवाद",
        Pair("thanks", "hi") to "शुक्रिया",
        Pair("please", "hi") to "कृपया",
        Pair("keyboard", "hi") to "कीबोर्ड",
        Pair("awesome", "hi") to "बहुत बढ़िया",

        // English to Chinese
        Pair("hello", "zh") to "你好",
        Pair("how are you", "zh") to "你好吗？",
        Pair("good morning", "zh") to "早上好",
        Pair("thank you", "zh") to "谢谢",
        Pair("keyboard", "zh") to "键盘",
        Pair("awesome", "zh") to "太棒了",

        // English to Italian
        Pair("hello", "it") to "ciao",
        Pair("thank you", "it") to "grazie",
        Pair("good morning", "it") to "buongiorno",
        Pair("keyboard", "it") to "tastiera",

        // English to Portuguese
        Pair("hello", "pt") to "olá",
        Pair("thank you", "pt") to "obrigado",
        Pair("good morning", "pt") to "bom dia",
        Pair("keyboard", "pt") to "teclado",

        // English to Russian
        Pair("hello", "ru") to "привет",
        Pair("thank you", "ru") to "спасибо",
        Pair("good morning", "ru") to "доброе утро",
        Pair("keyboard", "ru") to "клавиатура",

        // English to Arabic
        Pair("hello", "ar") to "مرحبا",
        Pair("thank you", "ar") to "شكرا لك",
        Pair("good morning", "ar") to "صباح الخير",
        Pair("keyboard", "ar") to "لوحة المفاتيح",

        // English to Korean
        Pair("hello", "ko") to "안녕하세요",
        Pair("thank you", "ko") to "감사합니다",
        Pair("good morning", "ko") to "좋은 아침",
        Pair("keyboard", "ko") to "키보드"
    )

    // Word vocabulary for word-by-word fallback
    private val wordMap: Map<Pair<String, String>, String> = mapOf(
        Pair("the", "es") to "el",
        Pair("and", "es") to "y",
        Pair("is", "es") to "es",
        Pair("this", "es") to "este",
        Pair("great", "es") to "genial",
        Pair("fast", "es") to "rápido",
        Pair("smooth", "es") to "suave",
        Pair("love", "es") to "amor",
        Pair("sound", "es") to "sonido",
        Pair("night", "es") to "noche",
        Pair("day", "es") to "día",

        Pair("the", "fr") to "le",
        Pair("and", "fr") to "et",
        Pair("is", "fr") to "est",
        Pair("this", "fr") to "ce",
        Pair("great", "fr") to "super",
        Pair("fast", "fr") to "rapide",
        Pair("smooth", "fr") to "fluide",

        Pair("the", "de") to "das",
        Pair("and", "de") to "und",
        Pair("is", "de") to "ist",
        Pair("great", "de") to "großartig",
        Pair("fast", "de") to "schnell"
    )

    /**
     * Translates input text from source language (default English) to target language
     */
    fun translate(text: String, targetLangCode: String): String {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return ""
        if (targetLangCode == "en") return text

        val lower = trimmed.lowercase()

        // 1. Direct phrase match
        phrasebook[Pair(lower, targetLangCode)]?.let { return it }

        // 2. Word-by-word translation fallback
        val words = trimmed.split(" ")
        val translatedWords = words.map { w ->
            val wLower = w.lowercase().replace(Regex("[^a-zA-Z]"), "")
            wordMap[Pair(wLower, targetLangCode)] ?: phrasebook[Pair(wLower, targetLangCode)] ?: w
        }

        return translatedWords.joinToString(" ")
    }
}
