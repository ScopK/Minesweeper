package org.oar.minesweeper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import org.oar.minesweeper.elements.Grid

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
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
