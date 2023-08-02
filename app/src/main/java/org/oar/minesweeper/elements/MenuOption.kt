package org.oar.minesweeper.elements

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import org.oar.minesweeper.control.ScreenProperties
import org.oar.minesweeper.control.ScreenProperties.adaptFontSize
import org.oar.minesweeper.control.ScreenProperties.toDpi
import org.oar.minesweeper.utils.ActivityController.findColor
import kotlin.math.roundToInt

class MenuOption
(
    val context: Context,
    @StringRes
    val textRes: Int,
    @ColorRes
    val colorRes: Int,
    @ColorRes
    val hoverColorRes: Int,
    private val procedure: Runnable,
    private val index: Int
) {
    companion object {
        private const val FACTOR_FONT_SIZE = 11f
        private const val FACTOR_MARGIN_IN_SIZE = 16f
        const val FACTOR_MARGIN_OUT_SIZE = 20f
        private const val FACTOR_MARGIN_BTW_SIZE = 10f
        private const val FACTOR_BORDER_SIZE = 1f
    }

    private val text = context.getString(textRes)
    private val color = context.findColor(colorRes)
    private val hoverColor = context.findColor(hoverColorRes)

    private val fontSize: Float = FACTOR_FONT_SIZE.adaptFontSize()
    private val marginInSize: Float = FACTOR_MARGIN_IN_SIZE.toDpi()
    private val marginOutSize: Float = FACTOR_MARGIN_OUT_SIZE.toDpi()
    private val marginBtwSize: Float = FACTOR_MARGIN_BTW_SIZE.toDpi()
    private val borderSize: Float = FACTOR_BORDER_SIZE.toDpi()

    private val textHeight: Float
    private val baseHeight: Float
    private val initHPoint: Float
    //private var w = 0

    var isHover = false

    init {
        val textPaint = Paint()
        textPaint.textSize = fontSize
        val fm = textPaint.fontMetrics
        textHeight = fm.descent - fm.ascent
        baseHeight = textHeight + 2 * marginInSize
        initHPoint = index * (baseHeight + marginBtwSize)
    }

    fun run() = procedure.run()

    fun touchIsIn(x: Int, y: Int): Boolean {
        val rect = getRect()
        return rect.contains(x, y)
    }

    fun getRect() = Rect(left, top, right, bottom)
    val left: Int get() = marginOutSize.roundToInt()
    val top: Int get() = (initHPoint + marginOutSize).roundToInt()
    val right: Int get() = (ScreenProperties.WIDTH - marginOutSize).roundToInt()
    val bottom: Int get() = (initHPoint + baseHeight + marginOutSize).roundToInt()

    fun draw(canvas: Canvas) {
        val rect = getRect()
        val myPaint = Paint()
        myPaint.color = if (isHover) hoverColor else color
        canvas.drawRect(rect, myPaint)
        myPaint.color = -0x1000000
        myPaint.strokeWidth = borderSize
        myPaint.style = Paint.Style.STROKE
        canvas.drawRect(rect, myPaint)
        val textPaint = Paint()
        textPaint.color = -0x1
        textPaint.textSize = fontSize
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            text,
            (ScreenProperties.WIDTH / 2).toFloat(),
            initHPoint + marginInSize + marginOutSize + fontSize,
            textPaint
        )
    }
}