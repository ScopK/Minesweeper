package org.oar.minesweeper.grid

import android.content.Context
import android.graphics.Rect
import org.oar.minesweeper.control.CanvasWrapper
import org.oar.minesweeper.models.Grid
import org.oar.minesweeper.models.Tile
import org.oar.minesweeper.models.TileStatus
import org.oar.minesweeper.skins.Skin
import org.oar.minesweeper.utils.ContextUtils.findColor
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


object GridDrawer {
    private lateinit var skin: Skin

    var tileSize: Int = 0
        private set

    fun setSkin(context: Context, skinClass: KClass<out Skin>, coverHueColor: Int) {
        try {
            skin = skinClass.createInstance().apply {
                coverHue = coverHueColor
                load(context)
            }
            tileSize = skin.defaultTileSize - 1

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
        skin: Skin = GridDrawer.skin,
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
        val dim = Rect().apply {
            left = tile.x * tileSize
            right = left + tileSize
            top = tile.y * tileSize
            bottom = top + tileSize
        }

        if (Rect.intersects(canvasW.viewport, dim)) {
            val canvas = canvasW.canvas
            val hashCode = tile.hashCode()

            val x = dim.left.toFloat()
            val y = dim.top.toFloat()

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