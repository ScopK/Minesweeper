package org.oar.minesweeper.ui.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import org.oar.minesweeper.grid.GridPosition
import org.oar.minesweeper.control.CanvasWrapper
import org.oar.minesweeper.grid.GridDrawer
import org.oar.minesweeper.models.Grid
import org.oar.minesweeper.skins.Skin
import org.oar.minesweeper.skins.WinSkin

open class GridViewerView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var sampleGrid: Grid? = null
    var skin: Skin = WinSkin().apply { load(context) }
        set(skin) {
            field = skin.apply { if (!loaded) load(context) }
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        sampleGrid?.also { grid ->
            CanvasWrapper(canvas, getCanvasPosition(), this).use { canvasW ->
                GridDrawer.draw(context, canvasW, grid, skin)
            }
        }
    }

    private fun getCanvasPosition(): GridPosition {
        val tileSize = (skin.defaultTileSize - 1).toFloat()
        val widthToDisplay = (sampleGrid!!.width + 2) * tileSize

        val scale = measuredWidth / widthToDisplay
        val position = tileSize * scale
        return GridPosition(position, position, scale)
    }
}