package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kgsoft.favorsbank.data.FavorsRepository
import com.kgsoft.favorsbank.model.PrayerTime
import com.kgsoft.favorsbank.ui.theme.EmeraldGreen
import com.kgsoft.favorsbank.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerTimesScreen(
    repository: FavorsRepository,
    onBack: () -> Unit
) {
    val prayerTimes = remember {
        listOf(
            PrayerTime("الفجر", "Fajr", "04:12 AM", isNext = false),
            PrayerTime("الشروق", "Sunrise", "05:40 AM", isNext = false),
            PrayerTime("الظهر", "Dhuhr", "12:15 PM", isNext = true),
            PrayerTime("العصر", "Asr", "03:45 PM", isNext = false),
            PrayerTime("المغرب", "Maghrib", "06:50 PM", isNext = false),
            PrayerTime("العشاء", "Isha", "08:20 PM", isNext = false)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("أوقات الصلاة والقبلة 🕌", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Next Prayer Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldGreen)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "الصلاة القادمة: صلاة الظهر",
                        color = GoldAccent,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "12:15 PM",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "متبقي حوالي ساعتين و 15 دقيقة",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }
            }

            // Qibla Indicator Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Explore,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("اتجاه القبلة 🕋", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("مكة المكرمة (142° SE)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "مستقر",
                            color = EmeraldGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Text(
                text = "مواقيت صلاة اليوم:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(prayerTimes) { prayer ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (prayer.isNext) EmeraldGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = prayer.nameAr,
                                fontSize = 16.sp,
                                fontWeight = if (prayer.isNext) FontWeight.Bold else FontWeight.Medium
                            )
                            Text(
                                text = prayer.time,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (prayer.isNext) EmeraldGreen else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
