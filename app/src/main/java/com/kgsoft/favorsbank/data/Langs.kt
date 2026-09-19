package com.kgsoft.favorsbank.data

/**
 * The 10 supported UI languages. [prayerMethod] is the AlAdhan API calculation
 * method most appropriate for the regions where the language is spoken, so
 * prayer times stay correct as the app spreads beyond the Arab world.
 */
data class AppLang(
    val code: String,
    val prefKey: String,
    val nativeName: String,
    val rtl: Boolean,
    val prayerMethod: Int,
)

val APP_LANGS = listOf(
    AppLang("ar", "arabic", "العربية", rtl = true, prayerMethod = 5), // Egyptian Authority — Sudan & Arab world
    AppLang("en", "english", "English", rtl = false, prayerMethod = 3), // Muslim World League — default
    AppLang("fr", "french", "Français", rtl = false, prayerMethod = 5), // West & North Africa
    AppLang("ur", "urdu", "اردو", rtl = true, prayerMethod = 1), // Karachi — Pakistan & India
    AppLang("id", "indonesian", "Bahasa Indonesia", rtl = false, prayerMethod = 17), // JAKIM — Indonesia & Malaysia
    AppLang("fa", "persian", "فارسی", rtl = true, prayerMethod = 7), // Tehran — Iran
    AppLang("sw", "swahili", "Kiswahili", rtl = false, prayerMethod = 3), // East Africa
    AppLang("ha", "hausa", "Hausa", rtl = false, prayerMethod = 3), // Nigeria & Niger
    AppLang("bn", "bengali", "বাংলা", rtl = false, prayerMethod = 1), // Karachi — Bangladesh
    AppLang("tr", "turkish", "Türkçe", rtl = false, prayerMethod = 13), // Diyanet — Turkey
)

/** Maps the stored preference value to a language (handles legacy values too). */
fun appLangFor(pref: String): AppLang {
    val p = pref.trim().lowercase()
    if (p.isEmpty()) return APP_LANGS[0]
    APP_LANGS.find { p.contains(it.prefKey) }?.let { return it }
    if (p.contains("francais")) return APP_LANGS[2] // legacy French key
    if (p.contains("english")) return APP_LANGS[1]
    return APP_LANGS[0]
}
