package org.oar.minesweeper.models

import java.util.*
import kotlin.math.abs

data class Position (
    val x: Int,
    val y: Int,
) {
    fun distance(position: Position): Float {
        return abs(x.toFloat() - position.x) + abs(y.toFloat() - position.y)
    }

    fun toPoint() = Point(x.toFloat(), y.toFloat())

    override fun toString(): String {
        return "Position($x, $y)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as Position
        return x == other.x && y == other.y
    }
    override fun hashCode() = Objects.hash(x, y)
}