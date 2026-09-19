package com.kgsoft.favorsbank.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.data.REVIEW_QUESTIONS
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.NopeRed
import com.kgsoft.favorsbank.ui.theme.Tajwal
import com.kgsoft.favorsbank.util.toast
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Daily self-review: 23 questions navigated with Previous/Next and answered
 * with نعم / ربما / لا. The original discarded answers; here a small session
 * score is kept and summarized at the end. Mirrors DailyReviewActivity.
 */
@Composable
fun DailyReviewScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val total = REVIEW_QUESTIONS.size

    var index by remember { mutableIntStateOf(0) }
    val answers = remember { mutableStateListOf<Int?>().apply { repeat(total) { add(null) } } }
    var showSummary by remember { mutableStateOf(false) }
    val slide = remember { Animatable(0f) }

    fun transition(next: Int) {
        scope.launch {
            slide.animateTo(100f, tween(150))
            index = next.coerceIn(0, total - 1)
            slide.snapTo(-100f)
            slide.animateTo(0f, tween(300))
        }
    }

    fun answer(value: Int) {
        answers[index] = value
        if (index < total - 1) transition(index + 1)
        else showSummary = true
    }

    val q = REVIEW_QUESTIONS[index]

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp, end = 16.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = GreenPrimary)
            }
            Text(
                Strings.dailyReview,
                fontFamily = Tajwal,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                "${index + 1}/$total",
                fontFamily = Tajwal,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.12f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(slide.value.roundToInt(), 0) }
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        q.question,
                        fontFamily = Tajwal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (q.sub.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            q.sub,
                            fontFamily = Tajwal,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { answer(0) },
                    colors = ButtonDefaults.buttonColors(containerColor = NopeRed),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f)
                ) { Text("لا", fontFamily = Tajwal, fontWeight = FontWeight.Bold, color = Color.White) }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { answer(1) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f)
                ) { Text("ربما", fontFamily = Tajwal, fontWeight = FontWeight.Bold, color = Color.White) }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { answer(2) },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f)
                ) { Text("نعم", fontFamily = Tajwal, fontWeight = FontWeight.Bold, color = Color.White) }
            }

            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = {
                        if (index > 0) transition(index - 1)
                        else context.toast(Strings.startOfQuestions)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(50)
                ) { Text(Strings.prev, fontFamily = Tajwal, color = GreenPrimary) }
                Button(
                    onClick = {
                        if (index < total - 1) transition(index + 1)
                        else context.toast(Strings.endOfQuestions)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(50)
                ) { Text(Strings.next, fontFamily = Tajwal, color = GreenPrimary) }
            }
        }
    }

    if (showSummary) {
        val yes = answers.count { it == 2 }
        val maybe = answers.count { it == 1 }
        val no = answers.count { it == 0 }
        AlertDialog(
            onDismissRequest = { showSummary = false },
            title = { Text(Strings.endOfQuestions, fontFamily = Tajwal, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "نعم: $yes\nربما: $maybe\nلا: $no",
                    fontFamily = Tajwal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showSummary = false
                    navController.popBackStack()
                }) { Text(Strings.close, fontFamily = Tajwal, color = GreenPrimary) }
            }
        )
    }
}
