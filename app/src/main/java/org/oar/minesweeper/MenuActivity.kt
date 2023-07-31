package org.oar.minesweeper

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.GridConfiguration
import org.oar.minesweeper.elements.GridSettings
import org.oar.minesweeper.utils.ActivityController

class MenuActivity : AppCompatActivity() {
    private val menuPanel: MenuPanel by lazy { MenuPanel(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(menuPanel)
        window.insetsController?.hide(WindowInsets.Type.statusBars())
    }

    fun startGrid(
        config: GridConfiguration,
        settings: GridSettings
    ) {
        val grid = Grid(config, settings)
        ActivityController.loadGrid(grid, this)
    }

    fun loadGrid() {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("l", true)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}