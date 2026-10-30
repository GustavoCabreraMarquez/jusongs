package com.swave.swave2000.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val contentUri: Uri,
    val artworkUri: Uri,
    val format: String = "MP3",
    val bitrate: String = "",
    val sampleRate: String = ""
)

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val artworkUri: Uri,
    val songCount: Int
)
