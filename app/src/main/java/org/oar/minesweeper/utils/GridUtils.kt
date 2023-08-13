package org.oar.minesweeper.utils

import org.json.JSONException
import org.json.JSONObject
import org.oar.minesweeper.control.CanvasPosition
import org.oar.minesweeper.control.GridDrawer.tileSize
import org.oar.minesweeper.control.MainLogic
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.GridConfiguration
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.elements.Tile.Status.*
import java.util.*
import kotlin.math.roundToInt

object GridUtils {

	fun getNeighbors(grid: Grid, tile: Tile): List<Tile> {
        val tiles: List<Tile> = grid.tiles
        return getNeighborsIdx(grid, tile)
            .map { idx -> tiles[idx] }
    }

    fun getNeighborsIdx(grid: Grid, tile: Tile): MutableList<Int> {
        return getNeighborsIdx(grid.gridConfig, tile)
    }

    fun getNeighborsIdx(gridConfig: GridConfiguration, tile: Tile): MutableList<Int> {
        val neighbors: MutableList<Int> = ArrayList()
        val w = gridConfig.width
        val h = gridConfig.height
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

    fun getTileByScreenCoords(grid: Grid, x: Float, y: Float, canvasPosition: CanvasPosition): Tile? {
        val tileSize = tileSize
        val scale = canvasPosition.scale
        val relativeX = x - canvasPosition.posX
        val relativeY = y - canvasPosition.posY

        if (relativeX < 0 || relativeY < 0) return null

        return getTileByCoords(grid,
            (relativeX / scale).roundToInt() / tileSize,
            (relativeY / scale).roundToInt() / tileSize)
    }

    fun getTileByCoords(grid: Grid, x: Int, y: Int): Tile? {
        if (x >= grid.width) return null

        val idx = y * grid.width + x
        return if (idx >= 0 && idx < grid.tiles.size) grid.tiles[idx] else null
    }

    fun findSafeOpenTile(grid: Grid): Tile {
        return grid.tiles[findSafeOpenTileIdx(grid)]
    }

    fun findSafeOpenTileIdx(grid: Grid): Int {
        val rnd = Random()
        val tiles = grid.tiles
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

    fun getJsonStatus(grid: Grid, seconds: Int, canvasPosition: CanvasPosition): JSONObject {
        val obj = JSONObject()

        try {
            obj.put("w", grid.width)
            obj.put("h", grid.height)
            obj.put("gs", grid.gridSettings.solvable)
            obj.put("x", canvasPosition.posX)
            obj.put("y", canvasPosition.posY)
            obj.put("s", canvasPosition.scale)
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
                getNeighbors(grid, tile).forEach { it.hasBombNear() }
            }
            if (!tile.isCovered) {
                logic.addRevealedTiles()
            }
            if (tile.status === FLAG) {
                getNeighbors(grid, tile).forEach { it.addFlaggedNear() }
                logic.addFlaggedBombs()
                if (tile.hasBomb) {
                    logic.addCorrectFlaggedBombs()
                }
            }
        }

        return logic
    }
}