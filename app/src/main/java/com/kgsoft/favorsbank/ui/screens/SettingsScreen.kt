package com.kgsoft.favorsbank.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.kgsoft.favorsbank.R
import com.kgsoft.favorsbank.data.PrefsRepository
import com.kgsoft.favorsbank.data.TOAST_ZIKR
import com.kgsoft.favorsbank.ui.Routes
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.Tajwal
import com.kgsoft.favorsbank.util.Notifier
import com.kgsoft.favorsbank.util.toast
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Settings: notifications, night theme, periodic zikr toasts and bug reports.
 * Mirrors SettingsActivity. The timers run while the app process is alive,
 * like the original java.util.Timer usage.
 */
@Composable
fun SettingsScreen(navController: NavController, prefs: PrefsRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val notificationsOn by prefs.notificationsEnabled.collectAsState(initial = false)
    val darkTheme by prefs.isDarkTheme.collectAsState(initial = false)
    val toastsOn by prefs.toastsEnabled.collectAsState(initial = false)
    val completedLog by prefs.completedLog.collectAsState(initial = emptyList())

    var toastTasbih by remember { mutableStateOf(true) }
    var toastProphet by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    // Periodic zikr toasts while enabled (original: every 3 minutes).
    var toastJob by remember { mutableStateOf<Job?>(null) }
    LaunchedEffect(toastsOn) {
        toastJob?.cancel()
        if (toastsOn) {
            toastJob = scope.launch {
                delay(2000)
                while (true) {
                    if (toastTasbih || toastProphet) {
                        context.toast(TOAST_ZIKR.random(), long = true)
                    }
                    delay(3 * 60 * 1000L)
                }
            }
        }
    }

    // One-shot "hurry to good deeds" reminder shortly after enabling.
    LaunchedEffect(notificationsOn) {
        if (notificationsOn) {
            if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            delay(1000)
            Notifier.showReminder(context)
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
            Text(Strings.settings, fontFamily = Tajwal, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SettingRow(
                title = Strings.notifications,
                subtitle = if (notificationsOn) Strings.enabled else Strings.disabled,
                checked = notificationsOn,
                onChecked = { scope.launch { prefs.setNotifications(it) } }
            )
            Spacer(Modifier.height(12.dp))
            SettingRow(
                title = Strings.nightTheme,
                subtitle = if (darkTheme) Strings.enabled else Strings.disabled,
                checked = darkTheme,
                onChecked = { scope.launch { prefs.setDarkTheme(it) } }
            )
            Spacer(Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { scope.launch { prefs.setToasts(!toastsOn) } }
                    ) {
                        Text(
                            Strings.zikrToasts,
                            fontFamily = Tajwal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = toastsOn,
                            onCheckedChange = { scope.launch { prefs.setToasts(it) } },
                            colors = SwitchDefaults.colors(checkedThumbColor = GreenPrimary)
                        )
                    }
                    if (toastsOn) {
                        Spacer(Modifier.height(4.dp))
                        ToastCheck(Strings.toastTasbih, toastTasbih) { toastTasbih = it }
                        ToastCheck(Strings.toastProphet, toastProphet) { toastProphet = it }
                        ToastCheck(
                            Strings.toastAll,
                            toastTasbih && toastProphet
                        ) {
                            toastTasbih = it
                            toastProphet = it
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Routes.DIAGLOG) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        Icons.Filled.BugReport,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            Strings.diagLogTitle,
                            fontFamily = Tajwal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            Strings.diagLogSubtitle,
                            fontFamily = Tajwal,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Routes.PRAYER_SETTINGS) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            Strings.prayerSettings,
                            fontFamily = Tajwal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            Strings.repairPrayerTimesDesc,
                            fontFamily = Tajwal,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Routes.REPORT) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Image(
                        painterResource(R.drawable.bug_report_black),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        Strings.reportBug,
                        fontFamily = Tajwal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Completed tasks log with dates.
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        Strings.completedLog,
                        fontFamily = Tajwal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = GreenPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    if (completedLog.isEmpty()) {
                        Text(
                            Strings.noCompletedTasks,
                            fontFamily = Tajwal,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            completedLog.forEach { entry ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            entry.title,
                                            fontFamily = Tajwal,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            maxLines = 2
                                        )
                                        Text(
                                            entry.date,
                                            fontFamily = Tajwal,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "+${entry.hasanat}",
                                        fontFamily = Tajwal,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = GreenPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onChecked(!checked) }
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontFamily = Tajwal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    subtitle,
                    fontFamily = Tajwal,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onChecked,
                colors = SwitchDefaults.colors(checkedThumbColor = GreenPrimary)
            )
        }
    }
}

@Composable
private fun ToastCheck(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChecked(!checked) }
    ) {
        Checkbox(checked = checked, onCheckedChange = onChecked)
        Text(label, fontFamily = Tajwal, fontSize = 14.sp)
    }
}
