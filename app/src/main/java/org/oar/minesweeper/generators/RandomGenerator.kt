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

        generateBombs(grid, bombs)
    }

    override fun forceCleanSpot(grid: Grid, tile: Tile) {
        val neighbors = grid.getNeighbors(tile)

        val defusedBombs = tile.bombsNear + if (tile.hasBomb) 1 else 0

        tile.hasBomb = false
        tile.bombsNear = 0

        val skipTiles = neighbors
            .onEach { it.hasBomb = false }
            .onEach { neighbor ->
                val level2Neighbors = grid.getNeighbors(neighbor)
                neighbor.bombsNear = level2Neighbors.count { it.hasBomb }

                level2Neighbors.forEach {
                    it.bombsNear = grid.getNeighbors(it).count { i -> i.hasBomb }
                }
            }
            .map { it.x + it.y * grid.width }
            .toMutableList()
            .apply { add(tile.x + tile.y * grid.width) }

        generateBombs(grid, defusedBombs, skipTiles)
    }

    private fun generateBombs(grid: Grid, bombs: Int, skipTiles: List<Int> = listOf()) {
        if (bombs == 0) return

        val w = grid.width
        val h = grid.height

        val random = Random()
        var idx: Int
        val max = w * h
        var i = 0
        while (i < bombs) {
            idx = random.nextInt(max)
            if (grid.tiles[idx].hasBomb || skipTiles.contains(idx))
                continue

            grid.tiles[idx].hasBomb = true
            grid.getNeighbors(grid.tiles[idx]).forEach { it.bombsNear++ }
            i++
        }
    }
}