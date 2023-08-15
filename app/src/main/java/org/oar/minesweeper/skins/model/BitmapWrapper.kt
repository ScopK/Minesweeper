package org.oar.minesweeper.skins.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

data class BitmapWrapper(
    private val bitmap: Bitmap,
    private val isEmpty: Boolean
) {
    companion object {
        private val defaultPaint = Paint()
    }

    fun drawBitmap(canvas: Canvas, x: Float, y: Float) {
        if (!isEmpty) {
            canvas.drawBitmap(bitmap, x, y, defaultPaint)
        }
    }
}