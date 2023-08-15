package org.oar.minesweeper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import org.oar.minesweeper.models.Grid
import org.oar.minesweeper.models.GridGenerationDetails
import org.oar.minesweeper.ui.views.GameView
import org.oar.minesweeper.utils.ActivityUtils.animateStartActivity


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
                val details = extras.getSerializable("options") as GridGenerationDetails

                gameView.viewTreeObserver.addOnGlobalLayoutListener(
                    object: OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            // at this point, view dimensions are set (view.width and view.height)
                            gameView.setGrid(grid, details)
                            gameView.postInvalidate()
                            // make sure it is only executed once:
                            gameView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    }
                )
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
        animateStartActivity(intent, true)
    }
}