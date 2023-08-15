package org.oar.minesweeper.generators.solver

import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.models.TileStatus

class Sketch(grid: Grid) : Iterable<Tile?> {
    val grid: Grid = grid.clone()

    val numbered: MutableList<Int> = mutableListOf()
    val numberedCopy: MutableList<Int>
        get() = numbered.toMutableList()

    val uncovered: MutableList<Int> = mutableListOf()
    val uncoveredCopy: MutableList<Int>
        get() = uncovered.toMutableList()

    val tiles: List<Tile>
        get() = grid.tiles

    init {
        tiles.indices.forEach { i ->
            val t = getTile(i)
            if (t.isUncovered) {
                uncovered.add(i)
                if (t.status !== TileStatus.A0) {
                    numbered.add(i)
                }
            }
        }
    }

    override fun iterator(): MutableIterator<Tile> {
        return grid.tiles.iterator()
    }

    fun getTile(idx: Int): Tile {
        return tiles[idx]
    }

    fun addUncovered(i: Int) {
        uncovered.add(i)
    }

    fun addNumbered(i: Int) {
        numbered.add(i)
        uncovered.add(i)
    }

    fun removeNumbered(i: Int) {
        numbered.remove(i)
    }
}