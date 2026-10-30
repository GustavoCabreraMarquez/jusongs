package com.swave.swave2000.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.swave.swave2000.theme.LocalThemeTokens

/**
 * Album art loaded by Coil, with a flat note placeholder while loading or when there is no art.
 * Corner rounding is up to the caller via [modifier] (`Modifier.clip(...)`).
 */
@Composable
fun ArtworkImage(
    uri: Uri?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    SubcomposeAsyncImage(
        model = uri,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        loading = { ArtPlaceholder() },
        error = { ArtPlaceholder() }
    )
}

@Composable
fun ArtPlaceholder(modifier: Modifier = Modifier) {
    val t = LocalThemeTokens.current
    Box(
        modifier.fillMaxSize().background(Brush.linearGradient(listOf(t.acc.copy(alpha = 0.55f), t.ink2.copy(alpha = 0.55f)))),
        contentAlignment = Alignment.Center
    ) {
        Icon(AppIcons.Songs, null, Modifier.size(28.dp), tint = t.onacc)
    }
}
