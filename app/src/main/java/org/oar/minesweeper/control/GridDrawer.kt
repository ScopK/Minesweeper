package org.oar.minesweeper.control

import android.content.Context
import android.graphics.Rect
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.skins.Skin
import org.oar.minesweeper.utils.ActivityController.findColor
import org.oar.minesweeper.utils.GridUtils
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

    fun draw(
        context: Context,
        canvasW: CanvasWrapper,
        grid: Grid,
        skin: Skin = this.skin
    ) {
        canvasW.canvas.drawColor(context.findColor(skin.backgroundColor))
        for (tile in grid.tiles) {
            draw(canvasW, tile, grid, skin)
        }
    }

    private fun draw(
        canvasW: CanvasWrapper,
        tile: Tile,
        grid: Grid,
        skin: Skin = this.skin
    ) {
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

            val x = (tile.x * size).toFloat()
            val y = (tile.y * size).toFloat()

            when (tile.status) {
                Tile.Status.COVERED ->    skin.drawCover(canvas, x, y, hashCode)
                Tile.Status.A0 ->         skin.drawEmpty(canvas, x, y)
                Tile.Status.BOMB ->       skin.drawBomb(canvas, x, y, hashCode)
                Tile.Status.BOMB_FINAL -> skin.drawBomb(canvas, x, y, hashCode, true)
                Tile.Status.FLAG ->       skin.drawFlag(canvas, x, y, hashCode)
                Tile.Status.FLAG_FAIL ->  skin.drawFlag(canvas, x, y, hashCode, true)

                else -> {
                    val numValue = GridUtils.getTileNumber(tile)
                    if (numValue > 0) {
                        skin.drawNumber(canvas, x, y, numValue, tile.flaggedNear, grid.gridSettings.visualHelp)
                    }
                }
            }
        }
    }
}