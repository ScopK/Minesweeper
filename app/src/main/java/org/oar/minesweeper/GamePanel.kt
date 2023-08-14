package org.oar.minesweeper

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import org.json.JSONException
import org.json.JSONObject
import org.oar.minesweeper.control.*
import org.oar.minesweeper.control.GridDrawer.draw
import org.oar.minesweeper.control.GridDrawer.tileSize
import org.oar.minesweeper.elements.Grid
import org.oar.minesweeper.elements.GridStartOptions
import org.oar.minesweeper.elements.Tile
import org.oar.minesweeper.ui.views.HudView
import org.oar.minesweeper.utils.ActivityController.loadGrid
import org.oar.minesweeper.utils.GridUtils.calculateLogicFromBareGrid
import org.oar.minesweeper.utils.GridUtils.findSafeOpenTile
import org.oar.minesweeper.utils.GridUtils.getJsonStatus
import org.oar.minesweeper.utils.GridUtils.getTileByScreenCoords
import java.io.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow

class GamePanel(
    context: Context,
    attrs: AttributeSet?,
) : View(context, attrs) {
    constructor(context: Context) : this(context, null)

    companion object {
        private val canvasPosition = CanvasPosition()
    }

    private var logic: MainLogic? = null

    lateinit var hudView: HudView
    private var timer: Timer? = null

    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val gestureDetector = GestureDetector(context, GestureListener())
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private var dragXPos = 0f
    private var dragYPos = 0f
    private var isResizing = false
    private var isMoving = false

    private var animation: Runnable? = null
    private var speedX = mutableListOf<Float>()
    private var speedY = mutableListOf<Float>()
    private var lastTime = 0L

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
            updateHud()
            canvasPosition.focus(tile)
        } else {
            grid.tiles.firstOrNull { tile -> tile.status === Tile.Status.A0 }
                ?.also { canvasPosition.focus(it) }
        }
    }

    fun setGrid(logic: MainLogic, seconds: Int) {
        this.logic = logic
        hudView.setValues(
            logic.totalTiles,
            logic.revisedTiles,
            logic.grid.bombs,
            logic.flaggedBombs)

        logic.onChangeListener = Runnable { updateHud() }

        timer?.close()
        timer = Timer.startTimer(seconds) {
            (context as Activity).runOnUiThread {
                hudView.setTimerValue(it)
            }
        }.apply { start() }

        logic.setFinishEvent { timer?.close() }
        canvasPosition.setContentDimensions(
            (logic.grid.width * tileSize).toFloat(),
            (logic.grid.height * tileSize).toFloat())
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val xCanvas = CanvasWrapper(canvas, canvasPosition)

        logic?.also { draw(context, xCanvas, it.grid, isGameOver = it.gameOver) }

        xCanvas.end()

        animation?.run()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        scaleDetector.onTouchEvent(event)

        if (!scaleDetector.isInProgress) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    animation = null
                    speedX.clear()
                    speedY.clear()

                    dragXPos = event.x
                    dragYPos = event.y
                    lastTime = System.currentTimeMillis()
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isResizing)
                        return true

                    val dx = event.x - dragXPos
                    val dy = event.y - dragYPos

                    if (isMoving || abs(dx) + abs(dy) > 35) {
                        dragXPos = event.x
                        dragYPos = event.y

                        System.currentTimeMillis()
                            .also {
                                speedX.add(dx)
                                speedY.add(dy)
                                if (speedX.size > 5) speedX.removeAt(0)
                                if (speedY.size > 5) speedY.removeAt(0)
                                lastTime = it
                            }

                        canvasPosition.translate(dx, dy)

                        isMoving = true
                        postInvalidate()
                    }
                }
                MotionEvent.ACTION_OUTSIDE,
                MotionEvent.ACTION_UP -> {
                    if ((System.currentTimeMillis() - lastTime) < 25) {
                        continueScrollingAnimation()
                        postInvalidate()
                    }
                    isMoving = false
                    isResizing = false
                }
                else -> {}
            }
        }
        return true
    }

    private fun continueScrollingAnimation() {
        val speedValueX = if (speedX.isEmpty()) 0f else speedX.average().toFloat()
        val speedValueY = if (speedY.isEmpty()) 0f else speedY.average().toFloat()

        if (speedValueX == 0f && speedValueY == 0f) {
            animation = null
            return
        }

        val totalFrames = ScreenProperties.FRAME_RATE
        var frame = 0

        val initialX = canvasPosition.posX
        val initialY = canvasPosition.posY

        val delta = (totalFrames * (totalFrames + 1) / 2) / (totalFrames + 1)
        var relativeEndX = speedValueX * delta
        var relativeEndY = speedValueY * delta

        animation = Runnable {
            val x = (frame / totalFrames)
                .let { x -> cos((x.pow(0.5f) - 1) * Math.PI / 2).toFloat() }

            val xPos = relativeEndX * x + initialX
            val yPos = relativeEndY * x + initialY

            var dx = xPos - canvasPosition.posX
            var dy = yPos - canvasPosition.posY

            if (abs(dx) > abs(speedValueX)) {
                relativeEndX -= dx - speedValueX
                dx = speedValueX
            }

            if (abs(dy) > abs(speedValueY)) {
                relativeEndY -= dy - speedValueY
                dy = speedValueY
            }

            canvasPosition.translate(dx, dy)

            if (frame > totalFrames) {
                animation = null
            } else {
                frame++
            }
            postInvalidate()
        }
    }

    public override fun onWindowVisibilityChanged(visibility: Int) {
        when (visibility) {
            VISIBLE -> timer?.unpause()
            GONE,
            INVISIBLE -> timer?.pause()
        }
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            isResizing = true

            val ratio = detector.scaleFactor
            canvasPosition.zoom(ratio)

            postInvalidate()
            return true
        }
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {
            if (isMoving) return

            getTileByScreenCoords(logic!!.grid, e.x, e.y, canvasPosition)
                ?.also {
                    if (logic!!.alternativeAction(it)) {
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

            val t = getTileByScreenCoords(logic!!.grid, e.x, e.y, canvasPosition)
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
            val obj = getJsonStatus(logic!!.grid, timer!!.deciSeconds, canvasPosition)

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
                canvasPosition.setValues(
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

    private fun updateHud() {
        logic?.apply {
            hudView.setValues(
                revisedTiles,
                flaggedBombs)

            if (status != MainLogic.GameStatus.PLAYING) {
                hudView.setEndStateBackground(status == MainLogic.GameStatus.WIN)
            }
        }
    }
}