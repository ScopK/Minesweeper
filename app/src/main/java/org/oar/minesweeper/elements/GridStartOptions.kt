package org.oar.minesweeper.elements

import java.io.Serializable

data class GridStartOptions(
    val firstTileReveal: Int? = null
) : Serializable