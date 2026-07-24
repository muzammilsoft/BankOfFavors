package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kgsoft.favorsbank.data.FavorsRepository
import com.kgsoft.favorsbank.ui.theme.EmeraldGreen
import com.kgsoft.favorsbank.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAccountScreen(
    repository: FavorsRepository,
    onBack: () -> Unit
) {
    val profile = remember { repository.getUserProfile() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("حسابي والإحصائيات 👤", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header
            Surface(
                shape = CircleShape,
                color = EmeraldGreen,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = profile.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = GoldAccent.copy(alpha = 0.2f),
                modifier = Modifier.padding(top = 6.dp)
            ) {
                Text(
                    text = profile.rankTitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stat Cards Grid
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("سجل الإنجازات المباركة", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem("رصيد الحسنات", "${profile.totalHasanat}", EmeraldGreen)
                        StatItem("الأذكار المكتملة", "${profile.azkarCompletedCount}", GoldAccent)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem("الأعمال المنجزة", "${profile.tasksCompletedCount}", Color(0xFF1565C0))
                        StatItem("التسبيحات الكلية", "${profile.tasbeehTotalCount}", Color(0xFF8E24AA))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Badges Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("أوسمة الإيمان 🏅", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    BadgeRow("وسام الأذكار", "قمت بختم الأذكار عدة مرات", profile.azkarCompletedCount > 0)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    BadgeRow("وسام التسبيح", "سجلت أكثر من 100 تسبيحة", profile.tasbeehTotalCount >= 100)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    BadgeRow("وسام سابق بالخيرات", "جمعت أكثر من 1000 حسنة", profile.totalHasanat >= 1000)
                }
            }
        }
    }
}

@Composable
fun StatItem(title: String, value: String, color: Color) {
    Column(
        modifier = Modifier.width(140.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun BadgeRow(title: String, desc: String, isUnlocked: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = if (isUnlocked) GoldAccent else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(
            text = if (isUnlocked) "مفتوح ✅" else "مغلق 🔒",
            fontSize = 12.sp,
            color = if (isUnlocked) EmeraldGreen else Color.Gray,
            fontWeight = FontWeight.Bold
        )
    }
}
