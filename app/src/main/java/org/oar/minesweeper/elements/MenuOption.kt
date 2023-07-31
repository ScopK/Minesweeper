package org.oar.minesweeper.elements

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
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

    private var fontSize = 0f
    private var marginInSize = 0f
    private var marginOutSize = 0f
    private var marginBtwSize = 0f
    private var borderSize = 0f
    private var textHeight = 0f
    private var baseHeight = 0f
    private var initHPoint = 0f
    private var w = 0

    var isHover = false

    fun run() = procedure.run()

    fun touchIsIn(x: Int, y: Int, scrollPosition: Float): Boolean {
        val r = getRect(scrollPosition)
        return r.contains(x, y)
    }

    fun setWindowValues(w: Int, h: Int, dpiW: Float, dpiH: Float) {
        this.w = w

        fontSize = FACTOR_FONT_SIZE.adaptFontSize()
        marginInSize = FACTOR_MARGIN_IN_SIZE.toDpi()
        marginOutSize = FACTOR_MARGIN_OUT_SIZE.toDpi()
        marginBtwSize = FACTOR_MARGIN_BTW_SIZE.toDpi()
        borderSize = FACTOR_BORDER_SIZE.toDpi()

        val textPaint = Paint()
        textPaint.textSize = fontSize
        val fm = textPaint.fontMetrics
        textHeight = fm.descent - fm.ascent
        baseHeight = textHeight + 2 * marginInSize
        initHPoint = index * (baseHeight + marginBtwSize)
    }

    fun getRect(scrollPosition: Float): Rect {
        val r = Rect()
        r.left = marginOutSize.roundToInt()
        r.top = (scrollPosition + initHPoint + marginOutSize).roundToInt()
        r.right = (w - marginOutSize).roundToInt()
        r.bottom = (scrollPosition + initHPoint + baseHeight + marginOutSize).roundToInt()
        return r
    }

    fun draw(canvas: Canvas, scrollPosition: Float) {
        val r = getRect(scrollPosition)
        val myPaint = Paint()
        myPaint.color = if (isHover) hoverColor else color
        canvas.drawRect(r, myPaint)
        myPaint.color = -0x1000000
        myPaint.strokeWidth = borderSize
        myPaint.style = Paint.Style.STROKE
        canvas.drawRect(r, myPaint)
        val textPaint = Paint()
        textPaint.color = -0x1
        textPaint.textSize = fontSize
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            text,
            (w / 2).toFloat(),
            scrollPosition + initHPoint + marginInSize + marginOutSize + fontSize,
            textPaint
        )
    }
}