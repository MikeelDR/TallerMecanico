package org.example.tallermecanico.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State

/**
 * Data classes for vehicle information
 */
data class Vehicle(
    val id: String,
    val make: String,
    val model: String,
    val year: Int,
    val licensePlate: String,
    val status: VehicleStatus,
    val fuelLevel: Int, // Percentage
    val mileage: Int,
    val lastServiceDate: String,
    val nextServiceDue: String
)

enum class VehicleStatus {
    ACTIVE,
    IN_SERVICE,
    REQUIRES_MAINTENANCE,
    OUT_OF_SERVICE,
    UNKNOWN,
    WAITING_FOR_SERVICE
}

/**
 * ViewModel for the Client Vehicle Status screen
 */
class ClientVehicleStatusViewModel(private val vehiculoId: String = "") : ViewModel() {
    // In a real app, this would come from a repository or data source
    private val _vehicles = mutableStateListOf<Vehicle>()
    val vehicles: List<Vehicle> = _vehicles

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // Selected vehicle if we're viewing a specific one
    private val _selectedVehicle = mutableStateOf<Vehicle?>(null)
    val selectedVehicle: State<Vehicle?> = _selectedVehicle

    init {
        fetchVehicles()
    }

    private fun fetchVehicles() {
        // Simulate network delay
        // In a real app, this would be a network call or database query
        _isLoading.value = true

        // Mock data for demonstration
        val vehiclesList = listOf(
            Vehicle(
                id = "VEH-001",
                make = "Toyota",
                model = "Camry",
                year = 2022,
                licensePlate = "ABC-1234",
                status = VehicleStatus.ACTIVE,
                fuelLevel = 75,
                mileage = 12500,
                lastServiceDate = "2024-03-15",
                nextServiceDue = "2024-09-15"
            ),
            Vehicle(
                id = "VEH-002",
                make = "Honda",
                model = "Civic",
                year = 2021,
                licensePlate = "XYZ-5678",
                status = VehicleStatus.REQUIRES_MAINTENANCE,
                fuelLevel = 25,
                mileage = 28750,
                lastServiceDate = "2023-11-10",
                nextServiceDue = "2024-05-10"
            ),
            Vehicle(
                id = "VEH-003",
                make = "Ford",
                model = "F-150",
                year = 2023,
                licensePlate = "DEF-9012",
                status = VehicleStatus.IN_SERVICE,
                fuelLevel = 50,
                mileage = 5200,
                lastServiceDate = "2024-04-22",
                nextServiceDue = "2024-10-22"
            )
        )

        _vehicles.addAll(vehiclesList)

        // If we have a specific vehiculoId, find and set that vehicle
        if (vehiculoId.isNotEmpty()) {
            _selectedVehicle.value = _vehicles.find { it.id == vehiculoId }

            // If vehicle not found, set error message
            if (_selectedVehicle.value == null) {
                _errorMessage.value = "Vehículo con ID $vehiculoId no encontrado"
            }
        }

        _isLoading.value = false
    }

    fun refreshVehicleData() {
        _vehicles.clear()
        _selectedVehicle.value = null
        _errorMessage.value = null
        fetchVehicles()
    }


}