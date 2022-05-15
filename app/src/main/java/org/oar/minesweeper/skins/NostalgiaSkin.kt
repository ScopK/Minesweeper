package org.oar.minesweeper.skins

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import org.oar.minesweeper.R

class NostalgiaSkin : MainSkin() {

    override var bgColor = -0xc3c3c4
    override lateinit var symbols: Array<Bitmap>

    private lateinit var empty: Bitmap
    private lateinit var covers: Array<Bitmap>

    override fun load(context: Context) {
        empty = getBitmap(context, R.drawable.tile_skin_win_empty)
        symbols = arrayOf(
            getBitmap(context, R.drawable.tile_skin_win_bomb),
            getBitmap(context, R.drawable.tile_skin_win_bomb_end),
            getBitmap(context, R.drawable.tile_skin_win_flag),
            getBitmap(context, R.drawable.tile_skin_win_flag_fail))

        for (i in 1..8) {
            numbers[i.toString() + "_" + i] = getBitmap(context, "tile_skin_win_" + i + "_" + i)
        }
        tileSize = symbols[0].height - 1
        val mainCover = getBitmap(context, R.drawable.tiles_win)
        val coverH = mainCover.height
        val coverW = mainCover.width

        covers = arrayOf(
            Bitmap.createBitmap(mainCover, 0, 0, coverW, coverH))
    }

    override fun drawCovered(canvas: Canvas, x: Float, y: Float) {
        val i = alternative % covers.size
        canvas.drawBitmap(covers[i], x, y, defaultPaint)
    }

    override fun drawEmpty(canvas: Canvas, x: Float, y: Float) {
        canvas.drawBitmap(empty, x, y, defaultPaint)
    }
}