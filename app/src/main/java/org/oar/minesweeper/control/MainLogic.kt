package org.oar.minesweeper.control

import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.utils.GridUtils.getNeighbors
import org.oar.minesweeper.utils.GridUtils.getTileStatus
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
        get() = grid.tiles.none { tile -> tile.status !== Tile.Status.COVERED }


    fun mainAction(tile: Tile) {
        if (gameOver) return
        when (tile.status) {
            Tile.Status.COVERED -> {
                tile.status = Tile.Status.FLAG
                getNeighbors(grid, tile).forEach(Consumer { obj: Tile -> obj.addFlaggedNear() })
                flaggedBombs++
                if (tile.hasBomb) {
                    correctFlaggedBombs++
                }
                if (Settings.discoveryMode == Settings.AUTOMATIC) {
                    getNeighbors(grid, tile)
                        .filter { tile2 -> tile2.isNumberVisible && tile2.flaggedNear == tile2.bombsNear }
                        .forEach { fastReveal(it) }
                }
                checkWin()
            }
            Tile.Status.FLAG -> {
                tile.status = Tile.Status.COVERED
                getNeighbors(grid, tile).forEach(Consumer { obj: Tile -> obj.removeFlaggedNear() })
                flaggedBombs--
                if (tile.hasBomb) {
                    correctFlaggedBombs--
                }
                if (Settings.discoveryMode == Settings.AUTOMATIC) {
                    getNeighbors(grid, tile)
                        .filter { tile2 -> tile2.isNumberVisible && tile2.flaggedNear == tile2.bombsNear }
                        .forEach { fastReveal(it) }
                }
                checkWin()
            }
            Tile.Status.A0 -> {}
            else -> {
                var executeMassReveal = false
                when (Settings.discoveryMode) {
                    Settings.EASY -> executeMassReveal = tile.flaggedNear == tile.bombsNear
                    Settings.NORMAL -> executeMassReveal = tile.flaggedNear >= tile.bombsNear
                    Settings.HARD -> executeMassReveal = true
                    else -> {}
                }
                if (executeMassReveal) {
                    getNeighbors(grid, tile).stream()
                        .filter { tile2-> tile2.status === Tile.Status.COVERED }
                        .forEach { reveal(it) }
                }
            }
        }
    }

    fun alternativeAction(tile: Tile): Boolean {
        if (gameOver) return false
        return when (tile.status) {
            Tile.Status.FLAG -> {
                getNeighbors(grid, tile).forEach(Consumer { obj: Tile -> obj.removeFlaggedNear() })
                flaggedBombs--
                if (tile.hasBomb) {
                    correctFlaggedBombs--
                }
                if (Settings.discoveryMode == Settings.AUTOMATIC)
                    fastReveal(tile)
                else
                    reveal(tile)
                checkWin()
                true
            }
            Tile.Status.COVERED -> {
                if (Settings.discoveryMode == Settings.AUTOMATIC)
                    fastReveal(tile)
                else
                    reveal(tile)
                checkWin()
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
            tile.status = Tile.Status.BOMB_FINAL
            gameOver()
        } else {
            when (tile.bombsNear) {
                0 -> {
                    tile.status = Tile.Status.A0
                    getNeighbors(grid, tile)
                        .filter { it.status === Tile.Status.COVERED }
                        .forEach { reveal(it) }
                }
                else -> tile.status = getTileStatus(tile.bombsNear)
            }
        }
    }

    fun fastReveal(tile: Tile) {
        if (tile.isCovered) {
            revealedTiles++
        }
        if (tile.hasBomb) {
            tile.status = Tile.Status.BOMB_FINAL
            gameOver()
        } else {
            var massReveal = true
            when (tile.bombsNear) {
                0 -> tile.status = Tile.Status.A0
                else -> {
                    tile.status = getTileStatus(tile.bombsNear)
                    massReveal = tile.bombsNear == tile.flaggedNear
                }
            }
            if (massReveal) {
                getNeighbors(grid, tile)
                    .filter { it.status === Tile.Status.COVERED }
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
                    if (tile.status === Tile.Status.COVERED) {
                        tile.status = Tile.Status.BOMB
                    }
                } else if (tile.status === Tile.Status.FLAG) {
                    tile.status = Tile.Status.FLAG_FAIL
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