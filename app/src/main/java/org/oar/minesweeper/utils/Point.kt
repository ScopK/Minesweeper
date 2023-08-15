package org.oar.minesweeper.utils

import kotlin.math.abs

data class Point (
    val x: Float,
    val y: Float,
) {
    fun distance(point: Point): Float {
        return abs(x - point.x) + abs(y - point.y)
    }

    fun toPosition() = Position(x.toInt(), y.toInt())
}