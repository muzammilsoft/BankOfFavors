package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kgsoft.favorsbank.data.FavorsRepository
import com.kgsoft.favorsbank.ui.theme.EmeraldGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    repository: FavorsRepository,
    onBack: () -> Unit
) {
    var vibrationEnabled by remember { mutableStateOf(repository.isVibrationEnabled()) }
    var selectedLanguage by remember { mutableStateOf(repository.getLanguage()) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات ⚙️", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            // General Preferences
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("تفضيلات التطبيق", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Vibration, contentDescription = null, tint = EmeraldGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("الاهتزاز عند التسبيح والأذكار", fontSize = 14.sp)
                        }
                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = { checked ->
                                vibrationEnabled = checked
                                repository.setVibrationEnabled(checked)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = EmeraldGreen
                            )
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = EmeraldGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("لغة التطبيق (Language)", fontSize = 14.sp)
                        }
                        Row {
                            FilterChip(
                                selected = selectedLanguage == "ar",
                                onClick = {
                                    selectedLanguage = "ar"
                                    repository.setLanguage("ar")
                                },
                                label = { Text("العربية") }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            FilterChip(
                                selected = selectedLanguage == "en",
                                onClick = {
                                    selectedLanguage = "en"
                                    repository.setLanguage("en")
                                },
                                label = { Text("English") }
                            )
                        }
                    }
                }
            }

            // About & Reset Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextButton(
                        onClick = { showAboutDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = EmeraldGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("عن تطبيق بنك الحسنات", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Button(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إعادة ضبط جميع البيانات")
                    }
                }
            }
        }

        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text("عن تطبيق بنك الحسنات 🕋") },
                text = {
                    Text("تطبيق إسلامي شامل للذكْر، والتسبيح، والأعمال الصالحة، والمحاسبة اليومية بلغة Kotlin ومصمم بأحدث تقنيات Jetpack Compose لتسهيل الطاعات وكسب الحسنات.")
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text("تم")
                    }
                }
            )
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("تأكيد تصفير البيانات") },
                text = { Text("هل أنت تأكد من رغبتك في مسح رصيد الحسنات والإحصائيات وإعادة الضبط؟") },
                confirmButton = {
                    Button(
                        onClick = {
                            repository.resetAllData()
                            showResetDialog = false
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("تأكيد المسح")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("إلغاء")
                    }
                }
            )
        }
    }
}
