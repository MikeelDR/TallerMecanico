package org.example.tallermecanico.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import org.example.tallermecanico.data.models.Cita
import org.example.tallermecanico.data.models.Servicio
import org.example.tallermecanico.data.models.Vehiculo
import org.example.tallermecanico.ui.data.repository.CitasRepository
import org.example.tallermecanico.ui.data.repository.NotificacionesRepository
import org.example.tallermecanico.ui.data.repository.ServiciosRepository
import org.example.tallermecanico.ui.data.repository.VehiculosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel que maneja la lógica y estado de las vistas para un trabajador.
 */
@HiltViewModel
class TrabajadorViewModel @Inject constructor(
    private val citasRepository: CitasRepository,
    private val serviciosRepository: ServiciosRepository,
    private val vehiculosRepository: VehiculosRepository,
    private val notificacionesRepository: NotificacionesRepository
) : ViewModel() {

    // Estado de citas
    private val _citasDeHoy = MutableStateFlow<List<Cita>>(emptyList())
    val citasDeHoy: StateFlow<List<Cita>> = _citasDeHoy.asStateFlow()

    // Estado de carga para las operaciones
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // Estado de errores
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Estado de notificaciones
    val notificaciones = notificacionesRepository.notificaciones
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Estado para notificaciones sin leer (contador)
    private val _notificacionesSinLeer = MutableStateFlow(0)
    val notificacionesSinLeer: StateFlow<Int> = _notificacionesSinLeer.asStateFlow()

    // Para servicios disponibles
    private val _servicios = MutableStateFlow<List<Servicio>>(emptyList())
    val servicios: StateFlow<List<Servicio>> = _servicios.asStateFlow()

    init {
        cargarDatos()
        observarNotificaciones()
    }

    /**
     * Carga los datos iniciales necesarios
     */
    private fun cargarDatos() {
        viewModelScope.launch {
            try {
                _cargando.value = true
                cargarCitasDeHoy()
                cargarServicios()
                _cargando.value = false
            } catch (e: Exception) {
                _error.value = "Error al cargar datos: ${e.message}"
                _cargando.value = false
            }
        }
    }

    /**
     * Observa cambios en notificaciones para actualizar el contador
     */
    private fun observarNotificaciones() {
        viewModelScope.launch {
            notificacionesRepository.notificaciones.collect { lista ->
                _notificacionesSinLeer.value = lista.count { !it.leida }
            }
        }
    }

    /**
     * Carga las citas programadas para hoy
     */
    fun cargarCitasDeHoy() {
        viewModelScope.launch {
            try {
                val hoy = LocalDate.now()
                _citasDeHoy.value = citasRepository.obtenerCitasPorFecha(hoy)
            } catch (e: Exception) {
                _error.value = "Error al cargar citas: ${e.message}"
            }
        }
    }







    /**
     * Carga la lista de servicios disponibles
     */
    private fun cargarServicios() {
        viewModelScope.launch {
            try {
                _servicios.value = serviciosRepository.obtenerServicios()
            } catch (e: Exception) {
                _error.value = "Error al cargar servicios: ${e.message}"
            }
        }
    }

    // Correct implementation with proper return type
    suspend fun ServiciosRepository.obtenerServicios(): List<Servicio> {
        // Here you would typically:
        // 1. Call a remote API or local database
        // 2. Process the data
        // 3. Return a list of Servicio objects

        return try {
            // Example implementation:
            // apiService.getServicios() or localDatabase.queryServicios()
            // For now, we'll return an empty list or placeholder data
            listOf(
                Servicio(1.toString(), "Cambio de aceite", "Cambio de aceite y filtro",
                    50.0.toString()
                ),
                Servicio(2.toString(), "Revisión de frenos", "Inspección y ajuste del sistema de frenos",
                    70.0.toString()
                ),
                Servicio(3.toString(), "Alineación", "Alineación de ruedas", 60.0.toString())
            )
        } catch (e: Exception) {
            // Handle specific exceptions here if needed
            throw e
        }
    }

    /**
     * Actualiza el estado de una cita
     */
    fun actualizarEstadoCita(citaId: String, nuevoEstado: String) {
        viewModelScope.launch {
            try {
                _cargando.value = true
                citasRepository.actualizarEstadoCita(citaId, nuevoEstado)
                cargarCitasDeHoy()

                // Crear notificación de cambio de estado
                notificacionesRepository.agregarNotificacion(
                    NotificacionesRepository.Notificacion(
                        id = java.util.UUID.randomUUID().toString(),
                        titulo = "Cambio de estado en cita",
                        mensaje = "La cita #$citaId ha cambiado a estado: $nuevoEstado",
                        leida = false,
                        fechaCreacion = System.currentTimeMillis()
                    )
                )

                _cargando.value = false
            } catch (e: Exception) {
                _error.value = "Error al actualizar cita: ${e.message}"
                _cargando.value = false
            }
        }
    }

    /**
     * Registra la finalización de un servicio
     */
    fun finalizarServicio(citaId: String, observaciones: String) {
        viewModelScope.launch {
            try {
                _cargando.value = true
                // Podría enviar información adicional como el tiempo que tomó, etc.
                citasRepository.finalizarServicio(citaId, observaciones)
                cargarCitasDeHoy()
                _cargando.value = false
            } catch (e: Exception) {
                _error.value = "Error al finalizar servicio: ${e.message}"
                _cargando.value = false
            }
        }
    }





    }

    /**
     * Obtiene detalles de un vehículo
     */
    suspend fun obtenerDetallesVehiculo(vehiculoId: String): Vehiculo? {
        return try {
            val vehiculosRepository = null
            vehiculosRepository.obtenerVehiculoPorId(vehiculoId)
        } catch (e: Exception) {
            val _error = null
            null
        }
    }

private fun Nothing?.obtenerVehiculoPorId(string: String): Vehiculo? {
    TODO("Not yet implemented")
}

/**
     * Marca una notificación como leída
     */
    fun marcarNotificacionComoLeida(notificacionId: String) {
    val viewModelScope = null
    viewModelScope?.launch {
        val notificacionesRepository = null
        notificacionesRepository.marcarComoLeida(notificacionId)
    }
    }

private fun Nothing?.marcarComoLeida(string: String) {


}

/**
     * Marca todas las notificaciones como leídas
     */
    fun marcarTodasLasNotificacionesComoLeidas() {
    val viewModelScope = null
    viewModelScope?.launch {
        val notificacionesRepository = null
        notificacionesRepository.marcarTodasComoLeidas()
    }
    }

private fun Nothing?.marcarTodasComoLeidas() {
    TODO("Not yet implemented")
}

/**
     * Limpia el estado de error
     */
    fun limpiarError() {
    val _error = null

    }
