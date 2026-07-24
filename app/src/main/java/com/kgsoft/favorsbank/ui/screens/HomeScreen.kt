package com.kgsoft.favorsbank.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kgsoft.favorsbank.data.FavorsRepository
import com.kgsoft.favorsbank.ui.theme.EmeraldGreen
import com.kgsoft.favorsbank.ui.theme.GoldAccent
import com.kgsoft.favorsbank.ui.theme.LightEmerald

data class QuickFeature(
    val titleAr: String,
    val titleEn: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: FavorsRepository,
    onNavigate: (String) -> Unit
) {
    var totalHasanat by remember { mutableStateOf(repository.getTotalHasanat()) }
    val userProfile = remember(totalHasanat) { repository.getUserProfile() }

    val dailyGoal = 1000L
    val progress = (totalHasanat.toFloat() / dailyGoal).coerceIn(0f, 1f)

    val randomZikrList = remember {
        listOf(
            "«مَنْ قَالَ: سُبْحَانَ اللَّهِ وَبِحَمْدِهِ فِي يَوْمٍ مِائَةَ مَرَّةٍ حُطَّتْ خَطَايَاهُ وَإِنْ كَانَتْ مِثْلَ زَبَدِ الْبَحْرِ»",
            "«كلمتان خفيفتان على اللسان ثقيلتان في الميزان حبيبتان إلى الرحمن: سبحان الله وبحمده سبحان الله العظيم»",
            "«أحب الكلام إلى الله أربع: سبحان الله، والحمد لله، ولا إله إلا الله، والله أكبر»",
            "«ألا أدلك على كنز من كنوز الجنة؟ لا حول ولا قوة إلا بالله»"
        )
    }
    val todayZikr = remember { randomZikrList.random() }

    val features = listOf(
        QuickFeature("أذكار الصباح والمساء", "Azkar", Icons.Default.WbSunny, "azkar", LightEmerald),
        QuickFeature("السبحة الإلكترونية", "Sibha", Icons.Default.TouchApp, "sibha", GoldAccent),
        QuickFeature("قائمة الأعمال الصالحة", "Good Deeds", Icons.Default.CheckCircle, "tasks", Color(0xFF2E7D32)),
        QuickFeature("المحاسبة اليومية", "Daily Review", Icons.Default.Assessment, "daily_review", Color(0xFF1565C0)),
        QuickFeature("أوقات الصلاة", "Prayer Times", Icons.Default.AccessTime, "prayer_times", Color(0xFF8E24AA)),
        QuickFeature("حسابي والإحصائيات", "My Account", Icons.Default.Person, "account", Color(0xFFD81B60))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- Header Card (Hasanat Score) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                EmeraldGreen,
                                LightEmerald
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "بنك الحسنات 🕋",
                                color = GoldAccent,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = userProfile.rankTitle,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = GoldAccent.copy(alpha = 0.2f),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "رصيد الحسنات الأسبوعي / الكلي:",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "$totalHasanat",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "حسنة",
                            color = GoldAccent,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress to Daily Goal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "الهدف اليومي (1000 حسنة)",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = GoldAccent,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                }
            }
        }

        // --- Zikr / Verse Card of the Day ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ذكر اليوم والدرر",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = todayZikr,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                )
            }
        }

        // --- Features Grid Section Title ---
        Text(
            text = "الأقسام الرئيسية",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Features 2-column Grid
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            features.chunked(2).forEach { rowFeatures ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowFeatures.forEach { feature ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(110.dp)
                                .clickable { onNavigate(feature.route) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = feature.color.copy(alpha = 0.15f),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = feature.icon,
                                            contentDescription = feature.titleAr,
                                            tint = feature.color,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = feature.titleAr,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
