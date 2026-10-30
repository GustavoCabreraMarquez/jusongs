package com.swave.swave2000.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swave.swave2000.theme.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

private const val SWEEP_DEGREES = 270f
private const val HALF_SWEEP = SWEEP_DEGREES / 2

/**
 * Rotary knob: dragging around the centre sets a value in 0..1 between hard MIN and MAX stops
 * (a 90° dead zone at the bottom). Grip ridges around the rim and an indicator notch show the position.
 */
@Composable
fun RotaryKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    diameter: Dp = 96.dp
) {
    val currentValue by rememberUpdatedState(value)
    val currentOnChange by rememberUpdatedState(onValueChange)

    fun update(pos: Offset, w: Int, h: Int) {
        val deg = Math.toDegrees(atan2(pos.x - w / 2f, -(pos.y - h / 2f)).toDouble()).toFloat()
        val v = when {
            deg > HALF_SWEEP -> 1f // dead zone: stick to the nearest stop
            deg < -HALF_SWEEP -> 0f
            else -> (deg + HALF_SWEEP) / SWEEP_DEGREES
        }
        if (v != currentValue) currentOnChange(v.coerceIn(0f, 1f))
    }

    Canvas(
        modifier
            .size(diameter)
            .semantics { contentDescription = "Volume knob" }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { update(it, size.width, size.height) },
                    onDrag = { change, _ ->
                        change.consume()
                        update(change.position, size.width, size.height)
                    }
                )
            }
    ) {
        val r = size.minDimension / 2
        val c = center
        fun at(angleDeg: Float, radius: Float): Offset {
            val a = Math.toRadians(angleDeg.toDouble())
            return Offset(c.x + (sin(a) * radius).toFloat(), c.y - (cos(a) * radius).toFloat())
        }

        // MIN / MAX stop marks on the faceplate
        for (stop in listOf(-HALF_SWEEP, HALF_SWEEP)) {
            drawLine(Y2KPlasticBevelShadow, at(stop, r * 0.97f), at(stop, r * 0.86f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
        }

        // Body and rim
        drawCircle(Y2KPlasticBevelShadow, radius = r * 0.84f, center = c.copy(y = c.y + 2.dp.toPx()))
        drawCircle(
            Brush.radialGradient(listOf(Y2KWarmCream, Y2KWarmCreamDark), center = at(-30f, r * 0.3f), radius = r),
            radius = r * 0.82f,
            center = c
        )

        // Grip ridges
        for (i in 0 until 36) {
            val a = i * 10f
            drawLine(Y2KWarmCreamDark, at(a, r * 0.72f), at(a, r * 0.80f), strokeWidth = 1.5.dp.toPx())
        }

        // Indicator notch
        val angle = -HALF_SWEEP + currentValue * SWEEP_DEGREES
        drawLine(Y2KLcdBackground, at(angle, r * 0.30f), at(angle, r * 0.66f), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(Y2KAccentAmber, radius = 3.dp.toPx(), center = at(angle, r * 0.66f))
    }
}
