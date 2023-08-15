package org.oar.minesweeper.control

import android.content.Context
import android.graphics.Rect
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.elements.TileStatus
import org.oar.minesweeper.skins.Skin
import org.oar.minesweeper.utils.ContextUtils.findColor
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


object GridDrawer {
    private lateinit var skin: Skin

    val tileSize: Int
        get() = skin.defaultTileSize - 1

    fun setSkin(context: Context, skinClass: KClass<out Skin>, coverHueColor: Int) {
        try {
            skin = skinClass.createInstance().apply {
                coverHue = coverHueColor
                load(context)
            }
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
        skin: Skin = this.skin,
        isGameOver: Boolean = false,
    ) {
        canvasW.canvas.drawColor(context.findColor(skin.backgroundColor))
        for (tile in grid.tiles) {
            draw(canvasW, tile, grid, skin, isGameOver)
        }
    }

    private fun draw(
        canvasW: CanvasWrapper,
        tile: Tile,
        grid: Grid,
        skin: Skin,
        isGameOver: Boolean
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
                TileStatus.COVERED ->    skin.drawCover(canvas, x, y, hashCode)
                TileStatus.A0 ->         skin.drawEmpty(canvas, x, y)
                TileStatus.BOMB ->       skin.drawBomb(canvas, x, y, hashCode)
                TileStatus.BOMB_FINAL -> skin.drawBomb(canvas, x, y, hashCode, true)
                TileStatus.FLAG ->       skin.drawFlag(canvas, x, y, hashCode, if (isGameOver) Skin.FLAG_OK else Skin.FLAG)
                TileStatus.FLAG_FAIL ->  skin.drawFlag(canvas, x, y, hashCode, Skin.FLAG_FAIL)

                else -> {
                    tile.status.tileNumber?.also {
                        skin.drawNumber(canvas, x, y, it, tile.flaggedNear, grid.gridSettings.visualHelp)
                    }
                }
            }
        }
    }
}