package com.kgsoft.favorsbank.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.kgsoft.favorsbank.R

val GreenPrimary = Color(0xFF00B276)
val GreenBright = Color(0xFF01B276)
val DarkBg = Color(0xFF263238)
val NopeRed = Color(0xFFC62828)

/**
 * "Grain ear" (سنبلة) corner style: two rounded diagonal corners, two sharp —
 * the app's identity shape used for home cards and prayer rows.
 */
val GrainShape = RoundedCornerShape(
    topStart = 18.dp,
    topEnd = 0.dp,
    bottomEnd = 18.dp,
    bottomStart = 0.dp
)

val Tajwal = FontFamily(
    Font(R.font.tajwal, FontWeight.Normal),
    Font(R.font.tajwal, FontWeight.Bold)
)
val QuranFont = FontFamily(
    Font(R.font.quran, FontWeight.Normal),
    Font(R.font.quran, FontWeight.Bold)
)

private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    secondary = GreenBright,
    background = Color.White,
    surface = Color.White,
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A)
)

private val DarkColors = darkColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    secondary = GreenBright,
    background = DarkBg,
    surface = DarkBg,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun BankOfHasanatTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MaterialTheme.typography.copy(
            displayLarge = MaterialTheme.typography.displayLarge.copy(fontFamily = Tajwal),
            displayMedium = MaterialTheme.typography.displayMedium.copy(fontFamily = Tajwal),
            displaySmall = MaterialTheme.typography.displaySmall.copy(fontFamily = Tajwal),
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontFamily = Tajwal),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontFamily = Tajwal),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontFamily = Tajwal),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = Tajwal),
            titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = Tajwal),
            titleSmall = MaterialTheme.typography.titleSmall.copy(fontFamily = Tajwal),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = Tajwal),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = Tajwal),
            bodySmall = MaterialTheme.typography.bodySmall.copy(fontFamily = Tajwal),
            labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = Tajwal),
            labelMedium = MaterialTheme.typography.labelMedium.copy(fontFamily = Tajwal),
            labelSmall = MaterialTheme.typography.labelSmall.copy(fontFamily = Tajwal)
        ),
        content = content
    )
}
