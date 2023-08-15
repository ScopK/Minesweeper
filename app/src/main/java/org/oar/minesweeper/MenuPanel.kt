package org.oar.minesweeper

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.ScrollView
import org.oar.minesweeper.control.ScreenProperties.load
import org.oar.minesweeper.control.ScreenProperties.toDpi
import org.oar.minesweeper.elements.GridConfiguration
import org.oar.minesweeper.elements.MenuOption
import org.oar.minesweeper.elements.MenuOption.Companion.FACTOR_MARGIN_OUT_SIZE
import org.oar.minesweeper.ui.views.components.HardTouchMoveView
import org.oar.minesweeper.ui.dialogs.StartGridDialog
import org.oar.minesweeper.utils.ContextUtils.findColor
import kotlin.math.roundToInt


@SuppressLint("ViewConstructor")
class MenuPanel(
    private val activity: MenuActivity
) : ScrollView(activity) {

    private val internalMenuView: InternalView

    init {
        load(activity)

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        setBackgroundColor(activity.findColor(R.color.defaultBackground))

        val linear = LinearLayout(activity)
        addView(linear, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        internalMenuView = InternalView()
        linear.addView(internalMenuView)
    }

    inner class InternalView: HardTouchMoveView(activity) {
        private val options: MutableList<MenuOption> = mutableListOf()
        private var ignoreTouchEvent = false

        init {
            addOptions()

            val maxHeight = options
                .maxOf { it.bottom }
                .let { it + FACTOR_MARGIN_OUT_SIZE.toDpi().toInt() }

            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                maxHeight)
        }

        private fun addOptions() {
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
                    R.string.menu_settings,
                    R.color.buttonGray,
                    R.color.buttonGrayH,
                    activity::openSettings,
                    ++counter
                ),
                MenuOption(
                    context,
                    R.string.visual_theme,
                    R.color.buttonGray,
                    R.color.buttonGrayH,
                    activity::openSkins,
                    ++counter
                )
            ).also {
                options.clear()
                options.addAll(it)
            }
        }

        private fun startProcess(width: Int, height: Int, bombs: Int): Runnable {
            val gridConfig = GridConfiguration(width, height, bombs)
            return Runnable {
                StartGridDialog(
                    activity,
                    gridConfig,
                    { activity.startGrid(gridConfig, it) }
                ).show(activity.supportFragmentManager, null)
            }
        }

        override fun hardOnTouch(event: MotionEvent): Boolean {
            val x = event.x.roundToInt()
            val y = event.y.roundToInt()


            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    ignoreTouchEvent = false
                    options.forEach { it.isHover = it.touchIsIn(x, y) }
                    this.postInvalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    ignoreTouchEvent = true
                    options.forEach { it.isHover = false }
                    this.postInvalidate()
                }
                MotionEvent.ACTION_UP -> {
                    if (!ignoreTouchEvent) {
                        options
                            .filter { it.touchIsIn(x, y) }
                            .forEach {
                                it.run()
                                it.isHover = false
                            }
                        this.postInvalidate()
                    }
                }
            }
            return true
        }

        override fun draw(canvas: Canvas) {
            super.draw(canvas)
            options.forEach { it.draw(canvas) }
        }
    }
}