// File: org.example.tallermecanico.ui.data.repository.VehiculosRepositoryImpl.kt
package org.example.tallermecanico.ui.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.example.tallermecanico.data.models.EstadoVehiculo
import org.example.tallermecanico.data.models.VehiculoRegistrado
import org.example.tallermecanico.viewmodel.VehiculosRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehiculosRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : VehiculosRepository {

    private val vehiculosRegistradosCollection = firestore.collection("vehiculos_registrados")
    private val vehiculosCollection = firestore.collection("vehiculos")
    private val originalRepository = VehiculosRepository() // Original implementation

    override suspend fun obtenerVehiculosPorUsuario(usuarioId: String): List<VehiculoRegistrado> =
        withContext(Dispatchers.IO) {
            // Delegate to the original implementation
            val snapshot = vehiculosRegistradosCollection.whereEqualTo("usuarioId", usuarioId).get().await()
            snapshot.toObjects(VehiculoRegistrado::class.java)
        }

    override suspend fun obtenerVehiculoPorId(vehiculoId: String): VehiculoRegistrado? =
        withContext(Dispatchers.IO) {
            val document = vehiculosRegistradosCollection.document(vehiculoId).get().await()
            document.toObject(VehiculoRegistrado::class.java)
        }

    override suspend fun guardarVehiculo(vehiculo: VehiculoRegistrado): Boolean =
        withContext(Dispatchers.IO) {
            try {
                vehiculosRegistradosCollection.document(vehiculo.id).set(vehiculo).await()
                true
            } catch (e: Exception) {
                false
            }
        }

    override suspend fun actualizarVehiculo(vehiculo: VehiculoRegistrado): Boolean =
        withContext(Dispatchers.IO) {
            try {
                vehiculosRegistradosCollection.document(vehiculo.id).set(vehiculo).await()
                true
            } catch (e: Exception) {
                false
            }
        }

    override suspend fun eliminarVehiculo(vehiculoId: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                vehiculosRegistradosCollection.document(vehiculoId).delete().await()
                true
            } catch (e: Exception) {
                false
            }
        }

    override suspend fun obtenerEstadoVehiculo(vehiculoId: String): EstadoVehiculo? =
        withContext(Dispatchers.IO) {
            val document = vehiculosCollection.document(vehiculoId).get().await()
            document.toObject(EstadoVehiculo::class.java)
        }

    override fun getEstadoVehiculoFlow(vehiculoId: String): Flow<EstadoVehiculo?> {
        // Delegate to the original repository
        return originalRepository.getEstadoVehiculoFlow(vehiculoId)
    }

    override fun getVehiculosPorUsuarioFlow(userId: String): Flow<List<EstadoVehiculo>> {
        // Delegate to the original repository
        return originalRepository.getVehiculosPorUsuarioFlow(userId)
    }

    override fun cargarVehiculosDelUsuario(userId: String): Flow<List<EstadoVehiculo>> {
        // Delegate to the original repository
        return originalRepository.getVehiculosPorUsuarioFlow(userId)
    }

    override suspend fun getEstadoVehiculo(vehiculoId: String): EstadoVehiculo {
        // Delegate to the original repository
        return originalRepository.getEstadoVehiculo(vehiculoId)
    }

     suspend fun actualizarEstadoVehiculo(
        estadoVehiculo: EstadoVehiculo,
        vehiculoId: String,
        nuevoEstado: String,
        descripcion: String,
        citaId: String?
    ) {
        // Delegate to the original repository
        originalRepository.actualizarEstadoVehiculo(estadoVehiculo.toString())
    }

    override suspend fun getVehiculoRegistradoById(string: String): VehiculoRegistrado? {
        TODO("Not yet implemented")
    }



    override fun actualizarEstadoVehiculo(
        vehiculoId: String,
        nuevoEstado: String,
        descripcion: String,
        citaId: String?,
        notificarAlCliente: Boolean
    ) {
        TODO("Not yet implemented")
    }
}



private fun VehiculosRepository.actualizarEstadoVehiculo(string: String) {
    TODO("Not yet implemented")
}

private fun VehiculosRepository.getVehiculosPorUsuarioFlow(string: String): Flow<List<EstadoVehiculo>> {
    TODO("Not yet implemented")
}

private fun VehiculosRepository.getEstadoVehiculoFlow(string: String): Flow<EstadoVehiculo?> {
    TODO("Not yet implemented")
}
