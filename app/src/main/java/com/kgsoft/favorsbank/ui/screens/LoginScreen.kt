package com.kgsoft.favorsbank.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.data.PrefsRepository
import com.kgsoft.favorsbank.ui.Routes
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.Tajwal
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Login screen. Login is optional (it can be skipped); credentials are stored
 * in the "profile" DataStore, like the original LoginActivity.
 *
 * Design: green card with solid white input fields (dark text) so the form
 * stays high-contrast and readable on the green background.
 */
@Composable
fun LoginScreen(navController: NavController, prefs: PrefsRepository) {
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dontShowAgain by remember { mutableStateOf(false) }
    val cardOffset = remember { Animatable(-400f) }

    // Entrance animation: card slides down, like the original 900ms translation.
    LaunchedEffect(Unit) {
        cardOffset.animateTo(0f, animationSpec = tween(900))
    }

    fun goHome() {
        navController.navigate(Routes.HOME) {
            popUpTo(Routes.LOGIN) { inclusive = true }
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White,
        focusedTextColor = Color(0xFF1A1A1A),
        unfocusedTextColor = Color(0xFF1A1A1A),
        cursorColor = GreenPrimary,
        focusedLabelColor = GreenPrimary,
        unfocusedLabelColor = GreenPrimary.copy(alpha = 0.8f),
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White.copy(alpha = 0.7f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, cardOffset.value.roundToInt()) },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = GreenPrimary),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    Strings.loginTitle,
                    fontFamily = Tajwal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    Strings.loginSubtitle,
                    fontFamily = Tajwal,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(Strings.username, fontFamily = Tajwal) },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(Strings.password, fontFamily = Tajwal) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(50),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        scope.launch {
                            prefs.setUsername(username)
                            prefs.setPassword(password)
                            if (dontShowAgain) prefs.setDontShowAgain(true)
                            goHome()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(Strings.login, fontFamily = Tajwal, fontWeight = FontWeight.Bold, color = GreenPrimary)
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { dontShowAgain = !dontShowAgain }
                ) {
                    Checkbox(
                        checked = dontShowAgain,
                        onCheckedChange = { dontShowAgain = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.White,
                            checkmarkColor = GreenPrimary,
                            uncheckedColor = Color.White
                        )
                    )
                    Text(Strings.dontShowAgain, fontFamily = Tajwal, color = Color.White, fontSize = 14.sp)
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            Strings.skip,
            fontFamily = Tajwal,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = GreenPrimary,
            modifier = Modifier.clickable { goHome() }
        )
    }
}
