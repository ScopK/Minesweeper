package org.oar.minesweeper.models

import kotlin.math.abs

data class Position (
    val x: Int,
    val y: Int,
) {
    fun distance(position: Position): Float {
        return abs(x.toFloat() - position.x) + abs(y.toFloat() - position.y)
    }

    fun toPoint() = Point(x.toFloat(), y.toFloat())
}