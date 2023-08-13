package org.oar.minesweeper.skins.model

data class SkinSet(
    val empty: BitmapWrapper,
    val covers: List<BitmapWrapper>,
    val flag: BitmapWrapper,
    val flagOk: BitmapWrapper,
    val flagFail: BitmapWrapper,
    val bomb: BitmapWrapper,
    val bombEnd: BitmapWrapper,
    val numbers: Map<Pair<Int, Int>, BitmapWrapper>
)
