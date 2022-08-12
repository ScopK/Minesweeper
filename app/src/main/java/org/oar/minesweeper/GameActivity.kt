package org.oar.minesweeper

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Window
import android.view.WindowInsets
import org.oar.minesweeper.elements.Grid

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class GameActivity : Activity() {
    private val gamePanel: GamePanel by lazy { GamePanel(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val extras = this.intent.extras

        if (extras == null) {
            finish();
        } else {
            setContentView(gamePanel)
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars())
                hide(WindowInsets.Type.navigationBars())
            }

            if (extras.getBoolean("l")) {
                if (!gamePanel.loadState()) {
                    finish()
                }
            } else {
                val grid: Grid = extras.getSerializable("grid") as Grid
                gamePanel.setNewGrid(grid)
                gamePanel.postInvalidate()
            }
        }
    }

    override fun onPause() {
        gamePanel.saveState()
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
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}