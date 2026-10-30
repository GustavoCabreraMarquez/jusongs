package com.swave.swave2000.components

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.swave.swave2000.theme.AppTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/** Prototype artboard: every coordinate below lives in this 360x780 space, scaled "slice" (cover). */
internal const val ART_W = 360f
internal const val ART_H = 780f

/** Per-theme numbers copied from the `art(...)` function of `modern-time-of-day.html`. */
private class ArtSpec(
    val sky: IntArray,
    val sun: Int,
    val sunAlpha: Float,
    val ridges: IntArray,
    val beam: Int,
    val beamAlpha: Float,
    val ray: Int,
    val snowCaps: Boolean = false,
    val groundGlow: Boolean = false
)

private fun c(hex: Long) = Color(0xFF000000 or hex).toArgb()

private fun specOf(theme: AppTheme) = when (theme) {
    AppTheme.Morning -> ArtSpec(
        sky = intArrayOf(c(0xf9d9c0), c(0xfbe6d3), c(0xf3d6dc)),
        sun = c(0xffd9a0), sunAlpha = 0.8f,
        ridges = intArrayOf(c(0xd9b8c8), c(0xbda6c4), c(0x9fb0c4), c(0xb7c9b4)),
        beam = c(0xfff1c9), beamAlpha = 0.55f, ray = c(0xfff6e0)
    )
    AppTheme.Noon -> ArtSpec(
        sky = intArrayOf(c(0xf4d9a8), c(0xfdf0cf), c(0xfbe3c4)),
        sun = c(0xfffbe8), sunAlpha = 1f,
        ridges = intArrayOf(c(0xdcbfc4), c(0xc4adc2), c(0xa5b4c4), c(0xb9cbb0)),
        beam = c(0xfffbe6), beamAlpha = 0.9f, ray = c(0xffffff), groundGlow = true
    )
    AppTheme.Night -> ArtSpec(
        sky = intArrayOf(c(0x16204a), c(0x2b2f6e), c(0x4a3f86)),
        sun = c(0xdfe6ff), sunAlpha = 0.85f,
        ridges = intArrayOf(c(0x6d74b4), c(0x5a5fa0), c(0x46508c), c(0x2f3a6e)),
        beam = c(0xaab8ff), beamAlpha = 0.4f, ray = c(0xcfd8ff), snowCaps = true
    )
}

// Ridge polygons; the bottom edge sits at 800 (not 520/600/700/780) so displacement never exposes the sky below.
private val RIDGES = listOf(
    floatArrayOf(-40f, 400f, 70f, 270f, 130f, 340f, 200f, 240f, 290f, 360f, 400f, 300f, 400f, 520f, -40f, 520f),
    floatArrayOf(-40f, 470f, 40f, 380f, 120f, 450f, 220f, 350f, 330f, 450f, 400f, 410f, 400f, 600f, -40f, 600f),
    floatArrayOf(-40f, 540f, 90f, 460f, 180f, 530f, 280f, 450f, 400f, 520f, 400f, 700f, -40f, 700f)
)

// Snow caps drawn on the peaks at night.
private val SNOW_CAPS = listOf(
    floatArrayOf(70f, 270f, 48f, 296f, 66f, 292f, 78f, 304f, 96f, 290f, 130f, 340f, 110f, 300f, 94f, 306f),
    floatArrayOf(200f, 240f, 172f, 282f, 188f, 276f, 202f, 290f, 220f, 274f, 238f, 288f),
    floatArrayOf(290f, 360f, 262f, 392f, 282f, 386f, 296f, 398f, 318f, 380f, 330f, 392f, 314f, 366f),
    floatArrayOf(40f, 380f, 20f, 410f, 36f, 404f, 50f, 416f, 74f, 402f, 60f, 386f),
    floatArrayOf(220f, 350f, 196f, 382f, 214f, 378f, 228f, 390f, 246f, 376f)
)

/** Foreground hills: `M-40 640 Q100 560 200 620 T400 600 L400 780 L-40 780Z`, sampled into a polygon. */
private fun hills(): FloatArray {
    val pts = ArrayList<Float>()
    fun quad(x0: Float, y0: Float, cx: Float, cy: Float, x1: Float, y1: Float) {
        for (i in 0..20) {
            val t = i / 20f
            val u = 1 - t
            pts += u * u * x0 + 2 * u * t * cx + t * t * x1
            pts += u * u * y0 + 2 * u * t * cy + t * t * y1
        }
    }
    quad(-40f, 640f, 100f, 560f, 200f, 620f)
    quad(200f, 620f, 300f, 680f, 400f, 600f) // T: control reflected through (200, 620)
    pts += listOf(400f, 800f, -40f, 800f)
    return pts.toFloatArray()
}

