package com.example.proyecto.ui.perfil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.model.Cliente
import com.example.proyecto.data.model.ClienteRequest
import com.example.proyecto.data.repository.ClientesRepository
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    private val repository = ClientesRepository()

    // ── Estados de UI ────────────────────────────────────────────
    sealed class ClientesUiState {
        object Idle : ClientesUiState()
        object Loading : ClientesUiState()
        data class Error(val mensaje: String) : ClientesUiState()
        data class GuardadoExitoso(val mensaje: String) : ClientesUiState()
        data class EliminadoExitoso(val mensaje: String) : ClientesUiState()
    }

    private val _uiState = MutableLiveData<ClientesUiState>(ClientesUiState.Idle)
    val uiState: LiveData<ClientesUiState> = _uiState

    // Lista completa para el grid
    private val _clientes = MutableLiveData<List<Cliente>>(emptyList())
    val clientes: LiveData<List<Cliente>> = _clientes

    // Cliente actualmente cargado en el formulario (null = modo "nuevo")
    private val _clienteSeleccionado = MutableLiveData<Cliente?>(null)
    val clienteSeleccionado: LiveData<Cliente?> = _clienteSeleccionado

    // ── Cargar todos los clientes (refrescar grid) ───────────────
    fun cargarClientes(bearerToken: String) {
        viewModelScope.launch {
            val result = repository.obtenerTodos(bearerToken)
            result.fold(
                onSuccess = { lista -> _clientes.value = lista },
                onFailure = { error ->
                    _uiState.value = ClientesUiState.Error(error.message ?: "Error al cargar clientes")
                }
            )
        }
    }

    // ── Buscar por clave (mientras se teclea) ────────────────────
    fun buscarPorClave(bearerToken: String, clave: String) {
        if (clave.isBlank()) {
            _clienteSeleccionado.value = null
            return
        }

        viewModelScope.launch {
            val result = repository.buscarPorClave(bearerToken, clave)
            result.fold(
                onSuccess = { respuesta ->
                    _clienteSeleccionado.value = if (respuesta.encontrado) respuesta.cliente else null
                },
                onFailure = {
                    // Si falla la búsqueda no bloqueamos la captura, solo no autocompleta
                    _clienteSeleccionado.value = null
                }
            )
        }
    }

    // ── Seleccionar cliente desde el grid (click en una fila) ────
    fun seleccionarCliente(cliente: Cliente) {
        _clienteSeleccionado.value = cliente
    }

    // ── Limpiar selección (botón "Nuevo") ─────────────────────────
    fun limpiarSeleccion() {
        _clienteSeleccionado.value = null
    }

    // ── Guardar (inserta o actualiza según si hay cliente seleccionado) ──
    fun guardar(bearerToken: String, clave: String, nombre: String, edad: Int, fechaNacimiento: String) {
        if (clave.isBlank() || nombre.isBlank()) {
            _uiState.value = ClientesUiState.Error("Clave y Nombre son obligatorios")
            return
        }

        _uiState.value = ClientesUiState.Loading
        val request = ClienteRequest(clave.trim(), nombre.trim(), edad, fechaNacimiento)
        val clienteActual = _clienteSeleccionado.value

        viewModelScope.launch {
            val result = if (clienteActual != null) {
                repository.actualizar(bearerToken, clienteActual.id, request)
            } else {
                repository.insertar(bearerToken, request)
            }

            result.fold(
                onSuccess = { clienteGuardado ->
                    _uiState.value = ClientesUiState.GuardadoExitoso(
                        if (clienteActual != null) "Cliente actualizado" else "Cliente guardado"
                    )
                    _clienteSeleccionado.value = clienteGuardado
                    cargarClientes(bearerToken)  // refresca el grid
                },
                onFailure = { error ->
                    _uiState.value = ClientesUiState.Error(error.message ?: "Error al guardar")
                }
            )
        }
    }

    // ── Eliminar cliente seleccionado ─────────────────────────────
    fun eliminar(bearerToken: String) {
        val cliente = _clienteSeleccionado.value
        if (cliente == null) {
            _uiState.value = ClientesUiState.Error("Selecciona un cliente primero")
            return
        }

        _uiState.value = ClientesUiState.Loading

        viewModelScope.launch {
            val result = repository.eliminar(bearerToken, cliente.id)
            result.fold(
                onSuccess = {
                    _uiState.value = ClientesUiState.EliminadoExitoso("Cliente eliminado")
                    _clienteSeleccionado.value = null
                    cargarClientes(bearerToken)  // refresca el grid
                },
                onFailure = { error ->
                    _uiState.value = ClientesUiState.Error(error.message ?: "Error al eliminar")
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = ClientesUiState.Idle
    }
}