package com.example.drivehubapp.network

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val nome: String,
    val email: String,
    @SerializedName("base_id") val baseId: Int?
)