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

/**
 * Replaces the Sketchware-era SharedPreferences files ("profile", "settings",
 * "theme", "archives") with DataStore, keeping the exact same store names and
 * string keys so existing installs migrate transparently.
 */
private val Context.profileStore: DataStore<Preferences> by preferencesDataStore(name = "profile")
private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
private val Context.themeStore: DataStore<Preferences> by preferencesDataStore(name = "theme")
private val Context.archivesStore: DataStore<Preferences> by preferencesDataStore(name = "archives")

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
}

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
}

/** One-shot read helper for places that need a plain value instead of a Flow. */
suspend fun <T> Flow<T>.firstValue(): T = first()
