package com.swave.swave2000.data

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.swave.swave2000.model.Album
import com.swave.swave2000.model.Song
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers

data class Library(
    val songs: List<Song>,
    val albums: List<Album>,
    val albumSongs: Map<Long, List<Song>>
) {
    companion object {
        val EMPTY = Library(emptyList(), emptyList(), emptyMap())
    }
}

/** Only audio under <storage>/Music/Jusongs is part of the library. */
const val LIBRARY_FOLDER = "Music/Jusongs"

class MediaRepository(private val context: Context) {

    private val collection: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    private val albumArtBase: Uri = Uri.parse("content://media/external/audio/albumart")

    /** Emits the library now and again whenever MediaStore reports a change. */
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun libraryFlow(): Flow<Library> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                trySend(Unit)
            }
        }
        context.contentResolver.registerContentObserver(collection, true, observer)
        trySend(Unit)
        awaitClose { context.contentResolver.unregisterContentObserver(observer) }
    }
        .conflate()
        .debounce(500)
        .map { load() }
        .flowOn(Dispatchers.IO)

    /** Best-effort creation of the reserved folder so it shows up in file managers / USB transfer. */
    fun ensureLibraryFolder() {
        runCatching {
            @Suppress("DEPRECATION")
            java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "Jusongs").mkdirs()
        }
    }

    fun load(): Library {
        val rows = context.contentResolver.query(
            collection,
            AudioRowMapper.projection,
            "${MediaStore.Audio.Media.DATA} LIKE ?",
            arrayOf("%/${LIBRARY_FOLDER}/%"),
            null
        )?.use { AudioRowMapper.read(CursorRows(it)) }.orEmpty()
        return build(rows)
    }

    private fun build(allRows: List<AudioRow>): Library {
        val rows = allRows.filter(AudioRowMapper::accept)
        val pairs = rows.map { it to toSong(it) }
        val songs = pairs.map { it.second }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })
        val albumSongs = pairs.groupBy({ it.first.albumId }, { it })
            .mapValues { (_, group) ->
                group.sortedWith(
                    compareBy<Pair<AudioRow, Song>>({ it.first.track }, { it.second.title.lowercase() })
                ).map { it.second }
            }
        val albums = AudioRowMapper.summarizeAlbums(rows)
            .map { Album(it.id, it.title, it.artist, artworkFor(it.id), it.songCount) }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })
        return Library(songs, albums, albumSongs)
    }

    private fun artworkFor(albumId: Long): Uri = ContentUris.withAppendedId(albumArtBase, albumId)

    private fun toSong(row: AudioRow) = Song(
        id = row.id,
        title = AudioRowMapper.cleanTitle(row),
        artist = AudioRowMapper.cleanArtist(row.artist),
        album = AudioRowMapper.cleanAlbum(row.album),
        albumId = row.albumId,
        duration = row.durationMs,
        contentUri = ContentUris.withAppendedId(collection, row.id),
        artworkUri = artworkFor(row.albumId),
        format = AudioRowMapper.formatOf(row)
    )
}

private class CursorRows(private val c: Cursor) : AudioRows {
    override fun next() = c.moveToNext()
    override fun long(column: String) = c.getLong(c.getColumnIndexOrThrow(column))
    override fun int(column: String) = c.getInt(c.getColumnIndexOrThrow(column))
    override fun string(column: String): String? =
        c.getColumnIndexOrThrow(column).let { if (c.isNull(it)) null else c.getString(it) }
}
