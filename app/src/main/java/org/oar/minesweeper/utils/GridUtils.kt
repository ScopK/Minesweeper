package org.oar.minesweeper.utils

import android.content.Context
import org.json.JSONException
import org.json.JSONObject
import org.oar.minesweeper.grid.GridPosition
import org.oar.minesweeper.grid.GridDrawer.tileSize
import org.oar.minesweeper.grid.GameLogic
import org.oar.minesweeper.models.Grid
import org.oar.minesweeper.models.*
import org.oar.minesweeper.models.TileStatus.*
import org.oar.minesweeper.utils.PreferencesUtils.loadBoolean
import java.util.*
import kotlin.math.roundToInt
import kotlin.reflect.full.createInstance

object GridUtils {

    fun Grid.generate(callback: (GridGenerationDetails) -> Unit) {
        tiles.clear()
        val gen = gridSettings.generatorClass.createInstance()
        gen.generateNewGrid(this, callback)
    }

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


    fun Grid.getTileByScreenCoords(x: Float, y: Float, gridPosition: GridPosition): Tile? {
        val scale = gridPosition.scale
        val relativeX = (x - gridPosition.posX) / scale
        val relativeY = (y - gridPosition.posY) / scale

        if (relativeX < 0 || relativeY < 0) return null

        return getTileByCoords(
            relativeX.roundToInt() / tileSize,
            relativeY.roundToInt() / tileSize)
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

    fun Grid.generateLogic(): GameLogic {
        val logic = GameLogic(this)

        tiles.forEach { tile ->
            if (tile.hasBomb) {
                getNeighbors(tile).forEach { it.bombsNear++ }
            }
            if (!tile.isCovered) {
                logic.addRevealedTiles()
            }
            if (tile.status === FLAG) {
                getNeighbors(tile).forEach { it.flaggedNear++ }
                logic.addFlaggedBombs()
                if (tile.hasBomb) {
                    logic.addCorrectFlaggedBombs()
                }
            }
        }

        return logic
    }

    fun Grid.toJson(deciSeconds: Int, gridPosition: GridPosition): JSONObject {
        val jsonObj = JSONObject()

        try {
            jsonObj.put("w", width)
            jsonObj.put("h", height)
            jsonObj.put("gs", gridSettings.solvable)
            jsonObj.put("x", gridPosition.posX)
            jsonObj.put("y", gridPosition.posY)
            jsonObj.put("s", gridPosition.scale)
            jsonObj.put("t", deciSeconds)
            jsonObj.put("ts",
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
        return jsonObj
    }

    fun Context.gridFromJson(
        jsonObj: JSONObject,
        gridSettings: GridSettings? = null
    ): Grid {
        val height = jsonObj.getInt("h")
        val width = jsonObj.getInt("w")
        val tilesList: MutableList<Tile> = mutableListOf()
        var bombs = 0
        val stringGrid = jsonObj.getString("ts")

        for (i in stringGrid.indices) {
            var t: Tile? = null
            when (stringGrid[i]) {
                'B' -> t = Tile(i % width, i / width, TileStatus.BOMB)
                ' ' -> t = Tile(i % width, i / width, TileStatus.A0)
                '1' -> t = Tile(i % width, i / width, TileStatus.A1)
                '2' -> t = Tile(i % width, i / width, TileStatus.A2)
                '3' -> t = Tile(i % width, i / width, TileStatus.A3)
                '4' -> t = Tile(i % width, i / width, TileStatus.A4)
                '5' -> t = Tile(i % width, i / width, TileStatus.A5)
                '6' -> t = Tile(i % width, i / width, TileStatus.A6)
                '7' -> t = Tile(i % width, i / width, TileStatus.A7)
                '8' -> t = Tile(i % width, i / width, TileStatus.A8)
                'f' -> t = Tile(i % width, i / width, TileStatus.FLAG)
                'c' -> t = Tile(i % width, i / width, TileStatus.COVERED)
                'F' -> {
                    t = Tile(i % width, i / width, TileStatus.FLAG)
                        .apply { hasBomb = true }
                    bombs++
                }
                'C' -> {
                    t = Tile(i % width, i / width, TileStatus.COVERED)
                        .apply { hasBomb = true }
                    bombs++
                }
            }
            if (t != null) tilesList.add(t)
        }

        val isSolvable = if (jsonObj.has("gs"))
            jsonObj.getBoolean("gs")
        else
            false

        val config = GridConfiguration(width, height, bombs)
        val settings = gridSettings ?: GridSettings(
            loadBoolean("lastRevealFirst", true),
            isSolvable,
            loadBoolean("lastVisualHelp", false),
        )

        return Grid(config, settings).apply {
            tiles = tilesList
        }
    }
}