private fun circle(cx: Float, cy: Float, r: Float): FloatArray {
    val out = FloatArray(96)
    for (i in 0 until 48) {
        val a = i / 48f * 2 * PI.toFloat()
        out[i * 2] = cx + cos(a) * r
        out[i * 2 + 1] = cy + sin(a) * r
    }
    return out
}

// ---- Value noise: stand-in for feTurbulence (fractalNoise) -------------------------------------------------------

private fun hash(ix: Int, iy: Int, seed: Int): Float {
    var h = ix * 374761393 + iy * 668265263 + seed * 1274126177
    h = (h xor (h ushr 13)) * 1274126177
    return ((h xor (h ushr 16)) and 0xFFFF) / 65535f
}

private fun valueNoise(x: Float, y: Float, seed: Int): Float {
    val x0 = floor(x).toInt()
    val y0 = floor(y).toInt()
    val fx = x - x0
    val fy = y - y0
    val sx = fx * fx * (3 - 2 * fx)
    val sy = fy * fy * (3 - 2 * fy)
    val a = hash(x0, y0, seed)
    val b = hash(x0 + 1, y0, seed)
    val cc = hash(x0, y0 + 1, seed)
    val d = hash(x0 + 1, y0 + 1, seed)
    return (a + (b - a) * sx) + ((cc + (d - cc) * sx) - (a + (b - a) * sx)) * sy
}

/** 3-octave fractal noise in 0..1, base frequency 0.035 like the prototype's filter. */
private fun fbm(x: Float, y: Float, seed: Int): Float {
    var sum = 0f
    var amp = 1f
    var freq = 0.035f
    var norm = 0f
    for (o in 0 until 3) {
        sum += valueNoise(x * freq, y * freq, seed + o * 31) * amp
        norm += amp
        amp *= 0.5f
        freq *= 2f
    }
    return sum / norm
}

/** feDisplacementMap equivalent: subdivide edges and push each point by `scale * (noise - 0.5)`. */
private fun displacedPath(poly: FloatArray, scale: Float): Path {
    val path = Path()
    val n = poly.size / 2
    var first = true
    fun add(x: Float, y: Float) {
        val px = x + scale * (fbm(x, y, 4) - 0.5f)
        val py = y + scale * (fbm(x, y, 9) - 0.5f)
        if (first) path.moveTo(px, py) else path.lineTo(px, py)
        first = false
    }
    for (i in 0 until n) {
        val x0 = poly[i * 2]
        val y0 = poly[i * 2 + 1]
        val j = (i + 1) % n
        val x1 = poly[j * 2]
        val y1 = poly[j * 2 + 1]
        val steps = max(1, (hypot(x1 - x0, y1 - y0) / 6f).toInt())
        for (s in 0 until steps) {
            val t = s / steps.toFloat()
            add(x0 + (x1 - x0) * t, y0 + (y1 - y0) * t)
        }
    }
    path.close()
    return path
}

// ---- Rendering ---------------------------------------------------------------------------------------------------

/** Cover-fit ("slice") transform of the 360x780 art into a w x h surface. */
internal class ArtTransform(w: Float, h: Float) {
    val scale = max(w / ART_W, h / ART_H)
    val tx = (w - ART_W * scale) / 2f
    val ty = (h - ART_H * scale) / 2f
}

/**
 * Renders the static scenery of [theme] into a bitmap of [width] x [height] px. Runs on a background thread.
 * Oil-paint approximation: wobbly edges from a noise displacement, short brush strokes clipped to each ridge,
 * and a tiled grain overlay.
 */
internal fun renderArt(theme: AppTheme, width: Int, height: Int): Bitmap {
    val spec = specOf(theme)
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val tf = ArtTransform(width.toFloat(), height.toFloat())
    canvas.save()
    canvas.translate(tf.tx, tf.ty)
    canvas.scale(tf.scale, tf.scale)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Sky
    paint.shader = LinearGradient(0f, 0f, 0f, ART_H, spec.sky, floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
    canvas.drawRect(-ART_W, -ART_H, ART_W * 2, ART_H * 2, paint)
    paint.shader = null

    // Sun / moon
    paint.color = spec.sun
    paint.alpha = (spec.sunAlpha * 255).toInt()
    canvas.drawPath(displacedPath(circle(270f, 190f, 60f), 14f), paint)

    // Ridges: back to front, each with brush strokes
    val rnd = Random(11)
    val layers = RIDGES + listOf(hills())
    layers.forEachIndexed { i, polygon ->
        val path = displacedPath(polygon, 14f)
        paint.color = spec.ridges[i]
        paint.alpha = 255
        canvas.drawPath(path, paint)
        canvas.save()
        canvas.clipPath(path)
        brushStrokes(canvas, spec.ridges[i], rnd, if (i == 3) 150 else 90)
        canvas.restore()
    }

    if (spec.snowCaps) {
        paint.color = c(0xf4f7ff)
        for (cap in SNOW_CAPS) canvas.drawPath(displacedPath(cap, 14f), paint)
    }

    // Window light: a soft beam (blur stdDeviation 14) plus, at noon, the glow where it lands on the floor
    val beamBlur = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        maskFilter = BlurMaskFilter(blurRadiusFor(14f * tf.scale), BlurMaskFilter.Blur.NORMAL)
    }
    beamBlur.color = spec.beam
    beamBlur.alpha = (spec.beamAlpha * 255).toInt()
    canvas.drawPath(Path().apply { moveTo(360f, 0f); lineTo(120f, 780f); lineTo(360f, 780f); close() }, beamBlur)
    if (spec.groundGlow) {
        beamBlur.color = c(0xfff7d6)
        beamBlur.alpha = (0.55f * 255).toInt()
        canvas.drawOval(290f - 120f, 770f - 40f, 290f + 120f, 770f + 40f, beamBlur)
        beamBlur.alpha = (0.25f * 255).toInt()
        canvas.drawOval(330f - 60f, 700f - 90f, 330f + 60f, 700f + 90f, beamBlur)
    }

    // Two thin rays
    val ray = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = spec.ray
        alpha = (0.35f * 255).toInt()
    }
    canvas.drawLine(200f, -10f, -20f, 560f, ray)
    canvas.drawLine(310f, -10f, 90f, 560f, ray)
    canvas.restore()

    grain(canvas, width, height, strength = if (theme == AppTheme.Night) 30 else 22)
    return bmp
}

