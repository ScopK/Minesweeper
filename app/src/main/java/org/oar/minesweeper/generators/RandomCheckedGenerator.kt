package org.oar.minesweeper.generators

import org.oar.minesweeper.grid.GameLogic
import org.oar.minesweeper.models.Grid
import org.oar.minesweeper.models.GridGenerationDetails
import org.oar.minesweeper.models.Tile
import org.oar.minesweeper.generators.solver.*
import org.oar.minesweeper.utils.GridUtils.findSafeOpenTileIdx
import org.oar.minesweeper.utils.GridUtils.getNeighbors
import org.oar.minesweeper.utils.GridUtils.getNeighborsIdx

open class RandomCheckedGenerator : RandomGenerator() {

    companion object {
        private const val MAX_SWAPPING_RETRY = 5
    }

    protected var selectedSafeTile = 0
    private var sketch: Sketch? = null
    override fun generateNewGrid(grid: Grid, onFinish: (GridGenerationDetails) -> Unit) {
        Thread {
            generateNewRandomGrid(grid)
            selectedSafeTile = grid.findSafeOpenTileIdx()

            var originalGrid = grid
            var numBombsLeft = 1
            var retrySwapping = MAX_SWAPPING_RETRY

            while (numBombsLeft != 0) {
                val bombsLeft = solve(originalGrid.clone())
                numBombsLeft = bombsLeft?.size ?: Int.MAX_VALUE

                if (numBombsLeft > 0) {
                    if (numBombsLeft < grid.bombs / 20 && retrySwapping > 0) {
                        retrySwapping--
                        swapBombs(grid, bombsLeft!!)
                        originalGrid = grid

                    } else {
                        generateNewRandomGrid(grid)
                        originalGrid = grid
                        selectedSafeTile = grid.findSafeOpenTileIdx()
                        retrySwapping = MAX_SWAPPING_RETRY
                    }
                }
            }

            onFinish(
                GridGenerationDetails(selectedSafeTile)
            )
        }.start()
    }

    override fun forceCleanSpot(grid: Grid, tile: Tile) {
        throw RuntimeException("Function not supported for solvable grids")
    }

    fun swapBombs(grid: Grid, toReplace: Set<Int>) {
        val protectedTiles = grid.getNeighborsIdx(grid.tiles[selectedSafeTile])
        protectedTiles.add(selectedSafeTile)

        for (rplc in toReplace) {
            var aim: Int
            do {
                aim = grid.findSafeOpenTileIdx()
            } while (protectedTiles.indexOf(aim) >= 0)

            val t0: Tile = grid.tiles[rplc]
            t0.hasBomb = false
            grid.getNeighbors(t0).forEach { it.bombsNear-- }

            val tf: Tile = grid.tiles[aim]
            tf.hasBomb = true
            grid.getNeighbors(tf).forEach { it.bombsNear++ }
        }
    }

    fun solve(grid: Grid): Set<Int>? {
        val ml = GameLogic(grid)

        val t: Tile = grid.tiles[selectedSafeTile]
        ml.reveal(t)

        sketch = Sketch(grid)
        Solver.setSolver(ml, sketch!!)

        val solvers: MutableList<Solver> = mutableListOf(
            BasicSolver(),
            PossibleSolver(),
            DeepSolver(),
            AdvancedSolver(),
        )

        @Suppress("ControlFlowWithEmptyBody")
        while (solvers.any { solver -> solver.analyze() });

        return Solver.leftBombs
    }
}