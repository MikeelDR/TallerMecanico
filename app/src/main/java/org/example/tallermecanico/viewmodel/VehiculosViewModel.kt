package org.example.tallermecanico.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.tallermecanico.data.models.Vehiculo
import org.example.tallermecanico.ui.data.repository.VehiculosRepository

// Estado UI para manejar diferentes estados de la interfaz
sealed class VehiculosUIState {
    object Loading : VehiculosUIState()
    data class Success(val data: List<Vehiculo>) : VehiculosUIState()
    data class Error(val message: String) : VehiculosUIState()
    object Empty : VehiculosUIState()
}

// Estado UI para operaciones de un vehículo específico
sealed class VehiculoOperationState {
    object Idle : VehiculoOperationState()
    object Loading : VehiculoOperationState()
    data class Success(val vehiculo: Vehiculo? = null) : VehiculoOperationState()
    data class Error(val message: String) : VehiculoOperationState()
}

class VehiculosViewModel(private val vehiculosRepository: VehiculosRepository) : ViewModel() {

    private val _uiState = MutableLiveData<VehiculosUIState>(VehiculosUIState.Empty)
    val uiState: LiveData<VehiculosUIState> = _uiState

    private val _vehiculoActual = MutableLiveData<Vehiculo?>(null)
    val vehiculoActual: LiveData<Vehiculo?> = _vehiculoActual

    private val _operationState = MutableLiveData<VehiculoOperationState>(VehiculoOperationState.Idle)
    val operationState: LiveData<VehiculoOperationState> = _operationState

    // Alcance de corrutina para operaciones de viewmodel
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    fun cargarVehiculosDelUsuario(usuarioId: String) {
        _uiState.value = VehiculosUIState.Loading

        viewModelScope.launch {
            try {
                val vehiculosRegistrados = withContext(Dispatchers.IO) {
                    vehiculosRepository.obtenerVehiculosPorUsuario(usuarioId)
                }

                if (vehiculosRegistrados.isEmpty()) {
                    _uiState.value = VehiculosUIState.Empty
                } else {
                    // Convertir VehiculoRegistrado a Vehiculo si es necesario
                    val vehiculos = vehiculosRegistrados.map { vehiculoRegistrado ->
                        // Aquí realizamos la conversión de VehiculoRegistrado a Vehiculo
                        // Asumiendo que tienes un constructor o método que permite esta conversión
                        Vehiculo(
                            id = vehiculoRegistrado.id,
                            marca = vehiculoRegistrado.marcaVehiculo,
                            modelo = vehiculoRegistrado.modeloVehiculo,
                            anio = vehiculoRegistrado.anoVehiculo,
                            placa = vehiculoRegistrado.placa,
                            userId = vehiculoRegistrado.userId,
                            // Completa con otros campos necesarios si los hay
                        )
                    }
                    _uiState.value = VehiculosUIState.Success(vehiculos)
                }
            } catch (e: Exception) {
                _uiState.value = VehiculosUIState.Error(e.message ?: "Error desconocido al cargar vehículos")
            }
        }
    }

    fun cargarVehiculoPorId(vehiculoId: String) {
        _operationState.value = VehiculoOperationState.Loading

        viewModelScope.launch {
            try {
                val vehiculo = withContext(Dispatchers.IO) {
                    vehiculosRepository.obtenerVehiculoPorId(vehiculoId)
                }

                if (vehiculo != null) {
                    _vehiculoActual.value = vehiculo
                    _operationState.value = VehiculoOperationState.Success(vehiculo)
                } else {
                    _operationState.value = VehiculoOperationState.Error("No se encontró el vehículo solicitado")
                }
            } catch (e: Exception) {
                _operationState.value = VehiculoOperationState.Error(e.message ?: "Error desconocido al cargar el vehículo")
            }
        }
    }




