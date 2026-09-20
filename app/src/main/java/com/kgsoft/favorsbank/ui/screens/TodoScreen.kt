package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.R
import com.kgsoft.favorsbank.data.AssetsRepo
import com.kgsoft.favorsbank.data.Job
import com.kgsoft.favorsbank.data.Mission
import com.kgsoft.favorsbank.data.PrefsRepository
import com.kgsoft.favorsbank.ui.DetailsPayload
import com.kgsoft.favorsbank.ui.DetailsStore
import com.kgsoft.favorsbank.ui.Routes
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.Tajwal
import com.kgsoft.favorsbank.util.copyToClipboard

/**
 * Tasks hub with two tabs: المهام (missions.json, language-aware) and
 * الأعمال (jobs.json). Mirrors TodoActivity + TasksFragmentActivity +
 * BusinessFragmentActivity.
 */
@Composable
fun TodoScreen(navController: NavController, prefs: PrefsRepository, initialTab: Int = 0) {
    val context = LocalContext.current
    val language by prefs.language.collectAsState(initial = "")
    var tab by remember { mutableStateOf(initialTab.coerceIn(0, 1)) }

    val missions = remember(language) {
        runCatching {
            AssetsRepo.loadMissions(context, english = language.contains("english"))
        }.getOrDefault(emptyList())
    }
    val jobs = remember {
        runCatching { AssetsRepo.loadJobs(context) }.getOrDefault(emptyList())
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
                if (tab == 0) Strings.tasks else Strings.business,
                fontFamily = Tajwal,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        TabRow(selectedTabIndex = tab, containerColor = MaterialTheme.colorScheme.background) {
            Tab(
                selected = tab == 0,
                onClick = { tab = 0 },
                text = { Text(Strings.tasks, fontFamily = Tajwal) },
                icon = {
                    Icon(
                        imageVector = vectorResource(R.drawable.ic_tasks),
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            )
            Tab(
                selected = tab == 1,
                onClick = { tab = 1 },
                text = { Text(Strings.business, fontFamily = Tajwal) },
                icon = {
                    Icon(
                        imageVector = vectorResource(R.drawable.ic_business),
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            )
        }
        if (tab == 0) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                items(missions) { m ->
                    MissionCard(
                        title = m.title.ifBlank { m.mission },
                        time = m.time,
                        onClick = {
                            DetailsStore.current = DetailsPayload(
                                title = m.title.ifBlank { m.mission },
                                time = m.time,
                                details = m.details,
                                earnings = m.earnings,
                                mission = m.mission,
                                isJob = false,
                                job = m.mission
                            )
                            navController.navigate(Routes.DETAILS)
                        },
                        onLongClick = {
                            // Replaces the buggy Sketchware long-click with a sane copy.
                            context.copyToClipboard("mission", "${m.mission}\n${m.details}")
                        }
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                Text(
                    Strings.suggestedBusiness,
                    fontFamily = Tajwal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = GreenPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(jobs) { j ->
                        MissionCard(
                            title = j.mission,
                            time = j.time,
                            onClick = {
                                DetailsStore.current = DetailsPayload(
                                    title = j.mission,
                                    time = j.time,
                                    details = j.details,
                                    earnings = j.earnings,
                                    mission = j.mission,
                                    isJob = true,
                                    job = j.mission
                                )
                                navController.navigate(Routes.DETAILS)
                            },
                            onLongClick = {
                                context.copyToClipboard("job", "${j.mission}\n${j.details}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MissionCard(
    title: String,
    time: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(3.dp, GreenPrimary, RoundedCornerShape(30.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontFamily = Tajwal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (time.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        time,
                        fontFamily = Tajwal,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            // Long-press copies the item; expose it via a small copy affordance too.
            IconButton(onClick = onLongClick) {
                Icon(
                    painterResource(R.drawable.ic_content_copy_black),
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
