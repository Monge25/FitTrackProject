package com.example.proyecto.ui.usuarios

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.model.ActualizarUsuarioRequest
import com.example.proyecto.data.model.Usuario
import com.example.proyecto.data.repository.AuthRepository
import kotlinx.coroutines.launch

class UsuariosViewModel : ViewModel() {

    private val repository = AuthRepository()

    sealed class UsuariosState {
        object Idle : UsuariosState()
        object Loading : UsuariosState()
        data class Error(val mensaje: String) : UsuariosState()
        data class Exito(val mensaje: String) : UsuariosState()
    }

    private val _usuarios = MutableLiveData<List<Usuario>>(emptyList())
    val usuarios: LiveData<List<Usuario>> = _usuarios

    private val _state = MutableLiveData<UsuariosState>(UsuariosState.Idle)
    val state: LiveData<UsuariosState> = _state

    fun cargarUsuarios(token: String) {
        _state.value = UsuariosState.Loading
        viewModelScope.launch {
            repository.obtenerUsuarios(token).fold(
                onSuccess = {
                    _usuarios.value = it
                    _state.value = UsuariosState.Idle
                },
                onFailure = {
                    _state.value = UsuariosState.Error(it.message ?: "Error al cargar usuarios")
                }
            )
        }
    }

    fun registrarUsuario(token: String, nombre: String, email: String, password: String, rol: String) {
        _state.value = UsuariosState.Loading
        viewModelScope.launch {
            val rolApi = if (rol == "ADMINISTRADOR") "ADMINISTRATOR" else "OPERATOR"
            repository.registrar(nombre, email, password, rolApi).fold(
                onSuccess = {
                    _state.value = UsuariosState.Exito("Usuario creado correctamente")
                    cargarUsuarios(token)
                },
                onFailure = {
                    _state.value = UsuariosState.Error(it.message ?: "Error al crear usuario")
                }
            )
        }
    }

    fun actualizarUsuario(
        token: String,
        id: Int,
        nombre: String,
        correo: String,
        rol: String,
        esActivo: Boolean,
        password: String?
    ) {
        _state.value = UsuariosState.Loading
        viewModelScope.launch {
            val rolNum = if (rol.uppercase().contains("ADMIN")) 0 else 1
            val request = ActualizarUsuarioRequest(
                nombre   = nombre,
                email    = correo,
                rol      = rolNum,
                esActivo = esActivo,
                password = password?.ifBlank { null }
            )
            repository.actualizarUsuario(token, id, request).fold(
                onSuccess = {
                    _state.value = UsuariosState.Exito("Usuario actualizado correctamente")
                    cargarUsuarios(token)
                },
                onFailure = {
                    _state.value = UsuariosState.Error(it.message ?: "Error al actualizar usuario")
                }
            )
        }
    }

    fun resetState() {
        _state.value = UsuariosState.Idle
    }
}