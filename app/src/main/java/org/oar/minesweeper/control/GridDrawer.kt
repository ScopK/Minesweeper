package org.oar.minesweeper.control

import android.content.Context
import android.graphics.Rect
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.skins.Skin
import org.oar.minesweeper.utils.ActivityController.findColor
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


object GridDrawer {
    private lateinit var skin: Skin

    val tileSize: Int
        get() = skin.defaultTileSize - 1

    fun setSkin(context: Context, skinClass: KClass<out Skin>) {
        try {
            skin = skinClass.createInstance().apply { load(context) }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        }
    }

    fun draw(context: Context, canvasW: CanvasWrapper, grid: Grid) {
        canvasW.canvas.drawColor(context.findColor(skin.backgroundColor))
        for (tile in grid.tiles) {
            draw(canvasW, tile, grid)
        }
    }

    private fun draw(canvasW: CanvasWrapper, tile: Tile, grid: Grid) {
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
            val hashCode = tile.hashCode()
            when (tile.status) {
                Tile.Status.COVERED ->
                    skin.drawCover(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), hashCode)
                Tile.Status.A0 ->
                    skin.drawEmpty(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat())
                Tile.Status.A1 ->
                    skin.drawNumber(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 1,
                        tile.flaggedNear, grid.gridSettings.visualHelp)
                Tile.Status.A2 ->
                    skin.drawNumber(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 2,
                        tile.flaggedNear, grid.gridSettings.visualHelp)
                Tile.Status.A3 ->
                    skin.drawNumber(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 3,
                        tile.flaggedNear, grid.gridSettings.visualHelp)
                Tile.Status.A4 ->
                    skin.drawNumber(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 4,
                        tile.flaggedNear, grid.gridSettings.visualHelp)
                Tile.Status.A5 ->
                    skin.drawNumber(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 5,
                        tile.flaggedNear, grid.gridSettings.visualHelp)
                Tile.Status.A6 ->
                    skin.drawNumber(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 6,
                        tile.flaggedNear, grid.gridSettings.visualHelp)
                Tile.Status.A7 ->
                    skin.drawNumber(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 7,
                        tile.flaggedNear, grid.gridSettings.visualHelp)
                Tile.Status.A8 ->
                    skin.drawNumber(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), 8,
                        tile.flaggedNear, grid.gridSettings.visualHelp)
                Tile.Status.BOMB ->
                    skin.drawBomb(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), hashCode)
                Tile.Status.BOMB_FINAL ->
                    skin.drawBomb(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), hashCode, true)
                Tile.Status.FLAG ->
                    skin.drawFlag(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), hashCode)
                Tile.Status.FLAG_FAIL ->
                    skin.drawFlag(canvas, (tile.x * size).toFloat(), (tile.y * size).toFloat(), hashCode, true)
            }
        }
    }
}