package com.kgsoft.favorsbank.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.R
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.Tajwal
import com.kgsoft.favorsbank.util.openUrl
import com.kgsoft.favorsbank.util.shareOwnApk

/**
 * About screen: app description dialog, policy link, update check, donate
 * panel and contact link. Mirrors AboutActivity (also fixes the dead
 * Telegram link, which the original built but never launched).
 */
@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current
    var showAbout by remember { mutableStateOf(false) }
    var showDonate by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = GreenPrimary)
            }
            Text(Strings.about, fontFamily = Tajwal, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                Strings.appName,
                fontFamily = Tajwal,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = GreenPrimary
            )
            Text(
                "الإصدار 1.0",
                fontFamily = Tajwal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(16.dp))

            AboutCard(Strings.aboutApp, R.drawable.about) { showAbout = true }
            AboutCard(Strings.policy, R.drawable.ic_local_library_black) {
                context.openUrl("https://www.apkpure.com/com.kgsoft.favorsbank")
            }
            AboutCard(Strings.donate, R.drawable.ic_donate) { showDonate = !showDonate }

            AnimatedVisibility(visible = showDonate) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.12f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Text(
                        "جزاك الله خيراً على نيتك الطيبة. يمكنك دعم استمرار التطبيق بمشاركته مع من تحب، فالدال على الخير كفاعله.",
                        fontFamily = Tajwal,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }

            AboutCard("تواصل معنا", R.drawable.ic_reply_black) {
                context.openUrl("https://t.me/muzammil_yahia")
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { context.openUrl("https://apkpure.com/p/com.kgsoft.favorsbank") },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(Strings.checkUpdates, fontFamily = Tajwal, color = Color.White)
            }
        }
    }

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            title = { Text(Strings.aboutApp, fontFamily = Tajwal, fontWeight = FontWeight.Bold) },
            text = { Text(Strings.aboutText, fontFamily = Tajwal, fontSize = 15.sp, lineHeight = 26.sp) },
            confirmButton = {
                TextButton(onClick = { showAbout = false }) {
                    Text(Strings.understood, fontFamily = Tajwal, color = GreenPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAbout = false }) {
                    Text(Strings.close, fontFamily = Tajwal)
                }
            }
        )
    }
}

@Composable
private fun AboutCard(title: String, icon: Int, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(18.dp)
        ) {
            Image(
                painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            Spacer(Modifier.width(14.dp))
            Text(title, fontFamily = Tajwal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
