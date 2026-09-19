package com.kgsoft.favorsbank.data

/**
 * Derives the hasanat reward from the number shown in the task's own reward
 * text (e.g. "100 حسنة", "195 * 10 = 1950 حسنة", "1,000 rewards").
 *
 * Rules:
 * - a digit group tied to حسنة/حسنات/reward(s) wins (first match);
 * - otherwise the Arabic words ألف (1000) and مليون/ملايين (1,000,000)
 *   tied to حسنة/حسنات;
 * - otherwise 0 — no invented default.
 */
object HasanatCalc {
    private val digitNearHasana =
        Regex("""(\d[\d,]*)\s*(?:حسن|rewards?)""", RegexOption.IGNORE_CASE)

    fun extract(earnings: String): Long {
        val t = earnings.replace('،', ',')
        digitNearHasana.find(t)?.let { m ->
            return m.groupValues[1].replace(",", "").toLongOrNull() ?: 0L
        }
        val mentionsHasana = t.contains("حسن") || t.contains("reward", ignoreCase = true)
        if (mentionsHasana) {
            if (t.contains("ملايين") || t.contains("مليون")) return 1_000_000L
            if (t.contains("ألف") || t.contains("الف")) return 1_000L
        }
        return 0L
    }
}
