// Em: com/example/drivehubapp/VehicleAdapter.kt
package com.example.drivehubapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.drivehubapp.model.Vehicle

// Definindo 'typealias' para facilitar a leitura das funções Lambda
typealias OnVehicleAction = (Vehicle) -> Unit

class VehicleAdapter(
    private val context: Context,
    private var vehicles: List<Vehicle>,
    private val currentUserId: Int,
    private val onCheckInClick: OnVehicleAction,
    private val onCheckOutClick: OnVehicleAction,
    private val onVehicleClick: OnVehicleAction
) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    // Armazena o veículo do usuário (se houver)
    private var myVehicle: Vehicle? = null
    // Armazena os outros veículos
    private var otherVehicles: List<Vehicle> = emptyList()

    init {
        // Separa as listas
        processVehicleList(vehicles)
    }

    // Lógica principal: Separa "meu" veículo do resto
    private fun processVehicleList(vehicles: List<Vehicle>) {
        myVehicle = vehicles.firstOrNull { it.motoristaAtualId == currentUserId }

        // Se eu estou em um veículo, a lista SÓ MOSTRA ele.
        // Se não, mostra os outros (disponíveis ou ocupados por terceiros).
        otherVehicles = if (myVehicle != null) {
            emptyList()
        } else {
            vehicles.filter { it.motoristaAtualId != currentUserId }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_vehicle, parent, false)
        return VehicleViewHolder(view)
    }

    // Decide qual veículo mostrar (o meu ou um da lista)
    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicleToShow = myVehicle ?: otherVehicles[position]
        holder.bind(vehicleToShow)
    }

    // A contagem de itens é 1 (se eu estou em um carro) ou o total de outros carros
    override fun getItemCount(): Int {
        return if (myVehicle != null) 1 else otherVehicles.size
    }

    fun updateData(newVehicles: List<Vehicle>) {
        this.vehicles = newVehicles
        processVehicleList(newVehicles)
        notifyDataSetChanged() // Recarrega a lista
    }

    inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Mapeamento dos componentes do layout (list_item_vehicle.xml)
        private val iconCar: ImageView = itemView.findViewById(R.id.iconCar)
        private val textPlacaModelo: TextView = itemView.findViewById(R.id.textPlacaModelo)
        private val textStatus: TextView = itemView.findViewById(R.id.textStatus)
        private val buttonSelecionar: Button = itemView.findViewById(R.id.buttonSelecionar)
        private val buttonSair: Button = itemView.findViewById(R.id.buttonSair)

        fun bind(vehicle: Vehicle) {
            textPlacaModelo.text = "${vehicle.placa} - ${vehicle.modelo}"

            when {
                // CASO 1: É O MEU VEÍCULO
                vehicle.motoristaAtualId == currentUserId -> {
                    textStatus.text = "Você está neste veículo"
                    iconCar.setColorFilter(ContextCompat.getColor(context, R.color.design_default_color_primary)) // Azul
                    buttonSelecionar.visibility = View.GONE
                    buttonSair.visibility = View.VISIBLE

                    buttonSair.setOnClickListener { onCheckOutClick(vehicle) }
                    itemView.setOnClickListener { onVehicleClick(vehicle) } // Clicar no card vai pro Dashboard
                }

                // CASO 2: DISPONÍVEL
                vehicle.disponivel && vehicle.motoristaAtualId == null -> {
                    textStatus.text = "Disponível (Base: ${vehicle.nomeBase ?: "N/A"})"
                    iconCar.setColorFilter(ContextCompat.getColor(context, android.R.color.holo_green_dark)) // Verde
                    buttonSelecionar.visibility = View.VISIBLE
                    buttonSair.visibility = View.GONE

                    buttonSelecionar.setOnClickListener { onCheckInClick(vehicle) }
                    itemView.setOnClickListener { onCheckInClick(vehicle) } // Clicar no card também seleciona
                }

                // CASO 3: OCUPADO POR OUTRO
                else -> {
                    textStatus.text = "Em uso"
                    iconCar.setColorFilter(ContextCompat.getColor(context, android.R.color.darker_gray)) // Cinza
                    buttonSelecionar.visibility = View.GONE
                    buttonSair.visibility = View.GONE

                    itemView.isEnabled = false // Desabilita o clique
                }
            }
        }
    }
}