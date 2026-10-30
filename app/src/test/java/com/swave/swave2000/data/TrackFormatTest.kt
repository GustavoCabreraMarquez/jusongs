package com.swave.swave2000.data

import org.junit.Assert.assertEquals
import org.junit.Test

class TrackFormatTest {
    @Test fun losslessShowsBitDepthAndRate() =
        assertEquals("FLAC 24-bit 96kHz", TrackFormat("FLAC", 24, 96000, 2800, true).label())

    @Test fun lossyShowsBitrateAndFractionalRate() =
        assertEquals("MP3 320kbps 44.1kHz", TrackFormat("MP3", null, 44100, 320, false).label())

    @Test fun missingFieldsAreOmitted() {
        assertEquals("WAV", TrackFormat("WAV", null, null, null, true).label())
        assertEquals("M4A 48kHz", TrackFormat("M4A", null, 48000, null, false).label())
    }
}
