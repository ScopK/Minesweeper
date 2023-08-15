package org.oar.minesweeper.generators

import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.GridStartOptions
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.elements.TileStatus
import org.oar.minesweeper.utils.GridUtils.findSafeOpenTileIdx
import org.oar.minesweeper.utils.GridUtils.getNeighbors

class RandomCheckedTestGenerator : RandomCheckedGenerator() {
    override fun generateNewGrid(grid: Grid, onFinish: (GridStartOptions) -> Unit) {
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
                GridStartOptions(selectedSafeTile)
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
            grid.getNeighbors(tiles[idx]).forEach { it.hasBombNear() }
        }
    }
}