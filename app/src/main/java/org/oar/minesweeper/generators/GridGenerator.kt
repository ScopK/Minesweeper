package org.oar.minesweeper.generators

import org.oar.minesweeper.elements.Grid

interface GridGenerator {
    fun generateNewGrid(grid: Grid, bombs: Int, onFinish: Runnable)
}