// Em: model/Vehicle.kt
package com.example.drivehubapp.model

import com.google.gson.annotations.SerializedName

// Modelo para a resposta genérica da API (status/message)
data class ApiResponse<T>(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: T?
)

// Modelo do Veículo
data class Vehicle(
    @SerializedName("id")
    val id: Int,
    @SerializedName("placa")
    val placa: String,
    @SerializedName("modelo")
    val modelo: String,
    @SerializedName("base_nome")
    val nomeBase: String?,
    @SerializedName("motorista_atual_id")
    val motoristaAtualId: Int?,
    @SerializedName("disponivel")
    val disponivel: Boolean
)

// Modelo para a requisição de Check-in
data class CheckInRequest(
    @SerializedName("veiculo_id")
    val veiculoId: Int
)