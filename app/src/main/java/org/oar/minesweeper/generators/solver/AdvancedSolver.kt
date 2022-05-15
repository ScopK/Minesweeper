package org.oar.minesweeper.generators.solver

import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.utils.GridUtils.getNeighborsIdx

class AdvancedSolver : Solver() {

    override fun analyze(): Boolean {
        var changesMade = false
        val sketchTiles = sketch.tiles
        val toReveal: MutableList<Int> = mutableListOf()

        for (i in sketch.numberedCopy) {
            val sTile = sketchTiles[i]
            val coveredIdx = getNeighborsIdx(sketch.grid, sTile)
                .filter { idx -> sketchTiles[idx].status === Tile.Status.COVERED }

            if (!coveredIdx.isEmpty()) {
                val matching = sketch.numberedCopy
                matching.remove(i)
                for (j in coveredIdx) {
                    val neight = getNeighborsIdx(sketch.grid, sketchTiles[j])
                        .filter { idx -> sketchTiles[idx].status === sTile.status }

                    val it = matching.iterator()
                    while (it.hasNext()) {
                        if (!neight.contains(it.next())) it.remove()
                    }
                }
                matching
                    .map { k -> getNeighborsIdx(sketch.grid, sketchTiles[k]) }
                    .flatten()
                    .filter { idx -> sketchTiles[idx].status === Tile.Status.COVERED }
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