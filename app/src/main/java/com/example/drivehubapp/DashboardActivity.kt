package com.example.drivehubapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.drivehubapp.databinding.ActivityDashboardBinding // <-- 1. Importe o Binding

class DashboardActivity : AppCompatActivity() {

    // <-- 2. Declare o binding
    private lateinit var binding: ActivityDashboardBinding

    // <-- 3. A variável deve ficar AQUI DENTRO
    private var currentVehicleId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // <-- 4. Configure o Binding
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Este é o código da minha resposta anterior ---

        // Recebe o ID do veículo enviado pela VehicleSelectionActivity
        currentVehicleId = intent.getIntExtra("VEHICLE_ID", -1)

        if (currentVehicleId == -1) {
            // Se, por algum motivo, o ID não for passado,
            // informa o erro e fecha a activity
            Toast.makeText(this, "Erro: ID do Veículo não encontrado", Toast.LENGTH_LONG).show()
            finish() // Volta para a tela anterior
            return
        }

        // Agora você pode usar 'currentVehicleId' para o checklist, etc.
        Toast.makeText(this, "Veículo ID: $currentVehicleId selecionado", Toast.LENGTH_SHORT).show()

        // Ex: Atualize o título da Toolbar (se você tiver uma no seu layout)
        // (Assumindo que sua toolbar tenha o id 'toolbar' no XML)
        // setSupportActionBar(binding.toolbar)
        // supportActionBar?.title = "Jornada - Veículo $currentVehicleId"

        // ... resto da lógica do dashboard (checklist, etc) ...
    }
}