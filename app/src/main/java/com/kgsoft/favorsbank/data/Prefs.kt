package com.kgsoft.favorsbank.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Replaces the Sketchware-era SharedPreferences files ("profile", "settings",
 * "theme", "archives") with DataStore, keeping the exact same store names and
 * string keys so existing installs migrate transparently.
 */
private val Context.profileStore: DataStore<Preferences> by preferencesDataStore(name = "profile")
private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
private val Context.themeStore: DataStore<Preferences> by preferencesDataStore(name = "theme")
private val Context.archivesStore: DataStore<Preferences> by preferencesDataStore(name = "archives")
private val Context.prayerStore: DataStore<Preferences> by preferencesDataStore(name = "prayer")
private val Context.completedStore: DataStore<Preferences> by preferencesDataStore(name = "completed")

object PrefKeys {
    // profile
    val USERNAME = stringPreferencesKey("username")
    val PASSWORD = stringPreferencesKey("password")
    val DONT_SHOW_AGAIN = stringPreferencesKey("dontShowAgain")
    val HASANAT = stringPreferencesKey("hasanat")
    val BONUS = stringPreferencesKey("bonus")
    val FIRST_RUN = stringPreferencesKey("firstRun")
    // settings
    val LANGUAGE = stringPreferencesKey("language")
    val NOTIFICATIONS = stringPreferencesKey("notifications")
    val TOASTS = stringPreferencesKey("toasts")
    // theme
    val THEME = stringPreferencesKey("theme")
    // prayer
    val PRAYER_LAT = stringPreferencesKey("lat")
    val PRAYER_LNG = stringPreferencesKey("lng")
    val PRAYER_DATE = stringPreferencesKey("date")
    val PRAYER_JSON = stringPreferencesKey("timings")
    val PRAYER_CITY = stringPreferencesKey("city")
    val PRAYER_COUNTRY = stringPreferencesKey("country")
    val PRAYER_CITY_LABEL = stringPreferencesKey("cityLabel")
    val PRAYER_MANUAL = stringPreferencesKey("manualTimings")
    // completed log
    val COMPLETED_LOG = stringPreferencesKey("log")
}

/** One completed task entry: title, completion date (yyyy-MM-dd) and hasanat earned. */
@Serializable
data class CompletedTask(
    val title: String = "",
    val date: String = "",
    val hasanat: Long = 0L
)

private val logJson = Json { ignoreUnknownKeys = true }

class PrefsRepository(private val context: Context) {

    // ---------- profile ----------
    val username: Flow<String?> = context.profileStore.data.map { it[PrefKeys.USERNAME] }
    val password: Flow<String?> = context.profileStore.data.map { it[PrefKeys.PASSWORD] }
    val dontShowAgain: Flow<Boolean> =
        context.profileStore.data.map { it[PrefKeys.DONT_SHOW_AGAIN] == "true" }
    val hasanat: Flow<Long> = context.profileStore.data.map { it[PrefKeys.HASANAT]?.toLongOrNull() ?: 0L }
    val bonus: Flow<String?> = context.profileStore.data.map { it[PrefKeys.BONUS] }
    val firstRun: Flow<Int> = context.profileStore.data.map { it[PrefKeys.FIRST_RUN]?.toIntOrNull() ?: 0 }

    suspend fun setUsername(v: String) = context.profileStore.edit { it[PrefKeys.USERNAME] = v }
    suspend fun setPassword(v: String) = context.profileStore.edit { it[PrefKeys.PASSWORD] = v }
    suspend fun setDontShowAgain(v: Boolean) =
        context.profileStore.edit { it[PrefKeys.DONT_SHOW_AGAIN] = if (v) "true" else "false" }
    suspend fun setBonus(v: String) = context.profileStore.edit { it[PrefKeys.BONUS] = v }

    /** Adds [amount] to the hasanat balance (stored as a numeric string, like the original). */
    suspend fun addHasanat(amount: Long) {
        context.profileStore.edit {
            val current = it[PrefKeys.HASANAT]?.toLongOrNull() ?: 0L
            it[PrefKeys.HASANAT] = (current + amount).toString()
        }
    }

    suspend fun incrementFirstRun(): Int {
        var next = 0
        context.profileStore.edit {
            next = (it[PrefKeys.FIRST_RUN]?.toIntOrNull() ?: 0) + 1
            it[PrefKeys.FIRST_RUN] = next.toString()
        }
        return next
    }

    // ---------- settings ----------
    val language: Flow<String> =
        context.settingsStore.data.map { it[PrefKeys.LANGUAGE] ?: "" }
    val notificationsEnabled: Flow<Boolean> =
        context.settingsStore.data.map { it[PrefKeys.NOTIFICATIONS] == "true" }
    val toastsEnabled: Flow<Boolean> =
        context.settingsStore.data.map { it[PrefKeys.TOASTS] == "true" }

    suspend fun setLanguage(v: String) = context.settingsStore.edit { it[PrefKeys.LANGUAGE] = v }
    suspend fun setNotifications(v: Boolean) =
        context.settingsStore.edit { it[PrefKeys.NOTIFICATIONS] = if (v) "true" else "false" }
    suspend fun setToasts(v: Boolean) =
        context.settingsStore.edit { it[PrefKeys.TOASTS] = if (v) "true" else "false" }

    // ---------- theme ----------
    val isDarkTheme: Flow<Boolean> =
        context.themeStore.data.map { it[PrefKeys.THEME] == "dark" }

