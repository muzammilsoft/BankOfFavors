package com.kgsoft.favorsbank.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kgsoft.favorsbank.data.FavorsRepository
import com.kgsoft.favorsbank.ui.theme.EmeraldGreen
import com.kgsoft.favorsbank.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SibhaScreen(
    repository: FavorsRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val zikrPresets = remember {
        listOf(
            "سبحان الله",
            "الحمد لله",
            "لا إله إلا الله",
            "الله أكبر",
            "أستغفر الله واتوب إليه",
            "اللهم صلِّ وسلم على نبينا محمد"
        )
    }

    var selectedZikr by remember { mutableStateOf(zikrPresets[0]) }
    var count by remember { mutableStateOf(0) }
    var targetCount by remember { mutableStateOf(33) }
    var totalSessionCount by remember { mutableStateOf(0) }

    fun vibrate() {
        if (!repository.isVibrationEnabled()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(40)
            }
        } catch (_: Exception) {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("السبحة الإلكترونية 📿", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        count = 0
                        totalSessionCount = 0
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "تصفير")
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Preset Zikr Selector dropdown / chips
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "اختر الذكر المبارك:",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                ScrollableTabRow(
                    selectedTabIndex = zikrPresets.indexOf(selectedZikr),
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent
                ) {
                    zikrPresets.forEach { preset ->
                        Tab(
                            selected = selectedZikr == preset,
                            onClick = {
                                selectedZikr = preset
                                count = 0
                            },
                            text = { Text(preset, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Target Selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("الهدف:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    listOf(33, 100, 1000).forEach { target ->
                        FilterChip(
                            selected = targetCount == target,
                            onClick = { targetCount = target },
                            label = { Text("$target") }
                        )
                    }
                }
            }

            // Big Counter Display & Circle Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = selectedZikr,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = CircleShape,
                    color = EmeraldGreen,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .size(200.dp)
                        .clickable {
                            vibrate()
                            count++
                            totalSessionCount++
                            repository.addTasbeehCount(1)

                            if (count >= targetCount) {
                                vibrate()
                                count = 0
                            }
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$count",
                                fontSize = 56.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldAccent
                            )
                            Text(
                                text = "من $targetCount",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Icon(
                                Icons.Default.TouchApp,
                                contentDescription = "انقر للتسبيح",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // Total Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("مجموع الجلسة", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$totalSessionCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                    }
                    VerticalDivider(
                        modifier = Modifier
                            .height(30.dp)
                            .width(1.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("إجمالي التسبيحات", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${repository.getUserProfile().tasbeehTotalCount}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                    }
                }
            }
        }
    }
}
