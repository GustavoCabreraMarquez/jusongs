package com.swave.swave2000.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swave.swave2000.components.AppIcons
import com.swave.swave2000.components.ArtworkImage
import com.swave.swave2000.components.ButtonLabel
import com.swave.swave2000.components.CircleButton
import com.swave.swave2000.components.TactileButton
import com.swave.swave2000.components.glass
import com.swave.swave2000.data.Library
import com.swave.swave2000.model.Album
import com.swave.swave2000.theme.LocalThemeTokens

/**
 * Home: big title, a "hero" card with the current track (or a shuffle prompt when nothing is queued)
 * and a 2-column album grid. [current] / [onOpenNowPlaying] / [onTogglePlay] feed the hero card.
 */
@Composable
fun HomeTab(
    library: Library,
    onShuffleAll: () -> Unit,
    onOpenAlbum: (Album) -> Unit,
    modifier: Modifier = Modifier,
    current: PlayerUiState = PlayerUiState(),
    onOpenNowPlaying: () -> Unit = {},
    onTogglePlay: () -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    if (library.songs.isEmpty()) {
        EmptyLibrary(modifier, onRefresh)
        return
    }
    val t = LocalThemeTokens.current
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            ScreenTitle("Good day", "${plural(library.songs.size, "song")}  ·  ${plural(library.albums.size, "album")}")
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            if (current.hasQueue) {
                HeroCard(
                    cover = { ArtworkImage(current.artworkUri, Modifier.size(64.dp).clip(RoundedCornerShape(14.dp)), current.title) },
                    title = current.title,
                    subtitle = current.artist,
                    onClick = onOpenNowPlaying,
                    action = {
                        CircleButton(
                            if (current.isPlaying) AppIcons.Pause else AppIcons.Play,
                            if (current.isPlaying) "Pause" else "Play",
                            onTogglePlay, solid = true
                        )
                    }
                )
            } else {
                HeroCard(
                    cover = {
                        library.albums.firstOrNull()?.let { ArtworkImage(it.artworkUri, Modifier.size(64.dp).clip(RoundedCornerShape(14.dp)), it.title) }
                    },
                    title = "Shuffle all",
                    subtitle = plural(library.songs.size, "song"),
                    onClick = onShuffleAll,
                    action = { CircleButton(AppIcons.Shuffle, "Shuffle all", onShuffleAll, solid = true) }
                )
            }
        }
        if (current.hasQueue) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TactileButton(onClick = onShuffleAll) { ButtonLabel("Shuffle all") }
                }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Your albums", style = MaterialTheme.typography.titleMedium, color = t.ink, modifier = Modifier.weight(1f))
                TactileButton(onClick = onRefresh) { ButtonLabel("Refresh library") }
            }
        }
        items(library.albums, key = { it.id }) { album ->
            AlbumCard(album, onClick = { onOpenAlbum(album) })
        }
    }
}

@Composable
private fun HeroCard(
    cover: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    action: @Composable () -> Unit
) {
    val t = LocalThemeTokens.current
    Row(
        Modifier.fillMaxWidth().glass().clickable(onClick = onClick).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        cover()
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = t.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = t.ink2, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        action()
    }
}

