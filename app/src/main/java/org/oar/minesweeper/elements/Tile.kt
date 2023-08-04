package org.oar.minesweeper.elements

import java.io.Serializable
import java.util.*
import kotlin.jvm.JvmOverloads

class Tile(
    val x: Int,
    val y: Int,
    var status: Status = Status.COVERED
) : Serializable, Cloneable {
    enum class Status {
        COVERED, A0, A1, A2, A3, A4, A5, A6, A7, A8, FLAG, FLAG_FAIL, BOMB, BOMB_FINAL
    }

    var customFlag: MutableList<Char> = mutableListOf()
    var hasBomb = false

    var flaggedNear = 0
        private set
    var bombsNear = 0
        private set
    val isCovered: Boolean
        get() = status == Status.COVERED || status == Status.FLAG
    val isUncovered: Boolean
        get() = !isCovered
    val isNumberVisible: Boolean
        get() = !isCovered && status != Status.A0


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
            Status.COVERED -> "#"
            Status.A0 -> "·"
            Status.A1 -> "1"
            Status.A2 -> "2"
            Status.A3 -> "3"
            Status.A4 -> "4"
            Status.A5 -> "5"
            Status.A6 -> "6"
            Status.A7 -> "7"
            Status.A8 -> "8"
            Status.FLAG -> "F"
            Status.BOMB, Status.BOMB_FINAL -> "X"
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