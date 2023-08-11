package org.oar.minesweeper.control

import java.io.Closeable

class Timer(
    private val initDeciSecs: Int,
    private val notifier: (Int) -> Unit
) : Closeable, Thread() {

    companion object {
        fun startTimer(startTime: Int = 0, notifier: (Int) -> Unit): Timer {
            return Timer(startTime, notifier)
        }
    }

    private var initTime: Long = 0
    private var pausedTime: Long = 0
    private var running = true

    var deciSeconds: Int
        get() = ((System.nanoTime() - initTime) / 100000000L).toInt()
        set(deciSeconds) {
            initTime = System.nanoTime() - deciSeconds * 100000000L
            pausedTime = 0
        }

    var seconds: Int
        get() = ((System.nanoTime() - initTime) / 1000000000L).toInt()
        set(seconds) {
            initTime = System.nanoTime() - seconds * 1000000000L
            pausedTime = 0
        }

    fun pause() {
        if (pausedTime == 0L) {
            pausedTime = System.nanoTime()
        }
    }

    fun unpause() {
        if (pausedTime != 0L) {
            initTime += System.nanoTime() - pausedTime
        }
        pausedTime = 0
    }

    override fun close() {
        running = false
        interrupt()
    }

    override fun run() {
        super.run()
        running = true
        initTime = System.nanoTime() - initDeciSecs * 100000000L
        pausedTime = 0
        while (running) {
            if (pausedTime == 0L) {
                notifier(this.seconds)
            }
            try {
                sleep(1000)
            } catch (e: InterruptedException) {
                break
            }
        }
    }
}