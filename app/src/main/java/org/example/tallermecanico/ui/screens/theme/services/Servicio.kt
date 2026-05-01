package org.example.tallermecanico.ui.theme.services

data class Servicio(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val disponible: Boolean = true,
    val duracion: String = "",
    val tipo: String = "",
    val marca: String = "",
    val modelo: String = "",
    val placa: String = ""
)
