package org.oar.minesweeper.control

import android.graphics.Canvas
import android.graphics.Rect
import org.oar.minesweeper.control.ScreenProperties.BUTTON_PANEL_HEIGHT
import org.oar.minesweeper.control.ScreenProperties.HEIGHT
import org.oar.minesweeper.control.ScreenProperties.STATUS_BAR_HEIGHT
import org.oar.minesweeper.control.ScreenProperties.WIDTH
import org.oar.minesweeper.elements.GridPosition

class CanvasWrapper(
    val canvas: Canvas,
    gridPosition: GridPosition
) {

    val visibleSpace = Rect()

    init {
        val posX = gridPosition.posX
        val posY = gridPosition.posY
        val scale = gridPosition.scale

        canvas.save()
        canvas.translate(posX, posY)
        canvas.scale(scale, scale)

        visibleSpace.left = (-posX / scale).toInt()
        visibleSpace.right = ((WIDTH - posX) / scale).toInt()
        visibleSpace.top = (-posY / scale).toInt()
        visibleSpace.bottom = ((HEIGHT + STATUS_BAR_HEIGHT + BUTTON_PANEL_HEIGHT - posY) / scale).toInt()
    }

    fun end() {
        canvas.restore()
    }
}