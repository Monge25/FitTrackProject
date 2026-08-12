package com.example.proyecto.data.wear

// Puente simple entre PhoneWearListenerService (que recibe los
// mensajes del reloj en segundo plano, fuera del ciclo de vida de
// cualquier Activity) y EntrenamientoActivoActivity (que tiene la UI
// en vivo del entrenamiento). Mientras la Activity está en primer
// plano, se registra aquí para enterarse si el usuario pausa,
// reanuda o termina el entrenamiento desde el reloj.
object EntrenamientoBridge {

    interface Listener {
        fun onPausarDesdeReloj()
        fun onReanudarDesdeReloj()
        fun onFinalizarDesdeReloj()
    }

    @Volatile
    private var listener: Listener? = null

    fun registrar(listener: Listener) {
        EntrenamientoBridge.listener = listener
    }

    fun quitar(listener: Listener) {
        if (EntrenamientoBridge.listener === listener) {
            EntrenamientoBridge.listener = null
        }
    }

    fun notificarPausar() {
        listener?.onPausarDesdeReloj()
    }

    fun notificarReanudar() {
        listener?.onReanudarDesdeReloj()
    }

    fun notificarFinalizar() {
        listener?.onFinalizarDesdeReloj()
    }
}