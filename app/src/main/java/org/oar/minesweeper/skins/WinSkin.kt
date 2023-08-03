package org.oar.minesweeper.skins

import org.oar.minesweeper.R

class WinSkin: Skin() {
    override val visualHelp = false

    override val resource = R.drawable.win_skin
    override var backgroundColor = R.color.winSkinBackground
}