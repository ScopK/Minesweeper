package org.oar.minesweeper.utils

import android.R
import android.app.Activity
import android.content.Intent
import org.oar.minesweeper.GameActivity
import org.oar.minesweeper.LoadingActivity
import org.oar.minesweeper.elements.Grid

object ActivityUtils {

    fun Activity.startGridActivity(grid: Grid) {
        if (grid.gridSettings.solvable) {
            val intent = Intent(this, LoadingActivity::class.java)
            intent.putExtra("grid", grid)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()

        } else {
            grid.generate {
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("grid", grid)
                intent.putExtra("options", it)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }
        }
    }
}