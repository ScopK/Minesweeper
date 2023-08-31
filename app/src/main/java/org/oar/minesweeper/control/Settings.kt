package org.oar.minesweeper.control

object Settings {
    const val DISABLED = 0
    const val NORMAL = 1
    const val AUTOMATIC = 2
    const val FILENAME = "state.save"

    var showTime = true
    var switchActions = false
    var discoveryMode = 0
}