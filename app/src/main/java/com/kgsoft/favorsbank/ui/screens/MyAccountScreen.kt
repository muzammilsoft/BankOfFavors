package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.R
import com.kgsoft.favorsbank.data.PrefsRepository
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.Tajwal
import kotlinx.coroutines.launch

/**
 * Profile screen: avatar, name, hasanat balance and editable credentials.
 * Mirrors MyAccountActivity.
 */
@Composable
fun MyAccountScreen(navController: NavController, prefs: PrefsRepository) {
    val scope = rememberCoroutineScope()
    val username by prefs.username.collectAsState(initial = null)
    val password by prefs.password.collectAsState(initial = null)
    val hasanat by prefs.hasanat.collectAsState(initial = 0L)

    var editing by remember { mutableStateOf(false) }
    var nameInput by remember(username) { mutableStateOf(username ?: "") }
    var passInput by remember(password) { mutableStateOf(password ?: "") }

    val displayName = username?.takeIf { it.isNotBlank() } ?: "عبدالله"
    val displayPass = password?.takeIf { it.isNotBlank() } ?: "بسم الله"

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp, end = 16.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = GreenPrimary)
            }
            Text(
                Strings.myAccount,
                fontFamily = Tajwal,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { editing = !editing }) {
                Icon(
                    painterResource(R.drawable.edit),
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(R.drawable.default_profile_pic),
                contentDescription = null,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                displayName,
                fontFamily = Tajwal,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GreenPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        Strings.balance,
                        fontFamily = Tajwal,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp
                    )
                    Text(
                        "$hasanat ${Strings.hasanatSuffix}",
                        fontFamily = Tajwal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        Strings.accountDetails,
                        fontFamily = Tajwal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = GreenPrimary
                    )
                    Spacer(Modifier.height(12.dp))
                    if (editing) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text(Strings.username, fontFamily = Tajwal) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = passInput,
                            onValueChange = { passInput = it },
                            label = { Text(Strings.password, fontFamily = Tajwal) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    prefs.setUsername(nameInput)
                                    prefs.setPassword(passInput)
                                    editing = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            Text(Strings.save, fontFamily = Tajwal, color = Color.White)
                        }
                    } else {
                        AccountRow(Strings.username, displayName)
                        Spacer(Modifier.height(8.dp))
                        AccountRow(Strings.password, displayPass)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "حافظ على حسناتك بالمداومة على الطاعات، وتذكر أن القليل الدائم خير من الكثير المنقطع.",
                fontFamily = Tajwal,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun AccountRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            label,
            fontFamily = Tajwal,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.weight(1f)
        )
        Text(value, fontFamily = Tajwal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
