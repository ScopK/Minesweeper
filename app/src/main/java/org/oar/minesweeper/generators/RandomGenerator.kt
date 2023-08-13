package org.oar.minesweeper.generators

import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.GridStartOptions
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.utils.GridUtils.getNeighbors
import java.util.*

open class RandomGenerator : GridGenerator {
    override fun generateNewGrid(grid: Grid, onFinish: (GridStartOptions) -> Unit) {
        Thread {
            generateNewRandomGrid(grid)
            onFinish(GridStartOptions())
        }.start()
    }

    protected fun generateNewRandomGrid(grid: Grid) {
        val w = grid.width
        val h = grid.height
        val bombs = grid.bombs

        grid.tiles.clear()
        for (j in 0 until h) {
            for (i in 0 until w) {
                grid.tiles.add(Tile(i, j, Tile.Status.COVERED))
            }
        }
        val random = Random()
        var idx: Int
        val max = w * h
        var i = 0
        while (i < bombs) {
            idx = random.nextInt(max)
            if (grid.tiles[idx].hasBomb) {
                i--
                i++
                continue
            }
            grid.tiles[idx].hasBomb = true
            getNeighbors(grid, grid.tiles[idx]).forEach { it.hasBombNear() }
            i++
        }
    }
}