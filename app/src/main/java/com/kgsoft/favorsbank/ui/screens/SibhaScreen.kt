package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.R
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.Tajwal
import com.kgsoft.favorsbank.util.toast

/**
 * Digital tasbih. Default flow cycles سبحان الله (33) → الحمدلله (66) →
 * الله أكبر (99) → the full tahlil (100). A custom wird can be set via the
 * dialog or passed as a navigation argument (from the details screen).
 * Mirrors SibhaActivity.
 */
@Composable
fun SibhaScreen(navController: NavController, initialZikr: String? = null) {
    val context = LocalContext.current

    var counter by remember { mutableIntStateOf(0) }
    var label by remember { mutableStateOf(initialZikr ?: "سبحان الله") }
    var max by remember { mutableIntStateOf(if (initialZikr != null) 100 else 33) }
    var customMode by remember { mutableStateOf(initialZikr != null) }
    var showCustomDialog by remember { mutableStateOf(false) }

    fun stageFor(count: Int): Pair<String, Int> = when {
        count < 33 -> "سبحان الله" to 33
        count < 66 -> "الحمدلله" to 66
        count < 99 -> "الله أكبر" to 99
        else -> "لا إله إلا الله وحده لا شريك له، له الملك وله الحمد وهو على كل شيء قدير" to 100
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = GreenPrimary)
            }
            Text(Strings.sibha, fontFamily = Tajwal, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { showCustomDialog = true }) {
                Icon(
                    painterResource(R.drawable.ic_add_circle_outline_white),
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = {
                counter = 0
                label = "سبحان الله"
                max = 33
                customMode = false
            }) {
                Icon(
                    painterResource(R.drawable.ic_reset),
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painterResource(R.drawable.sibha_icon),
                contentDescription = null,
                modifier = Modifier.size(96.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                label,
                fontFamily = Tajwal,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                color = GreenPrimary
            )
            Spacer(Modifier.height(24.dp))
            Text(
                counter.toString(),
                fontFamily = Tajwal,
                fontWeight = FontWeight.Bold,
                fontSize = 72.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { (counter.toFloat() / max.coerceAtLeast(1)).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50)),
                color = GreenPrimary,
                trackColor = GreenPrimary.copy(alpha = 0.2f)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "$counter / $max",
                fontFamily = Tajwal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = {
                    val next = counter + 1
                    counter = next
                    if (!customMode) {
                        val (newLabel, newMax) = stageFor(next)
                        label = newLabel
                        max = newMax
                    }
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                modifier = Modifier.size(120.dp)
            ) {
                Text(
                    "سبّح",
                    fontFamily = Tajwal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
            Spacer(Modifier.height(16.dp))
            Row {
                TextButton(onClick = {
                    counter = 0
                    label = "سبحان الله"
                    max = 33
                    customMode = false
                }) {
                    Text(Strings.reset, fontFamily = Tajwal, color = GreenPrimary)
                }
                Spacer(Modifier.width(16.dp))
                TextButton(onClick = { showCustomDialog = true }) {
                    Text(Strings.customWird, fontFamily = Tajwal, color = GreenPrimary)
                }
            }
        }
    }

    if (showCustomDialog) {
        var wirdText by remember { mutableStateOf("") }
        var wirdCount by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            title = { Text(Strings.customWird, fontFamily = Tajwal, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = wirdText,
                        onValueChange = { wirdText = it },
                        label = { Text(Strings.wirdText, fontFamily = Tajwal) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = wirdCount,
                        onValueChange = { wirdCount = it },
                        label = { Text(Strings.repeatCount, fontFamily = Tajwal) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (wirdText.isBlank()) {
                        context.toast(Strings.dontLeaveEmpty)
                        return@TextButton
                    }
                    val count = wirdCount.toIntOrNull()
                    if (count == null || count <= 0) {
                        context.toast(Strings.numbersOnly)
                        return@TextButton
                    }
                    label = wirdText
                    max = count
                    counter = 0
                    customMode = true
                    showCustomDialog = false
                }) {
                    Text(Strings.save, fontFamily = Tajwal, color = GreenPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDialog = false }) {
                    Text(Strings.cancel, fontFamily = Tajwal)
                }
            }
        )
    }
}
