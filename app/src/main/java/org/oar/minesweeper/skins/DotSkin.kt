package org.oar.minesweeper.skins

import org.oar.minesweeper.R

class DotSkin: Skin() {
    override val visualHelp = true
    override val unhelpfulTileIsEmpty = true

    override val numberOfCovers = 4
    override val coverW = 138
    override val coverH = 140

    override val resource = R.drawable.dot_skin
    override var backgroundColor = R.color.defaultSkinBackground
}