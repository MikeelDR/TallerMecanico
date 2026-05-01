package org.example.tallermecanico.data.repository

import org.example.tallermecanico.data.models.Cita
import org.example.tallermecanico.data.source.local.CitasLocalDataSource
import org.example.tallermecanico.data.source.remote.CitasRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio que maneja las operaciones relacionadas con las citas.
 * Implementa el patrón Repository para abstraer las fuentes de datos.
 */
@Singleton
class CitasRepository @Inject constructor(
    private val localDataSource: CitasLocalDataSource,
    private val remoteDataSource: CitasRemoteDataSource,
) {

    /**
     * Obtiene todas las citas del usuario
     * @return Flow con la lista de citas
     */
    fun getCitas(): Flow<List<Cita>> {
        return localDataSource.getCitas()
    }

    /**
     * Obtiene una cita específica por su ID
     * @param citaId el ID de la cita
     * @return Flow con la cita encontrada o null si no existe
     */
    fun getCitaById(citaId: String): Flow<Cita?> {
        return localDataSource.getCitaById(citaId)
    }

    /**
     * Obtiene las citas programadas para una fecha específica
     * @param fecha la fecha para filtrar las citas
     * @return Flow con la lista de citas para esa fecha
     */
    fun getCitasByFecha(fecha: Date): Flow<List<Cita>> {
        return localDataSource.getCitasByFecha(fecha)
    }

    /**
     * Guarda una nueva cita
     * @param cita la cita a guardar
     * @return Flow que indica si la operación fue exitosa
     */
    fun saveCita(cita: Cita): Flow<Boolean> = flow {
        try {
            // Primero intentamos guardar en el servidor
            val success = remoteDataSource.saveCita(cita)
            if (success) {
                // Si se guardó en el servidor, lo guardamos localmente
                localDataSource.saveCita(cita)
                emit(true)
            } else {
                emit(false)
            }
        } catch (e: Exception) {
            // En caso de error con el servidor, guardamos solo localmente
            // y marcamos para sincronización posterior
            localDataSource.saveCita(cita, needsSync = true)
            emit(true)
        }
    }

    /**
     * Actualiza una cita existente
     * @param cita la cita actualizada
     * @return Flow que indica si la operación fue exitosa
     */
    fun updateCita(cita: Cita): Flow<Boolean> = flow {
        try {
            val success = remoteDataSource.updateCita(cita)
            if (success) {
                localDataSource.updateCita(cita)
                emit(true)
            } else {
                emit(false)
            }
        } catch (e: Exception) {
            localDataSource.updateCita(cita, needsSync = true)
            emit(true)
        }
    }

    /**
     * Elimina una cita
     * @param citaId el ID de la cita a eliminar
     * @return Flow que indica si la operación fue exitosa
     */
    fun deleteCita(citaId: String): Flow<Boolean> = flow {
        try {
            val success = remoteDataSource.deleteCita(citaId)
            if (success) {
                localDataSource.deleteCita(citaId)
                emit(true)
            } else {
                emit(false)
            }
        } catch (e: Exception) {
            localDataSource.markForDeletion(citaId)
            emit(true)
        }
    }

    /**
     * Sincroniza los datos locales con el servidor
     * @return Flow que indica si la sincronización fue exitosa
     */
    fun syncCitas(): Flow<Boolean> = flow {
        try {
            val pendingCitas = localDataSource.getPendingSyncCitas()
            val pendingDeletions = localDataSource.getPendingDeletions()

            // Actualizar citas pendientes
            for (cita in pendingCitas) {
                remoteDataSource.updateCita(cita)
                localDataSource.markAsSynced(cita.id)
            }

            // Eliminar citas pendientes de eliminación
            for (citaId in pendingDeletions) {
                remoteDataSource.deleteCita(citaId)
                localDataSource.removePendingDeletion(citaId)
            }

            // Obtener nuevas citas del servidor
            val remoteCitas = remoteDataSource.getAllCitas()
            localDataSource.updateFromRemote(remoteCitas)

            emit(true)
        } catch (e: Exception) {
            emit(false)
        }
    }
}