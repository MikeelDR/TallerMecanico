package org.example.tallermecanico.ui.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio que maneja las operaciones relacionadas con las notificaciones.
 */
@Singleton
class NotificacionesRepository @Inject constructor() {

    // Estado interno de las notificaciones
    private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())

    data class Notificacion(
        val id: String,
        val mensaje: String,
        val leida: Boolean = false,
        val citaId: String? = null,
        val vehiculoId: String? = null,
        val tipo: String? = null,
        val titulo: String,
        val fechaCreacion: Long
    )

    // Estado público expuesto como Flow
    val notificaciones: Flow<List<Notificacion>> = _notificaciones.asStateFlow()

    /**
     * Obtiene todas las notificaciones.
     */
    suspend fun obtenerNotificaciones(): List<Notificacion> {
        // Aquí podrías implementar la lógica para obtener notificaciones de una API o base de datos
        return _notificaciones.value
    }

    /**
     * Obtiene las notificaciones sin leer.
     */
    suspend fun obtenerNotificacionesSinLeer(): List<Notificacion> {
        return _notificaciones.value.filter { !it.leida }
    }

    /**
     * Agrega una nueva notificación.
     */
    suspend fun agregarNotificacion(notificacion: Notificacion) {
        _notificaciones.update { lista ->
            lista + notificacion
        }
    }

    /**
     * Marca una notificación como leída.
     */
    suspend fun marcarComoLeida(notificacionId: String) {
        _notificaciones.update { lista ->
            lista.map {
                if (it.id == notificacionId) it.copy(leida = true) else it
            }
        }
    }

    /**
     * Marca todas las notificaciones como leídas.
     */
    suspend fun marcarTodasComoLeidas() {
        _notificaciones.update { lista ->
            lista.map { it.copy(leida = true) }
        }
    }

    /**
     * Elimina una notificación.
     */
    suspend fun eliminarNotificacion(notificacionId: String) {
        _notificaciones.update { lista ->
            lista.filter { it.id != notificacionId }
        }
    }

    /**
     * Elimina todas las notificaciones.
     */
    suspend fun eliminarTodasLasNotificaciones() {
        _notificaciones.update { emptyList() }
    }
}