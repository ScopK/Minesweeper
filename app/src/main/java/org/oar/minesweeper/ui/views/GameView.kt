package org.oar.minesweeper.ui.views

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import org.oar.minesweeper.control.CanvasWrapper
import org.oar.minesweeper.grid.GridDrawer.draw
import org.oar.minesweeper.grid.GridDrawer.tileSize
import org.oar.minesweeper.grid.GameLogic
import org.oar.minesweeper.control.Timer
import org.oar.minesweeper.models.Grid
import org.oar.minesweeper.grid.GridPosition
import org.oar.minesweeper.grid.GridTouchControl
import org.oar.minesweeper.grid.GridTouchListener
import org.oar.minesweeper.models.GridGenerationDetails
import org.oar.minesweeper.utils.ActivityUtils.startGridActivity
import org.oar.minesweeper.utils.GridUtils.findSafeOpenTile
import org.oar.minesweeper.utils.GridUtils.generateLogic
import org.oar.minesweeper.utils.GridUtils.getTileByScreenCoords
import org.oar.minesweeper.utils.GridUtils.gridFromJson
import org.oar.minesweeper.utils.GridUtils.toJson
import org.oar.minesweeper.utils.SaveStateUtils.deleteState
import org.oar.minesweeper.utils.SaveStateUtils.loadState
import org.oar.minesweeper.utils.SaveStateUtils.saveState

class GameView(
    context: Context,
    attrs: AttributeSet,
) : View(context, attrs), GridTouchListener {

    companion object {
        private val gridPosition = GridPosition()
    }

    private lateinit var logic: GameLogic

    private val gridTouchControl: GridTouchControl

    private val hudView: HudView by lazy { rootView.findViewById(hudViewId) }
    private val hudViewId: Int
    private var timer: Timer? = null

    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    init {
        //make gameView focusable so it can handle events
        isFocusable = true

        attrs
            .getAttributeResourceValue("http://schemas.android.com/apk/res-auto", "hud", id)
            .also {
                if (it == id) throw RuntimeException("Hud view id not found")
                hudViewId = it
            }

        gridTouchControl = GridTouchControl(context, this)
    }

    override fun onTouchEvent(event: MotionEvent) = gridTouchControl.onTouchEvent(event)

    override fun pressed(x: Float, y: Float) {
        if (logic.gameOver) {
            // restart:
            (context as Activity).startGridActivity(logic.grid)
            return
        }

        val t = logic.grid.getTileByScreenCoords(x, y, gridPosition)
        if (t != null) {
            logic.mainAction(t)
        }
        postInvalidate()
    }

    override fun longPressed(x: Float, y: Float) {
        logic.grid.getTileByScreenCoords(x, y, gridPosition)
            ?.also {
                if (logic.alternativeAction(it)) {
                    vibrator.vibrate(VibrationEffect.createOneShot(1L, 1)) //VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }
        postInvalidate()
    }

    override fun move(dx: Float, dy: Float) {
        gridPosition.translate(dx, dy, this)
        postInvalidate()
    }

    override fun scale(ratio: Float) {
        gridPosition.zoom(ratio, this)
        postInvalidate()
    }

    fun setGrid(grid: Grid, details: GridGenerationDetails) {
        initGrid(GameLogic(grid), 0)

        if (grid.gridSettings.firstOpen && logic.allCovered) {
            val tile = details.firstTileReveal
                ?.let { grid.tiles[it] }
                ?: grid.findSafeOpenTile()

            logic.reveal(tile)
            gridPosition.focus(tile, this)
            updateHud()
        }
    }

    private fun initGrid(logic: GameLogic, deciSeconds: Int) {
        this.logic = logic

        hudView.setValues(
            logic.totalTiles,
            logic.revisedTiles,
            logic.grid.bombs,
            logic.flaggedBombs)

        logic.onChangeListener = Runnable { updateHud() }

        timer?.close()
        timer = Timer.startTimer(deciSeconds) {
            (context as Activity).runOnUiThread {
                hudView.setTimerValue(it)
            }
        }.apply { start() }

        logic.onEndListener = Runnable { timer?.close() }

        gridPosition.setContentDimensions(
            (logic.grid.width * tileSize).toFloat(),
            (logic.grid.height * tileSize).toFloat(),
            this)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        CanvasWrapper(canvas, gridPosition, this).use { canvasW ->
            draw(context, canvasW, logic.grid, isGameOver = logic.gameOver)
        }

        gridTouchControl.nextScrollAnimation()
    }

    public override fun onWindowVisibilityChanged(visibility: Int) {
        when (visibility) {
            VISIBLE -> timer?.unpause()
            GONE,
            INVISIBLE -> timer?.pause()
        }
    }

    fun saveState() {
        if (logic.gameOver) {
            context.deleteState()
        } else {
            val jsonObj = logic.grid.toJson(timer!!.deciSeconds, gridPosition)
            context.saveState(jsonObj)
        }
    }

    fun loadState(): Boolean {
        return context.loadState()
            ?.also { jsonObj ->
                val bareGrid = context.gridFromJson(jsonObj)
                val logic = bareGrid.generateLogic()
                initGrid(logic, jsonObj.getInt("t"))

                gridPosition.setValues(
                    jsonObj.getDouble("x").toFloat(),
                    jsonObj.getDouble("y").toFloat(),
                    jsonObj.getDouble("s").toFloat())
            } != null
    }

    private fun updateHud() {
        hudView.setValues(logic.revisedTiles, logic.flaggedBombs)

        if (logic.status != GameLogic.GameStatus.PLAYING) {
            hudView.setEndStateBackground(logic.status == GameLogic.GameStatus.WIN)
        }
    }
}