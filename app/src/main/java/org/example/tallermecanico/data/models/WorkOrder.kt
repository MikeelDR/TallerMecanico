package org.example.tallermecanico.data.models

import java.io.Serializable
import java.util.Date

/**
 * Modelo de datos para representar una orden de trabajo en el taller mecánico.
 */
data class WorkOrder(
    val id: String = "", // ID único de la orden de trabajo
    val vehicleId: String = "", // ID del vehículo asociado
    val clientId: String = "", // ID del cliente
    val description: String = "", // Descripción del trabajo a realizar
    val services: List<String> = listOf(), // Lista de servicios a realizar
    val totalCost: Double = 0.0, // Costo total del servicio
    val startDate: Date? = null, // Fecha de inicio del servicio
    val estimatedEndDate: Date? = null, // Fecha estimada de finalización
    val actualEndDate: Date? = null, // Fecha real de finalización
    val status: String = "Pendiente", // Estado de la orden (Pendiente, En Proceso, Completado, etc.)
    val mechanicId: String = "", // ID del mecánico asignado
    val notes: String = "", // Notas adicionales
    val parts: List<String> = listOf(), // Partes utilizadas
    val completed: Boolean = false, // Indica si el trabajo está completado
    val isPaid: Boolean = false // Indica si el servicio ha sido pagado
) : Serializable