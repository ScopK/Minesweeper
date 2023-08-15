package org.oar.minesweeper.elements


enum class TileStatus(
    val tileNumber: Int? = null
) {
    COVERED,
    A0,
    A1(1),
    A2(2),
    A3(3),
    A4(4),
    A5(5),
    A6(6),
    A7(7),
    A8(8),
    FLAG,
    FLAG_FAIL,
    BOMB,
    BOMB_FINAL;

    companion object {
        fun findByTileNumber(tileNumber: Int): TileStatus {
            for (status in values()) {
                if (status.tileNumber == tileNumber) return status
            }
            return A0
        }
    }
}
