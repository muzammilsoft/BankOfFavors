package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.R
import com.kgsoft.favorsbank.data.AYAT
import com.kgsoft.favorsbank.data.HOME_ZIKR
import com.kgsoft.favorsbank.data.PrefsRepository
import com.kgsoft.favorsbank.data.RECOMMENDATION_JOBS
import com.kgsoft.favorsbank.data.firstValue
import com.kgsoft.favorsbank.ui.Routes
import com.kgsoft.favorsbank.ui.Strings
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
 * tool cards grid and the navigation drawer. Mirrors MainActivity.
 */
@Composable
fun HomeScreen(navController: NavController, prefs: PrefsRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val hasanat by prefs.hasanat.collectAsState(initial = 0L)
    val username by prefs.username.collectAsState(initial = null)
    val notificationsOn by prefs.notificationsEnabled.collectAsState(initial = false)

    var verse by remember { mutableStateOf(AYAT.random()) }
    var zikr by remember { mutableStateOf(HOME_ZIKR.random()) }

    // Launch counter (original showed a debug toast on the 3rd launch).
    LaunchedEffect(Unit) {
        val count = prefs.incrementFirstRun()
        if (count == 3) context.toast(count.toString())
    }

    // Quran verse rotator: first after 2s, then every 3 minutes.
    LaunchedEffect(Unit) {
        delay(2000)
        while (true) {
            verse = AYAT.random()
            delay(3 * 60 * 1000L)
        }
    }
    // Zikr rotator: first after 10s, then every 3 minutes.
    LaunchedEffect(Unit) {
        delay(10_000)
        while (true) {
            zikr = HOME_ZIKR.random()
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
        ToolCard(Strings.salatTimes, R.drawable.salat_times_button) { navController.navigate(Routes.SALAT) },
        ToolCard(Strings.azkar, R.drawable.daynight_zikr) { navController.navigate(Routes.AZKAR) },
        ToolCard(Strings.sibha, R.drawable.sibha_icon) { navController.navigate(Routes.sibha()) },
        ToolCard(Strings.tasks, R.drawable.tasks_icon) { navController.navigate(Routes.todo(0)) },
        ToolCard(Strings.business, R.drawable.business_icon) { navController.navigate(Routes.todo(1)) },
        ToolCard(Strings.dailyReview, R.drawable.dailyaccountency_icon) { navController.navigate(Routes.REVIEW) },
        ToolCard(Strings.myAccount, R.drawable.account_icon) { navController.navigate(Routes.ACCOUNT) },
        ToolCard(Strings.share, R.drawable.share_icon) { context.shareOwnApk() }
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
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (hasanat > 0) "$hasanat ${Strings.hasanatSuffix}" else Strings.appName,
                    fontFamily = Tajwal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = GreenPrimary
                )
            }

            Spacer(Modifier.height(12.dp))

            // Rotating Quran verse
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    verse,
                    fontFamily = QuranFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            // Rotating zikr reminder
            Text(
                zikr,
                fontFamily = Tajwal,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = GreenPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(cards) { card ->
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = GreenPrimary),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { card.onClick() }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(card.icon),
                                contentDescription = null,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                card.title,
                                fontFamily = Tajwal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
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
