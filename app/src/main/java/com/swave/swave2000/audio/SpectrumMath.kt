package com.swave.swave2000.audio

import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/** Pure FFT / band maths for the spectrum display, kept free of Android types for testing. */
object SpectrumMath {
    const val FFT_SIZE = 1024
    const val BANDS = 16
    private const val MIN_HZ = 40f
    private const val MAX_HZ = 16000f
    private const val FLOOR_DB = 60f

    /** Lower and upper edge in Hz of [band], log-spaced between 40 Hz and 16 kHz (or Nyquist). */
    fun bandEdges(band: Int, sampleRate: Int, bandCount: Int = BANDS): Pair<Float, Float> {
        val maxHz = min(MAX_HZ, sampleRate / 2f)
        val ratio = maxHz / MIN_HZ
        return MIN_HZ * ratio.pow(band / bandCount.toFloat()) to MIN_HZ * ratio.pow((band + 1) / bandCount.toFloat())
    }

    /** Returns [bandCount] levels in 0..1 for a block of mono samples (size must be a power of two). */
    fun bands(samples: FloatArray, sampleRate: Int, bandCount: Int = BANDS): FloatArray {
        val n = samples.size
        require(n > 0 && n and (n - 1) == 0) { "size must be a power of two" }
        val re = FloatArray(n) { samples[it] * (0.5f - 0.5f * cos(2.0 * PI * it / (n - 1)).toFloat()) }
        val im = FloatArray(n)
        fft(re, im)

        val half = n / 2
        // A full-scale sine under a Hann window peaks at n/4 in its bin.
        val scale = n / 4f
        val mags = FloatArray(half) { sqrt(re[it] * re[it] + im[it] * im[it]) / scale }

        return FloatArray(bandCount) { b ->
            val (lo, hi) = bandEdges(b, sampleRate, bandCount)
            val loBin = max(1, floor(lo * n / sampleRate).toInt())
            val hiBin = min(half - 1, max(loBin, ceil(hi * n / sampleRate).toInt()))
            var peak = 0f
            for (k in loBin..hiBin) peak = max(peak, mags[k])
            val db = 20f * log10(peak + 1e-9f)
            ((db + FLOOR_DB) / FLOOR_DB).coerceIn(0f, 1f)
        }
    }

    /** In-place iterative radix-2 FFT. */
    fun fft(re: FloatArray, im: FloatArray) {
        val n = re.size
        var j = 0
        for (i in 1 until n) {
            var bit = n shr 1
            while (j and bit != 0) { j = j xor bit; bit = bit shr 1 }
            j = j xor bit
            if (i < j) {
                val tr = re[i]; re[i] = re[j]; re[j] = tr
                val ti = im[i]; im[i] = im[j]; im[j] = ti
            }
        }
        var len = 2
        while (len <= n) {
            val ang = -2.0 * PI / len
            val wr = cos(ang).toFloat()
            val wi = kotlin.math.sin(ang).toFloat()
            var i = 0
            while (i < n) {
                var cr = 1f
                var ci = 0f
                for (k in 0 until len / 2) {
                    val a = i + k
                    val b = a + len / 2
                    val xr = re[b] * cr - im[b] * ci
                    val xi = re[b] * ci + im[b] * cr
                    re[b] = re[a] - xr; im[b] = im[a] - xi
                    re[a] += xr; im[a] += xi
                    val nr = cr * wr - ci * wi
                    ci = cr * wi + ci * wr
                    cr = nr
                }
                i += len
            }
            len = len shl 1
        }
    }
}
