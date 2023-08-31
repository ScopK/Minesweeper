package org.oar.minesweeper.generators

import org.oar.minesweeper.models.Grid
import org.oar.minesweeper.models.GridGenerationDetails
import org.oar.minesweeper.models.Tile

interface GridGenerator {
    fun generateNewGrid(grid: Grid, onFinish: (GridGenerationDetails) -> Unit)
    fun forceCleanSpot(grid: Grid, tile: Tile)
}