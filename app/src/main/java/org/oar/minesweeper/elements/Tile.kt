package org.oar.minesweeper.elements

import org.oar.minesweeper.models.TileStatus
import java.io.Serializable
import java.util.*

class Tile(
    val x: Int,
    val y: Int,
    var status: TileStatus = TileStatus.COVERED
) : Serializable, Cloneable {

    var customFlag: MutableList<Char> = mutableListOf()
    var hasBomb = false

    var flaggedNear = 0
        private set
    var bombsNear = 0
        private set
    val isCovered: Boolean
        get() = status == TileStatus.COVERED || status == TileStatus.FLAG
    val isUncovered: Boolean
        get() = !isCovered
    val isNumberVisible: Boolean
        get() = !isCovered && status != TileStatus.A0


    fun addFlaggedNear() {
        flaggedNear++
    }

    fun removeFlaggedNear() {
        flaggedNear--
    }

    fun hasBombNear() {
        bombsNear++
    }

    fun doesntHaveBombNear() {
        bombsNear--
    }

    override fun toString(): String {
        return when (status) {
            TileStatus.COVERED -> "#"
            TileStatus.A0 -> "·"
            TileStatus.A1 -> "1"
            TileStatus.A2 -> "2"
            TileStatus.A3 -> "3"
            TileStatus.A4 -> "4"
            TileStatus.A5 -> "5"
            TileStatus.A6 -> "6"
            TileStatus.A7 -> "7"
            TileStatus.A8 -> "8"
            TileStatus.FLAG -> "F"
            TileStatus.BOMB, TileStatus.BOMB_FINAL -> "X"
            else -> " "
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(x, y, hasBomb, bombsNear, super.hashCode())
    }

    public override fun clone(): Tile {
        val tile = Tile(x, y, status)
        tile.hasBomb = hasBomb
        tile.flaggedNear = flaggedNear
        tile.bombsNear = bombsNear
        tile.customFlag = customFlag
        return tile
    }
}