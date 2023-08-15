package org.oar.minesweeper.generators

import org.oar.minesweeper.models.Grid
import org.oar.minesweeper.models.GridGenerationDetails

interface GridGenerator {
    fun generateNewGrid(grid: Grid, onFinish: (GridGenerationDetails) -> Unit)
}