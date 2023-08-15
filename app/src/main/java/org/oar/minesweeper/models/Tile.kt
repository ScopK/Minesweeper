package org.oar.minesweeper.models

import java.io.Serializable
import java.util.*

@Suppress("EqualsOrHashCode")
data class Tile(
    val x: Int,
    val y: Int,
    var status: TileStatus = TileStatus.COVERED
) : Serializable, Cloneable {

    val customFlag = mutableListOf<Char>()
    var hasBomb = false
    var flaggedNear = 0
    var bombsNear = 0

    val isCovered: Boolean
        get() = status == TileStatus.COVERED || status == TileStatus.FLAG

    val isUncovered: Boolean
        get() = !isCovered

    val isNumberVisible: Boolean
        get() = !isCovered && status != TileStatus.A0

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

    override fun hashCode() = Objects.hash(x, y, hasBomb, bombsNear, super.hashCode())

    public override fun clone(): Tile {
        return Tile(x, y, status).also {
            it.hasBomb = hasBomb
            it.flaggedNear = flaggedNear
            it.bombsNear = bombsNear
            it.customFlag.addAll(customFlag)
        }
    }
}