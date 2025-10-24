// Em um novo arquivo, ex: "network/DataTransferObjects.kt"
package com.example.drivehubapp // Use seu package name

data class LoginRequest(
    val cpf: String,
    val senha: String // Garanta que o nome do campo corresponde ao que a API espera
)

data class LoginResponse(
    val status: String,
    val message: String,
    val user: UserData?
)

data class UserData(
    val id: Int,
    val nome: String,
    val baseId: Int // Garanta que os nomes e tipos correspondem à API
)
