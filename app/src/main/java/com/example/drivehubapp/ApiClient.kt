package com.example.drivehubapp

import com.example.drivehubapp.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // ✅ URL base do seu servidor (sempre termina com /)
    private const val BASE_URL = "http://alagoasti.ddns.net/drivehub/public/api.php?action=login"

    // Interceptor de log — útil para depuração (mostra as requisições e respostas no Logcat)
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP com tempos de timeout ampliados e reconexão automática
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    // Criação do serviço Retrofit
    val service: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://alagoasti.ddns.net/drivehub/public/") // <- CORREÇÃO AQUI
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}