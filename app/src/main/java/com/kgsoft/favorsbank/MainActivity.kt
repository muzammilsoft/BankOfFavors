package com.kgsoft.favorsbank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kgsoft.favorsbank.data.PrefsRepository
import com.kgsoft.favorsbank.ui.BankApp
import com.kgsoft.favorsbank.ui.Routes

/**
 * Single-activity host for the whole app. The original had 14 activities;
 * here every screen is a Compose destination navigated with Navigation Compose.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefs = PrefsRepository(applicationContext)
        // Deep-link target for notification taps (the original opened TodoActivity).
        val startRoute = when (intent?.getStringExtra("route")) {
            "todo" -> Routes.todo()
            else -> Routes.SPLASH
        }
        setContent {
            BankApp(prefs = prefs, startRoute = startRoute)
        }
    }
}
