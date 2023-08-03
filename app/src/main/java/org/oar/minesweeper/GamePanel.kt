package org.oar.minesweeper

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import org.json.JSONException
import org.json.JSONObject
import org.oar.minesweeper.control.CanvasWrapper
import org.oar.minesweeper.control.GridDrawer.draw
import org.oar.minesweeper.control.GridDrawer.tileSize
import org.oar.minesweeper.control.MainLogic
import org.oar.minesweeper.control.Settings
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.GridStartOptions
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.utils.ActivityController.loadGrid
import org.oar.minesweeper.utils.GridUtils.calculateLogicFromBareGrid
import org.oar.minesweeper.utils.GridUtils.findSafeOpenTile
import org.oar.minesweeper.utils.GridUtils.getJsonStatus
import org.oar.minesweeper.utils.GridUtils.getTileByScreenCoords
import java.io.*
import kotlin.math.abs
import kotlin.math.roundToInt

class GamePanel(context: Context) : View(context) {
    private var logic: MainLogic? = null
    private var hud: Hud? = null
    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val gestureDetector = GestureDetector(context, GestureListener())
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private var dragXPos = 0f
    private var dragYPos = 0f
    private var isResizing = false
    private var isMoving = false

    init {
        //make gamePanel focusable so it can handle events
        isFocusable = true

        scaleDetector.isQuickScaleEnabled = false
        gestureDetector.setIsLongpressEnabled(true)
    }


    fun setNewGrid(grid: Grid, options: GridStartOptions) {
        setGrid(MainLogic(grid), 0)

        if (grid.gridSettings.firstOpen && logic!!.allCovered) {
            val tile = options.firstTileReveal
                ?.let { grid.tiles[it] }
                ?: findSafeOpenTile(grid)

            logic!!.reveal(tile)
            CanvasWrapper.focus(tile)
        } else {
            grid.tiles.firstOrNull { tile -> tile.status === Tile.Status.A0 }
                ?.also { CanvasWrapper.focus(it) }
        }
    }

    fun setGrid(logic: MainLogic, seconds: Int) {
        this.logic = logic

        hud?.stopTimer()
        hud = Hud(logic, this, context).apply {
            time = seconds
            startTimer()
        }

        logic.setFinishEvent { hud?.stopTimer() }
        CanvasWrapper.setContentDimensions(
            (logic.grid.width * tileSize).toFloat(),
            (logic.grid.height * tileSize).toFloat())
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val xCanvas = CanvasWrapper(canvas)

        logic?.also { draw(context, xCanvas, it.grid) }

        xCanvas.end()
        hud?.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        scaleDetector.onTouchEvent(event)

        if (!scaleDetector.isInProgress) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dragXPos = event.x
                    dragYPos = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isResizing)
                        return true

                    val dx = (event.x - dragXPos).roundToInt()
                    val dy = (event.y - dragYPos).roundToInt()

                    if (isMoving || abs(dx) + abs(dy) > 35) {
                        dragXPos = event.x
                        dragYPos = event.y

                        CanvasWrapper.translate(dx.toFloat(), dy.toFloat())

                        isMoving = true
                        postInvalidate()
                    }
                }
                MotionEvent.ACTION_OUTSIDE,
                MotionEvent.ACTION_UP -> {
                    isMoving = false
                    isResizing = false
                }
                else -> {}
            }
        }
        return true
    }

    public override fun onWindowVisibilityChanged(visibility: Int) {
        when (visibility) {
            VISIBLE -> hud?.resumeTimer()
            GONE,
            INVISIBLE -> hud?.pauseTimer()
        }
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            isResizing = true

            val ratio = detector.scaleFactor
            CanvasWrapper.zoom(ratio)

            postInvalidate()
            return true
        }
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {
            if (isMoving) return

            val t = getTileByScreenCoords(logic!!.grid, e.x, e.y)

            if (t != null) {
                if (logic!!.alternativeAction(t)) {
                    vibrator.vibrate(VibrationEffect.createOneShot(1L, 1)) //VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }
            postInvalidate()
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (logic!!.gameOver) {
                // restart:
                loadGrid(logic!!.grid, (context as Activity))
                postInvalidate()
                return true
            }

            val t = getTileByScreenCoords(logic!!.grid, e.x, e.y)
            if (t != null) {
                logic!!.mainAction(t)
            }
            postInvalidate()
            return true
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            if (e.action != MotionEvent.ACTION_UP) return false
            return if (isMoving) false else onSingleTapUp(e)
        }
    }

    fun saveState() {
        val saveStatePath = ContextWrapper(context).filesDir.path + "/" + Settings.FILENAME

        if (logic == null || logic!!.gameOver) {
            File(saveStatePath).delete()
            return
        }
        try {
            val obj = getJsonStatus(logic!!.grid, hud!!.time)

            PrintWriter(saveStatePath).use {
                it.print(obj)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadState(): Boolean {
        val saveStatePath = ContextWrapper(context).filesDir.path + "/" + Settings.FILENAME
        if (File(saveStatePath).exists()) {
            try {
                val line = BufferedReader(FileReader(saveStatePath)).use {
                    it.readLine()
                }

                val obj = JSONObject(line)
                val bareGrid = Grid.jsonGrid(context, obj)
                val logic = calculateLogicFromBareGrid(bareGrid)
                setGrid(logic, obj.getInt("t"))
                CanvasWrapper.setValues(
                    obj.getDouble("x").toFloat(),
                    obj.getDouble("y").toFloat(),
                    obj.getDouble("s").toFloat())

                return true
            } catch (e: JSONException) {
                System.err.println("Couldn't load \"$saveStatePath\"")
            } catch (e: IOException) {
                System.err.println("Couldn't load \"$saveStatePath\"")
            }
        }
        return false
    }
}