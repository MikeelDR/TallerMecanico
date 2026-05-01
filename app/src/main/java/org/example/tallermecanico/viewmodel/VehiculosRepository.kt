// File: org.example.tallermecanico.viewmodel.VehiculosRepository.kt
package org.example.tallermecanico.viewmodel

import kotlinx.coroutines.flow.Flow
import org.example.tallermecanico.data.models.EstadoVehiculo
import org.example.tallermecanico.data.models.VehiculoRegistrado

interface VehiculosRepository {
    // Add the methods that your ViewModel actually use
    suspend fun obtenerVehiculosPorUsuario(usuarioId: String): List<VehiculoRegistrado>
    suspend fun obtenerVehiculoPorId(vehiculoId: String): VehiculoRegistrado?
    suspend fun guardarVehiculo(vehiculo: VehiculoRegistrado): Boolean
    suspend fun actualizarVehiculo(vehiculo: VehiculoRegistrado): Boolean
    suspend fun eliminarVehiculo(vehiculoId: String): Boolean
    suspend fun obtenerEstadoVehiculo(vehiculoId: String): EstadoVehiculo?
    fun getEstadoVehiculoFlow(vehiculoId: String): Flow<EstadoVehiculo?>
    fun getVehiculosPorUsuarioFlow(userId: String): Flow<List<EstadoVehiculo>>
    fun cargarVehiculosDelUsuario(userId: String): Flow<List<EstadoVehiculo>>
    suspend fun getEstadoVehiculo(vehiculoId: String): EstadoVehiculo

    suspend fun getVehiculoRegistradoById(id: String): VehiculoRegistrado? {
        // lógica de búsqueda en Firebase, Room, etc.
        val vehiculoEncontrado = null
        return vehiculoEncontrado
    }
    fun actualizarEstadoVehiculo(
        vehiculoId: String,
        nuevoEstado: String,
        descripcion: String,
        citaId: String?,
        notificarAlCliente: Boolean

    )



}