package com.example.proyecto.wear.presentation.service

import android.os.Handler
import android.os.Looper
import com.example.proyecto.wear.presentation.data.model.WorkoutState

object WorkoutTimer {

    private val handler = Handler(Looper.getMainLooper())

    private var elapsedSeconds = 0L
    private var running = false

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (!running) return

            elapsedSeconds++
            WorkoutState.updateElapsedTime(elapsedSeconds)

            handler.postDelayed(this, 1000L)
        }
    }

    fun start() {
        if (running) return

        running = true
        handler.post(timerRunnable)
    }

    fun pause() {
        running = false
        handler.removeCallbacks(timerRunnable)
    }

    fun resume() {
        if (running) return

        running = true
        handler.post(timerRunnable)
    }

    fun stop() {
        running = false
        handler.removeCallbacks(timerRunnable)

        elapsedSeconds = 0L
    }
}