package com.swave.swave2000.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swave.swave2000.theme.*

enum class BevelType {
    Extruded, // Pushes out (buttons)
    Recessed  // Pushes in (LCD screen, album slots)
}

@Composable
fun BeveledContainer(
    modifier: Modifier = Modifier,
    type: BevelType = BevelType.Extruded,
    bevelWidth: Dp = 3.dp,
    cornerRadius: Dp = 8.dp,
    backgroundColor: Color = Y2KSageGreen,
    content: @Composable () -> Unit
) {
    val highlightColor = if (type == BevelType.Extruded) Y2KPlasticBevelLight else Y2KPlasticBevelShadow
    val shadowColor = if (type == BevelType.Extruded) Y2KPlasticBevelShadow else Y2KPlasticBevelLight

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .drawBehind {
                val w = size.width
                val h = size.height
                val b = bevelWidth.toPx()

                // Top-Left Bevel (Highlight in Extruded, Shadow in Recessed)
                val topLeftPath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(w, 0f)
                    lineTo(w - b, b)
                    lineTo(b, b)
                    lineTo(b, h - b)
                    lineTo(0f, h)
                    close()
                }
                drawPath(topLeftPath, highlightColor, alpha = 0.8f, style = Fill)

                // Bottom-Right Bevel (Shadow in Extruded, Highlight in Recessed)
                val bottomRightPath = Path().apply {
                    moveTo(w, h)
                    lineTo(0f, h)
                    lineTo(b, h - b)
                    lineTo(w - b, h - b)
                    lineTo(w - b, b)
                    lineTo(w, 0f)
                    close()
                }
                drawPath(bottomRightPath, shadowColor, alpha = 0.8f, style = Fill)
            }
            .background(backgroundColor)
            .padding(bevelWidth)
    ) {
        content()
    }
}
