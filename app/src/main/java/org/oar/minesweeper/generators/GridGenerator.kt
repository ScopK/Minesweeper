package org.oar.minesweeper.generators

import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.GridStartOptions

interface GridGenerator {
    fun generateNewGrid(grid: Grid, onFinish: (GridStartOptions) -> Unit)
}