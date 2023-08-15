package org.oar.minesweeper.elements

import org.oar.minesweeper.control.GridDrawer
import org.oar.minesweeper.control.ScreenProperties.HEIGHT
import org.oar.minesweeper.control.ScreenProperties.WIDTH
import org.oar.minesweeper.control.ScreenProperties.toDpi

class GridPosition(
    posX: Float = 0f,
    posY: Float = 0f,
    scale: Float = 0.2491268f.toDpi(),
) {
    var posX = posX
        private set
    var posY = posY
        private set
    var scale = scale
        private set

    private val minScale = 0.05f.toDpi()
    private val maxScale = 0.8f.toDpi()
    private var contentWidth = 0f
    private var contentHeight = 0f

    fun setValues(posX: Float, posY: Float, scale: Float) {
        this.posX = posX
        this.posY = posY
        this.scale = scale
    }

    fun translate(x: Float, y: Float) {
        posX += x
        posY += y

        // Check limits:
        val widthHalf = WIDTH / 2f
        val heightHalf = HEIGHT / 2f
        if (posX > widthHalf) posX =
            widthHalf else if (posX < widthHalf - contentWidth * scale) posX =
            widthHalf - contentWidth * scale
        if (posY > heightHalf) posY =
            heightHalf else if (posY < heightHalf - contentHeight * scale) posY =
            heightHalf - contentHeight * scale
    }

    fun zoom(zI: Float) {
        var z = zI
        var newScale = scale * z
        newScale = Math.max(minScale, Math.min(newScale, maxScale))
        z = newScale / scale
        scale = newScale

        // Use center screen as anchor:
        val widthHalf = WIDTH / 2f
        posX = widthHalf - (widthHalf - posX) * z
        val heightHalf = HEIGHT / 2f
        posY = heightHalf - (heightHalf - posY) * z
    }

    fun focus(x: Int, y: Int) {
        val tileSize = GridDrawer.tileSize
        posX = WIDTH / 2f - (x + .5f) * tileSize * scale
        posY = HEIGHT / 2f - (y + .5f) * tileSize * scale
    }

    fun focus(tile: Tile) {
        focus(tile.x, tile.y)
    }

    fun setContentDimensions(w: Float, h: Float) {
        contentWidth = w
        contentHeight = h
        translate(0f, 0f)
    }
}