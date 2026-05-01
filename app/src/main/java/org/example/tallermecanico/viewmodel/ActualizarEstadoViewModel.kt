package org.example.tallermecanico.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.tallermecanico.data.models.VehiculoRegistrado
import org.example.tallermecanico.ui.data.repository.VehiculosRepository
import javax.inject.Inject

@HiltViewModel
class ActualizarEstadoViewModel @Inject constructor(
    private val vehiculosRepository: org.example.tallermecanico.viewmodel.VehiculosRepository
) : ViewModel() {

    // Estado para el vehículo actual
    private val _vehiculo = MutableStateFlow<VehiculoRegistrado?>(null)
    val vehiculo: StateFlow<VehiculoRegistrado?> = _vehiculo.asStateFlow()

    // Estado para indicar carga
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    // Estado para manejar errores
    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    // Estado para indicar actualización exitosa
    private val _actualizacionExitosa = MutableStateFlow(false)
    val actualizacionExitosa: StateFlow<Boolean> = _actualizacionExitosa.asStateFlow()

    /**
     * Carga la información del vehículo por su ID
     */


    fun cargarVehiculo(vehiculoId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = ""
            _actualizacionExitosa.value = false

            try {
                val resultado = vehiculosRepository.getVehiculoRegistradoById(vehiculoId)
                if (resultado != null) {
                    _vehiculo.value = resultado
                } else {
                    _error.value = "No se encontró el vehículo"
                }
            } catch (e: Exception) {
                _error.value = "Error al obtener el vehículo: ${e.message ?: "Error desconocido"}"
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Actualiza el estado del vehículo
     */
    fun actualizarEstadoVehiculo(
        vehiculoId: String,
        nuevoEstado: String,
        descripcion: String,
        citaId: String? = null,
        notificarAlCliente: Boolean = true
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = ""
            _actualizacionExitosa.value = false

            try {
                // Usar el método del repositorio para actualizar el estado
                vehiculosRepository.actualizarEstadoVehiculo(
                    vehiculoId = vehiculoId,
                    nuevoEstado = nuevoEstado,
                    descripcion = descripcion,
                    citaId = citaId,
                    notificarAlCliente = notificarAlCliente
                )

                // Marcar como exitoso
                _actualizacionExitosa.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al actualizar el estado del vehículo"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun VehiculosRepository.actualizarEstadoVehiculo(
        vehiculoId: String,
        nuevoEstado: String,
        descripcion: String,
        citaId: String?,
        notificarAlCliente: Boolean
    ) {
    }

    /**
     * Resetea los estados para permitir nueva actualización
     */
    fun resetEstados() {
        _actualizacionExitosa.value = false
        _error.value = ""
    }


@HiltViewModel
class ActualizarEstadoViewModel @Inject constructor(
    private val vehiculosRepository: VehiculosRepository
) : ViewModel() {

    // Estado para el vehículo actual
    private val _vehiculo = MutableStateFlow<VehiculoRegistrado?>(null)
    val vehiculo: StateFlow<VehiculoRegistrado?> = _vehiculo.asStateFlow()

    // Estado para indicar carga
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    // Estado para manejar errores
    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    // Estado para indicar actualización exitosa
    private val _actualizacionExitosa = MutableStateFlow(false)
    val actualizacionExitosa: StateFlow<Boolean> = _actualizacionExitosa.asStateFlow()

    /**
     * Carga la información del vehículo por su ID
     */
    fun cargarVehiculo(vehiculoId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = ""
            _actualizacionExitosa.value = false

            try {
                val resultado = vehiculosRepository.getVehiculoRegistradoById(vehiculoId)
                if (resultado != null) {
                    _vehiculo.value = resultado
                } else {
                    _error.value = "No se encontró el vehículo"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar el vehículo"
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Actualiza el estado del vehículo
     */
    fun actualizarEstadoVehiculo(
        vehiculoId: String,
        nuevoEstado: String,
        descripcion: String,
        citaId: String? = null,
        notificarAlCliente: Boolean = true
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = ""
            _actualizacionExitosa.value = false

            try {
                // Usar el método del repositorio para actualizar el estado
                vehiculosRepository.actualizarEstadoVehiculo(
                    vehiculoId = vehiculoId,
                    nuevoEstado = nuevoEstado,
                    descripcion = descripcion,
                    citaId = citaId,
                    notificarAlCliente = notificarAlCliente
                )

                // Marcar como exitoso
                _actualizacionExitosa.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al actualizar el estado del vehículo"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun VehiculosRepository.actualizarEstadoVehiculo(
        vehiculoId: String,
        nuevoEstado: String,
        descripcion: String,
        citaId: String?,
        notificarAlCliente: Boolean

    ) {
        TODO("Not yet implemented")
    }

    /**
     * Resetea los estados para permitir nueva actualización
     */
    fun resetEstados() {
        _actualizacionExitosa.value = false
        _error.value = ""
    }


}
}
