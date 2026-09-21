package com.kgsoft.favorsbank.data

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.ArrayDeque
import java.util.Date
import java.util.Locale

/**
 * Temporary diagnostic logger.
 *
 * Records what the app does (prayer-times API calls, location flow, cache
 * hits...) into an in-memory ring buffer plus a small cache file, so the user
 * can open the "diagnostic log" screen in Settings and see exactly what the
 * app did — and share it when reporting a problem.
 *
 * Entries older than 24 hours are purged automatically on every write and
 * every read, so nothing accumulates. The UI never shows these messages;
 * failures stay silent on screen and are only recorded here.
 */
object DiagLog {
    private const val MAX_ENTRIES = 400
    private const val TTL_MS = 24 * 60 * 60 * 1000L
    private const val FILE_NAME = "diaglog.txt"

    private val lock = Any()
    private val mem = ArrayDeque<Pair<Long, String>>()
    private var cacheDir: File? = null
    private val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.US)

    /** Call once from MainActivity.onCreate. Safe to call repeatedly. */
    fun init(context: Context) {
        synchronized(lock) {
            if (cacheDir == null) {
                cacheDir = context.cacheDir
                loadLocked()
                purgeLocked()
            }
        }
        d("app", "diagnostic logger ready")
    }

    /** Record one line. Never throws; safe to call from any thread. */
    fun d(tag: String, msg: String) {
        val now = System.currentTimeMillis()
        val line = "${timeFmt.format(Date(now))} [$tag] $msg"
        synchronized(lock) {
            mem.addLast(now to line)
            while (mem.size > MAX_ENTRIES) mem.removeFirst()
            purgeLocked(now)
            persistLocked()
        }
    }

    /** Newest-first snapshot for the diagnostics screen. */
    fun snapshot(): List<String> = synchronized(lock) {
        purgeLocked()
        persistLocked()
        mem.map { it.second }.reversed()
    }

    /** Drop everything (memory + file). */
    fun clear() = synchronized(lock) {
        mem.clear()
        runCatching { cacheDir?.let { File(it, FILE_NAME).delete() } }
    }

    private fun loadLocked() {
        val dir = cacheDir ?: return
        runCatching {
            val f = File(dir, FILE_NAME)
            if (!f.exists()) return
            f.readLines().forEach { raw ->
                val sep = raw.indexOf('|')
                if (sep > 0) {
                    val ts = raw.substring(0, sep).toLongOrNull() ?: return@forEach
                    mem.addLast(ts to raw.substring(sep + 1))
                }
            }
            while (mem.size > MAX_ENTRIES) mem.removeFirst()
        }
    }

    private fun purgeLocked(now: Long = System.currentTimeMillis()) {
        val cutoff = now - TTL_MS
        while (mem.isNotEmpty() && mem.first().first < cutoff) mem.removeFirst()
    }

    private fun persistLocked() {
        val dir = cacheDir ?: return
        runCatching {
            File(dir, FILE_NAME).writeText(
                mem.joinToString("\n") { "${it.first}|${it.second}" } + "\n"
            )
        }
    }
}
