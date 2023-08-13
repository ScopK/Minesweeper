package org.oar.minesweeper.skins

import android.content.Context
import android.graphics.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import org.oar.minesweeper.skins.model.BitmapWrapper
import org.oar.minesweeper.skins.model.SkinSet
import kotlin.math.cos
import kotlin.math.sin


abstract class Skin {

    companion object {
        const val FLAG = 0
        const val FLAG_OK = 1
        const val FLAG_FAIL = -1
    }

    open val visualHelp = false
    protected open val useEmptyTileWhenUnhelpful = false
    open val acceptsHue = true

    open val defaultTileSize = 128

    protected open val numberOfCovers = 1
    protected open val coverW = 128
    protected open val coverH = 128

    @get:DrawableRes
    abstract val resource: Int
    @get:ColorRes
    abstract var backgroundColor: Int
    abstract var name: String

    private lateinit var set: SkinSet
    private lateinit var emptyBitmap: Bitmap

    var loaded = false
        private set

    var coverHue: Int = 0
        set(value) {
            if (loaded) throw RuntimeException("Cannot change hue of a loaded skin")
            field = value
        }

    fun load(context: Context) {
        loaded = true
        val bitmap = context.getBitmap(resource)
        val coloredBitmap = bitmap.hue(coverHue)

        emptyBitmap = Bitmap.createBitmap(defaultTileSize, defaultTileSize, bitmap.config)

        val lastRowY = defaultTileSize * 4

        val covers = (0 until numberOfCovers)
            .map { i -> coloredBitmap.sub(i * coverW, lastRowY, coverW, coverH) }
            .map { it.bitmapWrapper }

        set = SkinSet(
            bitmap.sub(defaultTileSize * 10, lastRowY).bitmapWrapper,
            covers,
            bitmap.sub(defaultTileSize * 7, lastRowY).bitmapWrapper,
            bitmap.sub(defaultTileSize * 8, lastRowY).bitmapWrapper,
            bitmap.sub(defaultTileSize * 9, lastRowY).bitmapWrapper,
            bitmap.sub(defaultTileSize * 11, lastRowY).bitmapWrapper,
            bitmap.sub(defaultTileSize * 12, lastRowY).bitmapWrapper,
            loadNumbers(bitmap)
        )
    }

    fun drawCover(canvas: Canvas, x: Float, y: Float, coverNumber: Int) {
        val i = coverNumber % numberOfCovers
        set.covers[i].drawBitmap(canvas, x, y)
    }

    fun drawEmpty(canvas: Canvas, x: Float, y: Float) {
        set.empty.drawBitmap(canvas, x, y)
    }

    fun drawNumber(
        canvas: Canvas,
        x: Float,
        y: Float,
        numValue: Int,
        numMarked: Int,
        visualHelp: Boolean = this.visualHelp
    ) {
        drawEmpty(canvas, x, y)

        val pair = if (this.visualHelp && visualHelp) {
            Pair(numValue, if (numMarked > numValue + 1) numValue + 1 else numMarked)
        } else if (useEmptyTileWhenUnhelpful) {
            Pair(numValue, 0)
        } else {
            Pair(numValue, numValue)
        }

        set.numbers[pair]!!.drawBitmap(canvas, x, y)
    }

    fun drawBomb(canvas: Canvas, x: Float, y: Float, coverNumber: Int, endBomb: Boolean = false) {
        if (endBomb) {
            drawEmpty(canvas, x, y)
            set.bombEnd.drawBitmap(canvas, x, y)
        } else {
            drawCover(canvas, x, y, coverNumber)
            set.bomb.drawBitmap(canvas, x, y)
        }
    }

    fun drawFlag(canvas: Canvas, x: Float, y: Float, coverNumber: Int, value: Int = FLAG) {
        drawCover(canvas, x, y, coverNumber)
        val bitmap = when(value) {
            FLAG_OK -> set.flagOk
            FLAG_FAIL -> set.flagFail
            else -> set.flag
        }
        bitmap.drawBitmap(canvas, x, y)
    }

