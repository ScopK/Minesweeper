package org.oar.minesweeper.generators

import org.oar.minesweeper.utils.GridUtils.getNeighbors
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.Tile
import java.util.*
import java.util.function.Consumer

open class RandomGenerator : GridGenerator {
    override fun generateNewGrid(grid: Grid, bombs: Int, onFinish: Runnable) {
        Thread {
            generateNewRandomGrid(grid, bombs)
            onFinish.run()
        }.start()
    }

    protected fun generateNewRandomGrid(grid: Grid, bombs: Int) {
        val w = grid.w
        val h = grid.h
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
            grid.tiles[idx].plantBomb()
            getNeighbors(grid, grid.tiles[idx]).forEach(Consumer { obj: Tile -> obj.hasBombNear() })
            i++
        }
    }
}