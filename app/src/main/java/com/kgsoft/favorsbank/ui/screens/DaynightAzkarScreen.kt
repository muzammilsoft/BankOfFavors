package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.data.AzkarItem
import com.kgsoft.favorsbank.data.CORE_INDEX
import com.kgsoft.favorsbank.data.DAY_AZKAR
import com.kgsoft.favorsbank.data.NIGHT_AZKAR
import com.kgsoft.favorsbank.ui.components.TrilingualContent
import com.kgsoft.favorsbank.ui.theme.GreenBright
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.Tajwal

/**
 * Morning / evening azkar with per-item repetition counters.
 * Mirrors DaynightAzkarActivity + DayAzkarFragmentActivity + NightAzkarFragmentActivity.
 */
@Composable
fun DaynightAzkarScreen(navController: NavController) {
    var tab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = GreenPrimary)
            }
            Text(
                "أذكار الصباح والمساء",
                fontFamily = Tajwal,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        TabRow(selectedTabIndex = tab, containerColor = MaterialTheme.colorScheme.background) {
            Tab(
                selected = tab == 0,
                onClick = { tab = 0 },
                text = { Text("أذكار الصباح", fontFamily = Tajwal) }
            )
            Tab(
                selected = tab == 1,
                onClick = { tab = 1 },
                text = { Text("أذكار المساء", fontFamily = Tajwal) }
            )
        }
        if (tab == 0) AzkarList(DAY_AZKAR) else AzkarList(NIGHT_AZKAR)
    }
}

@Composable
private fun AzkarList(items: List<AzkarItem>) {
    // Session counters, one per item (the original did not persist them either).
    val counters = remember(items) { mutableStateListOf(*IntArray(items.size) { 0 }.toTypedArray()) }
    LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        itemsIndexed(items) { index, item ->
            val done = counters[index]
            val remaining = (item.count - done).coerceAtLeast(0)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // If this zikr has trilingual data, show the three-line
                        // display (original + transliteration + meaning);
                        // otherwise fall back to the Arabic text as before.
                        val firstSeg = item.text.substringBefore("\n\n").trim()
                        val core = CORE_INDEX[firstSeg]
                        if (core != null) {
                            val virtue = item.text.substringAfter("\n\n", "")
                                .trim().takeIf { it.isNotEmpty() }
                            TrilingualContent(core.copy(virtueAr = virtue ?: core.virtueAr))
                        } else {
                            Text(item.text, fontFamily = Tajwal, fontSize = 15.sp, lineHeight = 26.sp)
                        }
                        Spacer(modifier = Modifier.padding(2.dp))
                        LinearProgressIndicator(
                            progress = { (done.toFloat() / item.count).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(50)),
                            color = GreenBright,
                            trackColor = GreenBright.copy(alpha = 0.2f)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    // Counter badge: tap to count one repetition (original tapped the badge).
                    androidx.compose.foundation.layout.Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .background(GreenBright.copy(alpha = 0.15f), RoundedCornerShape(50))
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                if (counters[index] < item.count) counters[index] = counters[index] + 1
                            }
                    ) {
                        Text(
                            remaining.toString(),
                            fontFamily = Tajwal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = GreenBright
                        )
                    }
                }
            }
        }
    }
}
