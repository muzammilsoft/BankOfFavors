package com.kgsoft.favorsbank.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kgsoft.favorsbank.data.SacredText
import com.kgsoft.favorsbank.ui.Strings
import com.kgsoft.favorsbank.ui.theme.GreenPrimary
import com.kgsoft.favorsbank.ui.theme.Tajwal

/**
 * Three-line sacred-text display, exactly as specified:
 *  1. the fixed Arabic original (always first, always present),
 *  2. the pronunciation guide in the user's language
 *     (falls back to the shared Latin transliteration),
 *  3. the official translation in the user's language
 *     (hidden when the UI is Arabic; falls back English → Arabic).
 *
 * The layout never constrains text height — every line wraps naturally with
 * generous line spacing, so translations of any length render without
 * distortion or clipping.
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
        if (translit.isNotBlank()) {
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
        // Arabic virtue note, when the source had one
        virtueAr?.let { virtue ->
            Text(
                text = virtue,
                fontFamily = Tajwal,
                fontSize = 13.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "×$count",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = GreenPrimary
            )
        }
    }
}
