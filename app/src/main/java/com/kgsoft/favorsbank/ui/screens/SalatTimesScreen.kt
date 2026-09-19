package com.kgsoft.favorsbank.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.data.APP_LANGS
import com.kgsoft.favorsbank.data.DayPrayers
import com.kgsoft.favorsbank.data.FALLBACK_LAT
import com.kgsoft.favorsbank.data.FALLBACK_LNG
import com.kgsoft.favorsbank.data.PrayerApi
import com.kgsoft.favorsbank.data.PrayerLocation
import com.kgsoft.favorsbank.data.PrefsRepository
import com.kgsoft.favorsbank.data.dayPrayersFromJson
import com.kgsoft.favorsbank.data.firstValue
import com.kgsoft.favorsbank.data.formatPrayerTime
import com.kgsoft.favorsbank.data.hhmmToMinutes
import com.kgsoft.favorsbank.data.isNetworkAvailable
import com.kgsoft.favorsbank.data.toJsonString
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GrainShape
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.Tajwal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Prayer times screen.
 *
 * Times come from the AlAdhan API (https://aladhan.com/prayer-times-api)
 * for the user's location. The location is asked for once and saved;
 * cached times are shown instantly and refreshed silently whenever the
 * internet is available — no error messages are ever shown.
 */
@Composable
fun SalatTimesScreen(navController: NavController, prefs: PrefsRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var prayers by remember { mutableStateOf<DayPrayers?>(null) }
    var askLocation by remember { mutableStateOf(false) }
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    suspend fun refresh() {
        if (!isNetworkAvailable(context)) return // silent: keep showing cache
        val lat = prefs.prayerLat.firstValue() ?: return
        val lng = prefs.prayerLng.firstValue() ?: return
        val today = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date())
        // Calculation method follows the UI language's region
        // (Egypt for Arabs, Karachi for Urdu/Bengali, Diyanet for Turkish, ...).
        val method = APP_LANGS.find { it.code == Strings.langCode }?.prayerMethod ?: 5
        val fresh = PrayerApi.fetchTimings(lat, lng, today, method)
        if (fresh != null) {
            prefs.cachePrayerTimes(today, fresh.toJsonString())
            prayers = fresh
        }
        // on failure: silent, keep cache
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        scope.launch {
            val loc = if (granted) PrayerLocation.lastKnown(context) else null
            val (lat, lng) = loc ?: (FALLBACK_LAT to FALLBACK_LNG)
            prefs.savePrayerLocation(lat, lng)
            refresh()
        }
    }

    // Clock tick for the countdown.
    LaunchedEffect(Unit) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(1000)
        }
    }

    // Load cache, then refresh silently when online; ask location once.
    LaunchedEffect(Unit) {
        val cachedJson = prefs.cachedPrayerJson.firstValue()
        if (cachedJson != null) prayers = dayPrayersFromJson(cachedJson)
        if (prefs.prayerLat.firstValue() == null) {
            askLocation = true
        } else {
            refresh()
        }
    }

    val prayerList = prayers?.let {
        listOf(
            Strings.fajr to it.fajr,
            Strings.shuruj to it.sunrise,
            Strings.dhuhr to it.dhuhr,
            Strings.asr to it.asr,
            Strings.maghrib to it.maghrib,
            Strings.isha to it.isha
        )
    } ?: emptyList()

    // Next prayer + countdown.
    val cal = Calendar.getInstance()
    val nowMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    val nextIndex = prayerList.indexOfFirst { (_, t) ->
        (hhmmToMinutes(t) ?: Int.MAX_VALUE) > nowMinutes
    }.let { if (it == -1) 0 else it }
    val nextName = prayerList.getOrNull(nextIndex)?.first
    val nextMinutes = prayerList.getOrNull(nextIndex)?.second?.let(::hhmmToMinutes)
    val remainingMillis = if (nextMinutes != null) {
        var diff = nextMinutes * 60_000L -
            (cal.get(Calendar.HOUR_OF_DAY) * 3_600_000L +
                cal.get(Calendar.MINUTE) * 60_000L +
                cal.get(Calendar.SECOND) * 1000L)
        if (diff < 0) diff += 24 * 3_600_000L
        diff
    } else null
    // Silence unused warning when list is empty.
    @Suppress("UNUSED_VARIABLE")
    val tick = nowMillis

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = GreenPrimary)
            }
            Text(
                Strings.salatTimes,
                fontFamily = Tajwal,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Next-prayer hero card.
            Card(
                shape = GrainShape,
                colors = CardDefaults.cardColors(containerColor = GreenPrimary),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (nextName != null) "${Strings.remainingTime} ${Strings.nextPrayer}: $nextName"
                        else Strings.salatTimes,
                        fontFamily = Tajwal,
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        remainingMillis?.let {
                            val s = it / 1000
                            "%d:%02d:%02d".format(s / 3600, (s % 3600) / 60, s % 60)
                        } ?: "--:--:--",
                        fontFamily = Tajwal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(prayerList.size) { i ->
                    val (name, time) = prayerList[i]
                    val isNext = i == nextIndex
                    Card(
                        shape = GrainShape,
                        colors = CardDefaults.cardColors(
                            containerColor = if (isNext) GreenPrimary
                            else GreenPrimary.copy(alpha = 0.12f)
                        ),
                        elevation = CardDefaults.cardElevation(if (isNext) 4.dp else 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                name,
                                fontFamily = Tajwal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = if (isNext) Color.White
                                else MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                formatPrayerTime(time),
                                fontFamily = Tajwal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = if (isNext) Color.White else GreenPrimary
                            )
                        }
                    }
                }
            }
        }
    }

    // First-run location prompt (asked once; the choice is saved).
    if (askLocation) {
        AlertDialog(
            onDismissRequest = {
                askLocation = false
                scope.launch {
                    prefs.savePrayerLocation(FALLBACK_LAT, FALLBACK_LNG)
                    refresh()
                }
            },
            title = { Text(Strings.locationTitle, fontFamily = Tajwal, fontWeight = FontWeight.Bold) },
            text = { Text(Strings.locationMessage, fontFamily = Tajwal) },
            confirmButton = {
                TextButton(onClick = {
                    askLocation = false
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }) {
                    Text(Strings.useMyLocation, fontFamily = Tajwal, color = GreenPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    askLocation = false
                    scope.launch {
                        prefs.savePrayerLocation(FALLBACK_LAT, FALLBACK_LNG)
                        refresh()
                    }
                }) {
                    Text(Strings.useKhartoum, fontFamily = Tajwal)
                }
            }
        )
    }
}
