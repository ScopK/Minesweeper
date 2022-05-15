package org.oar.minesweeper.generators

import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.utils.GridUtils.findSafeOpenTileIdx
import org.oar.minesweeper.utils.GridUtils.getNeighbors
import java.util.function.Consumer

class RandomCheckedTestGenerator : RandomCheckedGenerator() {
    override fun generateNewGrid(grid: Grid, bombs: Int, onFinish: Runnable) {
        Thread {
            val test = false
            if (test) {
                test1(grid, bombs)
                selectedSafeTile = 24
                solve(grid)

            } else {
                generateNewRandomGrid(grid, bombs)
                selectedSafeTile = findSafeOpenTileIdx(grid)

                var numBombsLeft = 0

                while (numBombsLeft == 0) {
                    val bombsLeft = solve(grid)
                    numBombsLeft = bombsLeft?.size ?: 0

                    if (numBombsLeft == 0) {
                        generateNewRandomGrid(grid, bombs)
                        selectedSafeTile = findSafeOpenTileIdx(grid)
                    }
                }
            }
            onFinish.run()
        }.start()
    }

    private fun test1(grid: Grid, bombs: Int) {
        val w = grid.w
        val h = grid.h
        val tiles = grid.tiles
        tiles.clear()
        for (j in 0 until h) {
            for (i in 0 until w) {
                tiles.add(Tile(i, j, Tile.Status.COVERED))
            }
        }
        for (idx in intArrayOf(8, 10, 11, 12, 17, 22)) {
            tiles[idx].plantBomb()
            getNeighbors(grid, tiles[idx]).forEach(Consumer { obj: Tile -> obj.hasBombNear() })
        }
    }
}