package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.data.APP_LANGS
import com.kgsoft.favorsbank.data.DayPrayers
import com.kgsoft.favorsbank.data.DiagLog
import com.kgsoft.favorsbank.data.GeoApi
import com.kgsoft.favorsbank.data.GeoCity
import com.kgsoft.favorsbank.data.PrayerApi
import com.kgsoft.favorsbank.data.PrefsRepository
import com.kgsoft.favorsbank.data.dayPrayersFromJson
import com.kgsoft.favorsbank.data.firstValue
import com.kgsoft.favorsbank.data.isNetworkAvailable
import com.kgsoft.favorsbank.data.toJsonString
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.Tajwal
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Prayer times settings: repair (clear cache + reload from the internet),
 * change city via the open geocoding API (every city in the world),
 * and manual time entry as an override shown instead of internet times.
 */
@Composable
fun PrayerSettingsScreen(navController: NavController, prefs: PrefsRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var repairStatus by remember { mutableStateOf<String?>(null) }
    var repairing by remember { mutableStateOf(false) }

    var cityQuery by remember { mutableStateOf("") }
    var cityResults by remember { mutableStateOf<List<GeoCity>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    var cityMsg by remember { mutableStateOf<String?>(null) }

    var manual by remember { mutableStateOf<DayPrayers?>(null) }
    var fFajr by remember { mutableStateOf("") }
    var fSunrise by remember { mutableStateOf("") }
    var fDhuhr by remember { mutableStateOf("") }
    var fAsr by remember { mutableStateOf("") }
    var fMaghrib by remember { mutableStateOf("") }
    var fIsha by remember { mutableStateOf("") }
    var manualMsg by remember { mutableStateOf<String?>(null) }

    // Load the current manual override once.
    androidx.compose.runtime.LaunchedEffect(Unit) {
        prefs.manualPrayerJson.firstValue()?.let { dayPrayersFromJson(it) }?.let {
            manual = it
            fFajr = it.fajr; fSunrise = it.sunrise; fDhuhr = it.dhuhr
            fAsr = it.asr; fMaghrib = it.maghrib; fIsha = it.isha
        }
    }

    /** Clear the cache and reload timings with the saved place. */
    suspend fun repair() {
        if (!isNetworkAvailable(context)) {
            repairStatus = Strings.repairFailed
            return
        }
        repairing = true
        repairStatus = Strings.repairing
        try {
            DiagLog.d("prayer-settings", "repair: clearing cache")
            prefs.clearPrayerCache()
            val today = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date())
            val method = APP_LANGS.find { it.code == Strings.langCode }?.prayerMethod ?: 5
            val lat = prefs.prayerLat.firstValue()
            val lng = prefs.prayerLng.firstValue()
            val fresh = if (lat != null && lng != null) {
                DiagLog.d("prayer-settings", "repair: fetch by coords")
                PrayerApi.fetchTimings(lat, lng, today, method)
            } else {
                val city = prefs.prayerCity.firstValue()
                val country = prefs.prayerCountry.firstValue()
                if (city != null && country != null) {
                    DiagLog.d("prayer-settings", "repair: fetch by legacy city")
                    PrayerApi.fetchTimingsByCity(city, country, today, method)
                } else null
            }
            if (fresh != null) {
                prefs.cachePrayerTimes(today, fresh.toJsonString())
                DiagLog.d("prayer-settings", "repair: ok")
                repairStatus = Strings.repairDone
            } else {
                DiagLog.d("prayer-settings", "repair: api returned null")
                repairStatus = Strings.repairFailed
            }
        } finally {
            repairing = false
        }
    }

    /** Debounced online city search. */
    fun onCityQueryChanged(q: String) {
        cityQuery = q
        cityMsg = null
        searchJob?.cancel()
        if (q.trim().length < 2) {
            cityResults = emptyList()
            searching = false
            return
        }
        searching = true
        searchJob = scope.launch {
            delay(450)
            cityResults = GeoApi.searchCities(q)
            searching = false
        }
    }

    suspend fun selectCity(city: GeoCity) {
        val label = if (city.country.isNotBlank()) "${city.name}، ${city.country}"
        else city.name
        DiagLog.d("prayer-settings", "city selected: ${city.name} lat=${city.lat}")
        prefs.savePrayerPlace(city.lat, city.lng, label)
        cityMsg = Strings.citySaved
        cityQuery = ""
        cityResults = emptyList()
    }

    fun validHhmm(v: String): Boolean {
        val p = v.split(":")
        if (p.size != 2) return false
        val h = p[0].toIntOrNull() ?: return false
        val m = p[1].toIntOrNull() ?: return false
        return h in 0..23 && m in 0..59 && p[0].length == 2 && p[1].length == 2
    }

    fun saveManual() {
        val values = listOf(fFajr, fSunrise, fDhuhr, fAsr, fMaghrib, fIsha)
        if (values.any { !validHhmm(it) }) {
            manualMsg = Strings.invalidTime
            return
        }
        val dp = DayPrayers(
            date = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date()),
            fajr = fFajr, sunrise = fSunrise, dhuhr = fDhuhr,
            asr = fAsr, maghrib = fMaghrib, isha = fIsha
        )
        scope.launch {
            prefs.saveManualPrayerTimes(dp.toJsonString())
            manual = dp
            manualMsg = Strings.manualSaved
        }
    }

    fun clearManual() {
        scope.launch {
            prefs.clearManualPrayerTimes()
            manual = null
            fFajr = ""; fSunrise = ""; fDhuhr = ""
            fAsr = ""; fMaghrib = ""; fIsha = ""
            manualMsg = Strings.manualCleared
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = GreenPrimary)
            }
            Text(
                Strings.prayerSettings,
                fontFamily = Tajwal,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ---- Repair ----
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        Strings.repairPrayerTimes,
                        fontFamily = Tajwal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        Strings.repairPrayerTimesDesc,
                        fontFamily = Tajwal,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { scope.launch { repair() } },
                        enabled = !repairing,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (repairing) {
                            CircularProgressIndicator(
                                color = androidx.compose.ui.graphics.Color.White,
                                modifier = Modifier
                                    .height(20.dp)
                                    .width(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(
                            if (repairing) Strings.repairing else Strings.repair,
                            fontFamily = Tajwal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    repairStatus?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, fontFamily = Tajwal, fontSize = 13.sp, color = GreenPrimary)
                    }
                }
            }

            // ---- Change city (online search, every city in the world) ----
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        Strings.changeCityTitle,
                        fontFamily = Tajwal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cityQuery,
                        onValueChange = ::onCityQueryChanged,
                        placeholder = { Text(Strings.searchCityHint, fontFamily = Tajwal) },
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (searching) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            Strings.searchingCities,
                            fontFamily = Tajwal,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    if (cityResults.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        LazyColumn(modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)) {
                            items(cityResults) { city ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { scope.launch { selectCity(city) } }
                                        .padding(vertical = 8.dp, horizontal = 4.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            city.name,
                                            fontFamily = Tajwal,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        val sub = listOf(city.admin1, city.country)
                                            .filter { it.isNotBlank() }
                                            .joinToString("، ")
                                        if (sub.isNotBlank()) {
                                            Text(
                                                sub,
                                                fontFamily = Tajwal,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else if (!searching && cityQuery.trim().length >= 2) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            Strings.noCityResults,
                            fontFamily = Tajwal,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    cityMsg?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, fontFamily = Tajwal, fontSize = 13.sp, color = GreenPrimary)
                    }
                }
            }

            // ---- Manual times ----
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        Strings.manualTimes,
                        fontFamily = Tajwal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        Strings.manualTimesDesc,
                        fontFamily = Tajwal,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.height(12.dp))
                    val fields = listOf(
                        Strings.fajr to { v: String -> fFajr = v } to fFajr,
                        Strings.shuruj to { v: String -> fSunrise = v } to fSunrise,
                        Strings.dhuhr to { v: String -> fDhuhr = v } to fDhuhr,
                        Strings.asr to { v: String -> fAsr = v } to fAsr,
                        Strings.maghrib to { v: String -> fMaghrib = v } to fMaghrib,
                        Strings.isha to { v: String -> fIsha = v } to fIsha
                    )
                    fields.forEach { (labelToSetter, value) ->
                        val (label, setter) = labelToSetter
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                label,
                                fontFamily = Tajwal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = value,
                                onValueChange = setter,
                                placeholder = { Text("04:30", fontFamily = Tajwal) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.width(120.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = ::saveManual,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(Strings.saveManualTimes, fontFamily = Tajwal, fontWeight = FontWeight.Bold)
                    }
                    if (manual != null) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = ::clearManual,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(Strings.clearManualTimes, fontFamily = Tajwal)
                        }
                    }
                    manualMsg?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, fontFamily = Tajwal, fontSize = 13.sp, color = GreenPrimary)
                    }
                }
            }
        }
    }
}
