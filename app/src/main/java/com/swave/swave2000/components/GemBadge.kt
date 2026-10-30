package com.swave.swave2000.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swave.swave2000.theme.*

/** Faceted crystal used as logo, mini-player icon and artwork placeholder. */
@Composable
fun GemBadge(modifier: Modifier = Modifier, size: Dp = 28.dp) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        fun facet(color: Color, vararg pts: Pair<Float, Float>) {
            val path = Path().apply {
                moveTo(pts[0].first * w, pts[0].second * h)
                for (i in 1 until pts.size) lineTo(pts[i].first * w, pts[i].second * h)
                close()
            }
            drawPath(path, color)
        }
        facet(Y2KMetallicBadge, 0.05f to 0.35f, 0.28f to 0.08f, 0.72f to 0.08f, 0.95f to 0.35f, 0.5f to 0.95f)
        facet(Y2KSoftTeal, 0.05f to 0.35f, 0.95f to 0.35f, 0.5f to 0.95f)
        facet(Y2KSoftTealDark, 0.05f to 0.35f, 0.5f to 0.35f, 0.5f to 0.95f)
        facet(Y2KWarmCream, 0.28f to 0.08f, 0.5f to 0.35f, 0.72f to 0.08f)
        facet(Y2KPlasticBevelLight, 0.28f to 0.08f, 0.05f to 0.35f, 0.5f to 0.35f)
    }
}

@Composable
fun GemPlaceholder(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().background(Y2KWarmCream), contentAlignment = Alignment.Center) {
        GemBadge(size = 48.dp)
    }
}


