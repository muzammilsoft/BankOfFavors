package com.kgsoft.favorsbank.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kgsoft.favorsbank.R
import com.kgsoft.favorsbank.data.PrefsRepository
import com.kgsoft.favorsbank.data.firstValue
import com.kgsoft.favorsbank.ui.Routes
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import kotlinx.coroutines.delay

/**
 * Launch screen: green background with the small app logo centered.
 * After 2s: if the user checked "don't show again" the login is skipped
 * and we go straight home.
 */
@Composable
fun SplashScreen(navController: NavController, prefs: PrefsRepository) {
    LaunchedEffect(Unit) {
        delay(2000)
        val skipLogin = prefs.dontShowAgain.firstValue()
        navController.navigate(if (skipLogin) Routes.HOME else Routes.LOGIN) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
    }
}
