package com.swave.swave2000.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.swave.swave2000.theme.LocalThemeTokens

/** Thin horizontal slider, value in 0..1: 3dp `line` track, `ink` fill, 13dp round thumb. Replaces the rotary knob. */
@Composable
fun ThinSlider(value: Float, onValueChange: (Float) -> Unit, modifier: Modifier = Modifier, label: String = "Volume") {
    val t = LocalThemeTokens.current
    val onChange by rememberUpdatedState(onValueChange)
    val thumb = 13.dp
    Canvas(
        modifier
            .fillMaxWidth()
            .height(32.dp)
            .semantics { contentDescription = label }
            .pointerInput(Unit) {
                val r = thumb.toPx() / 2
                fun at(x: Float) = onChange(((x - r) / (size.width - 2 * r)).coerceIn(0f, 1f))
                detectTapGestures { at(it.x) }
            }
            .pointerInput(Unit) {
                val r = thumb.toPx() / 2
                fun at(x: Float) = onChange(((x - r) / (size.width - 2 * r)).coerceIn(0f, 1f))
                detectDragGestures(onDragStart = { at(it.x) }) { change, _ -> change.consume(); at(change.position.x) }
            }
    ) {
        val r = thumb.toPx() / 2
        val h = 3.dp.toPx()
        val cy = size.height / 2
        val trackW = size.width - 2 * r
        val x = r + trackW * value.coerceIn(0f, 1f)
        drawRoundRect(t.line, Offset(r, cy - h / 2), Size(trackW, h), CornerRadius(h / 2))
        drawRoundRect(t.ink, Offset(r, cy - h / 2), Size(x - r, h), CornerRadius(h / 2))
        drawCircle(t.ink, r, Offset(x, cy))
    }
}
