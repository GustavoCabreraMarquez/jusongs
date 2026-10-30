package com.swave.swave2000.components

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage

/** Album art loaded by Coil, with the gem as fallback while loading or when there is no art. */
@Composable
fun ArtworkImage(uri: Uri?, modifier: Modifier = Modifier, contentDescription: String? = null) {
    SubcomposeAsyncImage(
        model = uri,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        loading = { GemPlaceholder() },
        error = { GemPlaceholder() }
    )
}
