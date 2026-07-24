package com.kgsoft.favorsbank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kgsoft.favorsbank.data.FavorsRepository
import com.kgsoft.favorsbank.ui.screens.*
import com.kgsoft.favorsbank.ui.theme.FavorsBankTheme

class MainActivity : ComponentActivity() {

    private lateinit var repository: FavorsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = FavorsRepository(applicationContext)

        setContent {
            FavorsBankTheme {
                MainAppLayout(repository)
            }
        }
    }
}

@Composable
fun MainAppLayout(repository: FavorsRepository) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    val bottomNavItems = listOf(
        BottomNavItem("الرئيسية", "home", Icons.Default.Home),
        BottomNavItem("الأذكار", "azkar", Icons.Default.WbSunny),
        BottomNavItem("السبحة", "sibha", Icons.Default.TouchApp),
        BottomNavItem("المهام", "tasks", Icons.Default.CheckCircle),
        BottomNavItem("الإعدادات", "settings", Icons.Default.Settings)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(repository) { route ->
                    navController.navigate(route)
                }
            }
            composable("azkar") {
                AzkarScreen(repository) {
                    navController.popBackStack()
                }
            }
            composable("sibha") {
                SibhaScreen(repository) {
                    navController.popBackStack()
                }
            }
            composable("tasks") {
                TasksScreen(repository) {
                    navController.popBackStack()
                }
            }
            composable("daily_review") {
                DailyReviewScreen(repository) {
                    navController.popBackStack()
                }
            }
            composable("prayer_times") {
                PrayerTimesScreen(repository) {
                    navController.popBackStack()
                }
            }
            composable("account") {
                MyAccountScreen(repository) {
                    navController.popBackStack()
                }
            }
            composable("settings") {
                SettingsScreen(repository) {
                    navController.popBackStack()
                }
            }
        }
    }
}

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
