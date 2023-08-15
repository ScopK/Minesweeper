package org.oar.minesweeper.utils

import android.R
import android.app.Activity
import android.content.Intent
import org.oar.minesweeper.GameActivity
import org.oar.minesweeper.LoadingActivity
import org.oar.minesweeper.models.Grid
import org.oar.minesweeper.utils.GridUtils.generate

object ActivityUtils {

    fun Activity.startGridActivity(grid: Grid) {
        if (grid.gridSettings.solvable) {
            val intent = Intent(this, LoadingActivity::class.java)
            intent.putExtra("grid", grid)
            animateStartActivity(intent, true)

        } else {
            grid.generate {
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("grid", grid)
                intent.putExtra("options", it)
                animateStartActivity(intent, true)
            }
        }
    }

    fun Activity.animateStartActivity(intent: Intent, finishActivity: Boolean = false) {
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        if (finishActivity) {
            finish()
        }
    }
}