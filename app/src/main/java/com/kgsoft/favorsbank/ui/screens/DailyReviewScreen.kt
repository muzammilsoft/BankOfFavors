package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kgsoft.favorsbank.data.FavorsRepository
import com.kgsoft.favorsbank.model.DailyReviewItem
import com.kgsoft.favorsbank.ui.theme.EmeraldGreen
import com.kgsoft.favorsbank.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyReviewScreen(
    repository: FavorsRepository,
    onBack: () -> Unit
) {
    var reviewItems by remember {
        mutableStateOf(
            listOf(
                DailyReviewItem("1", "صلاة الفجر في الجماعة / بوقتها", 20),
                DailyReviewItem("2", "صلاة الظهر في وقتها", 15),
                DailyReviewItem("3", "صلاة العصر في وقتها", 15),
                DailyReviewItem("4", "صلاة المغرب في وقتها", 15),
                DailyReviewItem("5", "صلاة العشاء في وقتها", 15),
                DailyReviewItem("6", "قراءة ورد القرآن اليومي", 20),
                DailyReviewItem("7", "أذكار الصباح والمساء كاملاً", 20),
                DailyReviewItem("8", "السنن الراتبة وصلاة الضحى / الوتر", 15),
                DailyReviewItem("9", "صدقة اليوم والبر بالإخوان والوالدين", 15)
            )
        )
    }

    val totalPointsEarned = reviewItems.filter { it.isDone }.sumOf { it.points }
    val maxPoints = reviewItems.sumOf { it.points }

    val grade = remember(totalPointsEarned) {
        val pct = (totalPointsEarned.toFloat() / maxPoints.toFloat()) * 100
        when {
            pct >= 90 -> "ممتاز جداً - سابق بالخيرات 🌟"
            pct >= 70 -> "جيد جداً - واصل اجتهادك 🌿"
            pct >= 50 -> "مقبول - استعن بالله وجدد النية 🌱"
            else -> "بحاجة إلى شد الهمة وتجديد العهد 🌸"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("المحاسبة اليومية 📋", fontWeight = FontWeight.Bold) },
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
            // Score Summary Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("نتيجة المحاسبة اليومية", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$totalPointsEarned / $maxPoints درجة",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = grade,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }
            }

            Text(
                text = "راجع أعمال يومك وسجل إنجازك:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(reviewItems) { index, item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.isDone) EmeraldGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = item.isDone,
                                    onCheckedChange = { checked ->
                                        val updated = reviewItems.toMutableList()
                                        updated[index] = updated[index].copy(isDone = checked)
                                        reviewItems = updated

                                        if (checked) {
                                            repository.addHasanat(item.points)
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = EmeraldGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.titleAr,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "+${item.points} نقطة",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                        }
                    }
                }
            }
        }
    }
}
