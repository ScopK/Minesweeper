package org.oar.minesweeper.skins

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap

abstract class Skin {
    open val visualHelp = false
    protected open val unhelpfulTileIsEmpty = false

    open val defaultTileSize = 128

    protected open val numberOfCovers = 1
    protected open val coverW = 128
    protected open val coverH = 128

    @get:DrawableRes
    abstract val resource: Int
    @get:ColorRes
    abstract var backgroundColor: Int

    private val defaultPaint = Paint()
    private lateinit var set: SkinSet

    fun load(context: Context) {
        val bitmap = context.getBitmap(resource)
        val lastRowY = defaultTileSize * 4

        val covers = (0 until numberOfCovers)
            .map { i -> bitmap.sub(i * coverW, lastRowY, coverW, coverH) }

        set = SkinSet(
            bitmap.sub(defaultTileSize * 10, lastRowY),
            covers,
            bitmap.sub(defaultTileSize * 8, lastRowY),
            bitmap.sub(defaultTileSize * 9, lastRowY),
            bitmap.sub(defaultTileSize * 11, lastRowY),
            bitmap.sub(defaultTileSize * 12, lastRowY),
            loadNumbers(bitmap)
        )
    }

    fun drawCover(canvas: Canvas, x: Float, y: Float, coverNumber: Int) {
        val i = coverNumber % numberOfCovers
        canvas.drawBitmap(set.covers[i], x, y, defaultPaint)
    }

    fun drawEmpty(canvas: Canvas, x: Float, y: Float) {
        canvas.drawBitmap(set.empty, x, y, defaultPaint)
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
        } else if (unhelpfulTileIsEmpty) {
            Pair(numValue, 0)
        } else {
            Pair(numValue, numValue)
        }

        canvas.drawBitmap(set.numbers[pair]!!, x, y, defaultPaint)
    }

    fun drawBomb(canvas: Canvas, x: Float, y: Float, coverNumber: Int, endBomb: Boolean = false) {
        if (endBomb) {
            drawEmpty(canvas, x, y)
            canvas.drawBitmap(set.bombEnd, x, y, defaultPaint)
        } else {
            drawCover(canvas, x, y, coverNumber)
            canvas.drawBitmap(set.bomb, x, y, defaultPaint)
        }
    }

    fun drawFlag(canvas: Canvas, x: Float, y: Float, coverNumber: Int, flagFail: Boolean = false) {
        drawCover(canvas, x, y, coverNumber)
        canvas.drawBitmap(if (flagFail) set.flagFail else set.flag, x, y, defaultPaint)
    }

    private fun loadNumbers(bitmap: Bitmap): Map<Pair<Int, Int>, Bitmap> {
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
        )
    }

    private fun Bitmap.sub(
            x: Int,
            y: Int,
            width: Int = defaultTileSize,
            height: Int = defaultTileSize
        ) = Bitmap.createBitmap(this, x, y, width, height)

    private fun Context.getBitmap(@DrawableRes id: Int): Bitmap {
        val drawable = AppCompatResources.getDrawable(this, id)!!
        return drawable.toBitmap()
    }
}