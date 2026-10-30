package com.swave.swave2000.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.swave.swave2000.theme.LocalThemeTokens
import com.swave.swave2000.theme.MonoSmallStyle

/** Thin progress bar (3dp `line` track, `acc` fill) with small monospace times and the format label. Replaces the LCD. */
@Composable
fun ProgressPanel(
    progress: Float,
    elapsed: String,
    remaining: String,
    format: String?,
    modifier: Modifier = Modifier
) {
    val t = LocalThemeTokens.current
    val shape = RoundedCornerShape(3.dp)
    Column(modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().height(3.dp).clip(shape).background(t.line)) {
            Box(Modifier.fillMaxHeight().fillMaxWidth(progress.coerceIn(0f, 1f)).background(t.acc, shape))
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(elapsed, style = MonoSmallStyle, color = t.ink2)
            if (format != null) Text(format, style = MonoSmallStyle, color = t.ink2)
            Text(remaining, style = MonoSmallStyle, color = t.ink2)
        }
    }
}
