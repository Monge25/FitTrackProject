package com.example.proyecto.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    // Estados posibles del login
    sealed class LoginState {
        object Loading : LoginState()
        data class Success(val token: String) : LoginState()
        data class Error(val mensaje: String) : LoginState()
    }

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Completa todos los campos")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val result = repository.login(username, password)

            result.fold(
                onSuccess = { token ->
                    _loginState.value = LoginState.Success(token)
                },
                onFailure = { error ->
                    _loginState.value = LoginState.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }
}