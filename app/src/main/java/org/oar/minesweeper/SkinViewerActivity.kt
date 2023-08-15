package org.oar.minesweeper

import android.app.Activity
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import org.json.JSONObject
import org.oar.minesweeper.control.GridDrawer
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.models.GridSettings
import org.oar.minesweeper.models.TileStatus
import org.oar.minesweeper.skins.DefaultSkin
import org.oar.minesweeper.skins.DotAltSkin
import org.oar.minesweeper.skins.DotSkin
import org.oar.minesweeper.skins.WinSkin
import org.oar.minesweeper.ui.views.GridViewerView
import org.oar.minesweeper.utils.GridUtils.getNeighbors
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
    private var hueSelected = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_skin_viewer)
        window.insetsController?.hide(WindowInsets.Type.statusBars())

        skinIndex = loadInteger("skin")
        visualHelp = loadBoolean("lastVisualHelp", false)
        hueSelected = loadInteger("lastCoverHue", 0)

        val viewer = findViewById<GridViewerView>(R.id.viewer)
        val name = findViewById<TextView>(R.id.name)
        val hueSelector = findViewById<SeekBar>(R.id.hueSelector)
        val prev = findViewById<ImageView>(R.id.prev)
        val next = findViewById<ImageView>(R.id.next)
        val useVisualHelp = findViewById<SwitchMaterial>(R.id.useVisualHelp)

        viewer.sampleGrid = getSampleGrid()
        viewer.skin = skins[skinIndex].createInstance().apply { coverHue = hueSelected }


        name.text = viewer.skin.name

        hueSelector.isEnabled = viewer.skin.acceptsHue
        useVisualHelp.isEnabled = viewer.skin.visualHelp

        prev.setOnClickListener {
            if (skinIndex > 0) {
                skinIndex--
                viewer.skin = skins[skinIndex].createInstance().apply { coverHue = hueSelected }
                viewer.postInvalidate()

                name.text = viewer.skin.name
                hueSelector.isEnabled = viewer.skin.acceptsHue
                useVisualHelp.isEnabled = viewer.skin.visualHelp
            }
        }

        next.setOnClickListener {
            if (skinIndex < skins.size - 1) {
                skinIndex++
                viewer.skin = skins[skinIndex].createInstance().apply { coverHue = hueSelected }
                viewer.postInvalidate()

                name.text = viewer.skin.name
                hueSelector.isEnabled = viewer.skin.acceptsHue
                useVisualHelp.isEnabled = viewer.skin.visualHelp
            }
        }

        hueSelector.progress = hueSelected
        hueSelector.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                hueSelected = hueSelector.progress
                viewer.skin = skins[skinIndex].createInstance().apply { coverHue = hueSelected }
                viewer.postInvalidate()
            }
        })

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
            save("lastCoverHue", hueSelected)
            GridDrawer.setSkin(this, skins[skinIndex], hueSelected)
            finish()
        }
    }

    private fun getSampleGrid(): Grid {
        val obj = JSONObject("{\"w\":5,\"h\":5,\"gs\":true,\"x\":1,\"y\":1,\"s\":1,\"t\":348,\"ts\":\"   1C 112c 1F2c1334FcCCcF\"}")
        val settings = GridSettings(firstOpen = false, solvable = false, visualHelp)
        val grid = Grid.jsonGrid(this, obj, settings)

        grid.tiles
            .filter { it.status === TileStatus.FLAG }
            .flatMap { grid.getNeighbors(it) }
            .forEach { it.addFlaggedNear() }

        return grid
    }
}
