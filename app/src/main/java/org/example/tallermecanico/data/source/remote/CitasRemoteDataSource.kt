package org.example.tallermecanico.data.source.remote

import org.example.tallermecanico.data.models.Cita
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fuente de datos remota para las citas del taller mecánico.
 * Maneja las operaciones relacionadas con el servidor o API externa.
 */
@Singleton
class CitasRemoteDataSource @Inject constructor(
    private val citasApiService: CitasApiService
) {
    /**
     * Obtiene todas las citas desde el servidor
     * @return Lista de citas del servidor
     */
    suspend fun getAllCitas(): List<Cita> {
        return try {
            val response = citasApiService.getAllCitas()
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Guarda una nueva cita en el servidor
     * @param cita la cita a guardar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    suspend fun saveCita(cita: Cita): Boolean {
        return try {
            val response = citasApiService.saveCita(cita)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Actualiza una cita existente en el servidor
     * @param cita la cita actualizada
     * @return true si la operación fue exitosa, false en caso contrario
     */
    suspend fun updateCita(cita: Cita): Boolean {
        return try {
            val response = citasApiService.updateCita(cita.id, cita)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Elimina una cita del servidor
     * @param citaId el ID de la cita a eliminar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    suspend fun deleteCita(citaId: String): Boolean {
        return try {
            val response = citasApiService.deleteCita(citaId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Obtiene una cita específica desde el servidor
     * @param citaId el ID de la cita
     * @return la cita encontrada o null si no existe
     */
    suspend fun getCitaById(citaId: String): Cita? {
        return try {
            val response = citasApiService.getCitaById(citaId)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Interfaz para el servicio API de citas
 */
interface CitasApiService {
    suspend fun getAllCitas(): Response<List<Cita>>
    suspend fun getCitaById(citaId: String): Response<Cita>
    suspend fun saveCita(cita: Cita): Response<Any>
    suspend fun updateCita(citaId: String, cita: Cita): Response<Any>
    suspend fun deleteCita(citaId: String): Response<Any>
}