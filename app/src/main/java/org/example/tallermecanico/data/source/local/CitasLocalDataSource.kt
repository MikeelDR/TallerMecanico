package org.example.tallermecanico.data.source.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.example.tallermecanico.data.models.Cita
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fuente de datos local para las citas del taller mecánico.
 * Maneja las operaciones relacionadas con la persistencia local de citas.
 */
@Singleton
class CitasLocalDataSource @Inject constructor(
    private val citasDao: CitasDao
) {
    /**
     * Obtiene todas las citas almacenadas localmente
     * @return Flow con la lista de citas
     */
    fun getCitas(): Flow<List<Cita>> {
        return citasDao.getCitas()
    }

    /**
     * Obtiene una cita específica por su ID
     * @param citaId el ID de la cita
     * @return Flow con la cita encontrada o null si no existe
     */
    fun getCitaById(citaId: String): Flow<Cita?> {
        return citasDao.getCitaById(citaId)
    }

    /**
     * Obtiene las citas programadas para una fecha específica
     * @param fecha la fecha para filtrar las citas
     * @return Flow con la lista de citas para esa fecha
     */
    fun getCitasByFecha(fecha: Date): Flow<List<Cita>> {
        return citasDao.getCitasByFecha(fecha)
    }

    /**
     * Guarda una nueva cita localmente
     * @param cita la cita a guardar
     * @param needsSync indica si la cita necesita sincronizarse con el servidor
     */
    suspend fun saveCita(cita: Cita, needsSync: Boolean = false) {
        val citaEntidad = cita.copy(needsSync = needsSync)
        citasDao.insertCita(citaEntidad)
    }

    /**
     * Actualiza una cita existente
     * @param cita la cita actualizada
     * @param needsSync indica si la cita necesita sincronizarse con el servidor
     */
    suspend fun updateCita(cita: Cita, needsSync: Boolean = false) {
        val citaEntidad = cita.copy(needsSync = needsSync)
        citasDao.updateCita(citaEntidad)
    }

    /**
     * Elimina una cita localmente
     * @param citaId el ID de la cita a eliminar
     */
    suspend fun deleteCita(citaId: String) {
        citasDao.deleteCita(citaId)
    }

    /**
     * Marca una cita para eliminación futura (sincronización)
     * @param citaId el ID de la cita a marcar para eliminación
     */
    suspend fun markForDeletion(citaId: String) {
        citasDao.insertPendingDeletion(citaId)
    }

    /**
     * Obtiene las citas que necesitan sincronizarse con el servidor
     * @return Lista de citas pendientes de sincronización
     */
    suspend fun getPendingSyncCitas(): List<Cita> {
        return citasDao.getPendingSyncCitas()
    }

    /**
     * Obtiene los IDs de citas pendientes de eliminación en el servidor
     * @return Lista de IDs de citas pendientes de eliminación
     */
    suspend fun getPendingDeletions(): List<String> {
        return citasDao.getPendingDeletions()
    }

    /**
     * Marca una cita como sincronizada con el servidor
     * @param citaId el ID de la cita sincronizada
     */
    suspend fun markAsSynced(citaId: String) {
        citasDao.markAsSynced(citaId)
    }

    /**
     * Elimina un ID de la lista de pendientes de eliminación
     * @param citaId el ID de la cita a remover
     */
    suspend fun removePendingDeletion(citaId: String) {
        citasDao.removePendingDeletion(citaId)
    }

    /**
     * Actualiza la base de datos local con datos del servidor
     * @param citas lista de citas del servidor
     */
    suspend fun updateFromRemote(citas: List<Cita>) {
        citasDao.updateFromRemote(citas)
    }
}

/**
 * Interfaz para acceso a datos de citas en la base de datos local
 */
interface CitasDao {
    fun getCitas(): Flow<List<Cita>>
    fun getCitaById(citaId: String): Flow<Cita?>
    fun getCitasByFecha(fecha: Date): Flow<List<Cita>>
    suspend fun insertCita(cita: Cita)
    suspend fun updateCita(cita: Cita)
    suspend fun deleteCita(citaId: String)
    suspend fun insertPendingDeletion(citaId: String)
    suspend fun getPendingSyncCitas(): List<Cita>
    suspend fun getPendingDeletions(): List<String>
    suspend fun markAsSynced(citaId: String)
    suspend fun removePendingDeletion(citaId: String)
    suspend fun updateFromRemote(citas: List<Cita>)
}