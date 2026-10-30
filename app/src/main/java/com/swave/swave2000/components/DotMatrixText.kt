package com.swave.swave2000.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swave.swave2000.theme.*

/**
 * LCD-style text: glowing characters overlaid with a fine grid that mimics a dot-matrix panel.
 * Uses the monospace face until a bundled pixel font is added to res/font.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DotMatrixText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LcdTextStyle,
    marquee: Boolean = false
) {
    Text(
        text = text.uppercase(),
        maxLines = 1,
        softWrap = false,
        overflow = if (marquee) TextOverflow.Visible else TextOverflow.Clip,
        style = style.copy(shadow = Shadow(style.color.copy(alpha = 0.5f), blurRadius = 8f)),
        modifier = modifier
            .then(if (marquee) Modifier.basicMarquee() else Modifier)
            .drawWithContent {
                drawContent()
                val step = 2.5.dp.toPx()
                var x = 0f
                while (x < size.width) {
                    drawLine(Y2KLcdBackground, Offset(x, 0f), Offset(x, size.height), strokeWidth = 0.8f, alpha = 0.55f)
                    x += step
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(Y2KLcdBackground, Offset(0f, y), Offset(size.width, y), strokeWidth = 0.8f, alpha = 0.55f)
                    y += step
                }
            }
    )
}
