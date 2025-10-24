// Em: network/SessionManager.kt (ou utils/SessionManager.kt)
package com.example.drivehubapp.network

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("DriveHubPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val USER_ID = "user_id"
    }

    /**
     * Salva o ID do usuário após o login
     */
    fun saveUserId(id: Int) {
        val editor = prefs.edit()
        editor.putInt(USER_ID, id)
        editor.apply()
    }

    /**
     * Busca o ID do usuário para adicionar aos headers
     */
    fun getUserId(): Int {
        // Retorna -1 (ou 0) se não encontrado
        return prefs.getInt(USER_ID, -1)
    }

    /**
     * Limpa a sessão (para o Logout)
     */
    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}