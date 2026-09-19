package com.kgsoft.favorsbank.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** Prayer times for one day, as returned by the AlAdhan API (24h "HH:mm"). */
@Serializable
data class DayPrayers(
    val date: String = "",
    val fajr: String = "",
    val sunrise: String = "",
    val dhuhr: String = "",
    val asr: String = "",
    val maghrib: String = "",
    val isha: String = ""
)

private val prayerJson = Json { ignoreUnknownKeys = true }

fun DayPrayers.toJsonString(): String = prayerJson.encodeToString(DayPrayers.serializer(), this)

fun dayPrayersFromJson(raw: String): DayPrayers? =
    runCatching { prayerJson.decodeFromString(DayPrayers.serializer(), raw) }.getOrNull()

/** Fallback when the user skips location: Khartoum, Sudan. */
const val FALLBACK_LAT = 15.5007
const val FALLBACK_LNG = 32.5599

/**
 * AlAdhan prayer-times API (https://aladhan.com/prayer-times-api).
 * Plain HttpURLConnection + org.json: no extra dependencies.
 * Returns null on any failure — callers must fail silently and keep cache.
 */
object PrayerApi {
    suspend fun fetchTimings(lat: Double, lng: Double, date: String): DayPrayers? =
        withContext(Dispatchers.IO) {
            runCatching {
                val url = URL(
                    "https://api.aladhan.com/v1/timings/$date" +
                        "?latitude=$lat&longitude=$lng&method=5"
                )
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 12_000
                    readTimeout = 12_000
                    requestMethod = "GET"
                }
                try {
                    if (conn.responseCode != HttpURLConnection.HTTP_OK) return@runCatching null
                    val body = conn.inputStream.bufferedReader().use { it.readText() }
                    val t = JSONObject(body).getJSONObject("data").getJSONObject("timings")
                    DayPrayers(
                        date = date,
                        fajr = t.getString("Fajr").take(5),
                        sunrise = t.getString("Sunrise").take(5),
                        dhuhr = t.getString("Dhuhr").take(5),
                        asr = t.getString("Asr").take(5),
                        maghrib = t.getString("Maghrib").take(5),
                        isha = t.getString("Isha").take(5)
                    )
                } finally {
                    conn.disconnect()
                }
            }.getOrNull()
        }
}

/** Last-known device location via the framework LocationManager (no Play Services). */
object PrayerLocation {
    @SuppressLint("MissingPermission")
    fun lastKnown(context: Context): Pair<Double, Double>? = runCatching {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = lm.getProviders(true)
        var best: android.location.Location? = null
        for (p in providers) {
            val loc = lm.getLastKnownLocation(p) ?: continue
            if (best == null || (loc.accuracy < best.accuracy)) best = loc
        }
        best?.let { it.latitude to it.longitude }
    }.getOrNull()
}

/** True when there is an active network connection. */
fun isNetworkAvailable(context: Context): Boolean = runCatching {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
        as android.net.ConnectivityManager
    val net = cm.activeNetwork ?: return@runCatching false
    val caps = cm.getNetworkCapabilities(net) ?: return@runCatching false
    caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
}.getOrDefault(false)

/** "04:34" (24h) -> "4:34 ص" for display. */
fun formatPrayerTime(hhmm: String): String {
    val parts = hhmm.split(":")
    if (parts.size != 2) return hhmm
    val h = parts[0].toIntOrNull() ?: return hhmm
    val m = parts[1]
    val suffix = if (h < 12) "ص" else "م"
    val h12 = when (h % 12) { 0 -> 12; else -> h % 12 }
    return "$h12:$m $suffix"
}

/** "HH:mm" -> minutes since midnight, or null. */
fun hhmmToMinutes(hhmm: String): Int? {
    val parts = hhmm.split(":")
    if (parts.size != 2) return null
    val h = parts[0].toIntOrNull() ?: return null
    val m = parts[1].toIntOrNull() ?: return null
    return h * 60 + m
}
