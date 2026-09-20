package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.R
import com.kgsoft.favorsbank.data.AYAT
import com.kgsoft.favorsbank.data.HOME_ZIKR
import com.kgsoft.favorsbank.data.HOME_ZIKR_I18N
import com.kgsoft.favorsbank.data.VERSE_I18N
import com.kgsoft.favorsbank.data.PrefsRepository
import com.kgsoft.favorsbank.data.RECOMMENDATION_JOBS
import com.kgsoft.favorsbank.data.firstValue
import com.kgsoft.favorsbank.ui.Routes
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GrainShape
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.QuranFont
import com.kgsoft.favorsbank.ui.theme.Tajwal
import com.kgsoft.favorsbank.util.Notifier
import com.kgsoft.favorsbank.util.shareOwnApk
import com.kgsoft.favorsbank.util.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class ToolCard(
    val title: String,
    val icon: Int,
    val onClick: () -> Unit
)

/**
 * Home screen: balance header, rotating Quran verse, rotating zikr reminder,
 * wide tool buttons and the navigation drawer. Mirrors MainActivity.
 *
 * Tool buttons are full-width horizontal rows (text at the start, icon at the
 * end) using the "grain ear" shape — compact, leaving room for future additions.
 */
@Composable
fun HomeScreen(navController: NavController, prefs: PrefsRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val hasanat by prefs.hasanat.collectAsState(initial = 0L)
    val username by prefs.username.collectAsState(initial = null)
    val notificationsOn by prefs.notificationsEnabled.collectAsState(initial = false)

    var verseIndex by remember { mutableStateOf(AYAT.indices.random()) }
    var zikrIndex by remember { mutableStateOf(HOME_ZIKR.indices.random()) }
    val verse = AYAT[verseIndex]
    val zikr = HOME_ZIKR[zikrIndex]

    // Launch counter (original showed a debug toast on the 3rd launch).
    LaunchedEffect(Unit) {
        val count = prefs.incrementFirstRun()
        if (count == 3) context.toast(count.toString())
    }

    // Quran verse rotator: first after 2s, then every 3 minutes.
    LaunchedEffect(Unit) {
        delay(2000)
        while (true) {
            verseIndex = AYAT.indices.random()
            delay(3 * 60 * 1000L)
        }
    }
    // Zikr rotator: first after 10s, then every 3 minutes.
    LaunchedEffect(Unit) {
        delay(10_000)
        while (true) {
            zikrIndex = HOME_ZIKR.indices.random()
            delay(3 * 60 * 1000L)
        }
    }

    // Recommendation notification 2 minutes after start (original behavior;
    // its "repeat" interval was effectively infinite).
    LaunchedEffect(notificationsOn) {
        if (notificationsOn) {
            delay(2 * 60 * 1000L)
            Notifier.showRecommendation(context, RECOMMENDATION_JOBS.random())
        }
    }

    val cards = listOf(
        ToolCard(Strings.salatTimes, R.drawable.ic_salat) { navController.navigate(Routes.SALAT) },
        ToolCard(Strings.azkar, R.drawable.ic_azkar) { navController.navigate(Routes.AZKAR) },
        ToolCard(Strings.sibha, R.drawable.ic_sibha) { navController.navigate(Routes.sibha()) },
        ToolCard(Strings.tasks, R.drawable.ic_tasks) { navController.navigate(Routes.todo(0)) },
        ToolCard(Strings.business, R.drawable.ic_business) { navController.navigate(Routes.todo(1)) },
        ToolCard(Strings.dailyReview, R.drawable.ic_review) { navController.navigate(Routes.REVIEW) },
        ToolCard(Strings.myAccount, R.drawable.ic_account) { navController.navigate(Routes.ACCOUNT) },
        ToolCard(Strings.share, R.drawable.ic_share) { context.shareOwnApk() }
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    username = username ?: "عبدالله",
                    onProfile = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.ACCOUNT)
                    },
                    onSettings = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.SETTINGS)
                    },
                    onAbout = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.ABOUT)
                    },
                    onLanguage = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.LANGUAGE)
                    },
                    onExit = { (context as? android.app.Activity)?.finishAffinity() }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(Icons.Default.Menu, contentDescription = null, tint = GreenPrimary)
                }
                Text(
                    Strings.appName,
                    fontFamily = Tajwal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(Modifier.height(8.dp))

            // Hasanat balance header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, GreenPrimary, RoundedCornerShape(40.dp))
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (hasanat > 0) "$hasanat ${Strings.hasanatSuffix}" else Strings.appName,
                    fontFamily = Tajwal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = GreenPrimary
                )
            }

            Spacer(Modifier.height(10.dp))

            // Rotating Quran verse (showcase): large text for readability,
            // plus pronunciation + official translation when available.
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val sacredVerse = VERSE_I18N.getOrNull(verseIndex)
                    val vlang = Strings.langCode
                    Text(
                        verse,
                        fontFamily = QuranFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 34.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (sacredVerse != null) {
                        val vpron = sacredVerse.translit(vlang)
                        if (vpron.isNotBlank()) {
                            Text(
                                vpron,
                                fontStyle = FontStyle.Italic,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (vlang != "ar") {
                            Text(
                                sacredVerse.meaning(vlang),
                                fontSize = 15.sp,
                                lineHeight = 26.sp,
                                textAlign = TextAlign.Center,
                                color = GreenPrimary,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // Rotating zikr reminder (footer): trilingual when available.
            val sacredZikr = HOME_ZIKR_I18N.getOrNull(zikrIndex)
            val zlang = Strings.langCode
            if (sacredZikr != null && zlang != "ar") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        zikr.trim(),
                        fontFamily = Tajwal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = GreenPrimary
                    )
                    val zpron = sacredZikr.translit(zlang)
                    if (zpron.isNotBlank()) {
                        Text(
                            zpron,
                            fontStyle = FontStyle.Italic,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        sacredZikr.meaning(zlang),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        color = GreenPrimary.copy(alpha = 0.85f)
                    )
                }
            } else {
                Text(
                    zikr,
                    fontFamily = Tajwal,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = GreenPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(10.dp))

            // Tool buttons: full-width horizontal rows — text at the start side,
            // icon at the end side (right/left in RTL). Compact on purpose:
            // the freed space is reserved for future updates.
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                cards.forEach { card ->
                    Card(
                        shape = GrainShape,
                        colors = CardDefaults.cardColors(containerColor = GreenPrimary),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { card.onClick() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 14.dp)
                        ) {
                            Text(
                                card.title,
                                fontFamily = Tajwal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                painter = painterResource(card.icon),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerContent(
    username: String,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    onLanguage: () -> Unit,
    onExit: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onProfile() }
                .padding(vertical = 8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.default_profile_pic),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Text(username, fontFamily = Tajwal, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(Modifier.height(8.dp))
        DrawerItem(Strings.settings, R.drawable.ic_settings, onSettings)
        DrawerItem(Strings.about, R.drawable.about, onAbout)
        DrawerItem(Strings.language, R.drawable.icon_language_round, onLanguage)
        DrawerItem(Strings.exit, R.drawable.exit, onExit)
    }
}

@Composable
private fun DrawerItem(title: String, icon: Int, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(title, fontFamily = Tajwal, fontSize = 16.sp)
    }
}
