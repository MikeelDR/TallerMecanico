package org.example.tallermecanico.ui.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.example.tallermecanico.data.models.Cita
import org.example.tallermecanico.data.models.EstadoVehiculo
import org.example.tallermecanico.data.models.Vehicle
import org.example.tallermecanico.data.models.Vehiculo
import org.example.tallermecanico.data.models.VehiculoRegistrado

/**
 * Repositorio que maneja todas las operaciones relacionadas con los vehículos,
 * utilizando Firestore como fuente de datos.
 */

open class VehiculosRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        private const val COLLECTION_VEHICULOS = "vehiculos_registrados"
        private const val COLLECTION_VEHICLES = "vehicles"
        private const val COLLECTION_ESTADOS = "estados_vehiculo"
        private const val COLLECTION_CITAS = "citas"
        private const val COLLECTION_VEHICULOS_USUARIO = "vehiculos" // Colección para los vehículos de usuario
        private const val COLLECTION_VEHICULOS_REGISTRADOS = "vehiculos_registrados" // Colección para los vehículos registrados
        private const val COLLECTION_VEHICULOS_EN_REPARACION = "vehiculos_en_reparacion" // Colección para los vehículos en reparación
        private const val COLLECTION_VEHICULOS_DISPONIBLES = "vehiculos_disponibles" // Colección para los vehículos disponibles
        private const val COLLECTION_VEHICULOS_RESERVADOS = "vehiculos_reservados" // Colección para los vehículos reservados
        private const val COLLECTION_VEHICULOS_CANCELADOS = "vehiculos_cancelados" // Colección para los vehículos cancelados
        private const val COLLECTION_VEHICULOS_FINALIZADOS = "vehiculos_finalizados" // Colección para los vehículos finalizados
        private const val COLLECTION_VEHICULOS_PENDIENTES = "vehiculos_pendientes" // Colección para los vehículos pendientes
        private const val COLLECTION_VEHICULOS_REPARADOS = "vehiculos_reparados" // Colección para los vehículos reparados
        private const val COLLECTION_VEHICULOS_SIN_REPARAR = "vehiculos_sin_reparar" // Colección para los vehículos sin reparar
        private const val COLLECTION_VEHICULOS_EN_MANTENIMIENTO = "vehiculos_en_mantenimiento" // Colección para los vehículos en mantenimiento

    }

    // MÉTODOS PARA VEHÍCULOS DE USUARIO (Para compatibilidad con VehiculosViewModel)

    /**
     * Obtiene todos los vehículos de un usuario específico
     */
    suspend fun obtenerVehiculosPorUsuario(usuarioId: String): List<Vehiculo> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection(COLLECTION_VEHICULOS_USUARIO)
                    .whereEqualTo("usuarioId", usuarioId)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Vehiculo::class.java)?.apply {
                        // Asegurar que el ID se asigna correctamente
                        if (this.id.isBlank()) {
                            this.id = doc.id
                        }
                    }
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }



    /**
     * Obtiene un vehículo por su ID
     */
    suspend fun obtenerVehiculoPorId(vehiculoId: String): Vehiculo? {
        return withContext(Dispatchers.IO) {
            try {
                val doc = firestore.collection(COLLECTION_VEHICULOS_USUARIO)
                    .document(vehiculoId)
                    .get()
                    .await()

                if (doc.exists()) {
                    doc.toObject(Vehiculo::class.java)?.apply {
                        // Asegurar que el ID se asigna correctamente
                        if (this.id.isBlank()) {
                            this.id = doc.id
                        }
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Guarda un nuevo vehículo
     */
    suspend fun guardarVehiculo(vehiculo: Vehiculo): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val newVehiculo = if (vehiculo.id.isBlank()) {
                    val docRef = firestore.collection(COLLECTION_VEHICULOS_USUARIO).document()
                    vehiculo.apply { id = docRef.id }
                } else {
                    vehiculo
                }

                firestore.collection(COLLECTION_VEHICULOS_USUARIO)
                    .document(newVehiculo.id)
                    .set(newVehiculo)
                    .await()

                true
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Actualiza un vehículo existente
     */
    suspend fun actualizarVehiculo(vehiculo: Vehiculo): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (vehiculo.id.isBlank()) {
                    return@withContext false
                }

                firestore.collection(COLLECTION_VEHICULOS_USUARIO)
                    .document(vehiculo.id)
                    .set(vehiculo)
                    .await()

                true
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Elimina un vehículo
     */
    suspend fun eliminarVehiculo(vehiculoId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                firestore.collection(COLLECTION_VEHICULOS_USUARIO)
                    .document(vehiculoId)
                    .delete()
                    .await()

                true
            } catch (e: Exception) {
                false
            }
        }
    }

    // MÉTODOS PARA VEHÍCULOS REGISTRADOS

    /**
     * Obtiene todos los vehículos registrados de un usuario específico
     */
    fun getVehiculosRegistradosByUserId(userId: String): Flow<List<VehiculoRegistrado>> = flow {
        val snapshot = firestore.collection(COLLECTION_VEHICULOS)
            .whereEqualTo("userId", userId)
            .get()
            .await()

        val vehiculos = snapshot.documents.mapNotNull { doc ->
            doc.toObject(VehiculoRegistrado::class.java)?.copy(id = doc.id)
        }
        emit(vehiculos)
    }

    /**
     * Obtiene un vehículo registrado por su ID
     */
    suspend fun getVehiculoRegistradoById(vehiculoId: String): VehiculoRegistrado? {
        return withContext(Dispatchers.IO) {
            val doc = firestore.collection(COLLECTION_VEHICULOS)
                .document(vehiculoId)
                .get()
                .await()

            if (doc.exists()) {
                doc.toObject(VehiculoRegistrado::class.java)?.copy(id = doc.id)
            } else {
                null
            }
        }
    }

    /**
     * Guarda un nuevo vehículo registrado
     */
    suspend fun saveVehiculoRegistrado(vehiculo: VehiculoRegistrado): String {
        return withContext(Dispatchers.IO) {
            val newVehiculo = if (vehiculo.id.isBlank()) {
                val docRef = firestore.collection(COLLECTION_VEHICULOS).document()
                val id = docRef.id
                vehiculo.copy(id = id)
            } else {
                vehiculo
            }

            firestore.collection(COLLECTION_VEHICULOS)
                .document(newVehiculo.id)
                .set(newVehiculo)
                .await()

            newVehiculo.id
        }
    }

    /**
     * Actualiza un vehículo registrado existente
     */
    suspend fun updateVehiculoRegistrado(vehiculo: VehiculoRegistrado) {
        withContext(Dispatchers.IO) {
            firestore.collection(COLLECTION_VEHICULOS)
                .document(vehiculo.id)
                .set(vehiculo)
                .await()
        }
    }

    /**
     * Elimina un vehículo registrado
     */
    suspend fun deleteVehiculoRegistrado(vehiculoId: String) {
        withContext(Dispatchers.IO) {
            firestore.collection(COLLECTION_VEHICULOS)
                .document(vehiculoId)
                .delete()
                .await()
        }
    }

    // MÉTODOS PARA VEHICLES (VEHÍCULOS EN REPARACIÓN)

    /**
     * Obtiene todos los vehículos en reparación de un usuario
     */
    fun getVehiclesByOwnerId(ownerId: String): Flow<List<Vehicle>> = flow {
        val snapshot = firestore.collection(COLLECTION_VEHICLES)
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()

        val vehicles = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Vehicle::class.java)?.apply { id = doc.id }
        }
        emit(vehicles)
    }

    /**
     * Obtiene un vehículo por su ID
     */
    suspend fun getVehicleById(vehicleId: String): Vehicle? {
        return withContext(Dispatchers.IO) {
            val doc = firestore.collection(COLLECTION_VEHICLES)
                .document(vehicleId)
                .get()
                .await()

            if (doc.exists()) {
                doc.toObject(Vehicle::class.java)?.apply { id = doc.id }
            } else {
                null
            }
        }
    }

    /**
     * Guarda un nuevo vehículo en reparación
     */
    suspend fun saveVehicle(vehicle: Vehicle): String {
        return withContext(Dispatchers.IO) {
            val timestamp = System.currentTimeMillis()

            val newVehicle = if (vehicle.id == null) {
                val docRef = firestore.collection(COLLECTION_VEHICLES).document()
                vehicle.apply {
                    id = docRef.id
                    lastUpdated = timestamp
                }
            } else {
                vehicle.apply { lastUpdated = timestamp }
            }

            firestore.collection(COLLECTION_VEHICLES)
                .document(newVehicle.id!!)
                .set(newVehicle)
                .await()

            newVehicle.id!!
        }
    }

    /**
     * Actualiza el estado de un vehículo
     */
    suspend fun updateVehicleStatus(vehicleId: String, status: String, notes: String? = null) {
        withContext(Dispatchers.IO) {
            val updateData = mutableMapOf<String, Any>(
                "status" to status,
                "lastUpdated" to System.currentTimeMillis()
            )

            notes?.let { updateData["notes"] = it }

            firestore.collection(COLLECTION_VEHICLES)
                .document(vehicleId)
                .update(updateData)
                .await()
        }
    }

    /**
     * Elimina un vehículo en reparación
     */
    suspend fun deleteVehicle(vehicleId: String) {
        withContext(Dispatchers.IO) {
            firestore.collection(COLLECTION_VEHICLES)
                .document(vehicleId)
                .delete()
                .await()
        }
    }

    // MÉTODOS PARA ESTADOS DE VEHÍCULOS

    /**
     * Obtiene el historial de estados de un vehículo
     */
    fun getEstadosVehiculoByPlaca(placa: String): Flow<List<EstadoVehiculo>> = flow {
        val snapshot = firestore.collection(COLLECTION_ESTADOS)
            .whereEqualTo("placa", placa)
            .orderBy("fechaActualizacion", Query.Direction.DESCENDING)
            .get()
            .await()

        val estados = snapshot.documents.mapNotNull { doc ->
            doc.toObject(EstadoVehiculo::class.java)?.copy(id = doc.id)
        }
        emit(estados)
    }

    /**
     * Obtiene el estado actual de un vehículo por ID de cita
     */
    suspend fun getEstadoVehiculoByCitaId(citaId: String): EstadoVehiculo? {
        return withContext(Dispatchers.IO) {
            val snapshot = firestore.collection(COLLECTION_ESTADOS)
                .whereEqualTo("citaId", citaId)
                .limit(1)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val doc = snapshot.documents[0]
                doc.toObject(EstadoVehiculo::class.java)?.copy(id = doc.id)
            } else {
                null
            }
        }
    }

    /**
     * Obtiene el estado de un vehículo por su ID
     */
    suspend fun getEstadoVehiculo(vehiculoId: String): EstadoVehiculo {
        return withContext(Dispatchers.IO) {
            try {
                val doc = firestore.collection(COLLECTION_ESTADOS)
                    .document(vehiculoId)
                    .get()
                    .await()

                val estadoDirecto = doc.toObject(EstadoVehiculo::class.java)?.copy(id = doc.id)
                if (estadoDirecto != null) {
                    return@withContext estadoDirecto
                }

                val snapshot = firestore.collection(COLLECTION_ESTADOS)
                    .whereEqualTo("id", vehiculoId)
                    .limit(1)
                    .get()
                    .await()

                val estadoIndirecto = snapshot.documents.firstOrNull()?.toObject(EstadoVehiculo::class.java)?.copy(
                    id = snapshot.documents.first().id
                )
                if (estadoIndirecto != null) {
                    return@withContext estadoIndirecto
                }

                // No encontrado, devolver objeto vacío
                EstadoVehiculo(
                    id = vehiculoId,
                    marca = "",
                    modelo = ""
                )

            } catch (e: Exception) {
                EstadoVehiculo(
                    id = vehiculoId,
                    marca = "",
                    modelo = ""
                )
            }
        }
    }

    /**
     * Guarda un nuevo estado de vehículo
     */
    suspend fun saveEstadoVehiculo(estado: EstadoVehiculo): String {
        return withContext(Dispatchers.IO) {
            val timestamp = System.currentTimeMillis()
            val newEstado = if (estado.id.isBlank()) {
                val docRef = firestore.collection(COLLECTION_ESTADOS).document()
                estado.copy(
                    id = docRef.id,
                    fechaCreacion = timestamp,
                    fechaActualizacion = timestamp
                )
            } else {
                estado.copy(fechaActualizacion = timestamp)
            }

            firestore.collection(COLLECTION_ESTADOS)
                .document(newEstado.id)
                .set(newEstado)
                .await()

            newEstado.id
        }
    }

    /**
     * Actualiza el estado de un vehículo
     */
    suspend fun updateEstadoVehiculo(estado: EstadoVehiculo) {
        withContext(Dispatchers.IO) {
            val updatedEstado = estado.copy(fechaActualizacion = System.currentTimeMillis())

            firestore.collection(COLLECTION_ESTADOS)
                .document(estado.id)
                .set(updatedEstado)
                .await()
        }
    }

    /**
     * Actualiza el estado de un vehículo con parámetros adicionales
     */
    suspend fun actualizarEstadoVehiculo(
        idVehiculo: String,
        nuevoEstado: String,
        vehiculoId: String,
        descripcion: String,
        citaId: String?
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                firestore.collection("vehiculos")
                    .document(idVehiculo)
                    .update("estado", nuevoEstado)
                    .await()
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    // MÉTODOS PARA CITAS DE VEHÍCULOS

    /**
     * Obtiene todas las citas relacionadas con un vehículo
     */
    fun getCitasByVehiculoId(vehiculoId: String): Flow<List<Cita>> = flow {
        val snapshot = firestore.collection(COLLECTION_CITAS)
            .whereEqualTo("vehiculoId", vehiculoId)
            .orderBy("fechaDeseada", Query.Direction.DESCENDING)
            .get()
            .await()

        val citas = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Cita::class.java)?.copy(id = doc.id)
        }
        emit(citas)
    }

    /**
     * Obtiene una cita por su ID
     */
    suspend fun getCitaById(citaId: String): Cita? {
        return withContext(Dispatchers.IO) {
            val doc = firestore.collection(COLLECTION_CITAS)
                .document(citaId)
                .get()
                .await()

            if (doc.exists()) {
                doc.toObject(Cita::class.java)?.copy(id = doc.id)
            } else {
                null
            }
        }
    }

    /**
     * Obtiene las citas de un usuario específico
     */
    fun getCitasByUserId(userId: String): Flow<List<Cita>> = flow {
        val snapshot = firestore.collection(COLLECTION_CITAS)
            .whereEqualTo("userId", userId)
            .orderBy("fechaDeseada", Query.Direction.DESCENDING)
            .get()
            .await()

        val citas = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Cita::class.java)?.copy(id = doc.id)
        }
        emit(citas)
    }

    fun getVehiculosPorUsuarioFlow(string: String): Flow<List<EstadoVehiculo>> {
        TODO("Not yet implemented")
    }

    fun actualizarEstadoVehiculo(string: String) {
        TODO("Not yet implemented")
    }

    fun getEstadoVehiculoFlow(string: String): Flow<EstadoVehiculo?> {
        TODO("Not yet implemented")
    }
}