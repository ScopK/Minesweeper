package org.oar.minesweeper.models

import java.io.Serializable

data class GridGenerationDetails(
    val firstTileReveal: Int? = null
) : Serializable