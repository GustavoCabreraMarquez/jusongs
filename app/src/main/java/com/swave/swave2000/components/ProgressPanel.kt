package com.swave.swave2000.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.swave.swave2000.theme.LocalThemeTokens
import com.swave.swave2000.theme.MonoSmallStyle

/** Progress bar (8dp, in a 32dp touch target for tap/drag seeking; `line` track, `acc` fill) with small monospace times and the format label. Replaces the LCD. */
@Composable
fun ProgressPanel(
    progress: Float,
    elapsed: String,
    remaining: String,
    format: String?,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val t = LocalThemeTokens.current
    val shape = RoundedCornerShape(4.dp)
    val onSeekNow by rememberUpdatedState(onSeek)
    var dragFraction by remember { mutableStateOf<Float?>(null) }
    val shown = (dragFraction ?: progress).coerceIn(0f, 1f)
    Column(modifier.fillMaxWidth()) {
        // Tall transparent touch target around the thin bar; tap or drag to seek.
        Box(
            Modifier.fillMaxWidth().height(32.dp)
                .semantics { contentDescription = "Seek" }
                .pointerInput(Unit) {
                    detectTapGestures { onSeekNow((it.x / size.width).coerceIn(0f, 1f)) }
                }
                .pointerInput(Unit) {
                    fun frac(x: Float) = (x / size.width).coerceIn(0f, 1f)
                    detectDragGestures(
                        onDragStart = { dragFraction = frac(it.x) },
                        onDragEnd = { dragFraction?.let(onSeekNow); dragFraction = null },
                        onDragCancel = { dragFraction = null }
                    ) { change, _ -> change.consume(); dragFraction = frac(change.position.x) }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(Modifier.fillMaxWidth().height(8.dp).clip(shape).background(t.line)) {
                Box(Modifier.fillMaxHeight().fillMaxWidth(shown).background(t.acc, shape))
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(elapsed, style = MonoSmallStyle, color = t.ink2)
            if (format != null) Text(format, style = MonoSmallStyle, color = t.ink2)
            Text(remaining, style = MonoSmallStyle, color = t.ink2)
        }
    }
}
