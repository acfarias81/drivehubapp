// Em: network/ApiService.kt
package com.example.drivehubapp.network

import com.example.drivehubapp.model.ApiResponse
import com.example.drivehubapp.model.CheckInRequest
import com.example.drivehubapp.model.Vehicle
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // --- Login (Você já deve ter algo parecido) ---
    // (Ajuste LoginRequest e LoginResponse se os nomes forem diferentes)
    @POST("api.php?action=login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>


    // --- Jornada (Novos Endpoints) ---

    @GET("api.php?action=get_available_vehicles")
    fun getAvailableVehicles(): Call<ApiResponse<List<Vehicle>>>

    @POST("api.php?action=check_in_vehicle")
    fun checkInVehicle(@Body request: CheckInRequest): Call<ApiResponse<Any>> // 'Any' pois não esperamos dados de volta

    @POST("api.php?action=check_out_vehicle")
    fun checkOutVehicle(): Call<ApiResponse<Any>> // O ID do usuário vai pelo header


    // --- Checklist, Mensagens, etc (Adicionaremos depois) ---
    /*
    @POST("api.php?action=submit_checklist")
    fun submitChecklist(@Body checklistData: Any): Call<ApiResponse<Any>>

    @GET("api.php?action=get_messages")
    fun getMessages(): Call<ApiResponse<List<Any>>>
    */

}