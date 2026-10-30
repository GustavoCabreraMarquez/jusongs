package com.swave.swave2000.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin

class SpectrumMathTest {
    private val rate = 44100
    private val n = SpectrumMath.FFT_SIZE

    private fun sine(hz: Double, amp: Float = 1f) = FloatArray(n) { (amp * sin(2 * PI * hz * it / rate)).toFloat() }

    private fun bandOf(hz: Float) = (0 until SpectrumMath.BANDS).first {
        val (lo, hi) = SpectrumMath.bandEdges(it, rate)
        hz >= lo && hz < hi
    }

    @Test fun silenceIsZero() {
        assertTrue(SpectrumMath.bands(FloatArray(n), rate).all { it == 0f })
    }

    @Test fun toneLightsItsBandAndNotTheFarOnes() {
        val levels = SpectrumMath.bands(sine(1000.0), rate)
        assertEquals(SpectrumMath.BANDS, levels.size)
        assertTrue("1 kHz band should be loud, was ${levels[bandOf(1000f)]}", levels[bandOf(1000f)] > 0.8f)
        assertTrue(levels.first() < 0.2f)
        assertTrue(levels.last() < 0.2f)
    }

    @Test fun quieterToneGivesLowerLevel() {
        val loud = SpectrumMath.bands(sine(1000.0, 1f), rate)[bandOf(1000f)]
        val quiet = SpectrumMath.bands(sine(1000.0, 0.05f), rate)[bandOf(1000f)]
        assertTrue(quiet < loud)
    }

    @Test fun bandEdgesAreContiguousAndLogSpaced() {
        for (b in 0 until SpectrumMath.BANDS - 1) {
            assertEquals(SpectrumMath.bandEdges(b, rate).second, SpectrumMath.bandEdges(b + 1, rate).first, 0.01f)
        }
        assertEquals(40f, SpectrumMath.bandEdges(0, rate).first, 0.01f)
        assertEquals(16000f, SpectrumMath.bandEdges(SpectrumMath.BANDS - 1, rate).second, 0.5f)
    }
}
