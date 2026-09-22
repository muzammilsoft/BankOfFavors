package com.kgsoft.favorsbank.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kgsoft.favorsbank.data.PrefsRepository
import com.kgsoft.favorsbank.data.resolveAppLang
import com.kgsoft.favorsbank.ui.screens.AboutScreen
import com.kgsoft.favorsbank.ui.screens.DailyReviewScreen
import com.kgsoft.favorsbank.ui.screens.DaynightAzkarScreen
import com.kgsoft.favorsbank.ui.screens.DiagLogScreen
import com.kgsoft.favorsbank.ui.screens.HomeScreen
import com.kgsoft.favorsbank.ui.screens.LoginScreen
import com.kgsoft.favorsbank.ui.screens.MyAccountScreen
import com.kgsoft.favorsbank.ui.screens.PrayerSettingsScreen
import com.kgsoft.favorsbank.ui.screens.ReportBugsScreen
import com.kgsoft.favorsbank.ui.screens.SalatTimesScreen
import com.kgsoft.favorsbank.ui.screens.SetLanguageScreen
import com.kgsoft.favorsbank.ui.screens.SettingsScreen
import com.kgsoft.favorsbank.ui.screens.ShowDetailsScreen
import com.kgsoft.favorsbank.ui.screens.SibhaScreen
import com.kgsoft.favorsbank.ui.screens.SplashScreen
import com.kgsoft.favorsbank.ui.screens.TodoScreen
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val HOME = "home"
    const val SALAT = "salat"
    const val AZKAR = "azkar"
    const val SIBHA = "sibha?zikr={zikr}"
    const val TODO = "todo?tab={tab}"
    const val DETAILS = "details"
    const val REVIEW = "review"
    const val ACCOUNT = "account"
    const val SETTINGS = "settings"
    const val LANGUAGE = "language"
    const val ABOUT = "about"
    const val REPORT = "report"
    const val DIAGLOG = "diaglog"
    const val PRAYER_SETTINGS = "prayer_settings"

    fun sibha(zikr: String? = null) =
        if (zikr.isNullOrBlank()) "sibha" else "sibha?zikr=${java.net.URLEncoder.encode(zikr, "UTF-8")}"

    fun todo(tab: Int = 0) = "todo?tab=$tab"
}

/** Payload for the details screen, mirroring the Intent extras of ShowDetailsActivity. */
data class DetailsPayload(
    val title: String,
    val time: String,
    val details: String,
    val earnings: String,
    val mission: String,
    val isJob: Boolean,
    val job: String
)

object DetailsStore {
    var current: DetailsPayload? = null
}

@Composable
fun BankApp(prefs: PrefsRepository, startRoute: String = Routes.SPLASH) {
    val navController = rememberNavController()
    val darkTheme by prefs.isDarkTheme.collectAsState(initial = false)
    val language by prefs.language.collectAsState(initial = "")

    // Resolve the UI language: stored preference wins; otherwise auto-detect
    // the device language, falling back to English when unsupported.
    val appLang = resolveAppLang(language)
    LaunchedEffect(appLang) { Strings.langCode = appLang.code }

    com.kgsoft.favorsbank.ui.theme.BankOfHasanatTheme(darkTheme = darkTheme) {
        // Mirror layout direction for RTL languages (ar, ur, fa).
        CompositionLocalProvider(
            LocalLayoutDirection provides
                if (appLang.rtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
        // Central background: every screen sits on the theme background color.
        // (The window background is fixed white, so screens without their own
        // background would stay white in dark mode and light text would vanish.)
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
        NavHost(
            navController = navController,
            startDestination = startRoute,
            enterTransition = { slideInHorizontally { it / 3 } + fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { slideOutHorizontally { it / 3 } + fadeOut() }
        ) {
            composable(Routes.SPLASH) { SplashScreen(navController, prefs) }
            composable(Routes.LOGIN) { LoginScreen(navController, prefs) }
            composable(Routes.HOME) { HomeScreen(navController, prefs) }
            composable(Routes.SALAT) { SalatTimesScreen(navController, prefs) }
            composable(Routes.AZKAR) { DaynightAzkarScreen(navController) }
            composable(
                Routes.SIBHA,
                arguments = listOf(navArgument("zikr") { type = NavType.StringType; defaultValue = "" })
            ) { backStack ->
                val zikr = backStack.arguments?.getString("zikr").orEmpty()
                    .let { java.net.URLDecoder.decode(it, "UTF-8") }
                SibhaScreen(navController, initialZikr = zikr.ifBlank { null })
            }
            composable(
                Routes.TODO,
                arguments = listOf(navArgument("tab") { type = NavType.IntType; defaultValue = 0 })
            ) { backStack ->
                TodoScreen(navController, prefs, initialTab = backStack.arguments?.getInt("tab") ?: 0)
            }
            composable(Routes.DETAILS) { ShowDetailsScreen(navController, prefs) }
            composable(Routes.REVIEW) { DailyReviewScreen(navController) }
            composable(Routes.ACCOUNT) { MyAccountScreen(navController, prefs) }
            composable(Routes.SETTINGS) { SettingsScreen(navController, prefs) }
            composable(Routes.LANGUAGE) { SetLanguageScreen(navController, prefs) }
            composable(Routes.ABOUT) { AboutScreen(navController) }
            composable(Routes.REPORT) { ReportBugsScreen(navController) }
            composable(Routes.DIAGLOG) { DiagLogScreen(navController) }
            composable(Routes.PRAYER_SETTINGS) { PrayerSettingsScreen(navController, prefs) }
        }
        } // Surface: theme background behind every screen
        }
    }
}
