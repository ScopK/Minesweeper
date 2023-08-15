package org.oar.minesweeper.utils

import org.json.JSONException
import org.json.JSONObject
import org.oar.minesweeper.control.CanvasPosition
import org.oar.minesweeper.control.GridDrawer.tileSize
import org.oar.minesweeper.control.MainLogic
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.GridConfiguration
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.elements.TileStatus.*
import java.util.*
import kotlin.math.roundToInt

object GridUtils {

    fun Grid.getNeighbors(tile: Tile): List<Tile> {
        val tiles: List<Tile> = tiles
        return getNeighborsIdx(tile)
            .map { idx -> tiles[idx] }
    }

    fun Grid.getNeighborsIdx(tile: Tile): MutableList<Int> {
        return gridConfig.getNeighborsIdx(tile)
    }

    fun GridConfiguration.getNeighborsIdx(tile: Tile): MutableList<Int> {
        val neighbors: MutableList<Int> = ArrayList()
        val idx = tile.y * width + tile.x

        // Test (w=7, h=3):
        // 0  1  2  3  4  5  6
        // 7  8  9 10 11 12 13
        //14 15 16 17 18 19 20

        val checkLeft = idx % width != 0
        val checkTop = idx >= width
        val checkRight = ((idx + 1) % width) != 0
        val checkBottom = idx < width * (height - 1)
        if (checkLeft) {
            neighbors.add(idx - 1) // L
            if (checkTop) {
                neighbors.add(idx - width) // T
                neighbors.add(idx - width - 1) // TL
            }
            if (checkBottom) {
                neighbors.add(idx + width) // B
                neighbors.add(idx + width - 1) // BL
            }
        }
        if (checkRight) {
            neighbors.add(idx + 1) // R
            if (checkTop) {
                if (!checkLeft) neighbors.add(idx - width) // T
                neighbors.add(idx - width + 1) // TR

            }
            if (checkBottom) {
                if (!checkLeft) neighbors.add(idx + width) // B
                neighbors.add(idx + width + 1) // BR
            }
        } else if (!checkLeft) {
            if (checkTop) { // T
                neighbors.add(idx - width)
            }
            if (checkBottom) { // B
                neighbors.add(idx + width)
            }
        }
        return neighbors
    }


    fun Grid.getTileByScreenCoords(x: Float, y: Float, canvasPosition: CanvasPosition): Tile? {
        val tileSize = tileSize
        val scale = canvasPosition.scale
        val relativeX = x - canvasPosition.posX
        val relativeY = y - canvasPosition.posY

        if (relativeX < 0 || relativeY < 0) return null

        return getTileByCoords(
            (relativeX / scale).roundToInt() / tileSize,
            (relativeY / scale).roundToInt() / tileSize)
    }

    fun Grid.getTileByCoords(x: Int, y: Int): Tile? {
        if (x >= width) return null

        val idx = y * width + x
        return if (idx >= 0 && idx < tiles.size) tiles[idx] else null
    }

    fun Grid.findSafeOpenTile(): Tile {
        return tiles[findSafeOpenTileIdx()]
    }

    fun Grid.findSafeOpenTileIdx(): Int {
        val rnd = Random()
        val tiles = tiles
        var idx: Int
        do {
            idx = rnd.nextInt(tiles.size)
            val tile = tiles[idx]
        } while (tile.hasBomb || tile.bombsNear > 0 || tile.status !== COVERED)
        return idx
    }

    fun Grid.toJson(seconds: Int, canvasPosition: CanvasPosition): JSONObject {
        val obj = JSONObject()

        try {
            obj.put("w", width)
            obj.put("h", height)
            obj.put("gs", gridSettings.solvable)
            obj.put("x", canvasPosition.posX)
            obj.put("y", canvasPosition.posY)
            obj.put("s", canvasPosition.scale)
            obj.put("t", seconds)
            obj.put("ts",
                tiles
                    .joinToString("") { tile ->
                        when (tile.status) {
                            BOMB -> "B"
                            FLAG -> if (tile.hasBomb) "F" else "f"
                            COVERED -> if (tile.hasBomb) "C" else "c"
                            A1, A2, A3, A4, A5, A6, A7, A8 -> tile.status.tileNumber.toString()
                            else -> " "
                        }
                    }
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return obj
    }

    fun Grid.generateLogic(): MainLogic {
        val logic = MainLogic(this)

        tiles.forEach { tile ->
            if (tile.hasBomb) {
                getNeighbors(tile).forEach { it.hasBombNear() }
            }
            if (!tile.isCovered) {
                logic.addRevealedTiles()
            }
            if (tile.status === FLAG) {
                getNeighbors(tile).forEach { it.addFlaggedNear() }
                logic.addFlaggedBombs()
                if (tile.hasBomb) {
                    logic.addCorrectFlaggedBombs()
                }
            }
        }

        return logic
    }
}