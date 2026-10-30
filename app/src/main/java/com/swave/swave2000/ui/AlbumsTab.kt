package com.swave.swave2000.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swave.swave2000.components.glass
import com.swave.swave2000.data.LIBRARY_FOLDER
import com.swave.swave2000.model.Album
import com.swave.swave2000.theme.LocalThemeTokens

@Composable
fun AlbumsTab(albums: List<Album>, onOpen: (Album) -> Unit, modifier: Modifier = Modifier) {
    if (albums.isEmpty()) {
        EmptyLibrary(modifier)
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) { ScreenTitle("Albums") }
        items(albums, key = { it.id }) { album -> AlbumCard(album, onClick = { onOpen(album) }) }
    }
}

@Composable
fun EmptyLibrary(modifier: Modifier = Modifier) {
    val t = LocalThemeTokens.current
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Center) {
        Column(Modifier.glass(RoundedCornerShape(20.dp)).padding(20.dp)) {
            Text("No music found", style = MaterialTheme.typography.titleMedium, color = t.ink)
            Text(
                "Copy FLAC, MP3, WAV or M4A files into the $LIBRARY_FOLDER folder on your phone.",
                style = MaterialTheme.typography.bodyLarge,
                color = t.ink2,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
