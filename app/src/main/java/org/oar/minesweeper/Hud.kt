package org.oar.minesweeper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import org.oar.minesweeper.control.MainLogic
import org.oar.minesweeper.control.MainLogic.GameStatus
import org.oar.minesweeper.control.ScreenProperties.BUTTON_PANEL_HEIGHT
import org.oar.minesweeper.control.ScreenProperties.HEIGHT
import org.oar.minesweeper.control.ScreenProperties.WIDTH
import org.oar.minesweeper.control.ScreenProperties.adaptFontSize
import org.oar.minesweeper.control.ScreenProperties.toDpi
import org.oar.minesweeper.control.Settings
import kotlin.math.roundToInt


class Hud(
    private val logic: MainLogic,
    private val view: View,
    private val context: Context
) {

    companion object {
        private const val FONT_SIZE = 9
        private const val MARGIN = 10
    }

    private val fontHeight: Float
    private val margin: Float
    private val textStyle = Paint(Paint.FILTER_BITMAP_FLAG)
    private var timer: Timer? = null
    private var startingTime = 0
    private var timeStr = ""
    private var showTime = Settings.showTime

    var time: Int
        get() = timer?.time ?: -1
        set(seconds) {
            timer
                ?.apply { time = seconds }
                ?: run { startingTime = seconds }
        }

    init {
        textStyle.color = Color.WHITE
        textStyle.textSize = FONT_SIZE.adaptFontSize()

        val r = Rect()
        textStyle.getTextBounds("W", 0, 1, r)
        fontHeight = r.height().toFloat()
        margin = MARGIN.toDpi()
    }

    fun draw(canvas: Canvas) {
        var drawableRes = R.drawable.hud_background
        val showTime = when (logic.status) {
            GameStatus.PLAYING -> {
                this.showTime
            }
            GameStatus.LOSE -> {
                drawableRes = R.drawable.hud_background_lose
                true
            }
            GameStatus.WIN -> {
                drawableRes = R.drawable.hud_background_win
                true
            }
        }

        val height = HEIGHT + BUTTON_PANEL_HEIGHT
        AppCompatResources.getDrawable(context, drawableRes)
            ?.also {
                it.setBounds(
                    0,
                    height - margin.roundToInt() * 2 - fontHeight.roundToInt(),
                    WIDTH,
                    height,
                )
                it.draw(canvas)
            }


        textStyle.textAlign = Paint.Align.LEFT
        canvas.drawText(
            "Open: " + logic.revisedTiles + "/" + logic.totalTiles,
            margin,
            height - margin,
            textStyle
        )

        textStyle.textAlign = Paint.Align.RIGHT
        var rightText = if (showTime) "$timeStr   " else ""
        rightText += logic.flaggedBombs.toString() + "/" + logic.grid.bombs
        canvas.drawText(
            rightText,
            WIDTH - margin,
            height - margin,
            textStyle
        )
    }

    fun startTimer() {
        timer?.timerClose()
        timer = Timer(startingTime)
            .also { it.start() }
    }

    fun resumeTimer() {
        showTime = Settings.showTime
        timer?.timeResume()
    }

    fun pauseTimer() {
        timer?.timePause()
    }

    fun restartTimer() {
        timer
            ?.apply { restart() }
            ?: run {
                startingTime = 0
                startTimer()
            }
    }

    fun stopTimer() {
        timer?.timerClose()
        timer = null
    }

    fun timerNotify(time: Int) {
        var ss = time / 10
        var mm = ss / 60
        val hh = mm / 60
        ss %= 60
        mm %= 60

        timeStr = (if (hh == 0) "$mm:" else "$hh" + if (mm < 10) "0$mm" else mm) +
            if (ss < 10) "0$ss" else ss

        if (showTime) {
            view.postInvalidate(
                0,
                (HEIGHT - margin - fontHeight).toInt(),
                WIDTH,
                HEIGHT
            )
        }
    }

    private inner class Timer(
        var initDSecs: Int
    ) : Thread() {

		var initTime: Long = 0
		var pausedTime: Long = 0
		var running = true

        var time: Int
            get() = ((System.nanoTime() - initTime) / 100000000).toInt()
            set(dSeconds) {
                initTime = System.nanoTime() - dSeconds * 100000000L
                pausedTime = 0
            }

        fun restart() {
            running = true
            initTime = System.nanoTime()
            initDSecs = 0
            pausedTime = 0
        }

        fun timePause() {
            if (pausedTime == 0L) {
                pausedTime = System.nanoTime()
            }
        }

        fun timeResume() {
            if (pausedTime != 0L) {
                initTime += System.nanoTime() - pausedTime
            }
            pausedTime = 0
        }

        fun timerClose() {
            running = false
        }

        override fun run() {
            super.run()
            running = true
            initTime = System.nanoTime() - initDSecs * 100000000L
            pausedTime = 0
            while (running) {
                if (pausedTime == 0L) {
                    timerNotify(this.time)
                }
                try {
                    sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }
}