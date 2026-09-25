package com.kgsoft.favorsbank.ui.screens

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.kgsoft.favorsbank.data.APP_LANGS
import com.kgsoft.favorsbank.data.DayPrayers
import com.kgsoft.favorsbank.data.DiagLog
import com.kgsoft.favorsbank.data.FALLBACK_LAT
import com.kgsoft.favorsbank.data.FALLBACK_LNG
import com.kgsoft.favorsbank.data.GeoApi
import com.kgsoft.favorsbank.data.GeoCity
import com.kgsoft.favorsbank.data.PrayerApi
import com.kgsoft.favorsbank.data.PrayerLocation
import com.kgsoft.favorsbank.data.PrefsRepository
import com.kgsoft.favorsbank.data.dayPrayersFromJson
import com.kgsoft.favorsbank.data.firstValue
import com.kgsoft.favorsbank.data.formatPrayerTime
import com.kgsoft.favorsbank.data.hhmmToMinutes
import com.kgsoft.favorsbank.data.isLocationEnabled
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
 * cached times are shown instantly and refreshed whenever the internet is
 * available. Every API request shows a waiting indicator, followed by a
 * success or error message under the city row.
 */

/** Status of the last prayer-times API request, shown under the city row. */
private enum class FetchStatus { IDLE, LOADING, SUCCESS, ERROR }
@Composable
fun SalatTimesScreen(navController: NavController, prefs: PrefsRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var prayers by remember { mutableStateOf<DayPrayers?>(null) }
    var askLocation by remember { mutableStateOf(false) }
    var askGps by remember { mutableStateOf(false) }
    var checkGpsOnResume by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var cityQuery by remember { mutableStateOf("") }
    var cityResults by remember { mutableStateOf<List<GeoCity>>(emptyList()) }
    var searchingCities by remember { mutableStateOf(false) }
    var citySearchJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    var savedCityLabel by remember { mutableStateOf<String?>(null) }
    var isManual by remember { mutableStateOf(false) }
    var fetchStatus by remember { mutableStateOf(FetchStatus.IDLE) }

    /** Fetch timings for explicit coordinates and update state/cache. */
    suspend fun fetchFor(lat: Double, lng: Double) {
        if (!isNetworkAvailable(context)) {
            DiagLog.d("prayer-ui", "fetchFor: no network, keeping cache")
            fetchStatus = FetchStatus.ERROR
            return
        }
        fetchStatus = FetchStatus.LOADING
        val today = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date())
        // Calculation method follows the UI language's region
        // (Egypt for Arabs, Karachi for Urdu/Bengali, Diyanet for Turkish, ...).
        val method = APP_LANGS.find { it.code == Strings.langCode }?.prayerMethod ?: 5
        val fresh = PrayerApi.fetchTimings(lat, lng, today, method)
        if (fresh != null) {
            prefs.cachePrayerTimes(today, fresh.toJsonString())
            DiagLog.d("prayer-ui", "fetchFor: ok, cached for $today")
            prayers = fresh
            fetchStatus = FetchStatus.SUCCESS
        } else {
            DiagLog.d("prayer-ui", "fetchFor: api returned null, keeping cache")
            fetchStatus = FetchStatus.ERROR
        }
    }

    /** Fetch timings for a city + country (no GPS) and update state/cache. */
    suspend fun fetchForCity(cityEn: String, countryEn: String) {
        if (!isNetworkAvailable(context)) {
            DiagLog.d("prayer-ui", "fetchForCity: no network, keeping cache")
            fetchStatus = FetchStatus.ERROR
            return
        }
        fetchStatus = FetchStatus.LOADING
        val today = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date())
        val method = APP_LANGS.find { it.code == Strings.langCode }?.prayerMethod ?: 5
        val fresh = PrayerApi.fetchTimingsByCity(cityEn, countryEn, today, method)
        if (fresh != null) {
            prefs.cachePrayerTimes(today, fresh.toJsonString())
            DiagLog.d("prayer-ui", "fetchForCity: ok, cached for $today")
            prayers = fresh
            fetchStatus = FetchStatus.SUCCESS
        } else {
            DiagLog.d("prayer-ui", "fetchForCity: api returned null, keeping cache")
            fetchStatus = FetchStatus.ERROR
        }
    }

    /** Silent refresh with the saved place: coordinates first, legacy city second. */
    suspend fun refresh() {
        val lat = prefs.prayerLat.firstValue()
        val lng = prefs.prayerLng.firstValue()
        if (lat != null && lng != null) {
            fetchFor(lat, lng)
            return
        }
        // Legacy v1.7.0 installs saved city + country instead of coordinates.
        val city = prefs.prayerCity.firstValue()
        val country = prefs.prayerCountry.firstValue()
        if (city != null && country != null) {
            fetchForCity(city, country)
        }
    }

    /** User picked a city from the online search: save coords + label, fetch times. */
    suspend fun selectCity(city: GeoCity) {
        DiagLog.d("prayer-ui", "city selected: ${city.name} lat=${city.lat}")
        val label = if (city.country.isNotBlank()) "${city.name}، ${city.country}"
        else city.name
        prefs.savePrayerPlace(city.lat, city.lng, label)
        savedCityLabel = label
        askLocation = false
        cityQuery = ""
        cityResults = emptyList()
        isLoading = true
        try {
            fetchFor(city.lat, city.lng)
        } finally {
            isLoading = false
        }
    }

    /** Debounced online city search (every city in the world, no stored list). */
    fun onCityQueryChanged(q: String) {
        cityQuery = q
        citySearchJob?.cancel()
        if (q.trim().length < 2) {
            cityResults = emptyList()
            searchingCities = false
            return
        }
        searchingCities = true
        citySearchJob = scope.launch {
            delay(450)
            cityResults = GeoApi.searchCities(q)
            searchingCities = false
        }
    }

    /** Save the Khartoum fallback and fetch its times with a loading indicator. */
    suspend fun useFallbackWithLoading() {
        DiagLog.d("prayer-ui", "using Khartoum fallback")
        prefs.clearPrayerCity()
        savedCityLabel = null
        prefs.savePrayerLocation(FALLBACK_LAT, FALLBACK_LNG)
        isLoading = true
        try {
            fetchFor(FALLBACK_LAT, FALLBACK_LNG)
        } finally {
            isLoading = false
        }
    }

    /**
     * Full "use my location" flow: fresh GPS fix (with shimmer on screen),
     * then API fetch. Falls back to Khartoum when no fix arrives.
     */
    suspend fun locateAndFetch() {
        DiagLog.d("prayer-ui", "locateAndFetch: start")
        isLoading = true
        try {
            val loc = PrayerLocation.fresh(context)
            val (lat, lng) = loc ?: (FALLBACK_LAT to FALLBACK_LNG)
            if (loc == null) DiagLog.d("prayer-ui", "locateAndFetch: no fix, fallback to Khartoum")
            prefs.clearPrayerCity()
            savedCityLabel = null
            prefs.savePrayerLocation(lat, lng)
            fetchFor(lat, lng)
        } finally {
            isLoading = false
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        DiagLog.d("prayer-ui", "location permission granted=$granted")
        scope.launch {
            if (!granted) {
                useFallbackWithLoading() // permission denied: Khartoum
            } else if (isLocationEnabled(context)) {
                locateAndFetch()
            } else {
                DiagLog.d("prayer-ui", "permission ok but location providers off")
                askGps = true // permission ok, but GPS is off: ask to enable it
            }
        }
    }

    // When the user returns from the system location settings, retry the flow.
    val activityLifecycle = (context as? ComponentActivity)?.lifecycle
    DisposableEffect(activityLifecycle) {
        if (activityLifecycle == null) return@DisposableEffect onDispose {}
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && checkGpsOnResume) {
                checkGpsOnResume = false
                scope.launch {
                    if (isLocationEnabled(context)) locateAndFetch()
                    else useFallbackWithLoading()
                }
            }
        }
        activityLifecycle.addObserver(observer)
        onDispose { activityLifecycle.removeObserver(observer) }
    }

    // Clock tick for the countdown.
    LaunchedEffect(Unit) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(1000)
        }
    }

    // Load manual override (if the user set times by hand), else cache,
    // then refresh silently when online; ask location once.
    LaunchedEffect(Unit) {
        val manualJson = prefs.manualPrayerJson.firstValue()
        val manualTimes = manualJson?.let { dayPrayersFromJson(it) }
        if (manualTimes != null) {
            DiagLog.d("prayer-ui", "manual override active")
            prayers = manualTimes
            isManual = true
            savedCityLabel = prefs.prayerCityLabel.firstValue()
            return@LaunchedEffect
        }
        val cachedJson = prefs.cachedPrayerJson.firstValue()
        if (cachedJson != null) {
            prayers = dayPrayersFromJson(cachedJson)
            DiagLog.d("prayer-ui", "cache hit, prayers=${prayers != null}")
        } else {
            DiagLog.d("prayer-ui", "cache miss")
        }
        savedCityLabel = prefs.prayerCityLabel.firstValue()
        val hasPlace = prefs.prayerLat.firstValue() != null ||
            prefs.prayerCity.firstValue() != null
        if (!hasPlace) {
            DiagLog.d("prayer-ui", "no saved location, asking user")
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

            // Current city (tap to change it). Manual override gets a badge.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        cityQuery = ""
                        cityResults = emptyList()
                        askLocation = true
                    }
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    (savedCityLabel ?: Strings.myCurrentLocation) +
                        if (isManual) " (${Strings.manualBadge})" else "",
                    fontFamily = Tajwal,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    Strings.changeCity,
                    fontFamily = Tajwal,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
            }

            Spacer(Modifier.height(8.dp))

            // API request status: waiting indicator while fetching, then a
            // success or error message.
            if (fetchStatus != FetchStatus.IDLE) {
                val statusText = when (fetchStatus) {
                    FetchStatus.LOADING -> Strings.fetchingPrayerTimes
                    FetchStatus.SUCCESS -> Strings.prayerTimesUpdated
                    FetchStatus.ERROR -> Strings.prayerTimesFailed
                    FetchStatus.IDLE -> ""
                }
                val statusColor = when (fetchStatus) {
                    FetchStatus.SUCCESS -> GreenPrimary
                    FetchStatus.ERROR -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                }
                Text(
                    statusText,
                    fontFamily = Tajwal,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = statusColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            if (isLoading && prayers == null) {
                // Shimmer placeholders while the location fix / API request runs.
                ShimmerPrayerList()
            } else {
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
    }

    // First-run location prompt: search a city, use GPS, or Khartoum fallback.
    if (askLocation) {
        AlertDialog(
            onDismissRequest = {
                askLocation = false
                scope.launch { useFallbackWithLoading() }
            },
            title = { Text(Strings.locationTitle, fontFamily = Tajwal, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = cityQuery,
                        onValueChange = ::onCityQueryChanged,
                        placeholder = { Text(Strings.searchCityHint, fontFamily = Tajwal) },
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    if (searchingCities) {
                        Text(
                            Strings.searchingCities,
                            fontFamily = Tajwal,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                    ) {
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
                        if (!searchingCities && cityResults.isEmpty() && cityQuery.trim().length >= 2) {
                            item {
                                Text(
                                    Strings.noCityResults,
                                    fontFamily = Tajwal,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            },
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
                    scope.launch { useFallbackWithLoading() }
                }) {
                    Text(Strings.useKhartoum, fontFamily = Tajwal)
                }
            }
        )
    }

    // GPS is off: ask the user to enable it, then continue on resume.
    if (askGps) {
        AlertDialog(
            onDismissRequest = {
                askGps = false
                scope.launch { useFallbackWithLoading() }
            },
            title = { Text(Strings.gpsTitle, fontFamily = Tajwal, fontWeight = FontWeight.Bold) },
            text = { Text(Strings.gpsMessage, fontFamily = Tajwal) },
            confirmButton = {
                TextButton(onClick = {
                    askGps = false
                    checkGpsOnResume = true
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }) {
                    Text(Strings.openSettings, fontFamily = Tajwal, color = GreenPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    askGps = false
                    scope.launch { useFallbackWithLoading() }
                }) {
                    Text(Strings.cancel, fontFamily = Tajwal)
                }
            }
        )
    }
}

/** Animated shimmer brush for loading placeholders (theme-aware). */
@Composable
private fun shimmerBrush(): Brush {
    val base = MaterialTheme.colorScheme.surfaceVariant
    val shimmerColors = listOf(
        base.copy(alpha = 0.9f),
        base.copy(alpha = 0.35f),
        base.copy(alpha = 0.9f)
    )
    val transition = rememberInfiniteTransition(label = "prayerShimmer")
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "prayerShimmerTranslate"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translate - 600f, 0f),
        end = Offset(translate, 0f)
    )
}

/** Shimmer placeholder rows shown while prayer times are loading. */
@Composable
private fun ShimmerPrayerList() {
    val brush = shimmerBrush()
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            Strings.loadingPrayerTimes,
            fontFamily = Tajwal,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        repeat(6) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(GrainShape)
                    .background(brush)
            )
        }
    }
}
