package org.oar.minesweeper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Window
import android.view.WindowInsets
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.models.GridStartOptions
import org.oar.minesweeper.ui.views.GameView

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class GameActivity : Activity() {
    private val gameView: GameView by lazy { findViewById(R.id.panel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val extras = this.intent.extras

        if (extras == null) {
            finish();
        } else {
            setContentView(R.layout.activity_playing_grid)

            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars())
                hide(WindowInsets.Type.navigationBars())
            }

            if (extras.getBoolean("l")) {
                if (!gameView.loadState()) {
                    finish()
                }
            } else {
                val grid = extras.getSerializable("grid") as Grid
                val options = extras.getSerializable("options") as GridStartOptions
                gameView.setNewGrid(grid, options)
                gameView.postInvalidate()
            }
        }
    }

    override fun onPause() {
        gameView.saveState()
        super.onPause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}