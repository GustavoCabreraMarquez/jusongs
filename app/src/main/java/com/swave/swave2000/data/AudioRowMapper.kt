package com.swave.swave2000.data

import android.provider.MediaStore.Audio.Media as M

/** Minimal row-reading surface so the mapping can be unit-tested without a real Cursor. */
interface AudioRows {
    fun next(): Boolean
    fun long(column: String): Long
    fun int(column: String): Int
    fun string(column: String): String?
}

data class AudioRow(
    val id: Long,
    val title: String?,
    val artist: String?,
    val album: String?,
    val albumId: Long,
    val durationMs: Long,
    val mimeType: String?,
    val displayName: String?,
    val isMusic: Boolean,
    val track: Int
)

data class AlbumSummary(val id: Long, val title: String, val artist: String, val songCount: Int)

object AudioRowMapper {
    const val UNKNOWN_ARTIST = "Unknown Artist"
    const val UNKNOWN_ALBUM = "Unknown Album"
    const val VARIOUS_ARTISTS = "Various Artists"
    private const val MEDIASTORE_UNKNOWN = "<unknown>"

    private val extensions = setOf("flac", "mp3", "wav", "m4a")
    private val mimes = setOf(
        "audio/flac", "audio/x-flac",
        "audio/mpeg", "audio/mp3",
        "audio/wav", "audio/x-wav", "audio/wave", "audio/vnd.wave",
        "audio/mp4", "audio/x-m4a", "audio/m4a", "audio/mp4a-latm"
    )

    val projection = arrayOf(
        M._ID, M.TITLE, M.ARTIST, M.ALBUM, M.ALBUM_ID, M.DURATION,
        M.MIME_TYPE, M.DISPLAY_NAME, M.IS_MUSIC, M.TRACK
    )

    fun read(rows: AudioRows): List<AudioRow> {
        val out = ArrayList<AudioRow>()
        while (rows.next()) {
            out += AudioRow(
                id = rows.long(M._ID),
                title = rows.string(M.TITLE),
                artist = rows.string(M.ARTIST),
                album = rows.string(M.ALBUM),
                albumId = rows.long(M.ALBUM_ID),
                durationMs = rows.long(M.DURATION),
                mimeType = rows.string(M.MIME_TYPE),
                displayName = rows.string(M.DISPLAY_NAME),
                isMusic = rows.int(M.IS_MUSIC) != 0,
                track = rows.int(M.TRACK)
            )
        }
        return out
    }

    private fun extensionOf(row: AudioRow): String =
        row.displayName?.substringAfterLast('.', "")?.lowercase().orEmpty()

    fun accept(row: AudioRow): Boolean =
        row.isMusic && (row.mimeType?.lowercase() in mimes || extensionOf(row) in extensions)

    /** Short container/codec tag shown in lists, derived from the file name or MIME type. */
    fun formatOf(row: AudioRow): String {
        val ext = extensionOf(row)
        if (ext in extensions) return ext.uppercase()
        val mime = row.mimeType?.lowercase().orEmpty()
        return when {
            "flac" in mime -> "FLAC"
            "mpeg" in mime || "mp3" in mime -> "MP3"
            "wav" in mime || "wave" in mime -> "WAV"
            else -> "M4A"
        }
    }

    fun cleanArtist(raw: String?): String =
        raw?.takeIf { it.isNotBlank() && it != MEDIASTORE_UNKNOWN } ?: UNKNOWN_ARTIST

    fun cleanAlbum(raw: String?): String =
        raw?.takeIf { it.isNotBlank() && it != MEDIASTORE_UNKNOWN } ?: UNKNOWN_ALBUM

    fun cleanTitle(row: AudioRow): String =
        row.title?.takeIf { it.isNotBlank() } ?: row.displayName?.substringBeforeLast('.') ?: "Untitled"

    fun summarizeAlbums(rows: List<AudioRow>): List<AlbumSummary> =
        rows.groupBy { it.albumId }.map { (albumId, group) ->
            val artists = group.map { cleanArtist(it.artist) }.distinct()
            AlbumSummary(
                id = albumId,
                title = cleanAlbum(group.first().album),
                artist = if (artists.size == 1) artists.first() else VARIOUS_ARTISTS,
                songCount = group.size
            )
        }
}
