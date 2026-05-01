// File: org.example.tallermecanico.viewmodel.CitasRepository.kt
package org.example.tallermecanico.viewmodel

import kotlinx.coroutines.flow.Flow
import org.example.tallermecanico.data.models.Cita
import java.util.Date

interface CitasRepository {
    // Add methods that are actually used by your ViewModels
    fun getCitasByFecha(fecha: Date): Flow<List<Cita>>

    // Add other methods used by your ViewModel
    // For example:
    fun getCitasPendientesFlow(): Flow<List<Cita>>
    fun getAllCitasFlow(): Flow<List<Cita>>
    fun getCitasByUserIdFlow(userId: String): Flow<List<Cita>>
    suspend fun getCitaById(citaId: String): Cita?
    suspend fun crearCita(cita: Cita): String
    suspend fun aceptarCita(citaId: String, vehiculoId: String, servicioId: String)
    suspend fun rechazarCita(citaId: String)
    suspend fun completarCita(citaId: String)
    fun getCitasPendientes()
    fun getAllServicios()
    fun getCitasPorFecha(fecha: Date)
    fun actualizarEstadoCita(citaId: String, aceptada: Boolean, servicioId: String)
    fun registrarVehiculo(vehiculo: VehiculoRegistrado)
    fun getEstadoVehiculoById(vehiculoId: String)
    fun getVehiculoById(vehiculoId: String)
    fun actualizarEstadoVehiculo(vehiculoId: String, nuevoEstado: EstadoVehiculo)
    fun generarIdVehiculo(): String
    fun obtenerAnoDelModelo(modelo: String): String
    fun eliminarCita(citaId: String)
    fun actualizarCita(cita: Cita)
    fun agregarCita(cita: Cita)
    fun cargarTodasLasCitas()
    fun cargarCitasPendientes()
    fun cargarServicios()
    fun seleccionarCita(cita: Cita)
    fun cargarDatos()
    fun cargarEstadoVehiculo(vehiculoId: String)
    fun citaAceptada(citaId: String, vehiculoId: String, servicioId: String)
    fun enviarNotificacionCliente(clienteId: String, titulo: String, mensaje: String)
    fun getAllCitas(): List<Cita>

    // Add other methods as needed
}