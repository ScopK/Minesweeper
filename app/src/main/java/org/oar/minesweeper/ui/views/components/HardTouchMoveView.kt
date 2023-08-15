package org.oar.minesweeper.ui.views.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import org.oar.minesweeper.utils.Point

open class HardTouchMoveView(
    context: Context,
    attrs: AttributeSet?,
    private val threshold: Int = 35
) : View(context, attrs) {

    private var downPoint = Point(0f, 0f)

    constructor(context: Context, threshold: Int = 35) : this(context, null, threshold)

    @SuppressLint("ClickableViewAccessibility")
    final override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        onTouch(event)

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downPoint = Point(event.x, event.y)
                hardOnTouch(event)
            }
            MotionEvent.ACTION_MOVE -> {
                if (Point(event.x, event.y).distance(downPoint) > threshold) {
                    hardOnTouch(event)
                } else {
                    true
                }
            }
            MotionEvent.ACTION_UP -> hardOnTouch(event)
            else -> true
        }
    }

    open fun hardOnTouch(event: MotionEvent) = true
    open fun onTouch(event: MotionEvent) = true
}