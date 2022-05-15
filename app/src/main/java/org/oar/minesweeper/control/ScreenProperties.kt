package org.oar.minesweeper.control

import android.app.Activity
import android.graphics.Point
import android.util.DisplayMetrics

object ScreenProperties {
    var WIDTH = 0
    var HEIGHT = 0
    var HEIGHT_BAR_EXCLUDED = 0
    var DPI_W = 0f
    var DPI_H = 0f
    var DPI = 0f

    private var fontSizeBase = 0f

    fun load(activity: Activity) {

        val display = activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        WIDTH = size.x
        HEIGHT_BAR_EXCLUDED = size.y
        HEIGHT = HEIGHT_BAR_EXCLUDED
        DPI_W = metrics.xdpi
        DPI_H = metrics.ydpi
        DPI = (DPI_W + DPI_H) / 2f
        fontSizeBase = .14f * DPI


        // Status Bar Height
        var statusBarHeight = 0
        val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = activity.resources.getDimensionPixelSize(resourceId)
        }
        HEIGHT_BAR_EXCLUDED -= statusBarHeight
    }

    @JvmStatic
	fun dpiValue(v: Float): Float {
        return v * DPI / 100f
    }

    @JvmStatic
	fun fontSizeAdapted(fontSize: Float): Float {
        return fontSizeBase * fontSize / 12f
    }
}