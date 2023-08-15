package org.oar.minesweeper.generators.solver

import org.oar.minesweeper.elements.TileStatus
import org.oar.minesweeper.utils.GridUtils.getNeighborsIdx

class AdvancedSolver : Solver() {

    override fun analyze(): Boolean {
        var changesMade = false
        val sketchTiles = sketch.tiles
        val toReveal: MutableList<Int> = mutableListOf()

        for (i in sketch.numberedCopy) {
            val sTile = sketchTiles[i]
            val coveredIdx = sketch.grid.getNeighborsIdx(sTile)
                .filter { idx -> sketchTiles[idx].status === TileStatus.COVERED }

            if (coveredIdx.isNotEmpty()) {
                val matching = sketch.numberedCopy
                matching.remove(i)
                for (j in coveredIdx) {
                    val neight = sketch.grid.getNeighborsIdx(sketchTiles[j])
                        .filter { idx -> sketchTiles[idx].status === sTile.status }

                    val it = matching.iterator()
                    while (it.hasNext()) {
                        if (!neight.contains(it.next())) it.remove()
                    }
                }
                matching
                    .flatMap { k -> sketch.grid.getNeighborsIdx(sketchTiles[k]) }
                    .filter { idx -> sketchTiles[idx].status === TileStatus.COVERED }
                    .filter { idx -> !coveredIdx.contains(idx) && !toReveal.contains(idx) }
                    .forEach { e -> toReveal.add(e) }
            }
        }
        for (idx in toReveal) {
            reveal(idx)
            changesMade = true
        }
        return changesMade
    }
}