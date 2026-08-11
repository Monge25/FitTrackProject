package com.example.proyecto.wear.presentation.service

import android.os.Handler
import android.os.Looper
import com.example.proyecto.wear.presentation.data.model.WorkoutState
import kotlin.random.Random

object HeartRateSimulator {

    private val handler = Handler(Looper.getMainLooper())
    private var running = false

    private val heartRateRunnable = object : Runnable {
        override fun run() {
            if (!running) return

            val newHeartRate = Random.nextInt(
                from = 105,
                until = 136
            )

            WorkoutState.updateHeartRate(newHeartRate)

            handler.postDelayed(this, 3000L)
        }
    }

    fun start() {
        if (running) return

        running = true
        handler.post(heartRateRunnable)
    }

    fun stop() {
        running = false
        handler.removeCallbacks(heartRateRunnable)

        WorkoutState.updateHeartRate(80)
    }
}