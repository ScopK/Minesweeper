package org.oar.minesweeper.elements

import org.oar.minesweeper.generators.GridGenerator
import kotlin.Throws
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.util.ArrayList
import java.util.function.Consumer
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class Grid(
    val w: Int,
    val h: Int,
    val bombs: Int,
    private var generatorClass: Class<out GridGenerator>

) : Serializable, Cloneable {

    var tiles: MutableList<Tile> = mutableListOf()

    // GETTERS-SETTERS
    fun generate(callback: Runnable) {
        tiles.clear()
        try {
            val gen = generatorClass.newInstance()
            gen.generateNewGrid(this, bombs, callback)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        }
    }

    fun getGeneratorClass(): String {
        return generatorClass.name
    }

    fun startNewGame(callback: Runnable): Grid {
        return Grid(w, h, bombs, generatorClass).apply {
            generate(callback)
        }
    }

    companion object {
        @Throws(JSONException::class)
        fun jsonGrid(obj: JSONObject): Grid {
            val h = obj.getInt("h")
            val w = obj.getInt("w")
            val tilesList: MutableList<Tile> = mutableListOf()
            var bombs = 0
            val stringGrid = obj.getString("ts")

            for (i in stringGrid.indices) {
                var t: Tile? = null
                when (stringGrid[i]) {
                    'B' -> t = Tile(i % w, i / w, Tile.Status.BOMB)
                    ' ' -> t = Tile(i % w, i / w, Tile.Status.A0)
                    '1' -> t = Tile(i % w, i / w, Tile.Status.A1)
                    '2' -> t = Tile(i % w, i / w, Tile.Status.A2)
                    '3' -> t = Tile(i % w, i / w, Tile.Status.A3)
                    '4' -> t = Tile(i % w, i / w, Tile.Status.A4)
                    '5' -> t = Tile(i % w, i / w, Tile.Status.A5)
                    '6' -> t = Tile(i % w, i / w, Tile.Status.A6)
                    '7' -> t = Tile(i % w, i / w, Tile.Status.A7)
                    '8' -> t = Tile(i % w, i / w, Tile.Status.A8)
                    'f' -> t = Tile(i % w, i / w, Tile.Status.FLAG)
                    'c' -> t = Tile(i % w, i / w, Tile.Status.COVERED)
                    'F' -> {
                        t = Tile(i % w, i / w, Tile.Status.FLAG)
                        t.plantBomb()
                        bombs++
                    }
                    'C' -> {
                        t = Tile(i % w, i / w, Tile.Status.COVERED)
                        t.plantBomb()
                        bombs++
                    }
                }
                if (t != null) tilesList.add(t)
            }

            val generatorClass = Class.forName(obj.getString("g")) as Class<out GridGenerator>
            return Grid(w, h, bombs, generatorClass).apply {
                tiles = tilesList
            }
        }
    }

    public override fun clone(): Grid {
        return Grid(w, h, bombs, generatorClass).also { grid ->
            tiles.forEach { grid.tiles.add(it.clone()) }
        }
    }
}