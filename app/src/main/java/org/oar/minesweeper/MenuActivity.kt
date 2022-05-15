package org.oar.minesweeper

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.generators.GridGenerator
import org.oar.minesweeper.generators.RandomGenerator
import org.oar.minesweeper.utils.ActivityController
import kotlin.reflect.KClass

class MenuActivity : Activity() {
    private val menuPanel: MenuPanel by lazy { MenuPanel(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(menuPanel)
        window.insetsController?.hide(WindowInsets.Type.statusBars())
    }

    @JvmOverloads
    fun startGrid(
        w: Int,
        h: Int,
        b: Int,
        generatorClass: KClass<out GridGenerator> = RandomGenerator::class
    ) {
        val grid = Grid(w, h, b, generatorClass.java)
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