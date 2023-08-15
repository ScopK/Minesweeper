package org.oar.minesweeper.generators

import org.oar.minesweeper.models.Grid
import org.oar.minesweeper.models.GridGenerationDetails
import org.oar.minesweeper.models.Tile
import org.oar.minesweeper.models.TileStatus
import org.oar.minesweeper.utils.GridUtils.findSafeOpenTileIdx
import org.oar.minesweeper.utils.GridUtils.getNeighbors

class RandomCheckedTestGenerator : RandomCheckedGenerator() {
    override fun generateNewGrid(grid: Grid, onFinish: (GridGenerationDetails) -> Unit) {
        Thread {
            val test = false
            if (test) {
                test1(grid)
                selectedSafeTile = 24
                solve(grid)

            } else {
                generateNewRandomGrid(grid)
                selectedSafeTile = grid.findSafeOpenTileIdx()

                var numBombsLeft = 0

                while (numBombsLeft == 0) {
                    val bombsLeft = solve(grid)
                    numBombsLeft = bombsLeft?.size ?: 0

                    if (numBombsLeft == 0) {
                        generateNewRandomGrid(grid)
                        selectedSafeTile = grid.findSafeOpenTileIdx()
                    }
                }
            }
            onFinish(
                GridGenerationDetails(selectedSafeTile)
            )
        }.start()
    }

    private fun test1(grid: Grid) {
        val w = grid.width
        val h = grid.height

        val tiles = grid.tiles
        tiles.clear()
        for (j in 0 until h) {
            for (i in 0 until w) {
                tiles.add(Tile(i, j, TileStatus.COVERED))
            }
        }
        for (idx in intArrayOf(8, 10, 11, 12, 17, 22)) {
            tiles[idx].hasBomb = true
            grid.getNeighbors(tiles[idx]).forEach { it.bombsNear++ }
        }
    }
}