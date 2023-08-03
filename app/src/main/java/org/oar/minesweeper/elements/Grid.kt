package org.oar.minesweeper.elements

import android.content.Context
import org.json.JSONException
import org.json.JSONObject
import org.oar.minesweeper.utils.PreferencesUtils.loadBoolean
import java.io.Serializable
import kotlin.reflect.full.createInstance

class Grid (
    val gridConfig: GridConfiguration,
    val gridSettings: GridSettings

) : Serializable, Cloneable {

    val height get() = gridConfig.height
    val width get() = gridConfig.width
    val bombs get() = gridConfig.bombs

    var tiles: MutableList<Tile> = mutableListOf()

    // GETTERS-SETTERS
    fun generate(callback: (GridStartOptions) -> Unit) {
        tiles.clear()
        try {
            val gen = gridSettings.generatorClass.createInstance()
            gen.generateNewGrid(this, callback)

        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        }
    }

    fun getGeneratorClass(): String {
        return gridSettings.generatorClass.qualifiedName ?: ""
    }

    companion object {
        @Throws(JSONException::class)
        fun jsonGrid(context: Context, obj: JSONObject): Grid {
            val height = obj.getInt("h")
            val width = obj.getInt("w")
            val tilesList: MutableList<Tile> = mutableListOf()
            var bombs = 0
            val stringGrid = obj.getString("ts")

            for (i in stringGrid.indices) {
                var t: Tile? = null
                when (stringGrid[i]) {
                    'B' -> t = Tile(i % width, i / width, Tile.Status.BOMB)
                    ' ' -> t = Tile(i % width, i / width, Tile.Status.A0)
                    '1' -> t = Tile(i % width, i / width, Tile.Status.A1)
                    '2' -> t = Tile(i % width, i / width, Tile.Status.A2)
                    '3' -> t = Tile(i % width, i / width, Tile.Status.A3)
                    '4' -> t = Tile(i % width, i / width, Tile.Status.A4)
                    '5' -> t = Tile(i % width, i / width, Tile.Status.A5)
                    '6' -> t = Tile(i % width, i / width, Tile.Status.A6)
                    '7' -> t = Tile(i % width, i / width, Tile.Status.A7)
                    '8' -> t = Tile(i % width, i / width, Tile.Status.A8)
                    'f' -> t = Tile(i % width, i / width, Tile.Status.FLAG)
                    'c' -> t = Tile(i % width, i / width, Tile.Status.COVERED)
                    'F' -> {
                        t = Tile(i % width, i / width, Tile.Status.FLAG)
                        t.plantBomb()
                        bombs++
                    }
                    'C' -> {
                        t = Tile(i % width, i / width, Tile.Status.COVERED)
                        t.plantBomb()
                        bombs++
                    }
                }
                if (t != null) tilesList.add(t)
            }

            val isSolvable = if (obj.has("gs"))
                    obj.getBoolean("gs")
                else
                    false

            val gridConfig = GridConfiguration(width, height, bombs)
            val gridSettings = GridSettings(
                context.loadBoolean("lastRevealFirst", true),
                isSolvable,
                context.loadBoolean("lastVisualHelp", false),
            )

            return Grid(gridConfig, gridSettings).apply {
                tiles = tilesList
            }
        }
    }

    public override fun clone(): Grid {
        return Grid(gridConfig, gridSettings).also { grid ->
            tiles.forEach { grid.tiles.add(it.clone()) }
        }
    }
}