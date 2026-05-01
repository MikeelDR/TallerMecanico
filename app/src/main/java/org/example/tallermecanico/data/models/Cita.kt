package org.example.tallermecanico.data.models

import com.google.firebase.firestore.PropertyName
import java.util.Date
import com.google.firebase.Timestamp

/**
 * Enum que representa los posibles estados de una cita en el taller.
 */
enum class EstadoCita {
    PENDIENTE,
    EN_PROCESO,
    COMPLETADA,
    CANCELADA,
    REPROGRAMADA,
    EXITOSA,
    RECHAZADA
}

/**
 * Modelo de datos que representa una cita en el taller mecánico.
 *
 * @property id Identificador único de la cita
 * @property clienteId Identificador del cliente que solicitó la cita
 * @property vehiculoId Identificador del vehículo relacionado con la cita
 * @property fecha Fecha y hora programada para la cita
 * @property servicio Tipo de servicio a realizar
 * @property descripcion Descripción detallada del servicio o problema
 * @property estado Estado actual de la cita
 * @property historialEstados Historial de estados por los que ha pasado la cita
 * @property mecanicoId Identificador del mecánico asignado (puede ser nulo)
 * @property costoEstimado Estimación del costo del servicio
 * @property fechaCreacion Fecha en que se creó la cita
 * @property fechaActualizacion Última fecha de modificación de la cita
 * @property needsSync Indica si la cita requiere sincronización
 */
data class Citaa(
    @DocumentId var id: String = "",
    var vehiculoId: String = "",
    var clienteId: String = "",
    var descripcion: String = "",
    var estado: String = "pendiente, en_proceso, completada, cancelada", // pendiente, en_proceso, completada, cancelada
    @PropertyName("token_cliente")
    var tokenCliente: String = "",
    val servicio: String,
    val historialEstados: List<EstadoCita> = listOf(EstadoCita.PENDIENTE),
    val mecanicoId: String? = null,
    val costoEstimado: Double = 0.0,
    val fechaCreacion: Date = Date(),
    val fechaActualizacion: Date = Date(),
    val needsSync: Boolean = false,
    val getallCitas: List<Citaa> = emptyList(),
    val getallCitasPendientes: List<Citaa> = emptyList(),
    val agregarCita: List<Citaa> = emptyList(),
    val actualizarCita: List<Citaa> = emptyList(),
    val eliminarCita: List<Citaa> = emptyList(),
    val filtrarCitasPorFecha: List<Citaa> = emptyList(),
    val actualizarEstadoCita: List<Citaa> = emptyList(),
    val aceptarCitaYAsignarServicio: List<Citaa> = emptyList(),
    val obtenerEstadoVehiculoPorId: List<Citaa> = emptyList(),
    val obtenerVehiculoPorId: List<Citaa> = emptyList(),
    val actualizarEstadoVehiculo: List<Citaa> = emptyList(),
    val generarIdVehiculo: List<Citaa> = emptyList(),
    val obtenerAnoDelModelo: List<Citaa> = emptyList(),
    val fecha: Date,
    val cliente_uid: String? = null,
    val email: String? = null,

    )

annotation class DocumentId


/**
 * Lista de todos los posibles estados (útil para UI, pruebas, etc.)
 */
val todosLosEstados = EstadoCita.values().toList()

/**
 * Citas de ejemplo que cubren todos los estados posibles.
 */
val citasConTodosLosEstados = todosLosEstados.map { estado ->
    Citaa(
        id = "id_${estado.name}",
        clienteId = "cliente1",
        vehiculoId = "vehiculo1",
        fecha = Date(),
        servicio = "Servicio $estado",
        descripcion = "Descripción del estado $estado",
        estado = estado.toString(),
        historialEstados = listOf(estado)


    )
}
