package org.oar.minesweeper.generators

import org.oar.minesweeper.models.Grid
import org.oar.minesweeper.models.GridGenerationDetails
import org.oar.minesweeper.models.Tile
import org.oar.minesweeper.models.TileStatus
import org.oar.minesweeper.utils.GridUtils.getNeighbors
import java.util.*

open class RandomGenerator : GridGenerator {
    override fun generateNewGrid(grid: Grid, onFinish: (GridGenerationDetails) -> Unit) {
        Thread {
            generateNewRandomGrid(grid)
            onFinish(GridGenerationDetails())
        }.start()
    }

    protected fun generateNewRandomGrid(grid: Grid) {
        val w = grid.width
        val h = grid.height
        val bombs = grid.bombs

        grid.tiles.clear()
        for (j in 0 until h) {
            for (i in 0 until w) {
                grid.tiles.add(Tile(i, j, TileStatus.COVERED))
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
            grid.getNeighbors(grid.tiles[idx]).forEach { it.bombsNear++ }
            i++
        }
    }
}