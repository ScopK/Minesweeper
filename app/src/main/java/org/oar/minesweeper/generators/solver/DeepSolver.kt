package org.oar.minesweeper.generators.solver

import org.oar.minesweeper.models.Tile
import org.oar.minesweeper.models.TileStatus
import org.oar.minesweeper.utils.GridUtils.getNeighbors
import org.oar.minesweeper.utils.GridUtils.getNeighborsIdx

class DeepSolver : Solver() {

    companion object {
        private const val NONE = -1
        private const val CASE_1_1 = 1
        private const val CASE_2_1 = 2
    }

    override fun analyze(): Boolean {
        var changesMade = false
        val sketchTiles = sketch.tiles
        for (i in sketch.numberedCopy) {
            val sTile = sketchTiles[i]
            if (sTile.isNumberVisible) {
                val coveredIdx = sketch.grid.getNeighborsIdx(sTile)
                    .filter { idx -> sketchTiles[idx].status === TileStatus.COVERED }

                if (coveredIdx.isNotEmpty()) {
                    val bNear = sTile.bombsNear
                    val totalCovers = coveredIdx.size
                    coveredIdx
                        .map { idx -> sketchTiles[idx] }
                        .forEach { tile -> tile.customFlag.add('?') }

                    val numberedNeighbors = sketch.grid.getNeighbors(sTile)
                        .filter(Tile::isNumberVisible)

                    for (sTile2 in numberedNeighbors) {
                        val coveredIdx2 = sketch.grid.getNeighborsIdx(sTile2)
                            .filter { idx -> sketchTiles[idx].status === TileStatus.COVERED }

                        val bNear2 = sTile2.bombsNear
                        val totalCovers2 = coveredIdx2.size
                        val count = coveredIdx2
                            .count { idx: Int -> sketchTiles[idx].customFlag.contains('?') }

                        if (count > 0) {
                            when (matchLogic(bNear, bNear2, totalCovers, totalCovers2, count)) {
                                CASE_2_1 -> {
                                    for (idx in coveredIdx) {
                                        if (!coveredIdx2.contains(idx)) {
                                            markBomb(idx)
                                        }
                                    }
                                    for (idx in coveredIdx2) {
                                        if (!coveredIdx.contains(idx)) {
                                            reveal(idx)
                                        }
                                    }
                                    changesMade = true
                                }
                                CASE_1_1 -> {
                                    for (idx in coveredIdx2) {
                                        if (!coveredIdx.contains(idx)) {
                                            reveal(idx)
                                        }
                                    }
                                    changesMade = true
                                }
                            }
                        }
                    }
                    coveredIdx
                        .map { idx -> sketchTiles[idx] }
                        .forEach { tile -> tile.customFlag.remove('?') }
                }
            }
        }
        return changesMade
    }

    private fun matchLogic(
        bNear: Int,
        bNear2: Int,
        totalCovers: Int,
        totalCovers2: Int,
        count: Int
    ): Int {
        if (bNear > bNear2) {
            val diff = bNear - bNear2
            return if (totalCovers - diff == count) CASE_2_1 else NONE
        }
        return if (bNear == bNear2) {
            if (count == totalCovers) if (totalCovers == totalCovers2) NONE else CASE_1_1 else NONE
        } else NONE
    }
}