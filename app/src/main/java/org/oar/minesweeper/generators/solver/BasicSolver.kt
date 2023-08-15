package org.oar.minesweeper.generators.solver

import org.oar.minesweeper.models.TileStatus
import org.oar.minesweeper.utils.GridUtils.getNeighborsIdx

class BasicSolver : Solver() {

    override fun analyze(): Boolean {
        var changesMade = false
        val sketchTiles = sketch.tiles
        val sketchTilesUncovered: MutableList<Int> = ArrayList()
        for (i in sketch.uncovered) {
            val sTile = sketchTiles[i]
            if (sTile.bombsNear > 0) {
                val nearCovered = sketch.grid.getNeighborsIdx(sTile)
                    .filter { ix -> sketchTiles[ix].status === TileStatus.COVERED }

                if (sTile.bombsNear == nearCovered.size) {
                    changesMade = changesMade or (nearCovered.isNotEmpty())
                    nearCovered.forEach { idx -> markBomb(idx) }
                }
            } else {
                sketchTilesUncovered.add(i)
            }
        }

        for (i in sketchTilesUncovered) {
            val sTile = sketchTiles[i]
            if (sTile.customFlag.contains('0')) {
                for (idx in sketch.grid.getNeighborsIdx(sTile)) {
                    if (sketchTiles[idx].status === TileStatus.COVERED) {
                        reveal(idx)
                        changesMade = true
                    }
                }
            }
        }

        return changesMade
    }
}