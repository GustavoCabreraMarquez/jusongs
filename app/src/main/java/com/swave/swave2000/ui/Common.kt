package com.swave.swave2000.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swave.swave2000.components.ArtworkImage
import com.swave.swave2000.components.glass
import com.swave.swave2000.model.Album
import com.swave.swave2000.theme.LocalThemeTokens

/** Large screen title with an optional muted subtitle. */
@Composable
fun ScreenTitle(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    val t = LocalThemeTokens.current
    Column(modifier.fillMaxWidth().padding(top = 16.dp, bottom = 14.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = t.ink)
        if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = t.ink2, modifier = Modifier.padding(top = 2.dp))
    }
}

/** Album tile on a glass card, shared by Home and Albums. */
@Composable
fun AlbumCard(album: Album, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val t = LocalThemeTokens.current
    Column(modifier.glass(RoundedCornerShape(18.dp)).clickable(onClick = onClick).padding(8.dp)) {
        ArtworkImage(album.artworkUri, Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp)), album.title)
        Text(
            album.title,
            style = MaterialTheme.typography.bodyMedium,
            color = t.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp, start = 2.dp)
        )
        Text(
            album.artist,
            style = MaterialTheme.typography.bodySmall,
            color = t.ink2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
        )
    }
}

fun plural(n: Int, word: String) = if (n == 1) "1 $word" else "$n ${word}s"
