package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.data.APP_LANGS
import com.kgsoft.favorsbank.data.appLangFor
import com.kgsoft.favorsbank.data.PrefsRepository
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.Tajwal
import com.kgsoft.favorsbank.util.toast
import kotlinx.coroutines.launch

/**
 * Language picker. The choice is stored and applied after an app restart,
 * exactly like the original SetLanguageActivity.
 */
@Composable
fun SetLanguageScreen(navController: NavController, prefs: PrefsRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentRaw by prefs.language.collectAsState(initial = "")
    // Normalize legacy stored values (e.g. "francais") so the checkmark shows.
    val current = appLangFor(currentRaw).prefKey
    var pending by remember { mutableStateOf<String?>(null) }

    val languages = APP_LANGS

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = GreenPrimary)
            }
            Text(Strings.chooseLanguage, fontFamily = Tajwal, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(languages) { lang ->
                val key = lang.prefKey
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (current == key)
                            GreenPrimary.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            scope.launch {
                                prefs.setLanguage(key)
                                pending = key
                            }
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(lang.nativeName, fontFamily = Tajwal, fontSize = 18.sp, modifier = Modifier.weight(1f))
                        if (current == key) {
                            Text("✓", color = GreenPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }

    if (pending != null) {
        AlertDialog(
            onDismissRequest = { pending = null },
            title = { Text(Strings.saveChanges, fontFamily = Tajwal, fontWeight = FontWeight.Bold) },
            text = { Text(Strings.restartNeeded, fontFamily = Tajwal) },
            confirmButton = {
                TextButton(onClick = {
                    pending = null
                    (context as? android.app.Activity)?.finishAffinity()
                }) {
                    Text(Strings.restart, fontFamily = Tajwal, color = GreenPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    pending = null
                    context.toast(Strings.langChanged, long = true)
                    navController.popBackStack()
                }) {
                    Text(Strings.later, fontFamily = Tajwal)
                }
            }
        )
    }
}
