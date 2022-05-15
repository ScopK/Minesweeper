package org.oar.minesweeper.skins

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import java.util.HashMap

abstract class MainSkin {
    abstract var bgColor: Int
    abstract var symbols: Array<Bitmap>

    var tileSize = 128 - 1
    var helpEnabled = false
    var numbers = HashMap<String, Bitmap>()
	var defaultPaint = Paint()

	var alternative = 0

    abstract fun load(context: Context)
    abstract fun drawCovered(canvas: Canvas, x: Float, y: Float)
    abstract fun drawEmpty(canvas: Canvas, x: Float, y: Float)

    fun drawCovered(canvas: Canvas, x: Float, y: Float, idx: Int) {
        drawCovered(canvas, x, y)
        canvas.drawBitmap(symbols[idx]!!, x, y, defaultPaint)
    }

    fun drawEmpty(canvas: Canvas, x: Float, y: Float, idx: Int) {
        drawEmpty(canvas, x, y)
        canvas.drawBitmap(symbols[idx]!!, x, y, defaultPaint)
    }

    fun drawEmpty(canvas: Canvas, x: Float, y: Float, numValue: Int, numMarked: Int) {
        var numMarked = numMarked
        drawEmpty(canvas, x, y)
        if (helpEnabled) {
            if (numMarked > numValue + 1) numMarked = numValue + 1
            canvas.drawBitmap(numbers[numValue.toString() + "_" + numMarked]!!, x, y, defaultPaint)
        } else {
            canvas.drawBitmap(numbers[numValue.toString() + "_" + numValue]!!, x, y, defaultPaint)
        }
    }

    protected fun getBitmap(context: Context, id: Int): Bitmap {
        return (context.getDrawable(id) as BitmapDrawable?)!!.bitmap
    }

    protected fun getBitmap(context: Context, name: String?): Bitmap {
        val id = context.resources.getIdentifier(name, "drawable", context.packageName)
        return getBitmap(context, id)
    }
}