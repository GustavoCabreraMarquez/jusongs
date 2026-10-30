package com.swave.swave2000.data

import android.provider.MediaStore.Audio.Media as M
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private class FakeRows(private val rows: List<Map<String, Any?>>) : AudioRows {
    private var i = -1
    override fun next() = ++i < rows.size
    override fun long(column: String) = (rows[i][column] as Number).toLong()
    override fun int(column: String) = (rows[i][column] as Number).toInt()
    override fun string(column: String) = rows[i][column] as String?
}

private fun row(
    id: Long, title: String?, artist: String?, album: String?, albumId: Long,
    mime: String?, name: String?, music: Int = 1, track: Int = 1
) = mapOf(
    M._ID to id, M.TITLE to title, M.ARTIST to artist, M.ALBUM to album, M.ALBUM_ID to albumId,
    M.DURATION to 180_000L, M.MIME_TYPE to mime, M.DISPLAY_NAME to name, M.IS_MUSIC to music, M.TRACK to track
)

class AudioRowMapperTest {
    private val rows = AudioRowMapper.read(
        FakeRows(
            listOf(
                row(1, "One", "A", "Alb", 10, "audio/flac", "one.flac"),
                row(2, "Two", "B", "Alb", 10, "audio/mpeg", "two.mp3"),
                row(3, "Ring", "A", "Tones", 11, "audio/mpeg", "ring.mp3", music = 0),
                row(4, "Vorbis", "A", "Alb2", 12, "audio/ogg", "v.ogg"),
                row(5, "Odd", "<unknown>", "<unknown>", 13, "application/octet-stream", "odd.m4a"),
                row(6, null, null, null, 13, "audio/x-wav", "raw_take.WAV")
            )
        )
    )

    @Test fun readsAllRows() = assertEquals(6, rows.size)

    @Test fun filtersByMusicFlagMimeAndExtension() {
        assertEquals(listOf(1L, 2L, 5L, 6L), rows.filter(AudioRowMapper::accept).map { it.id })
    }

    @Test fun formatTagFromExtensionThenMime() {
        assertEquals("FLAC", AudioRowMapper.formatOf(rows[0]))
        assertEquals("MP3", AudioRowMapper.formatOf(rows[1]))
        assertEquals("M4A", AudioRowMapper.formatOf(rows[4]))
        assertEquals("WAV", AudioRowMapper.formatOf(rows[5]))
    }

    @Test fun cleansUnknownValues() {
        assertEquals(AudioRowMapper.UNKNOWN_ARTIST, AudioRowMapper.cleanArtist(rows[4].artist))
        assertEquals(AudioRowMapper.UNKNOWN_ALBUM, AudioRowMapper.cleanAlbum(rows[4].album))
        assertEquals("raw_take", AudioRowMapper.cleanTitle(rows[5]))
        assertFalse(AudioRowMapper.accept(rows[3]))
    }

    @Test fun groupsAlbums() {
        val albums = AudioRowMapper.summarizeAlbums(rows.filter(AudioRowMapper::accept)).associateBy { it.id }
        assertEquals(2, albums[10]!!.songCount)
        assertEquals(AudioRowMapper.VARIOUS_ARTISTS, albums[10]!!.artist)
        assertEquals(AudioRowMapper.UNKNOWN_ARTIST, albums[13]!!.artist)
        assertTrue(albums.keys == setOf(10L, 13L))
    }
}
