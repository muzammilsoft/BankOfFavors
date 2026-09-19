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
    fun loadMissions(context: Context, english: Boolean): List<Mission> {
        val file = if (english) "missions_en.json" else "missions.json"
        val text = context.assets.open(file).bufferedReader().use { it.readText() }
        return json.decodeFromString(ListSerializer(Mission.serializer()), text)
    }

    fun loadJobs(context: Context): List<Job> {
        val text = context.assets.open("jobs.json").bufferedReader().use { it.readText() }
        return json.decodeFromString(ListSerializer(Job.serializer()), text)
    }
}