/** Android's BlurMaskFilter radius for a given gaussian sigma (sigma = 0.57735 * radius + 0.5). */
private fun blurRadiusFor(sigmaPx: Float) = max(1f, (sigmaPx - 0.5f) / 0.57735f)

private fun brushStrokes(canvas: Canvas, base: Int, rnd: Random, count: Int) {
    val light = ColorUtils.setAlphaComponent(ColorUtils.blendARGB(base, 0xFFFFFFFF.toInt(), 0.28f), 34)
    val dark = ColorUtils.setAlphaComponent(ColorUtils.blendARGB(base, 0xFF000000.toInt(), 0.22f), 30)
    val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND }
    repeat(count) {
        val x = rnd.nextFloat() * (ART_W + 80) - 40
        val y = 230f + rnd.nextFloat() * 580f
        val len = 18f + rnd.nextFloat() * 36f
        val ang = (-38f + rnd.nextFloat() * 24f) * PI.toFloat() / 180f
        p.color = if (rnd.nextBoolean()) light else dark
        p.strokeWidth = 5f + rnd.nextFloat() * 9f
        canvas.drawLine(x, y, x + cos(ang) * len, y + sin(ang) * len, p)
    }
}

/** Fine paper grain: a small noise tile repeated over the whole surface with an overlay blend. */
private fun grain(canvas: Canvas, width: Int, height: Int, strength: Int) {
    val size = 96
    val tile = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val rnd = Random(5)
    val px = IntArray(size * size) {
        val g = 96 + rnd.nextInt(64)
        0xFF000000.toInt() or (g shl 16) or (g shl 8) or g
    }
    tile.setPixels(px, 0, size, 0, 0, size, size)
    val p = Paint().apply {
        shader = BitmapShader(tile, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        xfermode = PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
        alpha = strength
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), p)
    tile.recycle()
}

/** Downscale by [factor] and box-blur twice: a cheap stand-in for `backdrop-filter: blur()` behind glass bars. */
internal fun blurredCopy(src: Bitmap, factor: Int = 8, radius: Int = 3): Bitmap {
    val w = max(1, src.width / factor)
    val h = max(1, src.height / factor)
    val small = Bitmap.createScaledBitmap(src, w, h, true)
    val px = IntArray(w * h)
    small.getPixels(px, 0, w, 0, 0, w, h)
    val tmp = IntArray(w * h)
    repeat(2) {
        boxPass(px, tmp, w, h, radius, horizontal = true)
        boxPass(tmp, px, w, h, radius, horizontal = false)
    }
    small.setPixels(px, 0, w, 0, 0, w, h)
    return small
}

private fun boxPass(src: IntArray, dst: IntArray, w: Int, h: Int, r: Int, horizontal: Boolean) {
    val lines = if (horizontal) h else w
    val len = if (horizontal) w else h
    for (line in 0 until lines) {
        for (i in 0 until len) {
            var a = 0; var rr = 0; var g = 0; var b = 0; var n = 0
            for (k in max(0, i - r)..min(len - 1, i + r)) {
                val v = if (horizontal) src[line * w + k] else src[k * w + line]
                a += v ushr 24; rr += (v shr 16) and 0xFF; g += (v shr 8) and 0xFF; b += v and 0xFF; n++
            }
            val out = ((a / n) shl 24) or ((rr / n) shl 16) or ((g / n) shl 8) or (b / n)
            if (horizontal) dst[line * w + i] = out else dst[i * w + line] = out
        }
    }
}
