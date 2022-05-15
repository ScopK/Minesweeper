package org.oar.minesweeper

import android.graphics.Canvas
import org.oar.minesweeper.control.ScreenProperties.load
import org.oar.minesweeper.elements.MenuOption
import org.oar.minesweeper.control.ScreenProperties
import android.view.MotionEvent
import android.view.View
import org.oar.minesweeper.generators.RandomCheckedGenerator
import org.oar.minesweeper.generators.RandomCheckedTestGenerator
import java.util.ArrayList
import kotlin.math.roundToInt

class MenuPanel(
    private val activity: MenuActivity
) : View(activity) {

    val options: MutableList<MenuOption> = mutableListOf()
    private var scrollPosition = 0f
    private var maxScroll = 0f

    init {
        addOptions()
        updateWinValues()
    }

    fun updateWinValues() {
        var maxHeight = 0f
        for (option in options) {
            option.setWindowValues(
                ScreenProperties.WIDTH,
                ScreenProperties.HEIGHT_BAR_EXCLUDED,
                ScreenProperties.DPI_W,
                ScreenProperties.DPI_H)

            val r = option.getRect(0f)
            if (r.bottom > maxHeight) {
                maxHeight = r.bottom.toFloat()
            }
        }
        maxHeight += MenuOption.FACTOR_MARGIN_OUT_SIZE * ScreenProperties.DPI_H
        maxScroll = ScreenProperties.HEIGHT_BAR_EXCLUDED - maxHeight
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        load(activity)
        updateWinValues()
        super.onSizeChanged(w, h, oldw, oldh)
    }

    private var optionSelected: MenuOption? = null
    private var lastY = 0
    private var hasMoved = false
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x: Int
        val y: Int
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                x = Math.round(event.x)
                lastY = Math.round(event.y)
                optionSelected = null
                hasMoved = false
                for (option in options) {
                    if (option.touchIsIn(x, lastY, scrollPosition)) {
                        option.isHover = true
                        this.postInvalidate()
                        optionSelected = option
                        break
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (!hasMoved) {
                    x = event.x.roundToInt()
                    y = event.y.roundToInt()
                    if (optionSelected != null && optionSelected!!.touchIsIn(x, y, scrollPosition)) {
                        optionSelected!!.isHover = false
                        optionSelected!!.run()
                    }
                } else {
                    if (optionSelected != null) {
                        optionSelected!!.isHover = false
                    }
                }
                optionSelected = null
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                x = event.x.roundToInt()
                y = event.y.roundToInt()
                val scrollPositionInitial = scrollPosition
                if (maxScroll < 0) {
                    scrollPosition -= (lastY - y).toFloat()
                    lastY = y
                    if (scrollPosition > 0)
                        scrollPosition = 0f
                    else if (scrollPosition < maxScroll)
                        scrollPosition = maxScroll
                }
                if (scrollPositionInitial != scrollPosition) {
                    hasMoved = true
                }
                if (optionSelected != null) {
                    optionSelected!!.isHover = optionSelected!!.touchIsIn(x, y, scrollPosition)
                }
                this.postInvalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawColor(-0xdbdbcd)
        for (option in options) {
            option.draw(canvas, scrollPosition)
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            //int startString = State.getLastLoadedJSON()==null? R.string.menu_newgame : R.string.menu_continue;
            //options.get(0).setText(activity.getString(startString));
        }
    }

    fun addOptions() {
        var counter = -1
        val activity = context as MenuActivity
        options.clear()
        options.add(MenuOption(activity.getString(R.string.menu_load), activity::loadGrid,
                ++counter, -0xa860bf, -0x9e56b5))

        options.add(MenuOption(activity.getString(R.string.menu_game_name_1), { activity.startGrid(4, 6, 5) },
                ++counter, -0xbe8961, -0xb58058))

        options.add(MenuOption(activity.getString(R.string.menu_game_name_2), { activity.startGrid(10, 10, 12) },
                ++counter, -0xbe8961, -0xb58058))

        options.add(MenuOption(activity.getString(R.string.menu_game_name_3), { activity.startGrid(10, 10, 25) },
                ++counter, -0xbe8961, -0xb58058))

        options.add(MenuOption(activity.getString(R.string.menu_game_name_4), { activity.startGrid(15, 15, 50) },
                ++counter, -0xbe8961, -0xb58058))

        options.add(MenuOption(activity.getString(R.string.menu_game_name_5), { activity.startGrid(25, 25, 100) },
                ++counter, -0xbe8961, -0xb58058))

        options.add(MenuOption(activity.getString(R.string.menu_game_name_6),
                { activity.startGrid(50, 50, 603, RandomCheckedGenerator::class) },
                ++counter, -0xa860bf, -0x9e56b5))

        options.add(MenuOption(activity.getString(R.string.menu_game_name_7), { activity.startGrid(6, 20, 40) },
                ++counter, -0xbe8961, -0xb58058))

        options.add(MenuOption(activity.getString(R.string.menu_settings), activity::openSettings,
                ++counter, -0x7d7d7e, -0x6b6b6c))

        options.add(MenuOption("TEST",
                { activity.startGrid(50, 50, 603, RandomCheckedTestGenerator::class) },
                ++counter, -0xa860bf, -0x9e56b5))
    }
}