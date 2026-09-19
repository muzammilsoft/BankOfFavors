package com.kgsoft.favorsbank.data

/**
 * One zikr or verse shown in three lines:
 *  1. [arabic] — the fixed original text, always shown first.
 *  2. [transliteration] — Latin-script pronunciation guide, shared by all languages.
 *  3. translation — the meaning in the user's language ([translations] by language code).
 *
 * If a translation is missing for the current language, English is used,
 * and finally the Arabic original — the card never renders empty.
 */
data class TrilingualZikr(
    val arabic: String,
    val transliteration: String,
    val translations: Map<String, String>,
    val count: Int,
    val virtueAr: String? = null,
) {
    fun meaning(langCode: String): String =
        translations[langCode] ?: translations["en"] ?: arabic
}
