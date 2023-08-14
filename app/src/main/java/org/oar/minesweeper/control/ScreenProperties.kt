package org.oar.minesweeper.control

import android.app.Activity
import android.content.Context
import android.view.WindowInsets
import android.view.WindowManager

object ScreenProperties {
    var WIDTH = 0
    var HEIGHT = 0
    var STATUS_BAR_HEIGHT = 0
    var BUTTON_PANEL_HEIGHT = 0
    var NOTCH_HEIGHT = 0
    var DPI_W = 0f
    var DPI_H = 0f
    var DPI = 0f
    var FRAME_RATE = 0f

    private var fontSizeBase = 0f

    fun load(activity: Activity) {
        val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val windowMetrics = windowManager.currentWindowMetrics
        val windowInsets = windowMetrics.windowInsets

        val insets = windowInsets.getInsetsIgnoringVisibility(
            WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout())
        val insetsWidth = insets.right + insets.left
        BUTTON_PANEL_HEIGHT = insets.top + insets.bottom

        val notchRect = windowInsets.displayCutout
            ?.run { boundingRects.getOrNull(0) }
        NOTCH_HEIGHT = notchRect?.height() ?: 0

        val b = windowMetrics.bounds
        HEIGHT = b.height() - BUTTON_PANEL_HEIGHT - NOTCH_HEIGHT
        WIDTH = b.width() - insetsWidth // - (notchRect?.width() ?: 0)

        val displayMetrics = activity.resources.displayMetrics
        DPI_W = displayMetrics.xdpi
        DPI_H = displayMetrics.ydpi
        DPI = (DPI_W + DPI_H) / 2f
        fontSizeBase = .14f * DPI

        // Status Bar Height
        var statusBarHeight = 0
        val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = activity.resources.getDimensionPixelSize(resourceId)
        }
        STATUS_BAR_HEIGHT = statusBarHeight

        FRAME_RATE = activity.display?.refreshRate ?: 60f
    }

    fun Float.toDpi(): Float {
        return this * DPI / 100f
    }

    fun Int.toDpi(): Float {
        return this * DPI / 100f
    }

    fun Int.adaptFontSize(): Float {
        return fontSizeBase * this / 12f
    }
}