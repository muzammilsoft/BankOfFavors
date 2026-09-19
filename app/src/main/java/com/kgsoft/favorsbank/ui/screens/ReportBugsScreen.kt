package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.Tajwal
import com.kgsoft.favorsbank.util.sendBugReportEmail
import com.kgsoft.favorsbank.util.toast

/**
 * Bug report / suggestion screen. The message is sent as the subject of an
 * email to the developer, like the original ReportBugsActivity.
 */
@Composable
fun ReportBugsScreen(navController: NavController) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var kind by remember { mutableIntStateOf(0) } // 0 = suggestion, 1 = bug

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = GreenPrimary)
            }
            Text(Strings.reportBug, fontFamily = Tajwal, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                Strings.sendSuggestion,
                fontFamily = Tajwal,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = GreenPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                Strings.reportBugTitle,
                fontFamily = Tajwal,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(16.dp))

            listOf("إقتراح" to 0, "إبلاغ عن خطأ" to 1).forEach { (label, value) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = kind == value, onClick = { kind = value })
                    Text(label, fontFamily = Tajwal, fontSize = 15.sp)
                }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(Strings.writeHere, fontFamily = Tajwal) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(16.dp),
                maxLines = 8
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    if (text.isBlank()) {
                        context.toast("لا تترك الحقل خالياً")
                    } else {
                        val kindLabel = if (kind == 0) "إقتراح" else "بلاغ عن خطأ"
                        context.sendBugReportEmail("[$kindLabel] $text")
                    }
                },
                enabled = text.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(Strings.submit, fontFamily = Tajwal, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
