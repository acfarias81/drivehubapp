package com.example.drivehubapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.drivehubapp.databinding.ActivityLoginBinding
import com.example.drivehubapp.network.LoginRequest
import com.example.drivehubapp.network.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val PREFS_NAME = "drivehub_prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener {
            val cpf = binding.editTextCpf.text.toString().trim()
            val senha = binding.editTextPassword.text.toString().trim()
            if (cpf.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha CPF e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            doLogin(cpf, senha)
        }
    }

    private fun doLogin(cpf: String, senha: String) {
        setLoading(true)
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.service.login(LoginRequest(cpf = cpf, senha = senha)) // <-- CORREÇÃO
                }


                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null &&
                        (body.status.equals("ok", ignoreCase = true) || body.status.equals("success", ignoreCase = true)) &&
                        body.user != null
                    ) {
                        // CORREÇÃO: 'body.user' já é o objeto UserData.
                        // Não é necessário copiá-lo campo por campo.
                        // O erro 'No parameter with name 'baseId' found' era aqui,
                        // pois a classe UserData espera 'base_id' (vindo do JSON) e não 'baseId'.
                        saveUserData(body.user)

                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                        finish()
                    } else {
                        val msg = body?.message ?: "Erro no login"
                        Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_LONG).show()
                    }

                } else {
                    val err = response.errorBody()?.string()
                    Log.e("Login", "Erro HTTP: ${response.code()} - $err")
                    Toast.makeText(this@LoginActivity, "Erro no servidor: ${response.code()}", Toast.LENGTH_LONG).show()
                }

            } catch (ex: Exception) {
                Log.e("Login", "Exceção: ${ex.message}", ex)
                Toast.makeText(this@LoginActivity, "Falha de conexão: ${ex.message}", Toast.LENGTH_LONG).show()
            } finally {
                setLoading(false)
            }
        }
    }




    private fun setLoading(isLoading: Boolean) {
        binding.buttonLogin.isEnabled = !isLoading
        // habilite/disable outros campos conforme sua UI
    }

    private fun saveUserData(user: User) {
        val sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("USER_ID", user.id)
            putString("USER_NAME", user.nome)
            // CORREÇÃO: Use 'baseId' (camelCase)
            putInt("USER_BASE_ID", user.baseId ?: -1)
            putBoolean("IS_LOGGED_IN", true)
            apply()
        }
    }
}