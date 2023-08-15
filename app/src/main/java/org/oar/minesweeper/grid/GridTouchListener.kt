package org.oar.minesweeper.grid

interface GridTouchListener {
    fun move(dx: Float, dy: Float)
    fun scale(ratio: Float)
    fun longPressed(x: Float, y: Float)
    fun pressed(x: Float, y: Float)
}