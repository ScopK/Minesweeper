package org.oar.minesweeper.generators.solver

import org.oar.minesweeper.control.MainLogic
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.elements.TileStatus
import org.oar.minesweeper.utils.GridUtils.getNeighborsIdx

abstract class Solver {

    companion object {
        private lateinit var logic: MainLogic
        lateinit var logicTiles: List<Tile>
        lateinit var sketch: Sketch

        fun setSolver(logic: MainLogic, sketch: Sketch) {
            Companion.logic = logic
            logicTiles = logic.grid.tiles
            Companion.sketch = sketch
        }

        fun copyRevealed() {
            val sketchTiles = sketch.tiles
            for (i in logicTiles.indices) {
                val tile = logicTiles[i]
                val sTile = sketchTiles[i]
                if (tile.status !== TileStatus.COVERED && sTile.status === TileStatus.COVERED) {
                    val bombsNear = sTile.bombsNear
                    sTile.status = TileStatus.findByTileNumber(bombsNear)
                    if (bombsNear > 0) sketch.addNumbered(i) else sketch.addUncovered(i)
                }
            }
        }

        val leftBombs: Set<Int>?
            get() {
                val li: MutableSet<Int> = HashSet()
                for (i in sketch.numbered) {
                    val sTile = sketch.tiles[i]
                    val neigh = sketch.grid.getNeighborsIdx(sTile)
                    for (n in neigh) {
                        val t = logicTiles[n]
                        if (t.status === TileStatus.COVERED && t.hasBomb) li.add(n)
                    }
                }
                if (li.size == 0) {
                    for (i in logicTiles.indices) {
                        val t = logicTiles[i]
                        if (t.status === TileStatus.COVERED && t.hasBomb) li.add(i)
                    }
                    if (li.size > 0) {
                        return null
                    }
                }
                return li
            }
    }


    abstract fun analyze(): Boolean

    protected fun markBomb(index: Int) {
        if (logicTiles[index].status === TileStatus.FLAG) return
        logicTiles[index].status = TileStatus.FLAG

        val sketchTiles = sketch.tiles

        val tile = sketchTiles[index]
        tile.status = TileStatus.FLAG
        tile.customFlag.add('X')
        tile.hasBomb = false

        sketch.grid.getNeighborsIdx(tile)
            .onEach { idx ->
                val t = sketchTiles[idx]
                t.doesntHaveBombNear()
                if (t.isNumberVisible) {
                    val bombs = t.bombsNear
                    t.status = TileStatus.findByTileNumber(bombs)
                    if (bombs == 0) sketch.removeNumbered(idx)
                }
            }
            .map { idx -> sketchTiles[idx] }
            .filter { it.bombsNear == 0 }
            .forEach { it.customFlag.add('0') }
    }

    protected fun reveal(idx: Int) {
        val t = logicTiles[idx]
        logic.reveal(t)
        tileUpdate(idx)
    }

    private fun tileUpdate(idx: Int) {
        val sTile = sketch.getTile(idx)
        if (sTile.isCovered) {
            val tile = logicTiles[idx]

            if (tile.status === TileStatus.A0) {
                sketch.addUncovered(idx)
                sTile.status = TileStatus.A0

                sketch.grid.getNeighborsIdx(sTile)
                    .forEach { idxChild -> tileUpdate(idxChild) }

            } else {
                sketch.addNumbered(idx)
                sTile.status = TileStatus.findByTileNumber(sTile.bombsNear)
            }
        }
    }
}