package org.oar.minesweeper.utils

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

object ContextUtils {
    @ColorInt
    fun Context.findColor(@ColorRes colorRes: Int): Int {
        return this.resources.getColor(colorRes, null);
    }
}