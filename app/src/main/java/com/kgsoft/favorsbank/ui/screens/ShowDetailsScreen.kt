package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.R
import com.kgsoft.favorsbank.data.PrefsRepository
import com.kgsoft.favorsbank.ui.DetailsStore
import com.kgsoft.favorsbank.ui.Routes
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.Tajwal
import com.kgsoft.favorsbank.util.copyToClipboard
import com.kgsoft.favorsbank.util.shareText
import com.kgsoft.favorsbank.util.toast
import kotlinx.coroutines.launch

/**
 * Mission/job details: duration, reward, full description, favorite / copy /
 * share actions, "confirm completion" (adds the bonus to the hasanat balance)
 * and a shortcut to the tasbih. Mirrors ShowDetailsActivity.
 */
@Composable
fun ShowDetailsScreen(navController: NavController, prefs: PrefsRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val payload = DetailsStore.current

    if (payload == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("…", fontFamily = Tajwal)
        }
        return
    }

    var showConfirm by remember { mutableStateOf(false) }
    val favId = "fav_${payload.job.hashCode()}"
    val isFav by prefs.isFavorite(favId).collectAsState(initial = false)

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp, end = 16.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = GreenPrimary)
            }
            Text(
                payload.title,
                fontFamily = Tajwal,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                scope.launch {
                    if (isFav) prefs.removeFavorite(favId)
                    else prefs.setFavorite(favId, payload.job)
                }
            }) {
                Icon(
                    painterResource(
                        if (isFav) R.drawable.ic_bookmark_black
                        else R.drawable.ic_bookmark_outline_black
                    ),
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
            IconButton(onClick = {
                context.copyToClipboard("details", payload.details)
            }) {
                Icon(
                    painterResource(R.drawable.ic_copy_white),
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
            IconButton(onClick = {
                context.shareText(
                    "${payload.mission}\n\n${payload.details}",
                    "شارك و ضاعف أجرك مضاعفة"
                )
            }) {
                Icon(
                    painterResource(R.drawable.ic_share_white),
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.12f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(Strings.investmentDuration, payload.time)
                    Spacer(Modifier.height(8.dp))
                    DetailRow(Strings.netProfit, payload.earnings)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                Strings.investmentDetails,
                fontFamily = Tajwal,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = GreenPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                payload.mission,
                fontFamily = Tajwal,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.clickable {
                    context.copyToClipboard("mission", "${payload.details}\n${payload.job}")
                }
            )
            Spacer(Modifier.height(8.dp))
            Text(
                payload.details,
                fontFamily = Tajwal,
                fontSize = 15.sp,
                lineHeight = 26.sp
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick = { showConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(
                    Strings.confirmCompletion,
                    fontFamily = Tajwal,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            if (!payload.isJob) {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { navController.navigate(Routes.sibha(payload.mission)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary.copy(alpha = 0.15f))
                ) {
                    Text(
                        Strings.useSibha,
                        fontFamily = Tajwal,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                }
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text(Strings.didYouDoIt, fontFamily = Tajwal, fontWeight = FontWeight.Bold) },
            text = { Text(Strings.confirmHonestly, fontFamily = Tajwal) },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    scope.launch {
                        prefs.setBonus(payload.bonus.toString())
                        prefs.addHasanat(payload.bonus)
                        context.toast("${Strings.blessedMsg}${payload.earnings}")
                    }
                }) {
                    Text(Strings.yesOfCourse, fontFamily = Tajwal, color = GreenPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text(Strings.noLater, fontFamily = Tajwal)
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            label,
            fontFamily = Tajwal,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(value, fontFamily = Tajwal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