    private fun loadNumbers(bitmap: Bitmap): Map<Pair<Int, Int>, BitmapWrapper> {
        return mapOf(
            Pair(1, 2) to bitmap.sub(0, 0),
            Pair(1, 1) to bitmap.sub(defaultTileSize, 0),
            Pair(1, 0) to bitmap.sub(defaultTileSize * 2, 0),
            Pair(8, 0) to bitmap.sub(defaultTileSize * 3, 0),
            Pair(8, 1) to bitmap.sub(defaultTileSize * 4, 0),
            Pair(8, 2) to bitmap.sub(defaultTileSize * 5, 0),
            Pair(8, 3) to bitmap.sub(defaultTileSize * 6, 0),
            Pair(8, 4) to bitmap.sub(defaultTileSize * 7, 0),
            Pair(8, 5) to bitmap.sub(defaultTileSize * 8, 0),
            Pair(8, 6) to bitmap.sub(defaultTileSize * 9, 0),
            Pair(8, 7) to bitmap.sub(defaultTileSize * 10, 0),
            Pair(8, 8) to bitmap.sub(defaultTileSize * 11, 0),
            Pair(8, 9) to bitmap.sub(defaultTileSize * 12, 0),

            Pair(2, 3) to bitmap.sub(0, defaultTileSize),
            Pair(2, 2) to bitmap.sub(defaultTileSize, defaultTileSize),
            Pair(2, 1) to bitmap.sub(defaultTileSize * 2, defaultTileSize),
            Pair(2, 0) to bitmap.sub(defaultTileSize * 3, defaultTileSize),
            Pair(7, 0) to bitmap.sub(defaultTileSize * 4, defaultTileSize),
            Pair(7, 1) to bitmap.sub(defaultTileSize * 5, defaultTileSize),
            Pair(7, 2) to bitmap.sub(defaultTileSize * 6, defaultTileSize),
            Pair(7, 3) to bitmap.sub(defaultTileSize * 7, defaultTileSize),
            Pair(7, 4) to bitmap.sub(defaultTileSize * 8, defaultTileSize),
            Pair(7, 5) to bitmap.sub(defaultTileSize * 9, defaultTileSize),
            Pair(7, 6) to bitmap.sub(defaultTileSize * 10, defaultTileSize),
            Pair(7, 7) to bitmap.sub(defaultTileSize * 11, defaultTileSize),
            Pair(7, 8) to bitmap.sub(defaultTileSize * 12, defaultTileSize),

            Pair(3, 4) to bitmap.sub(0, defaultTileSize * 2),
            Pair(3, 3) to bitmap.sub(defaultTileSize, defaultTileSize * 2),
            Pair(3, 2) to bitmap.sub(defaultTileSize * 2, defaultTileSize * 2),
            Pair(3, 1) to bitmap.sub(defaultTileSize * 3, defaultTileSize * 2),
            Pair(3, 0) to bitmap.sub(defaultTileSize * 4, defaultTileSize * 2),
            Pair(6, 0) to bitmap.sub(defaultTileSize * 5, defaultTileSize * 2),
            Pair(6, 1) to bitmap.sub(defaultTileSize * 6, defaultTileSize * 2),
            Pair(6, 2) to bitmap.sub(defaultTileSize * 7, defaultTileSize * 2),
            Pair(6, 3) to bitmap.sub(defaultTileSize * 8, defaultTileSize * 2),
            Pair(6, 4) to bitmap.sub(defaultTileSize * 9, defaultTileSize * 2),
            Pair(6, 5) to bitmap.sub(defaultTileSize * 10, defaultTileSize * 2),
            Pair(6, 6) to bitmap.sub(defaultTileSize * 11, defaultTileSize * 2),
            Pair(6, 7) to bitmap.sub(defaultTileSize * 12, defaultTileSize * 2),

            Pair(4, 5) to bitmap.sub(0, defaultTileSize * 3),
            Pair(4, 4) to bitmap.sub(defaultTileSize, defaultTileSize * 3),
            Pair(4, 3) to bitmap.sub(defaultTileSize * 2, defaultTileSize * 3),
            Pair(4, 2) to bitmap.sub(defaultTileSize * 3, defaultTileSize * 3),
            Pair(4, 1) to bitmap.sub(defaultTileSize * 4, defaultTileSize * 3),
            Pair(4, 0) to bitmap.sub(defaultTileSize * 5, defaultTileSize * 3),
            Pair(5, 0) to bitmap.sub(defaultTileSize * 6, defaultTileSize * 3),
            Pair(5, 1) to bitmap.sub(defaultTileSize * 7, defaultTileSize * 3),
            Pair(5, 2) to bitmap.sub(defaultTileSize * 8, defaultTileSize * 3),
            Pair(5, 3) to bitmap.sub(defaultTileSize * 9, defaultTileSize * 3),
            Pair(5, 4) to bitmap.sub(defaultTileSize * 10, defaultTileSize * 3),
            Pair(5, 5) to bitmap.sub(defaultTileSize * 11, defaultTileSize * 3),
            Pair(5, 6) to bitmap.sub(defaultTileSize * 12, defaultTileSize * 3),
        ).mapValues { it.value.bitmapWrapper }
    }

    private val Bitmap.bitmapWrapper: BitmapWrapper
        get() = BitmapWrapper(this, emptyBitmap.sameAs(this))

    private fun Bitmap.sub(
            x: Int,
            y: Int,
            width: Int = defaultTileSize,
            height: Int = defaultTileSize
        ) = Bitmap.createBitmap(this, x, y, width, height)


    private fun Bitmap.hue(hue: Int): Bitmap {
        if (!acceptsHue) {
            return this
        }
        return hueToColorMatrix(hue)
            ?.let {
                val paint = Paint()
                paint.colorFilter = ColorMatrixColorFilter(it)

                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGBA_F16)
                val canvas = Canvas(bitmap)
                canvas.drawBitmap(this, 0f, 0f, paint)

                return bitmap
            }
            ?: this
    }


    private fun hueToColorMatrix(hue: Int): ColorMatrix? {
        val limitedHue = hue % 360
        if (limitedHue == 0) {
            return null
        }

        val value = limitedHue / 180f * Math.PI.toFloat()
        val cosVal = cos(value.toDouble()).toFloat()
        val sinVal = sin(value.toDouble()).toFloat()
        val lumR = 0.213f
        val lumG = 0.715f
        val lumB = 0.072f
        val mat = floatArrayOf(
            lumR + cosVal * (1 - lumR) + sinVal * -lumR,
            lumG + cosVal * -lumG + sinVal * -lumG,
            lumB + cosVal * -lumB + sinVal * (1 - lumB),
            0f, 0f,

            lumR + cosVal * -lumR + sinVal * 0.143f,
            lumG + cosVal * (1 - lumG) + sinVal * 0.140f,
            lumB + cosVal * -lumB + sinVal * -0.283f,
            0f, 0f,

            lumR + cosVal * -lumR + sinVal * -(1 - lumR),
            lumG + cosVal * -lumG + sinVal * lumG,
            lumB + cosVal * (1 - lumB) + sinVal * lumB,
            0f, 0f,

            0f, 0f, 0f, 1f, 0f,
            0f, 0f, 0f, 0f, 1f
        )
        return ColorMatrix(mat)
    }

    private fun Context.getBitmap(@DrawableRes id: Int): Bitmap {
        val drawable = AppCompatResources.getDrawable(this, id)!!
        return drawable.toBitmap()
    }
}