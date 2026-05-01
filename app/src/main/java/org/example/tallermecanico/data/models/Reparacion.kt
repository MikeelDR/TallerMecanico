package org.example.tallermecanico.data.models

import com.google.firebase.Timestamp

data class Reparacion(
    val id: String = "",  // Agrega un campo 'id' para almacenar el ID del documento
    val vehiculo_id: String = "",
    val fecha_reparacion: Timestamp? = null,
    val descripcion: String = "",
    val costo: Double = 0.0,
    val estado: String = "",
    val mecanico_uid: String = ""
)

fun bind(reparacion: Reparacion) {
    // Usando el parámetro reparacion
    val id = reparacion.id
    val vehiculoId = reparacion.vehiculo_id
    val fechaReparacion = reparacion.fecha_reparacion
    val descripcion = reparacion.descripcion
    val costo = reparacion.costo
    val estado = reparacion.estado
    val mecanicoUid = reparacion.mecanico_uid

    // Aquí continúa la lógica usando estas variables...
}