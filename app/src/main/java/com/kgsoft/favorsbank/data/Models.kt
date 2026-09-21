package com.kgsoft.favorsbank.data

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/** A "mission" from missions.json (tasks tab). */
@Serializable
data class Mission(
    val mission: String = "",
    val earnings: String = "",
    val details: String = "",
    val time: String = "",
    val title: String = ""
)

/** A "job" from jobs.json (business tab). */
@Serializable
data class Job(
    val mission: String = "",
    val earnings: String = "",
    val details: String = "",
    val time: String = ""
)

private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

object AssetsRepo {
    /**
     * Missions file for the UI language: a dedicated file when one exists
     * (ar/en/fr/sw), otherwise the English file as the universal fallback.
     */
    fun loadMissions(context: Context, langCode: String): List<Mission> {
        val file = when (langCode) {
            "ar" -> "missions.json"
            "fr" -> "missions_fr.json"
            "sw" -> "missions_sawahili.json"
            else -> "missions_en.json" // en + ur/id/fa/ha/bn/tr fall back to English
        }
        val text = context.assets.open(file).bufferedReader().use { it.readText() }
        return json.decodeFromString(ListSerializer(Mission.serializer()), text)
    }

    /**
     * Jobs file for the UI language: Arabic source, English for every other
     * language (no per-language jobs files exist yet).
     */
    fun loadJobs(context: Context, langCode: String): List<Job> {
        val file = if (langCode == "ar") "jobs.json" else "jobs_en.json"
        val text = context.assets.open(file).bufferedReader().use { it.readText() }
        return json.decodeFromString(ListSerializer(Job.serializer()), text)
    }

    /** Backwards-compatible overload (defaults to the old ar/en behavior). */
    fun loadMissions(context: Context, english: Boolean): List<Mission> =
        loadMissions(context, if (english) "en" else "ar")
}
