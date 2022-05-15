package org.oar.minesweeper.control

import android.content.Context
import android.graphics.Rect
import org.oar.minesweeper.skins.MainSkin
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.skins.DotHelpSkin
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


object GridDrawer {
    private lateinit var skin: MainSkin

    val tileSize: Int
        get() = skin.tileSize

    fun setSkin(context: Context, skinClass: KClass<out MainSkin>) {
        try {
            skin = skinClass.createInstance().apply { load(context) }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        }
    }

    fun draw(canvasW: CanvasWrapper, grid: Grid) {
        canvasW.canvas.drawColor(skin.bgColor)
        for (tile in grid.tiles) {
            draw(canvasW, tile)
        }
    }

    private fun draw(canvasW: CanvasWrapper, tile: Tile) {
        val visibleSpace = canvasW.visibleSpace
        val dim = Rect()
        val size = tileSize
        dim.left = tile.x * size
        dim.right = dim.left + size
        dim.top = tile.y * size
        dim.bottom = dim.top + size

        if (visibleSpace.contains(dim) || visibleSpace.intersects(
                dim.left, dim.top, dim.right, dim.bottom)) {

            val canvas = canvasW.canvas
            skin.alternative = tile.hashCode()
            when (tile.status) {
                Tile.Status.COVERED ->
                    skin.drawCovered(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat())
                Tile.Status.A0 ->
                    skin.drawEmpty(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat())
                Tile.Status.A1 ->
                    skin.drawEmpty(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 1,
                        tile.flaggedNear)
                Tile.Status.A2 ->
                    skin.drawEmpty(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 2,
                        tile.flaggedNear)
                Tile.Status.A3 ->
                    skin.drawEmpty(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 3,
                        tile.flaggedNear)
                Tile.Status.A4 ->
                    skin.drawEmpty(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 4,
                        tile.flaggedNear)
                Tile.Status.A5 ->
                    skin.drawEmpty(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 5,
                        tile.flaggedNear)
                Tile.Status.A6 ->
                    skin.drawEmpty(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 6,
                        tile.flaggedNear)
                Tile.Status.A7 ->
                    skin.drawEmpty(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 7,
                        tile.flaggedNear)
                Tile.Status.A8 ->
                    skin.drawEmpty(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 8,
                        tile.flaggedNear)
                Tile.Status.BOMB ->
                    skin.drawEmpty(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 0)
                Tile.Status.BOMB_FINAL ->
                    skin.drawEmpty(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 1)
                Tile.Status.FLAG ->
                    skin.drawCovered(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 2)
                Tile.Status.FLAG_FAIL ->
                    skin.drawCovered(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 3)
            }
        }
    }
}