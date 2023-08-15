package org.oar.minesweeper.utils

import kotlin.math.abs

data class Position (
    val x: Int,
    val y: Int,
) {
    fun distance(point: Position): Float {
        return abs(x.toFloat() - point.x) + abs(y.toFloat() - point.y)
    }

    fun toPoint() = Point(x.toFloat(), y.toFloat())
}