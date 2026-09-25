package com.kgsoft.favorsbank.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kgsoft.favorsbank.data.SacredText
import com.kgsoft.favorsbank.data.VIRTUE_I18N
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.Tajwal

/**
 * Three-line sacred-text display, exactly as specified:
 *  1. the fixed Arabic original (always first, always present),
 *  2. the pronunciation guide in the user's language
 *     (hidden in the Arabic UI; falls back to the shared Latin transliteration),
 *  3. the official translation in the user's language
 *     (hidden when the UI is Arabic; falls back English → Arabic),
 *  4. the virtue/reward note, translated to the UI language when available.
 *
 * The counter badge sits centered at the bottom of the card so the text
 * takes its full width. The layout never constrains text height — every
 * line wraps naturally with generous line spacing, so translations of any
 * length render without distortion or clipping.
 */
@Composable
fun TrilingualCard(
    s: SacredText,
    count: Int,
    virtueAr: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        TrilingualContent(s, count, virtueAr, modifier = Modifier.padding(16.dp))
    }
}

/**
 * The three text lines without the card wrapper, for embedding inside
 * other cards (e.g. the azkar counter cards).
 */
@Composable
fun TrilingualContent(
    s: SacredText,
    count: Int,
    virtueAr: String? = null,
    modifier: Modifier = Modifier
) {
    val lang = Strings.langCode
    val translit = s.translit(lang)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1 — original Arabic, fixed
        Text(
            text = s.arabic,
            fontFamily = Tajwal,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 30.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
        // 2 — pronunciation in the user's language
        // (hidden in the Arabic UI: the Arabic original needs no transliteration)
        if (lang != "ar" && translit.isNotBlank()) {
            Text(
                text = translit,
                fontStyle = FontStyle.Italic,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                modifier = Modifier.fillMaxWidth()
            )
        }
        // 3 — official translation in the user's language
        if (lang != "ar") {
            Text(
                text = s.meaning(lang),
                fontSize = 15.sp,
                lineHeight = 26.sp,
                color = GreenPrimary,
                modifier = Modifier.fillMaxWidth()
            )
        }
        // Virtue/reward note, translated to the UI language when available
        // (falls back to English, then to the Arabic original).
        virtueAr?.let { virtue ->
            val virtueText = VIRTUE_I18N[virtue]?.get(lang)
                ?: VIRTUE_I18N[virtue]?.get("en")
                ?: virtue
            Text(
                text = virtueText,
                fontFamily = Tajwal,
                fontSize = 13.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                modifier = Modifier.fillMaxWidth()
            )
        }
        // Counter badge at the bottom of the card so the text takes its full width.
        Text(
            text = "×$count",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = GreenPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
