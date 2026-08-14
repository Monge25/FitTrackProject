package com.example.proyecto.ui.rutinas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.model.Rutina
import com.example.proyecto.data.repository.RutinasRepository
import kotlinx.coroutines.launch

class RutinasViewModel : ViewModel() {

    private val repository = RutinasRepository()

    sealed class RutinasState {
        object Loading : RutinasState()
        object Idle : RutinasState()
        data class Error(val mensaje: String) : RutinasState()
        data class DesactivadaExitosa(val mensaje: String) : RutinasState()
        data class ActivadaExitosa(val mensaje: String) : RutinasState()
    }

    private val _rutinas = MutableLiveData<List<Rutina>>(emptyList())
    val rutinas: LiveData<List<Rutina>> = _rutinas

    private val _state = MutableLiveData<RutinasState>(RutinasState.Idle)
    val state: LiveData<RutinasState> = _state

    fun cargarRutinas(token: String) {
        _state.value = RutinasState.Loading
        viewModelScope.launch {
            repository.obtenerTodas(token).fold(
                onSuccess = {
                    _rutinas.value = it
                    _state.value = RutinasState.Idle
                },
                onFailure = {
                    _state.value = RutinasState.Error(it.message ?: "Error al cargar")
                }
            )
        }
    }

    fun desactivarRutina(token: String, rutinaId: Int) {
        viewModelScope.launch {
            repository.desactivar(token, rutinaId).fold(
                onSuccess = {
                    _state.value = RutinasState.DesactivadaExitosa("Rutina desactivada")
                    // Se marca como inactiva en vez de quitarla de la
                    // lista, para que pase a la sección de
                    // "desactivadas" en vez de desaparecer.
                    _rutinas.value = _rutinas.value?.map {
                        if (it.id == rutinaId) it.copy(esActivo = false) else it
                    }
                },
                onFailure = {
                    _state.value = RutinasState.Error(it.message ?: "Error al desactivar")
                }
            )
        }
    }

    fun activarRutina(token: String, rutinaId: Int) {
        viewModelScope.launch {
            repository.activar(token, rutinaId).fold(
                onSuccess = {
                    _state.value = RutinasState.ActivadaExitosa("Rutina activada")
                    _rutinas.value = _rutinas.value?.map {
                        if (it.id == rutinaId) it.copy(esActivo = true) else it
                    }
                },
                onFailure = {
                    _state.value = RutinasState.Error(it.message ?: "Error al activar")
                }
            )
        }
    }

    fun resetState() {
        _state.value = RutinasState.Idle
    }
}