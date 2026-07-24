package com.kgsoft.favorsbank.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val EmeraldGreen = Color(0xFF0F5132)
val LightEmerald = Color(0xFF198754)
val DarkEmerald = Color(0xFF0A3622)
val GoldAccent = Color(0xFFD4AF37)
val LightGold = Color(0xFFF3E5AB)
val WarmBackground = Color(0xFFF8F9FA)
val SurfaceCard = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF1C1D1F)
val TextSecondary = Color(0xFF5A6268)

val DarkBackground = Color(0xFF121814)
val DarkSurfaceCard = Color(0xFF1E2620)
val DarkTextPrimary = Color(0xFFECEFF1)
val DarkTextSecondary = Color(0xFFB0BEC5)

private val LightColors = lightColorScheme(
    primary = EmeraldGreen,
    onPrimary = Color.White,
    primaryContainer = LightEmerald.copy(alpha = 0.15f),
    onPrimaryContainer = DarkEmerald,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    secondaryContainer = LightGold.copy(alpha = 0.3f),
    onSecondaryContainer = Color(0xFF5C4900),
    background = WarmBackground,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFEFEFEF),
    onSurfaceVariant = TextSecondary
)

private val DarkColors = darkColorScheme(
    primary = LightEmerald,
    onPrimary = Color.White,
    primaryContainer = DarkEmerald,
    onPrimaryContainer = Color.White,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF332A00),
    onSecondaryContainer = LightGold,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurfaceCard,
    onSurface = DarkTextPrimary,
    surfaceVariant = Color(0xFF28322B),
    onSurfaceVariant = DarkTextSecondary
)

@Composable
fun FavorsBankTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
