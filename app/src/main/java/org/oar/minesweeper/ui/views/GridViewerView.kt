package org.oar.minesweeper.ui.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import org.oar.minesweeper.control.CanvasPosition
import org.oar.minesweeper.control.CanvasWrapper
import org.oar.minesweeper.control.GridDrawer
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.skins.Skin
import org.oar.minesweeper.skins.WinSkin

open class GridViewerView(
    context: Context,
    attrs: AttributeSet?,
) : View(context, attrs) {

    var sampleGrid: Grid? = null
    var skin: Skin = WinSkin().apply { load(context) }
        set(skin) {
            field = skin.apply { if (!loaded) load(context) }
        }

    constructor(context: Context) : this(context, null)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        sampleGrid?.also {
            val pos = getCanvasPosition()
            val canvasW = CanvasWrapper(canvas, pos)
            GridDrawer.draw(context, canvasW, it, skin)
        }
    }

    private fun getCanvasPosition(): CanvasPosition {
        val tileSize = (skin.defaultTileSize - 1).toFloat()
        val widthToDisplay = (sampleGrid!!.width + 2) * tileSize

        val scale = measuredWidth / widthToDisplay
        val position = tileSize * scale
        return CanvasPosition(position, position, scale)
    }
}