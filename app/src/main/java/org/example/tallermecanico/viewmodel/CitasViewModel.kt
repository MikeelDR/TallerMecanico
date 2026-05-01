package org.example.tallermecanico.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.example.tallermecanico.data.models.Cita
import org.example.tallermecanico.data.models.Servicio
import org.example.tallermecanico.ui.data.repository.CitasRepository
import org.example.tallermecanico.ui.data.repository.VehiculosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel para manejar la lógica de negocio relacionada con las citas del taller mecánico.
 */
@HiltViewModel
class CitasViewModel @Inject constructor(
    private val repository: CitasRepository,
    private val vehiculosRepository: VehiculosRepository
) : ViewModel() {

    // Estados para la lista de citas
    private val _citasList = MutableStateFlow<List<Cita>>(emptyList())
    val citasList: StateFlow<List<Cita>> = _citasList.asStateFlow()

    // Estado para citas pendientes
    private val _citasPendientes = MutableStateFlow<List<Cita>>(emptyList())
    val citasPendientes: StateFlow<List<Cita>> = _citasPendientes.asStateFlow()

    // Estado para todas las citas (incluyendo historial)
    private val _todasLasCitas = MutableStateFlow<List<Cita>>(emptyList())
    val todasLasCitas: StateFlow<List<Cita>> = _todasLasCitas.asStateFlow()

    // Estado para la cita seleccionada actualmente
    private val _selectedCita = MutableLiveData<Cita?>(null)
    val selectedCita: LiveData<Cita?> = _selectedCita

    // Estado para manejar errores
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // Estado para manejar la carga de datos
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Estado para la lista de estados de vehículos
    private val _estadosVehiculos = MutableStateFlow<Map<String, EstadoVehiculo>>(emptyMap())
    val estadosVehiculos: StateFlow<Map<String, EstadoVehiculo>> = _estadosVehiculos.asStateFlow()

    // Estado para la lista de servicios disponibles
    private val _servicios = MutableStateFlow<List<Servicio>>(emptyList())
    val servicios: StateFlow<List<Servicio>> = _servicios.asStateFlow()

    // ID único para cada vehículo en reparación
    private val _idVehiculosEnReparacion = MutableStateFlow<List<String>>(emptyList())
    val idVehiculosEnReparacion: StateFlow<List<String>> = _idVehiculosEnReparacion.asStateFlow()

    // Estado del vehículo consultado por ID
    private val _estadoVehiculo = MutableStateFlow<EstadoVehiculo?>(null)
    val estadoVehiculo: StateFlow<EstadoVehiculo?> = _estadoVehiculo.asStateFlow()

    // Estado para vehículo registrado
    private val _vehiculoRegistrado = MutableStateFlow<VehiculoRegistrado?>(null)
    val vehiculoRegistrado: StateFlow<VehiculoRegistrado?> = _vehiculoRegistrado.asStateFlow()

    // Fecha seleccionada para filtrar citas
    private val _fechaSeleccionada = MutableStateFlow<Date?>(null)
    val fechaSeleccionada: StateFlow<Date?> = _fechaSeleccionada.asStateFlow()

    init {
        // Inicializar datos
        cargarDatos()
    }

    /**
     * Carga todos los datos iniciales desde el repositorio
     */
    private fun cargarDatos() {
        cargarTodasLasCitas()
        cargarCitasPendientes()
        cargarServicios()
    }

    /**
     * Obtiene todas las citas desde el repositorio
     */
    fun cargarTodasLasCitas() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val citas = repository.getAllCitas()
                _todasLasCitas.value = citas
                _citasList.value = citas
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al cargar todas las citas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    /**
     * Carga solo las citas pendientes
     */
    fun cargarCitasPendientes() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val citas = repository.getCitasPendientes()
                _citasPendientes.value = citas
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al cargar citas pendientes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Carga la lista de servicios disponibles
     */
    fun cargarServicios() {
        viewModelScope.launch {
            try {
                val listaServicios = repository.getAllServicios()
                _servicios.value = listaServicios
            } catch (e: Exception) {
                _error.value = "Error al cargar servicios: ${e.message}"
            }
        }
    }

    /**
     * Selecciona una cita para ver sus detalles
     */
    fun seleccionarCita(cita: Cita) {
        _selectedCita.value = cita
    }

    /**
     * Agrega una nueva cita
     */
    fun agregarCita(cita: Cita) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.agregarCita(cita)
                cargarTodasLasCitas()
                cargarCitasPendientes()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al agregar la cita: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza una cita existente
     */
    fun actualizarCita(cita: Cita) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.actualizarCita(cita)
                cargarTodasLasCitas()
                cargarCitasPendientes()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al actualizar la cita: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Elimina una cita
     */
    fun eliminarCita(citaId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.eliminarCita(citaId)
                cargarTodasLasCitas()
                cargarCitasPendientes()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al eliminar la cita: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Filtra citas por fecha
     */
    fun filtrarCitasPorFecha(fecha: Date) {
        _isLoading.value = true
        _fechaSeleccionada.value = fecha
        viewModelScope.launch {
            try {
                val citas = repository.getCitasPorFecha(fecha)
                _citasList.value = citas
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al filtrar las citas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza el estado de una cita existente
     * @param citaId Identificador único de la cita
     * @param nuevoEstado Nuevo estado que se asignará a la cita
     * @param servicioId Identificador del servicio asociado (opcional)
     */
    fun actualizarEstadoCita(citaId: String, nuevoEstado: EstadoCita, servicioId: String = "") {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.actualizarEstadoCita(citaId, nuevoEstado, servicioId)
                // Refrescar la lista de citas para mostrar el cambio
                val fechaActual = _fechaSeleccionada.value ?: Date()
                filtrarCitasPorFecha(fechaActual)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al actualizar el estado de la cita: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Aceptar cita y asignar servicio
     */
    fun aceptarCitaYAsignarServicio(citaId: String, servicioId: String) {
        viewModelScope.launch {
            try {
                // Generar ID único para el vehículo
                val vehiculoId = generarIdVehiculo()

                // Actualizar la cita como aceptada y asignar servicio
                repository.actualizarEstadoCita(citaId, EstadoCita.CONFIRMADA, servicioId)

                // Crear registro de vehículo
                val cita = repository.getCitaById(citaId)
                cita?.let {
                    val nuevoVehiculo = VehiculoRegistrado(
                        id = vehiculoId,
                        citaId = citaId,
                        servicioId = servicioId,
                        marcaVehiculo = it.marcaVehiculo ?: it.modeloVehiculo.split(" ")[0],
                        modeloVehiculo = it.modeloVehiculo,
                        anoVehiculo = it.anoVehiculo ?: obtenerAnoDelModelo(it.modeloVehiculo),
                        nombreCliente = it.nombreCliente,
                        clienteId = it.clienteId,
                        estado = EstadoVehiculo(
                            idSeguimiento = vehiculoId,
                            vehiculo = it.modeloVehiculo,
                            cliente = it.nombreCliente,
                            estado = EstadoServicio.PENDIENTE,
                            descripcion = "Vehículo recibido. Pendiente de diagnóstico.",
                            fechaIngreso = Date().toString(),
                            descripcionProblema = it.descripcionProblema ?: it.notas ?: ""
                        ),
                        fechaCreacion = System.currentTimeMillis(),
                        vehiculoId = it.vehiculoId,
                        userId = it.clienteId,
                        placa = it.placa,
                        descripcionProblema = it.descripcionProblema ?: "",
                        notas = it.notas ?: "",
                        idSeguimiento = vehiculoId,
                        marca = it.marcaVehiculo ?: ""
                    )
                    repository.registrarVehiculo(nuevoVehiculo)

                    // Actualizar lista de IDs de vehículos en reparación
                    _idVehiculosEnReparacion.update { ids -> ids + vehiculoId }
                }
                cargarCitasPendientes() // Recargar citas pendientes
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al aceptar la cita: ${e.message}"
            }
        }
    }

    /**
     * Obtiene el estado de un vehículo según su ID
     */
    fun obtenerEstadoVehiculoPorId(vehiculoId: String) {
        viewModelScope.launch {
            try {
                val estado = repository.getEstadoVehiculoById(vehiculoId)
                // Actualizar estado del vehículo
                _estadoVehiculo.value = estado

                // Actualizar también en el mapa de estados
                if (estado != null) {
                    val mapaActualizado = _estadosVehiculos.value.toMutableMap()
                    mapaActualizado[vehiculoId] = estado
                    _estadosVehiculos.value = mapaActualizado
                }
            } catch (e: Exception) {
                _error.value = "Error al obtener el estado del vehículo: ${e.message}"
            }
        }
    }

    /**
     * Función para crear una nueva cita y generar ID de seguimiento
     */
    fun crearCita(cita: Cita): String {
        // Generar un ID único alfanumérico para esta cita
        val idSeguimiento = generarIdUnico()

        // Asociar ID con la cita
        val citaConId = cita.copy(idSeguimiento = idSeguimiento)

        // Guardar la cita en la base de datos
        viewModelScope.launch {
            try {
                repository.agregarCita(citaConId)

                // Actualizar lista de citas
                cargarTodasLasCitas()
                cargarCitasPendientes()

                // Actualizar lista de IDs de vehículos en reparación
                _idVehiculosEnReparacion.update { ids -> ids + idSeguimiento }
            } catch (e: Exception) {
                _error.value = "Error al crear la cita: ${e.message}"
            }
        }

        return idSeguimiento
    }

    /**
     * Limpia el error actual
     */
    fun limpiarError() {
        _error.value = null
    }

    /**
     * Genera un ID único alfanumérico
     * @return Un ID único de 6 caracteres
     */
    private fun generarIdUnico(): String {
        // Generar ID único alfanumérico de 6 caracteres
        val caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { caracteres.random() }.joinToString("")
    }

    /**
     * Genera un ID único para un vehículo (formato personalizable)
     */
    private fun generarIdVehiculo(): String {
        val timestamp = System.currentTimeMillis() % 10000 // Últimos 4 dígitos del timestamp
        val randomPart = UUID.randomUUID().toString().substring(0, 4)
        return "VEH-$timestamp-$randomPart"
    }

    /**
     * Extrae el año del modelo del vehículo
     */
    private fun obtenerAnoDelModelo(modeloVehiculo: String): String {
        // Intenta extraer un año (4 dígitos) del modelo
        val pattern = "\\d{4}".toRegex()
        val match = pattern.find(modeloVehiculo)
        return match?.value ?: "0000" // Si no encuentra, devuelve "0000"
    }

    // Extensión para comparar solo la fecha sin la hora
    private fun Date.toDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}-${calendar.get(
            Calendar.DAY_OF_MONTH)}"
    }

    companion object {
        // Puedes agregar constantes o métodos estáticos aquí si son necesarios
    }
}

/**
 * Modelo de datos para una Cita
 */
data class Cita(
    val id: String,
    val clienteId: String,
    val nombreCliente: String,
    val vehiculoId: String,
    val modeloVehiculo: String,
    val placa: String,
    val fecha: Date,
    val hora: String,
    val servicio: String,
    val estado: EstadoCita,
    val notas: String? = null,
    val idSeguimiento: String? = null,
    val descripcionProblema: String? = null,
    val marcaVehiculo: String? = null,
    val anoVehiculo: String? = null,
    val tokenCliente: String? = null,
    val descripcion: String = ""
)

/**
 * Estados posibles para una cita
 */
enum class EstadoCita {
    PENDIENTE,
    CONFIRMADA,
    EN_PROGRESO,
    COMPLETADA,
    CANCELADA
}

/**
 * Estados posibles para un servicio de vehículo
 */
enum class EstadoServicio {
    PENDIENTE,
    EN_PROCESO,
    COMPLETADO,
    CANCELADO
}

/**
 * Clase de datos que representa el estado de un vehículo
 */
data class EstadoVehiculo(
    val id: Int = 0,
    val idSeguimiento: String = "",
    val vehiculo: String = "",
    val cliente: String = "",
    val estado: EstadoServicio = EstadoServicio.PENDIENTE,
    val descripcion: String = "",
    val fechaIngreso: String = "",
    val fechaEstimadaEntrega: String = "",
    val servicios: List<String> = emptyList(),
    val descripcionProblema: String = "",
    val costoTotal: Double = 0.0,
    val tiempoEstimado: String = "",
    // Campos adicionales
    val enProceso: Boolean = false,
    val diagnostico: String = "",
    val reparacionesRealizadas: String = "",
    val reparacionEnCurso: Boolean = false,
    val pruebasRealizadas: Boolean = false,
    val completado: Boolean = false,
    val notas: String = "",
    val piezasReemplazadas: String = "",
    val porcentajeCompletado: Int = 0,
    val comentarios: String = "",
    val fechaCreacion: Long = 0,
    val fechaActualizacion: Long = 0,
    val estadoActual: String = "",
    val citaId: String = "",
    val servicioId: String = "",
    val vehiculoId: String = "",
    val userId: String = "",
    val marca: String = "",
    val modelo: String = "",
    val anio: String = "",
    val placa: String = "",
    val clienteId: String = "",
    val servicio: String = "",
    val fecha: String = "",
    val hora: String = "",
    val descripcionProblema2: String = "",
    val notas2: String = "",
    val idSeguimiento2: String = "",
    val marcaVehiculo: String = "",
)

/**
 * Clase que representa un servicio de taller
 */
data class Servicio(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val costo: Double,
    val tiempoEstimado: String
)

/**
 * Clase que representa un vehículo registrado en el taller
 */
data class VehiculoRegistrado(
    val id: String,
    val citaId: String,
    val servicioId: String,
    val vehiculoId: String,
    val userId: String,
    val placa: String,
    val descripcionProblema: String,
    val notas: String,
    val idSeguimiento: String,
    val marca: String,
    val marcaVehiculo: String,
    val modeloVehiculo: String,
    val anoVehiculo: String,
    val nombreCliente: String,
    val clienteId: String? = null,
    val estado: EstadoVehiculo,
    val fechaCreacion: Long
)