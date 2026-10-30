package com.swave.swave2000.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.swave.swave2000.theme.AppTheme
import com.swave.swave2000.theme.LocalThemeTokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.cos
import kotlin.random.Random

/** Rendered scenery plus a blurred copy, shared with the glass bars that fake `backdrop-filter`. */
@Stable
class Backdrop {
    var art by mutableStateOf<ImageBitmap?>(null)
    var blurred by mutableStateOf<ImageBitmap?>(null)
    var size by mutableStateOf(IntSize.Zero)
}

val LocalBackdrop = staticCompositionLocalOf { Backdrop() }

/** Full-bleed painted scenery for [theme]; at night it also drops snow. */
@Composable
fun PaintedBackground(theme: AppTheme, modifier: Modifier = Modifier) {
    val backdrop = LocalBackdrop.current
    val tokens = LocalThemeTokens.current
    val glass = tokens.glass

    Box(modifier.fillMaxSize().background(tokens.bg).onSizeChanged { backdrop.size = it }) {
        // Half resolution is plenty for soft scenery and keeps the bitmap at ~2.5 MB.
        LaunchedEffect(theme, backdrop.size) {
            val s = backdrop.size
            if (s.width == 0 || s.height == 0) return@LaunchedEffect
            val (art, blur) = withContext(Dispatchers.Default) {
                val bmp = renderArt(theme, s.width / 2, s.height / 2)
                bmp.asImageBitmap() to blurredCopy(bmp).asImageBitmap()
            }
            backdrop.art = art
            backdrop.blurred = blur
        }
        Canvas(Modifier.fillMaxSize()) {
            backdrop.art?.let { drawImage(it, dstSize = IntSize(size.width.toInt(), size.height.toInt()), filterQuality = FilterQuality.Medium) }
        }
        if (theme == AppTheme.Night) Snow()
        // Scrim from the prototype: glass at the top and bottom, clear in the middle, for text contrast.
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(0f to glass, 0.4f to Color.Transparent, 1f to glass)
            )
        )
    }
}

private class Flake(
    val x: Float, val r: Float, val alpha: Float,
    val fallSec: Float, val phaseSec: Float,
    val drift: Float, val driftSec: Float
)

private const val FLAKES = 70

private val flakes = Random(2000).let { rnd ->
    val radii = floatArrayOf(1f, 1.3f, 1.8f, 2.4f, 3f)
    List(FLAKES) {
        val fall = 6f + rnd.nextFloat() * 7.8f
        Flake(
            x = rnd.nextFloat() * 360f,
            r = radii[rnd.nextInt(radii.size)],
            alpha = 0.5f + rnd.nextFloat() * 0.44f,
            fallSec = fall,
            phaseSec = rnd.nextFloat() * fall,
            drift = (rnd.nextFloat() - 0.5f) * 50f,
            driftSec = 3f + rnd.nextFloat() * 3f
        )
    }
}

/** Static snowfall: flakes are frozen at their starting positions, so nothing animates or redraws. */
@Composable
private fun Snow() {
    Canvas(Modifier.fillMaxSize()) {
        val tf = ArtTransform(size.width, size.height)
        val t = 0f
        for (f in flakes) {
            val fall = ((t + f.phaseSec) % f.fallSec) / f.fallSec
            val y = -10f + 800f * fall
            val x = f.x + f.drift * (1f - cos(2f * PI.toFloat() * t / f.driftSec)) / 2f
            drawCircle(
                Color.White.copy(alpha = f.alpha),
                radius = f.r * tf.scale,
                center = Offset(tf.tx + x * tf.scale, tf.ty + y * tf.scale)
            )
        }
    }
}
