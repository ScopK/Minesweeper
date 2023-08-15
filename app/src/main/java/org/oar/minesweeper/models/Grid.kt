package org.oar.minesweeper.models

import java.io.Serializable

class Grid (
    val gridConfig: GridConfiguration,
    val gridSettings: GridSettings
) : Serializable, Cloneable {

    val height get() = gridConfig.height
    val width get() = gridConfig.width
    val bombs get() = gridConfig.bombs

    var tiles: MutableList<Tile> = mutableListOf()

    public override fun clone(): Grid {
        return Grid(gridConfig, gridSettings).also {
            tiles
                .map(Tile::clone)
                .also(it.tiles::addAll)
        }
    }
}
