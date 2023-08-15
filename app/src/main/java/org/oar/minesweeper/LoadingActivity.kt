package org.oar.minesweeper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import org.oar.minesweeper.models.Grid
import org.oar.minesweeper.utils.ActivityUtils.animateStartActivity
import org.oar.minesweeper.utils.GridUtils.generate
import kotlin.reflect.full.createInstance

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class LoadingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_loading)
        window.insetsController?.hide(WindowInsets.Type.statusBars())

        val grid = this.intent.extras?.getSerializable("grid") as Grid? ?:
            throw NullPointerException("A grid was expected")

        grid.generate {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("grid", grid)
            intent.putExtra("options", it)
            animateStartActivity(intent, true)
        }
    }
}
