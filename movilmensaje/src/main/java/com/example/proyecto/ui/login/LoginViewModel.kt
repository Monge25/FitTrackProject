package com.example.proyecto.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    sealed class LoginState {
        object Loading : LoginState()
        data class Success(
            val token: String,
            val nombre: String,
            val rol: String,
            val email: String,
            val usuarioId: Int
        ) : LoginState()
        data class Error(val mensaje: String) : LoginState()
    }

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Completa todos los campos")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val result = repository.login(email, password)

            result.fold(
                onSuccess = { respuesta ->
                    _loginState.value = LoginState.Success(
                        token = respuesta.token,
                        nombre = respuesta.nombre,
                        rol = respuesta.rol,
                        email = respuesta.email,
                        usuarioId =  respuesta.usuarioId
                    )
                },
                onFailure = { error ->
                    _loginState.value = LoginState.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }
}