    suspend fun setDarkTheme(dark: Boolean) {
        context.themeStore.edit {
            if (dark) it[PrefKeys.THEME] = "dark" else it.remove(PrefKeys.THEME)
        }
    }

    // ---------- archives (bookmarks; original used a broken "" key) ----------
    suspend fun setFavorite(id: String, value: String) =
        context.archivesStore.edit { it[stringPreferencesKey(id)] = value }

    suspend fun removeFavorite(id: String) =
        context.archivesStore.edit { it.remove(stringPreferencesKey(id)) }

    fun isFavorite(id: String): Flow<Boolean> =
        context.archivesStore.data.map { it[stringPreferencesKey(id)] != null }

    // ---------- prayer times ----------
    val prayerLat: Flow<Double?> =
        context.prayerStore.data.map { it[PrefKeys.PRAYER_LAT]?.toDoubleOrNull() }
    val prayerLng: Flow<Double?> =
        context.prayerStore.data.map { it[PrefKeys.PRAYER_LNG]?.toDoubleOrNull() }
    val cachedPrayerDate: Flow<String?> =
        context.prayerStore.data.map { it[PrefKeys.PRAYER_DATE] }
    val cachedPrayerJson: Flow<String?> =
        context.prayerStore.data.map { it[PrefKeys.PRAYER_JSON] }

    suspend fun savePrayerLocation(lat: Double, lng: Double) =
        context.prayerStore.edit {
            it[PrefKeys.PRAYER_LAT] = lat.toString()
            it[PrefKeys.PRAYER_LNG] = lng.toString()
        }

    suspend fun cachePrayerTimes(date: String, json: String) =
        context.prayerStore.edit {
            it[PrefKeys.PRAYER_DATE] = date
            it[PrefKeys.PRAYER_JSON] = json
        }

    // ---------- prayer city (city-search flow, no GPS needed) ----------
    val prayerCity: Flow<String?> =
        context.prayerStore.data.map { it[PrefKeys.PRAYER_CITY] }
    val prayerCountry: Flow<String?> =
        context.prayerStore.data.map { it[PrefKeys.PRAYER_COUNTRY] }
    val prayerCityLabel: Flow<String?> =
        context.prayerStore.data.map { it[PrefKeys.PRAYER_CITY_LABEL] }

    suspend fun savePrayerCity(cityEn: String, countryEn: String, labelAr: String) =
        context.prayerStore.edit {
            it[PrefKeys.PRAYER_CITY] = cityEn
            it[PrefKeys.PRAYER_COUNTRY] = countryEn
            it[PrefKeys.PRAYER_CITY_LABEL] = labelAr
        }

    suspend fun clearPrayerCity() = context.prayerStore.edit {
        it.remove(PrefKeys.PRAYER_CITY)
        it.remove(PrefKeys.PRAYER_COUNTRY)
        it.remove(PrefKeys.PRAYER_CITY_LABEL)
    }

    /**
     * Saves a place picked from the online city search: coordinates plus the
     * display label. Clears any legacy city/country pair so coordinates win.
     */
    suspend fun savePrayerPlace(lat: Double, lng: Double, label: String) =
        context.prayerStore.edit {
            it[PrefKeys.PRAYER_LAT] = lat.toString()
            it[PrefKeys.PRAYER_LNG] = lng.toString()
            it[PrefKeys.PRAYER_CITY_LABEL] = label
            it.remove(PrefKeys.PRAYER_CITY)
            it.remove(PrefKeys.PRAYER_COUNTRY)
        }

    /** Clears the cached timings so the next load is forced to fetch fresh ones. */
    suspend fun clearPrayerCache() = context.prayerStore.edit {
        it.remove(PrefKeys.PRAYER_DATE)
        it.remove(PrefKeys.PRAYER_JSON)
    }

    // ---------- manual prayer times (user-set override) ----------
    val manualPrayerJson: Flow<String?> =
        context.prayerStore.data.map { it[PrefKeys.PRAYER_MANUAL] }

    suspend fun saveManualPrayerTimes(json: String) =
        context.prayerStore.edit { it[PrefKeys.PRAYER_MANUAL] = json }

    suspend fun clearManualPrayerTimes() =
        context.prayerStore.edit { it.remove(PrefKeys.PRAYER_MANUAL) }

    // ---------- completed tasks log ----------
    val completedLog: Flow<List<CompletedTask>> =
        context.completedStore.data.map { prefs ->
            val raw = prefs[PrefKeys.COMPLETED_LOG] ?: return@map emptyList()
            runCatching {
                logJson.decodeFromString(ListSerializer(CompletedTask.serializer()), raw)
            }.getOrDefault(emptyList())
        }

    /** Appends a completed task with today's date (newest first, capped at 500). */
    suspend fun logCompletion(title: String, hasanat: Long) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        context.completedStore.edit { prefs ->
            val raw = prefs[PrefKeys.COMPLETED_LOG]
            val current = if (raw != null) runCatching {
                logJson.decodeFromString(ListSerializer(CompletedTask.serializer()), raw)
            }.getOrDefault(emptyList()) else emptyList()
            val updated = (listOf(CompletedTask(title, today, hasanat)) + current).take(500)
            prefs[PrefKeys.COMPLETED_LOG] =
                logJson.encodeToString(ListSerializer(CompletedTask.serializer()), updated)
        }
    }
}

/** One-shot read helper for places that need a plain value instead of a Flow. */
suspend fun <T> Flow<T>.firstValue(): T = first()
