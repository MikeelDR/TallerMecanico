package org.example.tallermecanico.data.models

data class Vehiculo(
    var id: String = "",
    val propietarioId: String = "",
    val marca: String = "",
    val modelo: String = "",
    val marcaVehiculo: String = "",
    val modeloVehiculo: String = "",
    val anoVehiculo: String = "",
    val anio: String = "",
    val fechaIngreso: String = "", // Formato de fecha como string, ej: "2023-05-12"
    val fecha_ingreso: String = "",
    val historial: List<HistorialReparacion> = listOf(),
    val kilometraje: Double = 0.0,
    val placa: String = "",
    val userId: String = "",
    val ultimaRevision: String = "", // Formato de fecha como string, ej: "2023-05-12"
    val usuarioId: String = "",
    val color: String = "",
    val tipo: String = "",
    val estado: String = "Activo",
    val fechaRegistro: Long = System.currentTimeMillis(),
    val ultimaActualizacion: Long = System.currentTimeMillis(),
    val notas: String = ""
) {
    // Constructor vacío requerido para Firebase
    constructor() : this("", "", "", "", 0.toString(), 0.0.toString(), "", )
}