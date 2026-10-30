package com.swave.swave2000.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swave.swave2000.components.ArtworkImage
import com.swave.swave2000.components.BevelType
import com.swave.swave2000.components.BeveledContainer
import com.swave.swave2000.data.LIBRARY_FOLDER
import com.swave.swave2000.model.Album
import com.swave.swave2000.theme.*

@Composable
fun AlbumsTab(albums: List<Album>, onOpen: (Album) -> Unit, modifier: Modifier = Modifier) {
    if (albums.isEmpty()) {
        EmptyLibrary(modifier)
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(albums, key = { it.id }) { album ->
            Column(Modifier.clickable { onOpen(album) }) {
                BeveledContainer(Modifier.fillMaxWidth().aspectRatio(1f), type = BevelType.Recessed, backgroundColor = Y2KSageGreenDark) {
                    ArtworkImage(album.artworkUri, Modifier.fillMaxSize(), album.title)
                }
                Text(
                    album.title,
                    modifier = Modifier.padding(top = 6.dp),
                    fontWeight = FontWeight.Bold,
                    color = Y2KTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    album.artist,
                    color = Y2KTextSecondary,
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun EmptyLibrary(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("NO MUSIC FOUND", fontWeight = FontWeight.Bold, color = Y2KTextPrimary)
        Text(
            "Copy FLAC, MP3, WAV or M4A files into the $LIBRARY_FOLDER folder on your phone.",
            color = Y2KTextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
