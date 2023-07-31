package org.oar.minesweeper.generators

import org.oar.minesweeper.utils.GridUtils.findSafeOpenTileIdx
import org.oar.minesweeper.utils.GridUtils.getNeighborsIdx
import org.oar.minesweeper.utils.GridUtils.getNeighbors
import org.oar.minesweeper.generators.solver.Sketch
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.control.MainLogic
import org.oar.minesweeper.elements.GridStartOptions
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.generators.solver.Solver
import org.oar.minesweeper.generators.solver.BasicSolver
import org.oar.minesweeper.generators.solver.PossibleSolver
import org.oar.minesweeper.generators.solver.DeepSolver
import org.oar.minesweeper.generators.solver.AdvancedSolver

open class RandomCheckedGenerator : RandomGenerator() {

    companion object {
        private const val MAX_SWAPPING_RETRY = 5
    }

    protected var selectedSafeTile = 0
    private var sketch: Sketch? = null
    override fun generateNewGrid(grid: Grid, onFinish: (GridStartOptions) -> Unit) {
        Thread {
            generateNewRandomGrid(grid)
            selectedSafeTile = findSafeOpenTileIdx(grid)

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
                        selectedSafeTile = findSafeOpenTileIdx(grid)
                        retrySwapping = MAX_SWAPPING_RETRY
                    }
                }
            }

            onFinish(
                GridStartOptions(selectedSafeTile)
            )
        }.start()
    }

    fun swapBombs(grid: Grid, toReplace: Set<Int>) {
        val protectedTiles = getNeighborsIdx(grid, grid.tiles[selectedSafeTile])
        protectedTiles.add(selectedSafeTile)

        for (rplc in toReplace) {
            var aim: Int
            do {
                aim = findSafeOpenTileIdx(grid)
            } while (protectedTiles.indexOf(aim) >= 0)

            val t0: Tile = grid.tiles[rplc]
            t0.plantBomb(false)
            getNeighbors(grid, t0).forEach { it.doesntHaveBombNear() }

            val tf: Tile = grid.tiles[aim]
            tf.plantBomb()
            getNeighbors(grid, tf).forEach { it.hasBombNear() }
        }
    }

    fun solve(grid: Grid): Set<Int>? {
        val ml = MainLogic(grid)

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