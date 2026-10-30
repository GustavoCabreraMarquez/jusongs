package com.swave.swave2000.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.swave.swave2000.theme.*

/** Recessed dark display with a faint pixel grid behind its content. */
@Composable
fun LcdPanel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    BeveledContainer(
        modifier = modifier,
        type = BevelType.Recessed,
        backgroundColor = Y2KLcdBackground,
        cornerRadius = 6.dp
    ) {
        Box {
            Canvas(Modifier.matchParentSize()) {
                val step = 6.dp.toPx()
                var x = step / 2
                while (x < size.width) {
                    var y = step / 2
                    while (y < size.height) {
                        drawCircle(Y2KLcdGrid, radius = 0.8.dp.toPx(), center = Offset(x, y))
                        y += step
                    }
                    x += step
                }
            }
            Column(Modifier.padding(12.dp), content = content)
        }
    }
}
