package org.oar.minesweeper

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import org.oar.minesweeper.control.ScreenProperties
import org.oar.minesweeper.control.ScreenProperties.load
import org.oar.minesweeper.elements.GridConfiguration
import org.oar.minesweeper.elements.MenuOption
import org.oar.minesweeper.ui.StartGridDialogFragment
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
                ScreenProperties.HEIGHT,
                ScreenProperties.DPI_W,
                ScreenProperties.DPI_H)

            val r = option.getRect(0f)
            if (r.bottom > maxHeight) {
                maxHeight = r.bottom.toFloat()
            }
        }
        maxHeight += MenuOption.FACTOR_MARGIN_OUT_SIZE * ScreenProperties.DPI_H
        maxScroll = ScreenProperties.HEIGHT - maxHeight
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
                x = event.x.roundToInt()
                lastY = event.y.roundToInt()
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

        listOf(
            MenuOption(
                context,
                R.string.menu_load,
                R.color.buttonGreen,
                R.color.buttonGreenH,
                activity::loadGrid,
                ++counter
            ),
            MenuOption(
                context,
                R.string.menu_game_name_1,
                R.color.buttonBlue,
                R.color.buttonBlueH,
                startProcess(4,6,5),
                ++counter
            ),
            MenuOption(
                context,
                R.string.menu_game_name_2,
                R.color.buttonBlue,
                R.color.buttonBlueH,
                startProcess(10, 10, 12),
                ++counter
            ),
            MenuOption(
                context,
                R.string.menu_game_name_3,
                R.color.buttonBlue,
                R.color.buttonBlueH,
                startProcess(10, 10, 25),
                ++counter
            ),
            MenuOption(
                context,
                R.string.menu_game_name_4,
                R.color.buttonBlue,
                R.color.buttonBlueH,
                startProcess(15, 15, 50),
                ++counter
            ),
            MenuOption(
                context,
                R.string.menu_game_name_5,
                R.color.buttonBlue,
                R.color.buttonBlueH,
                startProcess(25, 25, 100),
                ++counter
            ),
            MenuOption(
                context,
                R.string.menu_game_name_6,
                R.color.buttonBlue,
                R.color.buttonBlueH,
                startProcess(50, 50, 603),
                ++counter
            ),
            MenuOption(
                context,
                R.string.menu_game_name_7,
                R.color.buttonBlue,
                R.color.buttonBlueH,
                startProcess(6, 20, 40),
                ++counter
            ),
            MenuOption(
                context,
                R.string.menu_settings,
                R.color.buttonGray,
                R.color.buttonGrayH,
                activity::openSettings,
                ++counter
            )
        ).also {
            options.clear()
            options.addAll(it)
        }
    }

    private fun startProcess(width: Int, height: Int, bombs: Int) =
        startProcess(GridConfiguration(width, height, bombs))

    private fun startProcess(gridConfig: GridConfiguration): Runnable {
        return Runnable {
            StartGridDialogFragment(
                activity,
                gridConfig,
                { activity.startGrid(gridConfig, it) }
            ).show(activity.supportFragmentManager, null)
        }
    }
}