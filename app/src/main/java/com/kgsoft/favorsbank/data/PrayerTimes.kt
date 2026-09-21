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
    suspend fun fetchTimings(lat: Double, lng: Double, date: String, method: Int = 5): DayPrayers? =
        withContext(Dispatchers.IO) {
            DiagLog.d(
                "prayer-api",
                "fetch lat=${"%.2f".format(lat)} lng=${"%.2f".format(lng)} method=$method date=$date"
            )
            runCatching {
                val url = URL(
                    "https://api.aladhan.com/v1/timings/$date" +
                        "?latitude=$lat&longitude=$lng&method=$method"
                )
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 12_000
                    readTimeout = 12_000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "BankOfFavors/1.6.0")
                    setRequestProperty("Accept", "application/json")
                }
                try {
                    val code = conn.responseCode
                    DiagLog.d("prayer-api", "HTTP $code")
                    if (code != HttpURLConnection.HTTP_OK) return@runCatching null
                    val body = conn.inputStream.bufferedReader().use { it.readText() }
                    DiagLog.d("prayer-api", "body ${body.length} chars")
                    val t = JSONObject(body).getJSONObject("data").getJSONObject("timings")
                    val parsed = DayPrayers(
                        date = date,
                        fajr = t.getString("Fajr").take(5),
                        sunrise = t.getString("Sunrise").take(5),
                        dhuhr = t.getString("Dhuhr").take(5),
                        asr = t.getString("Asr").take(5),
                        maghrib = t.getString("Maghrib").take(5),
                        isha = t.getString("Isha").take(5)
                    )
                    DiagLog.d("prayer-api", "parsed ok fajr=${parsed.fajr} isha=${parsed.isha}")
                    parsed
                } finally {
                    conn.disconnect()
                }
            }.onFailure {
                DiagLog.d(
                    "prayer-api",
                    "FAILED ${it.javaClass.simpleName}: ${it.message?.take(160)}"
                )
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

    /**
     * Fresh single location fix. Tries last-known first; if none, requests one
     * update from the enabled providers and waits [timeoutMs] for a fix.
     * Returns null when no fix arrives in time. Caller must hold a location
     * permission before calling.
     */
    @SuppressLint("MissingPermission")
    suspend fun fresh(
        context: Context,
        timeoutMs: Long = 20_000
    ): Pair<Double, Double>? {
        lastKnown(context)?.let {
            DiagLog.d("prayer-loc", "last-known fix lat=${"%.2f".format(it.first)}")
            return it
        }
        DiagLog.d("prayer-loc", "no last-known fix, requesting fresh update")
        return runCatching {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val providers = lm.getProviders(true)
                .filter { it == LocationManager.GPS_PROVIDER || it == LocationManager.NETWORK_PROVIDER }
            DiagLog.d("prayer-loc", "enabled providers: $providers")
            if (providers.isEmpty()) return@runCatching null
            kotlinx.coroutines.suspendCancellableCoroutine<Pair<Double, Double>?> { cont ->
                val looper = android.os.Looper.getMainLooper()
                val handler = android.os.Handler(looper)
                var listener: android.location.LocationListener? = null
                var timeout: Runnable? = null
                fun stop() {
                    listener?.let { runCatching { lm.removeUpdates(it) } }
                    timeout?.let { handler.removeCallbacks(it) }
                }
                listener = object : android.location.LocationListener {
                    override fun onLocationChanged(loc: android.location.Location) {
                        if (cont.isActive) {
                            stop()
                            DiagLog.d(
                                "prayer-loc",
                                "fresh fix lat=${"%.2f".format(loc.latitude)} " +
                                    "acc=${loc.accuracy.toInt()}m"
                            )
                            cont.resume(loc.latitude to loc.longitude, null)
                        }
                    }
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }
                timeout = Runnable {
                    stop()
                    DiagLog.d("prayer-loc", "fresh fix TIMEOUT after ${timeoutMs}ms")
                    if (cont.isActive) cont.resume(null, null)
                }
                runCatching {
                    for (p in providers) lm.requestLocationUpdates(p, 0L, 0f, listener!!, looper)
                }.onFailure {
                    DiagLog.d("prayer-loc", "requestLocationUpdates FAILED: ${it.message?.take(120)}")
                    stop()
                    if (cont.isActive) cont.resume(null, null)
                    return@suspendCancellableCoroutine
                }
                cont.invokeOnCancellation { stop() }
                handler.postDelayed(timeout!!, timeoutMs)
            }
        }.onFailure {
            DiagLog.d("prayer-loc", "fresh() FAILED ${it.javaClass.simpleName}: ${it.message?.take(120)}")
        }.getOrNull()
    }
}

/** True when at least one location provider (GPS or network) is enabled. */
fun isLocationEnabled(context: Context): Boolean = runCatching {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}.getOrDefault(false)

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
