package org.oar.minesweeper

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import org.oar.minesweeper.models.Grid
import org.oar.minesweeper.models.GridConfiguration
import org.oar.minesweeper.models.GridSettings
import org.oar.minesweeper.ui.dialogs.StartGridDialog
import org.oar.minesweeper.ui.views.components.MenuButtonView
import org.oar.minesweeper.utils.ActivityUtils.animateStartActivity
import org.oar.minesweeper.utils.ActivityUtils.startGridActivity
import org.oar.minesweeper.utils.SaveStateUtils.hasState

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main_menu)
        window.insetsController?.hide(WindowInsets.Type.statusBars())

        findViewById<MenuButtonView>(R.id.loadButton)
            .apply {
                setOnClickListener { loadGrid() }
                if (!hasState()) visibility =  View.GONE
            }

        findViewById<MenuButtonView>(R.id.startGame1)
            .setOnClickListener { startGrid(4,6,5) }

        findViewById<MenuButtonView>(R.id.startGame2)
            .setOnClickListener { startGrid(10,10,25) }

        findViewById<MenuButtonView>(R.id.startGame3)
            .setOnClickListener { startGrid(15,15,60) }

        findViewById<MenuButtonView>(R.id.startGame4)
            .setOnClickListener { startGrid(25,25,125) }

        findViewById<MenuButtonView>(R.id.startGame5)
            .setOnClickListener { startGrid(50,50,603) }

        findViewById<MenuButtonView>(R.id.visualThemeButton)
            .setOnClickListener { openSkins() }

        findViewById<MenuButtonView>(R.id.settingsButton)
            .setOnClickListener { openSettings() }
    }

    private fun startGrid(width: Int, height: Int, bombs: Int) {
        val gridConfig = GridConfiguration(width, height, bombs)
        StartGridDialog(
            this,
            gridConfig,
            { startGrid(gridConfig, it) }
        ).show(supportFragmentManager, null)
    }

    private fun startGrid(
        config: GridConfiguration,
        settings: GridSettings
    ) {
        val grid = Grid(config, settings)
        startGridActivity(grid)
    }

    private fun loadGrid() {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("l", true)
        animateStartActivity(intent, true)
    }

    private fun openSkins() {
        val intent = Intent(this, SkinViewerActivity::class.java)
        animateStartActivity(intent)
    }

    private fun openSettings() {
        val intent = Intent(this, PreferencesActivity::class.java)
        animateStartActivity(intent)
    }
}