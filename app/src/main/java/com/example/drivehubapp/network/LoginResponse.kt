package com.example.drivehubapp.network


data class LoginResponse(
    val status: String,
    val message: String?,  // <- importante
    val user: User?
)