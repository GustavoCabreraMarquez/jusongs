package com.swave.swave2000.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** Thin-stroke 24x24 icons (1.8 stroke, round caps) taken from the prototype's inline SVGs. Tint with `Icon`. */
object AppIcons {
    private fun icon(name: String, vararg d: String, filled: Boolean = false): ImageVector {
        val b = ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f)
        for (path in d) {
            b.addPath(
                pathData = PathParser().parsePathString(path).toNodes(),
                fill = if (filled) SolidColor(Color.Black) else null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            )
        }
        return b.build()
    }

    val Play = icon("Play", "M8 5l11 7-11 7z", filled = true)
    val Pause = icon("Pause", "M7 5v14M17 5v14")
    val Previous = icon("Previous", "M16 4l-9 8 9 8M5 4v16")
    val Next = icon("Next", "M8 4l9 8-9 8M19 4v16")
    val Back = icon("Back", "M15 5l-7 7 7 7")
    val Home = icon("Home", "M4 11l8-7 8 7v9h-5v-6H9v6H4z")
    val Albums = icon("Albums", "M12 4a8 8 0 1 0 0 16a8 8 0 1 0 0-16z", "M12 10a2 2 0 1 0 0 4a2 2 0 1 0 0-4z")
    val Songs = icon("Songs", "M9 18V6l10-2v12", "M7 16a2 2 0 1 0 0 4a2 2 0 1 0 0-4z", "M17 14a2 2 0 1 0 0 4a2 2 0 1 0 0-4z")
    val Shuffle = icon("Shuffle", "M4 7h3l10 10h3M4 17h3l3-3M13 10l4-3h3M17 4l3 3-3 3M17 14l3 3-3 3")
}
