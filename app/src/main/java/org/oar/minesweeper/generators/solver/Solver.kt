package org.oar.minesweeper.generators.solver

import org.oar.minesweeper.utils.GridUtils.getTileStatus
import org.oar.minesweeper.utils.GridUtils.getNeighborsIdx
import org.oar.minesweeper.control.MainLogic
import org.oar.minesweeper.elements.Tile
import java.util.HashSet

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
                if (tile.status !== Tile.Status.COVERED && sTile.status === Tile.Status.COVERED) {
                    val bombsNear = sTile.bombsNear
                    sTile.status = getTileStatus(bombsNear)
                    if (bombsNear > 0) sketch.addNumbered(i) else sketch.addUncovered(i)
                }
            }
        }

        val leftBombs: Set<Int>?
            get() {
                val li: MutableSet<Int> = HashSet()
                for (i in sketch.numbered) {
                    val sTile = sketch.tiles[i]
                    val neigh = getNeighborsIdx(sketch.grid, sTile)
                    for (n in neigh) {
                        val t = logicTiles[n]
                        if (t.status === Tile.Status.COVERED && t.hasBomb) li.add(n)
                    }
                }
                if (li.size == 0) {
                    for (i in logicTiles.indices) {
                        val t = logicTiles[i]
                        if (t.status === Tile.Status.COVERED && t.hasBomb) li.add(i)
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
        if (logicTiles[index].status === Tile.Status.FLAG) return
        logicTiles[index].status = Tile.Status.FLAG

        val sketchTiles = sketch.tiles

        val tile = sketchTiles[index]
        tile.status = Tile.Status.FLAG
        tile.customFlag.add('X')
        tile.defuseBomb()

        getNeighborsIdx(sketch.grid, tile)
            .onEach { idx ->
                val t = sketchTiles[idx]
                t.doesntHaveBombNear()
                if (t.isNumberVisible) {
                    val bombs = t.bombsNear
                    t.status = getTileStatus(bombs)
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

            if (tile.status === Tile.Status.A0) {
                sketch.addUncovered(idx)
                sTile.status = Tile.Status.A0
                for (idxChild in getNeighborsIdx(sketch.grid, sTile)) {
                    tileUpdate(idxChild)
                }

            } else {
                sketch.addNumbered(idx)
                sTile.status = getTileStatus(sTile.bombsNear)
            }
        }
    }
}