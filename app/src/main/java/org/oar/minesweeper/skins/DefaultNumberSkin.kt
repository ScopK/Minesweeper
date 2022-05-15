package org.oar.minesweeper.skins

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import org.oar.minesweeper.R

class DefaultNumberSkin : MainSkin() {

    override var bgColor = -0xc3c3c4
    override lateinit var symbols: Array<Bitmap>

    private lateinit var empty: Bitmap
    private lateinit var covers: Array<Bitmap>

    companion object {
        private const val ALTERNATIVES_MAX = 4
    }

    override fun load(context: Context) {
        empty = getBitmap(context, R.drawable.tile_skin_def_empty)
        symbols = arrayOf(
            getBitmap(context, R.drawable.tile_skin_def_bomb),
            getBitmap(context, R.drawable.tile_skin_def_bomb_end),
            getBitmap(context, R.drawable.tile_skin_def_flag),
            getBitmap(context, R.drawable.tile_skin_def_flag_fail))

        for (i in 1..8) {
            numbers[i.toString() + "_" + i] = getBitmap(context, "tile_skin_def_" + i + "_" + i)
        }
        tileSize = symbols[0].height - 1
        val mainCover = getBitmap(context, R.drawable.tiles_def)
        val coverH = mainCover.height
        val coverW = mainCover.width / ALTERNATIVES_MAX

        covers = (0 until ALTERNATIVES_MAX)
            .map { i -> Bitmap.createBitmap(mainCover, i * coverW, 0, coverW, coverH) }
            .toTypedArray()
    }

    override fun drawCovered(canvas: Canvas, x: Float, y: Float) {
        val i = alternative % covers.size
        canvas.drawBitmap(covers[i], x, y, defaultPaint)
    }

    override fun drawEmpty(canvas: Canvas, x: Float, y: Float) {
        canvas.drawBitmap(empty, x, y, defaultPaint)
    }
}