package com.example.drivehubapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.drivehubapp.databinding.ActivityLoginBinding
// Importações necessárias
import com.example.drivehubapp.network.ApiClient
import com.example.drivehubapp.network.ApiService
import com.example.drivehubapp.network.LoginRequest
import com.example.drivehubapp.network.SessionManager
import com.example.drivehubapp.network.User // Assumindo que esta é sua classe de usuário
import com.example.drivehubapp.network.LoginResponse // Assumindo que esta é sua classe de resposta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager // <-- CORREÇÃO: Declarar a variável

    // private val PREFS_NAME = "drivehub_prefs" // <-- Removido, SessionManager agora cuida disso

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o SessionManager (Como planejado no Passo 1)
        sessionManager = SessionManager(this)

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
                // <-- CORREÇÃO: Obter o serviço a partir do ApiClient com Contexto
                // Isso é vital para o Interceptor funcionar nas próximas telas
                val apiService = ApiClient.getClient(this@LoginActivity).create(ApiService::class.java)

                // Seu LoginRequest parece estar correto
                val request = LoginRequest(cpf = cpf, senha = senha)

                val response = withContext(Dispatchers.IO) {
                    apiService.login(request)
                }

                if (response.isSuccessful) {
                    val body = response.body() // Tipo esperado: LoginResponse

                    // Assumindo que seu LoginResponse tem 'status' e 'user'
                    if (body != null &&
                        (body.status.equals("ok", ignoreCase = true) || body.status.equals("success", ignoreCase = true)) &&
                        body.user != null // 'user' é o objeto com os dados
                    ) {

                        // <-- CORREÇÃO: Usar o SessionManager para salvar o ID
                        sessionManager.saveUserId(body.user.id)

                        // <-- CORREÇÃO: Navegar para a tela de Seleção de Veículo
                        // (DashboardActivity será acessada DEPOIS de selecionar o veículo)
                        startActivity(Intent(this@LoginActivity, VehicleSelectionActivity::class.java))
                        finish() // Impede de voltar ao Login

                    } else {
                        val msg = body?.message ?: "Usuário ou senha inválidos"
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
        binding.editTextCpf.isEnabled = !isLoading
        binding.editTextPassword.isEnabled = !isLoading
    }

    // <-- CORREÇÃO: Esta função não é mais necessária
    // O SessionManager.saveUserId() já faz o trabalho principal.
    /*
    private fun saveUserData(user: User) {
        val sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("USER_ID", user.id)
            putString("USER_NAME", user.nome)
            putInt("USER_BASE_ID", user.baseId ?: -1)
            putBoolean("IS_LOGGED_IN", true)
            apply()
        }
    }
    */
}