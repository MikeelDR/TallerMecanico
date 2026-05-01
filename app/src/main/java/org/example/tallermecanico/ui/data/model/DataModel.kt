package org.example.tallermecanico.data.models

import java.util.Date

// Clases para gestión de usuarios
data class Usuario(
    val uid: String = "", // Puede ser generado por Firebase o tu base de datos
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val telefono: String = "",
    val rol: String = "Cliente" // Por defecto, los nuevos usuarios son clientes
)


// Clase para representar un historial de modificaciones en un vehículo
data class HistorialModificacion(
    val id: String = "",
    val vehiculoId: String = "",
    val trabajadorId: String = "",
    val nombreTrabajador: String = "",
    val estadoAnterior: EstadoVehiculo? = null,
    val estadoNuevo: EstadoVehiculo? = null,
    val descripcion: String = "",
    val fechaModificacion: Date = Date()
)

// Clases para gestión de diagnósticos y trabajos
data class Diagnostico(
    val id: String = "",
    val vehiculoId: String = "",
    val descripcion: String = "",
    val costoEstimado: Double = 0.0,
    val tiempoEstimadoDias: Int = 0,
    val repuestosNecesarios: String = "",
    val fechaCreacion: Date = Date()
)

// Enum para los estados de una orden de trabajo
enum class EstadoOrdenTrabajo(val nombre: String) {
    PENDIENTE("Pendiente"),
    EN_PROGRESO("En progreso"),
    ESPERANDO_REPUESTOS("Esperando repuestos"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada")
}

// Clase para representar un repuesto utilizado en un trabajo
data class RepuestoUtilizado(
    val repuestoId: String = "",
    val nombre: String = "",
    val cantidad: Int = 0,
    val precioUnitario: Double = 0.0
)

// Clase para representar un detalle de trabajo en una orden
data class DetalleTrabajo(
    val id: String = "",
    val descripcion: String = "",
    val horasTrabajo: Double = 0.0,
    val costoManoObra: Double = 0.0,
    val repuestosUtilizados: List<RepuestoUtilizado> = listOf(),
    val completado: Boolean = false
)

// Clase para representar una orden de trabajo
data class OrdenTrabajo(
    val id: String = "",
    val vehiculoId: String = "",
    val diagnosticoId: String = "",
    val fechaInicio: Date = Date(),
    val fechaEstimadaFinalizacion: Date? = null,
    val fechaFinalizacion: Date? = null,
    val trabajadorId: String = "",
    val nombreTrabajador: String = "",
    val detallesTrabajo: List<DetalleTrabajo> = listOf(),
    val estado: EstadoOrdenTrabajo = EstadoOrdenTrabajo.PENDIENTE,
    val costoTotal: Double = 0.0,
    val observaciones: String = ""
)

// Clases para gestión de notificaciones
enum class TipoNotificacion(val descripcion: String) {
    INFO("Información"),
    ALERTA("Alerta"),
    IMPORTANTE("Importante")
}

// Clase para representar una notificación para el cliente
data class NotificacionCliente(
    val id: String = "",
    val clienteId: String = "",
    val vehiculoId: String = "",
    val titulo: String = "",
    val mensaje: String = "",
    val tipo: TipoNotificacion = TipoNotificacion.INFO,
    val fechaCreacion: Date = Date(),
    val leida: Boolean = false
)

data class Vehicle(
    var id: String? = null,
    val ownerId: String? = null,
    val ownerName: String? = null,
    val make: String? = null,
    val model: String? = null,
    val year: String? = null,
    val licensePlate: String? = null,
    val serviceType: String? = null,
    var status: String? = null,
    var notes: String? = null,
    val createdAt: Long? = null,
    var lastUpdated: Long? = null
)


// Modelo para las Citas
data class Cita(
    var id: String = "",
    val userId: String = "",
    val nombre: String = "",
    val telefono: String = "",
    val email: String = "",
    val marca: String = "",
    val año: String = "",
    val fecha_hora: String = "",
    val modelo: String = "",
    val anio: String = "",
    val placa: String = "",
    val kilometraje: String = "",
    val descripcionProblema: String = "",
    val fechaSolicitud: String = "",
    val fechaDeseada: String = "",
    val horaDeseada: String = "",
    val estado: String = "pendiente", // pendiente, aceptada, rechazada, completada
    val vehiculoId: String = "",
    val servicioId: String = "",
    val fechaCreacion: String = System.currentTimeMillis().toString(),
    val nombreCliente: String = "",
    val marcaVehiculo: String = "",
    val modeloVehiculo: String = "",
    val anioVehiculo: String = "",
    val anoVehiculo: String? = null,
    val placaVehiculo: String = "",
    val kilometrajeVehiculo: String = "",
    val descripcionProblemaVehiculo: String = "",
    val time: String = "",
    val name: String = "",
    val citaId: String = "",
    val servicioid: String = "",
    val propietario: String = "",
    val clienteId: String = "",
    val servicio: String = "",
    val notas: String? = null,
    val diagnostico: String = "",
    val fecha: String = "",
    val hora: String = "",
    val needsSync: Boolean,
    val syncId: String? = null,
    val selectedDate: String = "",
    val idSeguimiento: String,
    val usuarioId: String,
    val cliente_uid: String? = null,

    )

// Modelo para el Estado de un Vehículo en reparación
data class EstadoVehiculo(
    val id: String = "",
    val citaId: String = "",
    val userId: String = "",
    val tecnicoId: String = "", // ID del técnico asignado
    val marca: String = "",
    val modelo: String = "",
    val anio: String = "",
    val placa: String = "",
    val servicio: String? = null, // Nombre del servicio
    val diagnostico: String? = null,
    val reparacionesRealizadas: String? = null,
    val piezasReemplazadas: String? = null,
    val porcentajeCompletado: Int? = 0,
    val estadoActual: String? = "En espera", // En espera, Diagnóstico, Reparación, Pruebas, Completado
    val comentarios: String? = null,
    val costoEstimado: Double? = null,
    val fechaCreacion: Long = 0,
    val fechaActualizacion: Long = 0,
    val fechaIngreso: String = ""
)

// Modelo para los Servicios ofrecidos
data class Servicio(
    var id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val precioBase: Double = 0.0,
    val tiempoEstimado: String = "",
    val iconoUrl: String = "",
    val precio: Double = 0.0
)

// Modelo para Vehículos Registrados
data class VehiculoRegistrado(
    val id: String = "",
    val userId: String = "",
    val marca: String = "",
    val modelo: String = "",
    val anio: String = "",
    val placa: String = "",
    val marcaVehiculo: String = "",
    val modeloVehiculo: String = "",
    val anoVehiculo: String? = null,
    val placaVehiculo: String = "",
    val kilometrajeVehiculo: String = "",
    val descripcionProblemaVehiculo: String = "",
    val marcaModelo: String = "",
    val color: String = "",
    val kilometraje: String = "",
    val vin: String = "",
    val fechaRegistro: Long = System.currentTimeMillis(),
    val fotoUrl: String = "",
    val historialServicio: List<String> = emptyList() // Lista de IDs de servicios previos
)
