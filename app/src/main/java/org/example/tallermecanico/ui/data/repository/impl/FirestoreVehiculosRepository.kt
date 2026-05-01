package org.example.tallermecanico.ui.data.repository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import org.example.tallermecanico.data.models.Vehiculo
import org.example.tallermecanico.ui.data.repository.VehiculosRepository

class FirestoreVehiculosRepository(private val db: FirebaseFirestore) : VehiculosRepository() {

    private val _vehiculos = MutableStateFlow<List<Vehiculo>>(emptyList())
    val vehiculos: Flow<List<Vehiculo>> = _vehiculos.asStateFlow()

    private val TAG = "FirestoreVehiculosRepo"
    private val COLLECTION_NAME = "vehiculos"

    init {
        // Cargar vehículos al inicializar
        loadVehiculos()
    }

    private fun loadVehiculos() {
        db.collection(COLLECTION_NAME)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error al escuchar cambios en vehículos", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val vehiculosList = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.id
                            val marca = doc.getString("marca") ?: ""
                            val modelo = doc.getString("modelo") ?: ""
                            val anio = doc.getLong("anio")?.toInt() ?: 0
                            val placa = doc.getString("placa") ?: ""
                            val color = doc.getString("color") ?: ""
                            val usuarioId = doc.getString("usuarioId") ?: ""

                            Vehiculo(
                                id = id,
                                marca = marca,
                                modelo = modelo,
                                anio = anio.toString(),
                                placa = placa,
                                color = color,
                                usuarioId = usuarioId
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error al parsear vehículo: ${doc.id}", e)
                            null
                        }
                    }
                    _vehiculos.value = vehiculosList
                }
            }
    }

    suspend fun getVehiculoById(id: String): Vehiculo? {
        return try {
            val docSnapshot = db.collection(COLLECTION_NAME).document(id).get().await()
            if (docSnapshot.exists()) {
                val marca = docSnapshot.getString("marca") ?: ""
                val modelo = docSnapshot.getString("modelo") ?: ""
                val anio = docSnapshot.getLong("anio")?.toInt() ?: 0
                val placa = docSnapshot.getString("placa") ?: ""
                val color = docSnapshot.getString("color") ?: ""
                val usuarioId = docSnapshot.getString("usuarioId") ?: ""

                Vehiculo(
                    id = id,
                    marca = marca,
                    modelo = modelo,
                    anio = anio.toString(),
                    placa = placa,
                    color = color,
                    usuarioId = usuarioId
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener vehículo por ID: $id", e)
            null
        }
    }

    suspend fun saveVehiculo(vehiculo: Vehiculo): Boolean {
        return try {
            val vehiculoMap = hashMapOf(
                "marca" to vehiculo.marca,
                "modelo" to vehiculo.modelo,
                "anio" to vehiculo.anio,
                "placa" to vehiculo.placa,
                "color" to vehiculo.color,
                "usuarioId" to vehiculo.usuarioId
            )

            if (vehiculo.id.isBlank()) {
                // Nuevo vehículo
                db.collection(COLLECTION_NAME)
                    .add(vehiculoMap)
                    .await()
            } else {
                // Actualizar vehículo existente
                db.collection(COLLECTION_NAME)
                    .document(vehiculo.id)
                    .set(vehiculoMap)
                    .await()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar vehículo", e)
            false
        }
    }

    suspend fun deleteVehiculo(id: String): Boolean {
        return try {
            db.collection(COLLECTION_NAME)
                .document(id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar vehículo: $id", e)
            false
        }
    }

    suspend fun getVehiculosByUsuarioId(usuarioId: String): List<Vehiculo> {
        return try {
            val snapshot = db.collection(COLLECTION_NAME)
                .whereEqualTo("usuarioId", usuarioId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    val id = doc.id
                    val marca = doc.getString("marca") ?: ""
                    val modelo = doc.getString("modelo") ?: ""
                    val anio = doc.getLong("anio")?.toInt() ?: 0
                    val placa = doc.getString("placa") ?: ""
                    val color = doc.getString("color") ?: ""

                    Vehiculo(
                        id = id,
                        marca = marca,
                        modelo = modelo,
                        anio = anio.toString(),
                        placa = placa,
                        color = color,
                        usuarioId = usuarioId
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error al parsear vehículo: ${doc.id}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener vehículos por usuario: $usuarioId", e)
            emptyList()
        }
    }
}