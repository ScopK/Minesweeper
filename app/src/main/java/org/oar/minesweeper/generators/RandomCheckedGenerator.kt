package org.oar.minesweeper.generators

import org.oar.minesweeper.utils.GridUtils.findSafeOpenTileIdx
import org.oar.minesweeper.utils.GridUtils.getNeighborsIdx
import org.oar.minesweeper.utils.GridUtils.getNeighbors
import org.oar.minesweeper.generators.solver.Sketch
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.control.MainLogic
import org.oar.minesweeper.control.Settings
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.generators.solver.Solver
import org.oar.minesweeper.generators.solver.BasicSolver
import org.oar.minesweeper.generators.solver.PossibleSolver
import org.oar.minesweeper.generators.solver.DeepSolver
import org.oar.minesweeper.generators.solver.AdvancedSolver
import java.util.ArrayList
import java.util.function.Consumer

open class RandomCheckedGenerator : RandomGenerator() {
    protected var selectedSafeTile = 0
    private var sketch: Sketch? = null
    override fun generateNewGrid(grid: Grid, bombs: Int, onFinish: Runnable) {
        Thread {
            generateNewRandomGrid(grid, bombs)
            selectedSafeTile = findSafeOpenTileIdx(grid)

            var originalGrid = grid
            var numBombsLeft = 1

            while (numBombsLeft != 0) {
                val bombsLeft = solve(originalGrid.clone())
                numBombsLeft = bombsLeft?.size ?: Int.MAX_VALUE

                if (numBombsLeft > 0) {
                    if (numBombsLeft < bombs / 20) {
                        swapBombs(grid, bombsLeft!!)
                        originalGrid = grid

                    } else {
                        generateNewRandomGrid(grid, bombs)
                        originalGrid = grid
                        selectedSafeTile = findSafeOpenTileIdx(grid)
                    }
                }
            }

            if (Settings.firstOpen) {
                val ml = MainLogic(grid)
                val t: Tile = grid.tiles[selectedSafeTile]
                ml.reveal(t)
            }

            onFinish.run()
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