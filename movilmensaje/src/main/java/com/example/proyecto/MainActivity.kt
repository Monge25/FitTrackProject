package com.example.proyecto
import com.example.movilmensaje.R

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyecto.ui.login.LoginViewModel
import com.example.proyecto.ui.recuperar.RecuperarPasswordActivity
import com.example.proyecto.ui.registro.RegistroActivity
import com.example.proyecto.utils.TokenManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var tokenManager: TokenManager

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressLogin: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tokenManager = TokenManager(this)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressLogin = findViewById(R.id.progressLogin)

//        lifecycleScope.launch {
//            val token = tokenManager.obtenerToken()
//            if (!token.isNullOrEmpty()) {
//                irAClientes()
//            }
//        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty()) {
                etEmail.error = "Ingresa tu correo electrónico"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Ingresa la contraseña"
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        findViewById<android.widget.TextView>(
            R.id.tvIrARegistro
        ).setOnClickListener {
            startActivity(
                Intent(this, RegistroActivity::class.java)
            )
        }

        findViewById<android.widget.TextView>(
            R.id.tvOlvidoPassword
        ).setOnClickListener {
            startActivity(
                Intent(this, RecuperarPasswordActivity::class.java)
            )
        }

        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginViewModel.LoginState.Loading -> {
                    progressLogin.visibility = View.VISIBLE
                    btnLogin.isEnabled = false
                }
                is LoginViewModel.LoginState.Success -> {
                    progressLogin.visibility = View.GONE
                    btnLogin.isEnabled = true
                    lifecycleScope.launch {
                        tokenManager.guardarToken(state.token)
                        tokenManager.guardarDatosUsuario(
                            nombre = state.nombre,
                            rol = state.rol
                        )
                        irAPrincipal()
                    }
                }
                is LoginViewModel.LoginState.Error -> {
                    progressLogin.visibility = View.GONE
                    btnLogin.isEnabled = true
                    Toast.makeText(this, state.mensaje, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun irAPrincipal() {
        val intent = Intent(this, Principal::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun irAClientes() {
        val intent = Intent(this, Principal::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}