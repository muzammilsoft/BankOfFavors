package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.data.DiagLog
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.Tajwal
import com.kgsoft.favorsbank.util.toast

/**
 * Diagnostic log viewer: what the app did in the last 24 hours
 * (prayer-times API calls, location flow, cache...). Entries expire
 * automatically after 24h. Copy the log to share it when reporting a bug.
 */
@Composable
fun DiagLogScreen(navController: NavController) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var entries by remember { mutableStateOf(DiagLog.snapshot()) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = GreenPrimary)
            }
            Text(Strings.diagLogTitle, fontFamily = Tajwal, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    clipboard.setText(AnnotatedString(entries.joinToString("\n")))
                    context.toast(Strings.logCopied)
                },
                enabled = entries.isNotEmpty(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                modifier = Modifier.weight(1f)
            ) {
                Text(Strings.copyLog, fontFamily = Tajwal, fontWeight = FontWeight.Bold, color = Color.White)
            }
            OutlinedButton(
                onClick = { entries = DiagLog.snapshot() },
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f)
            ) {
                Text(Strings.refreshLog, fontFamily = Tajwal, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = {
                    DiagLog.clear()
                    entries = emptyList()
                    context.toast(Strings.logCleared)
                },
                enabled = entries.isNotEmpty(),
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f)
            ) {
                Text(Strings.clearLog, fontFamily = Tajwal, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(12.dp))

        if (entries.isEmpty()) {
            Text(
                Strings.noLogEntries,
                fontFamily = Tajwal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            )
        } else {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(entries) { line ->
                        Text(
                            line,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
