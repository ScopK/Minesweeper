package org.oar.minesweeper

import android.app.Activity
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import org.json.JSONObject
import org.oar.minesweeper.control.GridDrawer
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.GridSettings
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.skins.DefaultSkin
import org.oar.minesweeper.skins.DotAltSkin
import org.oar.minesweeper.skins.DotSkin
import org.oar.minesweeper.skins.WinSkin
import org.oar.minesweeper.ui.views.GridViewerView
import org.oar.minesweeper.utils.GridUtils
import org.oar.minesweeper.utils.PreferencesUtils.loadBoolean
import org.oar.minesweeper.utils.PreferencesUtils.loadInteger
import org.oar.minesweeper.utils.PreferencesUtils.save
import kotlin.reflect.full.createInstance

class SkinViewerActivity : Activity() {

    private val skins = listOf(
        DefaultSkin::class,
        DotSkin::class,
        DotAltSkin::class,
        WinSkin::class
    )

    private var skinIndex = 0
    private var visualHelp = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_skin_viewer)
        window.insetsController?.hide(WindowInsets.Type.statusBars())

        skinIndex = loadInteger("skin")
        visualHelp = loadBoolean("lastVisualHelp", false)

        val viewer = findViewById<GridViewerView>(R.id.viewer)
        viewer.sampleGrid = getSampleGrid()
        viewer.skin = skins[skinIndex].createInstance()

        val name = findViewById<TextView>(R.id.name)
        name.text = viewer.skin.name

        val prev = findViewById<ImageView>(R.id.prev)
        prev.setOnClickListener {
            if (skinIndex > 0) {
                skinIndex--
                viewer.skin = skins[skinIndex].createInstance()
                name.text = viewer.skin.name
                viewer.postInvalidate()
            }
        }

        val next = findViewById<ImageView>(R.id.next)
        next.setOnClickListener {
            if (skinIndex < skins.size - 1) {
                skinIndex++
                viewer.skin = skins[skinIndex].createInstance()
                name.text = viewer.skin.name
                viewer.postInvalidate()
            }
        }

        val useVisualHelp = findViewById<SwitchMaterial>(R.id.useVisualHelp)
        useVisualHelp.isChecked = visualHelp
        useVisualHelp.setOnCheckedChangeListener { _, isChecked ->
            visualHelp = isChecked
            viewer.sampleGrid?.gridSettings?.visualHelp = isChecked
            viewer.postInvalidate()
        }

        findViewById<ImageView>(R.id.cancel).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.confirm).setOnClickListener {
            save("skin", skinIndex)
            save("lastVisualHelp", visualHelp)
            GridDrawer.setSkin(this, skins[skinIndex])
            finish()
        }
    }

    private fun getSampleGrid(): Grid {
        val obj = JSONObject("{\"w\":5,\"h\":5,\"gs\":true,\"x\":1,\"y\":1,\"s\":1,\"t\":348,\"ts\":\"   1C 112c 1F2c1334FcCCcF\"}")
        val settings = GridSettings(firstOpen = false, solvable = false, visualHelp)
        val grid = Grid.jsonGrid(this, obj, settings)

        grid.tiles
            .filter { it.status === Tile.Status.FLAG }
            .flatMap { GridUtils.getNeighbors(grid, it) }
            .forEach { it.addFlaggedNear() }

        return grid
    }
}
