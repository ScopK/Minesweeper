package org.oar.minesweeper.skins

import android.graphics.Bitmap

data class SkinSet(
    val empty: Bitmap,
    val covers: List<Bitmap>,
    val flag: Bitmap,
    val flagFail: Bitmap,
    val bomb: Bitmap,
    val bombEnd: Bitmap,
    val numbers: Map<Pair<Int, Int>, Bitmap>
)
