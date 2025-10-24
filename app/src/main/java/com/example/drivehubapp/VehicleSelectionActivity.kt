// Em: com/example/drivehubapp/VehicleSelectionActivity.kt
package com.example.drivehubapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drivehubapp.databinding.ActivityVehicleSelectionBinding
import com.example.drivehubapp.model.CheckInRequest
import com.example.drivehubapp.model.Vehicle
import com.example.drivehubapp.network.ApiClient
import com.example.drivehubapp.network.ApiService
import com.example.drivehubapp.network.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VehicleSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVehicleSelectionBinding
    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager
    private var currentUserId: Int = -1
    private var vehicleAdapter: VehicleAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa
        apiService = ApiClient.getClient(this).create(ApiService::class.java)
        sessionManager = SessionManager(this)
        currentUserId = sessionManager.getUserId()

        // Configura a Toolbar
        setSupportActionBar(binding.toolbar)
        setupToolbarMenu()

        // Se o ID for inválido, força o logout
        if (currentUserId <= 0) {
            handleLogout()
            return
        }

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        // Recarrega a lista sempre que a tela voltar ao foco
        // (Ex: ao voltar do Dashboard)
        loadVehicles()
    }

    private fun setupToolbarMenu() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    handleLogout()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        // Adapter é inicializado vazio
        vehicleAdapter = VehicleAdapter(
            context = this,
            vehicles = emptyList(),
            currentUserId = currentUserId,
            onCheckInClick = { vehicle -> handleCheckIn(vehicle) },
            onCheckOutClick = { vehicle -> handleCheckOut(vehicle) },
            onVehicleClick = { vehicle -> navigateToDashboard(vehicle.id) }
        )
        binding.recyclerViewVehicles.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewVehicles.adapter = vehicleAdapter
    }

    private fun loadVehicles() {
        setLoading(true)
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.getAvailableVehicles()
                }

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.status == "success" && apiResponse.data != null) {
                        // Sucesso
                        displayVehicles(apiResponse.data)
                    } else {
                        showError(apiResponse?.message ?: "Falha ao carregar dados")
                    }
                } else {
                    showError("Erro de servidor: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("VehicleSelection", "Exceção: ${e.message}", e)
                showError("Falha na conexão: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    private fun displayVehicles(vehicles: List<Vehicle>) {
        if (vehicles.isEmpty()) {
            showError("Nenhum veículo encontrado.")
        } else {
            binding.textEmptyView.visibility = View.GONE
            binding.recyclerViewVehicles.visibility = View.VISIBLE
            vehicleAdapter?.updateData(vehicles) // Atualiza os dados no adapter
        }
    }

    // Ação: Selecionar um veículo
    private fun handleCheckIn(vehicle: Vehicle) {
        setLoading(true) // Mostra loading
        lifecycleScope.launch {
            try {
                val request = CheckInRequest(veiculoId = vehicle.id)
                val response = withContext(Dispatchers.IO) {
                    apiService.checkInVehicle(request)
                }

                if (response.isSuccessful && response.body()?.status == "success") {
                    // Sucesso! Navega para o Dashboard
                    navigateToDashboard(vehicle.id)
                } else {
                    val msg = response.body()?.message ?: "Não foi possível selecionar o veículo"
                    Toast.makeText(this@VehicleSelectionActivity, msg, Toast.LENGTH_LONG).show()
                    setLoading(false) // Para o loading se falhar
                }
            } catch (e: Exception) {
                Toast.makeText(this@VehicleSelectionActivity, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                setLoading(false) // Para o loading se falhar
            }
            // O loading será parado pelo 'finally' do loadVehicles() no onResume
        }
    }

    // Ação: Sair do veículo
    private fun handleCheckOut(vehicle: Vehicle) {
        setLoading(true)
        lifecycleScope.launch {
            try {
                // A API (check_out_vehicle) não precisa do ID, pois o user_id
                // (no header) já define quem está saindo.
                val response = withContext(Dispatchers.IO) {
                    apiService.checkOutVehicle()
                }

                if (response.isSuccessful && response.body()?.status == "success") {
                    // Sucesso! Recarrega a lista
                    loadVehicles()
                } else {
                    val msg = response.body()?.message ?: "Não foi possível sair do veículo"
                    Toast.makeText(this@VehicleSelectionActivity, msg, Toast.LENGTH_LONG).show()
                    setLoading(false)
                }
            } catch (e: Exception) {
                Toast.makeText(this@VehicleSelectionActivity, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                setLoading(false)
            }
            // O loading será parado pelo 'finally' do loadVehicles()
        }
    }

    // Ação: Navegar para o Dashboard
    private fun navigateToDashboard(vehicleId: Int) {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("VEHICLE_ID", vehicleId) // Envia o ID do veículo
        startActivity(intent)
    }

    // Ação: Logout
    private fun handleLogout() {
        sessionManager.clearSession()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        // Trava a lista enquanto carrega
        binding.recyclerViewVehicles.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        binding.textEmptyView.text = message
        binding.textEmptyView.visibility = View.VISIBLE
        binding.recyclerViewVehicles.visibility = View.GONE
    }
}