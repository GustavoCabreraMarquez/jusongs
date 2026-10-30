package com.swave.swave2000.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swave.swave2000.theme.LocalThemeTokens

/** Outline pill with the audio format, e.g. `FLAC 24/96`. */
@Composable
fun FormatBadge(label: String, modifier: Modifier = Modifier) {
    val t = LocalThemeTokens.current
    Text(
        label,
        modifier = modifier
            .border(1.dp, t.line, RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        style = MaterialTheme.typography.labelSmall,
        color = t.ink2,
        maxLines = 1
    )
}
