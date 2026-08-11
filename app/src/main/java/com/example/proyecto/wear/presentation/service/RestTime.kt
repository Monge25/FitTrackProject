package com.example.proyecto.wear.presentation.service

import android.os.Handler
import android.os.Looper
import com.example.proyecto.wear.presentation.data.model.WorkoutState

object RestTimer {

    private val handler = Handler(Looper.getMainLooper())

    private var remainingSeconds = 45
    private var running = false

    private val restRunnable = object : Runnable {
        override fun run() {
            if (!running) return

            WorkoutState.updateRestTime(remainingSeconds)

            if (remainingSeconds <= 0) {
                stop()
                WorkoutState.finishRest()
                WorkoutTimer.resume()
                return
            }

            remainingSeconds--
            handler.postDelayed(this, 1000L)
        }
    }

    fun start(seconds: Int = 45) {
        stop()

        remainingSeconds = seconds
        running = true

        WorkoutState.updateRestTime(seconds)
        handler.post(restRunnable)
    }

    fun stop() {
        running = false
        handler.removeCallbacks(restRunnable)
    }
}