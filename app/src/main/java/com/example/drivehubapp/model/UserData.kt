package com.example.drivehubapp.model

import com.google.gson.annotations.SerializedName

data class UserData(
    val id: Int,
    val nome: String,

    // 1. Permite que o email seja nulo (como na resposta anterior)
    val email: String?,

    // 2. Mapeia o 'base_id' do JSON para a propriedade 'base_id'
    // Se preferir usar camelCase (baseId), você pode fazer:
    // @SerializedName("base_id")
    // val baseId: Int?

    // Vou usar snake_case para bater com o código que corrigi:
    val base_id: Int?
)