    fun guardarVehiculo(vehiculo: Vehiculo, onComplete: (Boolean) -> Unit = {}) {
        _operationState.value = VehiculoOperationState.Loading

        viewModelScope.launch {
            try {
                val exito = withContext(Dispatchers.IO) {
                    vehiculosRepository.guardarVehiculo(vehiculo)
                }

                if (exito) {
                    // Actualizar la lista si ya tenemos vehículos cargados
                    actualizarListaVehiculos()
                    _operationState.value = VehiculoOperationState.Success()
                } else {
                    _operationState.value = VehiculoOperationState.Error("Error al guardar el vehículo")
                }
                onComplete(exito)
            } catch (e: Exception) {
                _operationState.value = VehiculoOperationState.Error(e.message ?: "Error desconocido al guardar el vehículo")
                onComplete(false)
            }
        }
    }

    fun actualizarVehiculo(vehiculo: Vehiculo, onComplete: (Boolean) -> Unit = {}) {
        _operationState.value = VehiculoOperationState.Loading

        viewModelScope.launch {
            try {
                val exito = withContext(Dispatchers.IO) {
                    vehiculosRepository.actualizarVehiculo(vehiculo)
                }

                if (exito) {
                    // Actualizar la lista local si el vehículo ya está en ella
                    when (val currentState = _uiState.value) {
                        is VehiculosUIState.Success -> {
                            val updatedList = currentState.data.map {
                                if (it.id == vehiculo.id) vehiculo else it
                            }
                            _uiState.value = VehiculosUIState.Success(updatedList)
                        }
                        else -> { /* No hacer nada si no hay lista cargada */ }
                    }

                    // Actualizar el vehículo actual si está siendo visualizado
                    if (_vehiculoActual.value?.id == vehiculo.id) {
                        _vehiculoActual.value = vehiculo
                    }

                    _operationState.value = VehiculoOperationState.Success(vehiculo)
                } else {
                    _operationState.value = VehiculoOperationState.Error("Error al actualizar el vehículo")
                }
                onComplete(exito)
            } catch (e: Exception) {
                _operationState.value = VehiculoOperationState.Error(e.message ?: "Error desconocido al actualizar el vehículo")
                onComplete(false)
            }
        }
    }

    fun eliminarVehiculo(vehiculoId: String, onComplete: (Boolean) -> Unit = {}) {
        _operationState.value = VehiculoOperationState.Loading

        viewModelScope.launch {
            try {
                val exito = withContext(Dispatchers.IO) {
                    vehiculosRepository.eliminarVehiculo(vehiculoId)
                }

                if (exito) {
                    // Eliminar de la lista local
                    when (val currentState = _uiState.value) {
                        is VehiculosUIState.Success -> {
                            val filteredList = currentState.data.filter { it.id != vehiculoId }
                            _uiState.value = if (filteredList.isEmpty()) {
                                VehiculosUIState.Empty
                            } else {
                                VehiculosUIState.Success(filteredList)
                            }
                        }
                        else -> { /* No hacer nada si no hay lista cargada */ }
                    }

                    // Limpiar el vehículo actual si es el que se eliminó
                    if (_vehiculoActual.value?.id == vehiculoId) {
                        _vehiculoActual.value = null
                    }

                    _operationState.value = VehiculoOperationState.Success()
                } else {
                    _operationState.value = VehiculoOperationState.Error("Error al eliminar el vehículo")
                }
                onComplete(exito)
            } catch (e: Exception) {
                _operationState.value = VehiculoOperationState.Error(e.message ?: "Error desconocido al eliminar el vehículo")
                onComplete(false)
            }
        }
    }

    // Método privado para actualizar la lista después de una operación exitosa
    private fun actualizarListaVehiculos() {
        // Obtener el ID del usuario actual si está disponible
        val currentState = _uiState.value
        if (currentState is VehiculosUIState.Success && currentState.data.isNotEmpty()) {
            val usuarioId = currentState.data.firstOrNull()?.usuarioId
            if (usuarioId != null) {
                cargarVehiculosDelUsuario(usuarioId)
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = VehiculoOperationState.Idle
    }
}

/**
 * Factory para crear instancias de VehiculosViewModel con las dependencias necesarias
 */
class VehiculosViewModelFactory(private val vehiculosRepository: VehiculosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VehiculosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VehiculosViewModel(vehiculosRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}