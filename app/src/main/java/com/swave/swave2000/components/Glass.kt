package com.swave.swave2000.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.swave.swave2000.theme.LocalThemeTokens

val CardShape = RoundedCornerShape(20.dp)

/** Translucent card: `glass` fill with a 1dp `line` border. No blur, so it is safe on scrolling content. */
@Composable
fun Modifier.glass(shape: Shape = CardShape): Modifier {
    val t = LocalThemeTokens.current
    return this.clip(shape).background(t.glass, shape).border(1.dp, t.line, shape)
}

/**
 * Glass with a real blurred backdrop, for surfaces fixed on screen (mini-player, bottom bar). Draws the
 * pre-blurred scenery from [LocalBackdrop] aligned with the window, so it only works over the painted
 * background, not over scrolling content.
 */
@Composable
fun Modifier.glassBlur(shape: Shape = CardShape, border: Boolean = true): Modifier {
    val t = LocalThemeTokens.current
    val backdrop = LocalBackdrop.current
    var pos by remember { mutableStateOf(Offset.Zero) }
    return this
        .onGloballyPositioned { pos = it.positionInRoot() }
        .clip(shape)
        .drawBehind {
            val blurred = backdrop.blurred ?: return@drawBehind
            translate(-pos.x, -pos.y) {
                drawImage(blurred, dstSize = IntSize(backdrop.size.width, backdrop.size.height), filterQuality = FilterQuality.Medium)
            }
        }
        .background(t.glass, shape)
        .then(if (border) Modifier.border(1.dp, t.line, shape) else Modifier)
}
