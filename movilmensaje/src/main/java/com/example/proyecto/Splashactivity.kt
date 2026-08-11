package com.example.proyecto
import com.example.movilmensaje.R

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


// Pantalla de carga inicial: fondo oscuro con degradado, logo con
// resplandor y una barra de progreso que se llena en 3 segundos.
// Al terminar, pasa sola al login (MainActivity).
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val DURACION_SPLASH_MS = 3000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        animarEntrada()
        animarBarraProgreso()

        Handler(Looper.getMainLooper()).postDelayed(
            {
                irALogin()
            },
            DURACION_SPLASH_MS
        )
    }

    private fun animarEntrada() {
        val contenedorLogo = findViewById<View>(R.id.contenedorLogoSplash)
        val nombreApp = findViewById<TextView>(R.id.tvSplashNombreApp)
        val eslogan = findViewById<TextView>(R.id.tvSplashEslogan)

        contenedorLogo.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.anim_splash_logo)
        )

        val animacionTexto = AnimationUtils.loadAnimation(this, R.anim.anim_splash_texto)
        nombreApp.startAnimation(animacionTexto)
        eslogan.startAnimation(animacionTexto)
    }

    private fun animarBarraProgreso() {
        val barra = findViewById<ProgressBar>(R.id.progresoSplash)

        ObjectAnimator.ofInt(barra, "progress", 0, 100)
            .apply {
                duration = DURACION_SPLASH_MS
                start()
            }
    }

    private fun irALogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}