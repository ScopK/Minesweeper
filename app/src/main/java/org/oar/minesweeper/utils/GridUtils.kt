package org.oar.minesweeper.utils

import org.json.JSONException
import org.json.JSONObject
import org.oar.minesweeper.control.CanvasWrapper
import org.oar.minesweeper.control.GridDrawer.tileSize
import org.oar.minesweeper.control.MainLogic
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.elements.Tile.Status.*
import java.util.*
import java.util.function.Consumer
import kotlin.math.roundToInt

object GridUtils {

    @JvmStatic
	fun getNeighbors(grid: Grid, tile: Tile): List<Tile> {
        val tiles: List<Tile> = grid.tiles
        return getNeighborsIdx(grid, tile)
            .map { idx -> tiles[idx] }
    }

    fun getNeighborsIdx(grid: Grid, tile: Tile): MutableList<Int> {
        val neighbors: MutableList<Int> = ArrayList()
        val w = grid.w
        val h = grid.h
        val idx = tile.y * w + tile.x

        // Test (w=7, h=3):
        // 0  1  2  3  4  5  6
        // 7  8  9 10 11 12 13
        //14 15 16 17 18 19 20

        val checkLeft = idx % w != 0
        val checkTop = idx >= w
        val checkRight = ((idx + 1) % w) != 0
        val checkBottom = idx < w * (h - 1)
        if (checkLeft) {
            neighbors.add(idx - 1) // L
            if (checkTop) {
                neighbors.add(idx - w) // T
                neighbors.add(idx - w - 1) // TL
            }
            if (checkBottom) {
                neighbors.add(idx + w) // B
                neighbors.add(idx + w - 1) // BL
            }
        }
        if (checkRight) {
            neighbors.add(idx + 1) // R
            if (checkTop) {
                if (!checkLeft) neighbors.add(idx - w) // T
                neighbors.add(idx - w + 1) // TR

            }
            if (checkBottom) {
                if (!checkLeft) neighbors.add(idx + w) // B
                neighbors.add(idx + w + 1) // BR
            }
        } else if (!checkLeft) {
            if (checkTop) { // T
                neighbors.add(idx - w)
            }
            if (checkBottom) { // B
                neighbors.add(idx + w)
            }
        }
        return neighbors
    }

    fun getTileByScreenCoords(grid: Grid, x: Float, y: Float): Tile? {
        val tileSize = tileSize
        val scale: Float = CanvasWrapper.scale
        val posX: Float = CanvasWrapper.posX
        val posY: Float = CanvasWrapper.posY
        return getTileByCoords(grid,
            ((-posX + x) / scale).roundToInt() / tileSize,
            ((-posY + y) / scale).roundToInt() / tileSize)
    }

    fun getTileByCoords(grid: Grid, x: Int, y: Int): Tile? {
        val w = grid.w
        val idx = y * w + x
        return if (idx >= 0 && idx < grid.tiles.size) grid.tiles[idx] else null
    }

    fun findSafeOpenTile(grid: Grid): Tile {
        return grid.tiles[findSafeOpenTileIdx(grid)]
    }

    fun findSafeOpenTileIdx(grid: Grid): Int {
        val rnd = Random()
        val tiles= grid.tiles
        var idx: Int
        do {
            idx = rnd.nextInt(tiles.size)
            val tile = tiles[idx]
        } while (tile.hasBomb || tile.bombsNear > 0 || tile.status !== COVERED)
        return idx
    }

    fun getTileNumber(tile: Tile): Int {
        return when (tile.status) {
            A1 -> 1
            A2 -> 2
            A3 -> 3
            A4 -> 4
            A5 -> 5
            A6 -> 6
            A7 -> 7
            A8 -> 8
            else -> -1
        }
    }

	fun getTileStatus(value: Int): Tile.Status {
        return when (value) {
            1 -> A1
            2 -> A2
            3 -> A3
            4 -> A4
            5 -> A5
            6 -> A6
            7 -> A7
            8 -> A8
            else -> A0
        }
    }

    fun getJsonStatus(grid: Grid, seconds: Int): JSONObject {
        val obj = JSONObject()

        try {
            obj.put("w", grid.w)
            obj.put("h", grid.h)
            obj.put("g", grid.getGeneratorClass())
            obj.put("x", CanvasWrapper.posX)
            obj.put("y", CanvasWrapper.posY)
            obj.put("s", CanvasWrapper.scale)
            obj.put("t", seconds)
            obj.put("ts",
                grid.tiles
                    .joinToString("") { tile ->
                        when (tile.status) {
                            BOMB -> "B"
                            FLAG -> if (tile.hasBomb) "F" else "f"
                            COVERED -> if (tile.hasBomb) "C" else "c"
                            A1 -> "1"
                            A2 -> "2"
                            A3 -> "3"
                            A4 -> "4"
                            A5 -> "5"
                            A6 -> "6"
                            A7 -> "7"
                            A8 -> "8"
                            A0 -> " "
                            else -> " "
                        }
                    }
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return obj
    }

    fun calculateLogicFromBareGrid(grid: Grid): MainLogic {
        val logic = MainLogic(grid)

        grid.tiles.forEach { tile ->
            if (tile.hasBomb) {
                getNeighbors(grid, tile).forEach(Consumer { obj: Tile -> obj.hasBombNear() })
            }
            if (!tile.isCovered) {
                logic.addRevealedTiles()
            }
            if (tile.status === FLAG) {
                getNeighbors(grid, tile).forEach(Consumer { obj: Tile -> obj.addFlaggedNear() })
                logic.addFlaggedBombs()
                if (tile.hasBomb) {
                    logic.addCorrectFlaggedBombs()
                }
            }
        }

        return logic
    }
}