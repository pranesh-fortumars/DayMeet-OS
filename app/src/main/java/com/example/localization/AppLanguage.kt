package com.example.localization

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flagEmoji: String,
    val isRtl: Boolean = false
) {
    ENGLISH("en", "English", "English", "🇺🇸"),
    SPANISH("es", "Spanish", "Español", "🇪🇸"),
    FRENCH("fr", "French", "Français", "🇫🇷"),
    GERMAN("de", "German", "Deutsch", "🇩🇪"),
    HINDI("hi", "Hindi", "हिन्दी", "🇮🇳"),
    JAPANESE("ja", "Japanese", "日本語", "🇯🇵"),
    CHINESE("zh", "Chinese (Simplified)", "中文 (简体)", "🇨🇳"),
    ARABIC("ar", "Arabic", "العربية", "🇸🇦", isRtl = true),
    PORTUGUESE("pt", "Portuguese", "Português", "🇧🇷"),
    RUSSIAN("ru", "Russian", "Русский", "🇷🇺"),
    ITALIAN("it", "Italian", "Italiano", "🇮🇹"),
    KOREAN("ko", "Korean", "한국어", "🇰🇷"),
    TAMIL("ta", "Tamil", "தமிழ்", "🇮🇳");

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}
