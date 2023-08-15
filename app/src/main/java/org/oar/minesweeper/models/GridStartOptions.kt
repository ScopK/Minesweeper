package org.oar.minesweeper.models

import java.io.Serializable

data class GridStartOptions(
    val firstTileReveal: Int? = null
) : Serializable