package org.oar.minesweeper.control

import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.elements.TileStatus
import org.oar.minesweeper.utils.GridUtils.getNeighbors
import java.util.function.Consumer

class MainLogic(
    val grid: Grid
) {
    enum class GameStatus {
        PLAYING, WIN, LOSE
    }

    var status = GameStatus.PLAYING
        private set
    var flaggedBombs = 0
        private set
    private var correctFlaggedBombs = 0
    private var revealedTiles = 0
    private var finishEvent: Consumer<Boolean>? = null

    val gameOver: Boolean
        get() = status == GameStatus.WIN || status == GameStatus.LOSE
    val totalTiles: Int
        get() = grid.tiles.size
    val revisedTiles: Int
        get() = revealedTiles + flaggedBombs
    val allCovered: Boolean
        get() = grid.tiles.none { it.status !== TileStatus.COVERED }

    var onChangeListener: Runnable? = null

    fun mainAction(tile: Tile) {
        if (gameOver) return
        when (tile.status) {
            TileStatus.COVERED -> {
                tile.status = TileStatus.FLAG
                grid.getNeighbors(tile).forEach { it.addFlaggedNear() }
                flaggedBombs++
                if (tile.hasBomb) {
                    correctFlaggedBombs++
                }
                if (Settings.discoveryMode == Settings.AUTOMATIC) {
                    grid.getNeighbors(tile)
                        .filter { tile2 -> tile2.isNumberVisible && tile2.flaggedNear == tile2.bombsNear }
                        .forEach { fastReveal(it) }
                }
                checkWin()
                onChangeListener?.run()
            }
            TileStatus.FLAG -> {
                tile.status = TileStatus.COVERED
                grid.getNeighbors(tile).forEach { it.removeFlaggedNear() }
                flaggedBombs--
                if (tile.hasBomb) {
                    correctFlaggedBombs--
                }
                if (Settings.discoveryMode == Settings.AUTOMATIC) {
                    grid.getNeighbors(tile)
                        .filter { it.isNumberVisible && it.flaggedNear == it.bombsNear }
                        .forEach { fastReveal(it) }
                }
                checkWin()
                onChangeListener?.run()
            }
            TileStatus.A0 -> {}
            else -> {
                val executeMassReveal = when (Settings.discoveryMode) {
                    Settings.EASY   -> tile.flaggedNear == tile.bombsNear
                    Settings.NORMAL -> tile.flaggedNear >= tile.bombsNear
                    Settings.HARD   -> true
                    else            -> false
                }

                if (executeMassReveal) {
                    grid.getNeighbors(tile)
                        .filter { it.status === TileStatus.COVERED }
                        .forEach { reveal(it) }
                    onChangeListener?.run()
                }
            }
        }
    }

    fun alternativeAction(tile: Tile): Boolean {
        if (gameOver) return false
        return when (tile.status) {
            TileStatus.COVERED -> {
                if (Settings.discoveryMode == Settings.AUTOMATIC)
                    fastReveal(tile)
                else
                    reveal(tile)
                checkWin()
                onChangeListener?.run()
                true
            }
            else -> false
        }
    }

    fun reveal(tile: Tile) {
        if (tile.isCovered) {
            revealedTiles++
        }
        if (tile.hasBomb) {
            tile.status = TileStatus.BOMB_FINAL
            gameOver()
        } else {
            when (tile.bombsNear) {
                0 -> {
                    tile.status = TileStatus.A0
                    grid.getNeighbors(tile)
                        .filter { it.status === TileStatus.COVERED }
                        .forEach { reveal(it) }
                }
                else -> tile.status = TileStatus.findByTileNumber(tile.bombsNear)
            }
        }
    }

    fun fastReveal(tile: Tile) {
        if (tile.isCovered) {
            revealedTiles++
        }
        if (tile.hasBomb) {
            tile.status = TileStatus.BOMB_FINAL
            gameOver()
        } else {
            var massReveal = true
            when (tile.bombsNear) {
                0 -> tile.status = TileStatus.A0
                else -> {
                    tile.status = TileStatus.findByTileNumber(tile.bombsNear)
                    massReveal = tile.bombsNear == tile.flaggedNear
                }
            }
            if (massReveal) {
                grid.getNeighbors(tile)
                    .filter { it.status === TileStatus.COVERED }
                    .forEach { fastReveal(it) }
            }
        }
    }

    fun checkWin() {
        if (correctFlaggedBombs == grid.bombs && correctFlaggedBombs == flaggedBombs) {
            gameWin()
        }
    }

    fun gameOver() {
        if (status == GameStatus.LOSE) return
        status = GameStatus.LOSE
        grid.tiles
            .forEach { tile ->
                if (tile.hasBomb) {
                    if (tile.status === TileStatus.COVERED) {
                        tile.status = TileStatus.BOMB
                    }
                } else if (tile.status === TileStatus.FLAG) {
                    tile.status = TileStatus.FLAG_FAIL
                }
            }
        if (finishEvent != null) finishEvent!!.accept(false)
    }

    fun gameWin() {
        status = GameStatus.WIN
        if (finishEvent != null) finishEvent!!.accept(true)
    }

    fun setFinishEvent(finishEvent: Consumer<Boolean>?) {
        this.finishEvent = finishEvent
    }

    fun addRevealedTiles() {
        revealedTiles++
    }

    fun addFlaggedBombs() {
        flaggedBombs++
    }

    fun addCorrectFlaggedBombs() {
        correctFlaggedBombs++
    }

}