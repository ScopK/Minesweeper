package org.oar.minesweeper.ui.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import org.oar.minesweeper.R
import org.oar.minesweeper.utils.PreferencesUtils.loadBoolean


class HudView(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    companion object {
        private const val ICONS_SIZE = 64
    }

    private var totalTiles = 0
    private var openTiles = 0
    private var totalBombs = 0
    private var flaggedTiles = 0

    private val remainingTilesView: HudLabel
    private val timeView: HudLabel
    private val remainingBombsView: HudLabel

    init {
        background = ContextCompat.getDrawable(context, R.drawable.hud_background)
        setPadding(50, 35, 50, 35)

        remainingTilesView = HudLabel(context, R.drawable.ic_open_tile_48dp)
            .also {
                addView(it)
                it.updateLayoutParams<LayoutParams> {
                    topToTop = LayoutParams.PARENT_ID
                    bottomToBottom = LayoutParams.PARENT_ID
                    startToStart = LayoutParams.PARENT_ID
                }
            }

        timeView = HudLabel(context, R.drawable.ic_timer_48dp)
            .also {
                addView(it)
                it.updateLayoutParams<LayoutParams> {
                    topToTop = LayoutParams.PARENT_ID
                    bottomToBottom = LayoutParams.PARENT_ID
                    startToStart = LayoutParams.PARENT_ID
                    endToEnd = LayoutParams.PARENT_ID
                }
            }

        if (!context.loadBoolean("showTime", true)) {
            timeView.visibility = GONE
        }

        remainingBombsView = HudLabel(context, R.drawable.ic_bomb_48dp)
            .also {
                addView(it)
                it.updateLayoutParams<LayoutParams> {
                    topToTop = LayoutParams.PARENT_ID
                    bottomToBottom = LayoutParams.PARENT_ID
                    endToEnd = LayoutParams.PARENT_ID
                }
            }
    }

    fun setValues(totalTiles: Int, openTiles: Int, totalBombs: Int, flaggedBombs: Int) {
        this.totalTiles = totalTiles
        this.openTiles = openTiles
        this.totalBombs = totalBombs
        this.flaggedTiles = flaggedBombs

        remainingTilesView.text =
            String.format(context.getString(R.string.hud_x_out_of), openTiles, totalTiles)

        remainingBombsView.text =
            String.format(context.getString(R.string.hud_x_out_of), flaggedBombs, totalBombs)
    }

    fun setValues(openTiles: Int, flaggedBombs: Int) {
        this.openTiles = openTiles
        this.flaggedTiles = flaggedBombs

        remainingTilesView.text =
            String.format(context.getString(R.string.hud_x_out_of), openTiles, totalTiles)

        remainingBombsView.text =
            String.format(context.getString(R.string.hud_x_out_of), flaggedBombs, totalBombs)
    }

    fun setEndStateBackground(win: Boolean) {
        background = ContextCompat.getDrawable(context,
            if (win) R.drawable.hud_background_win
            else     R.drawable.hud_background_lose
        )
        timeView.visibility = VISIBLE
    }

    fun resetBackground() {
        background = ContextCompat.getDrawable(context, R.drawable.hud_background)
        if (!context.loadBoolean("showTime", true)) {
            timeView.visibility = GONE
        }
    }

    fun setTimerValue(time: Int) {
        timeView.text = time.secondsToTimeString()
    }

    private fun Int.secondsToTimeString(): String {
        val ss = this % 60
        val mm = this / 60 % 60
        val hh = this / 3600

        val minAndSecs = "$mm" + if (ss < 10) ":0$ss" else ":$ss"
        return if (hh == 0) {
            minAndSecs
        } else {
            if (mm < 10) "$hh:0$minAndSecs" else "$hh:$minAndSecs"
        }
    }

    inner class HudLabel(
        context: Context,
        @DrawableRes drawableRes: Int
    ): LinearLayout(context) {

        private val textView: TextView

        var text: String
            get() = textView.text as String
            set(text) { textView.text = text }

        init {
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            gravity = Gravity.CENTER
            orientation = HORIZONTAL

            ImageView(context)
                .apply {
                    addView(this)
                    setImageResource(drawableRes)
                    layoutParams = LayoutParams(ICONS_SIZE, ICONS_SIZE)
                        .apply { marginEnd = 10 }
                }

            textView = TextView(context).apply {
                addView(this)
                setTextColor(Color.WHITE)
                layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    .apply { textSize = 17f }
            }
        }
    }
}