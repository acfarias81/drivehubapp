package com.example.drivehubapp.network

import com.example.drivehubapp.network.LoginRequest
import com.example.drivehubapp.network.LoginResponse


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api.php?action=login") // <- Coloca a ação direto aqui
    suspend fun login(
        @Body loginData: LoginRequest
    ): Response<LoginResponse>
}