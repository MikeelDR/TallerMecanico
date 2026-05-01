package org.example.tallermecanico.ui.data.repository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import org.example.tallermecanico.data.models.Cita
import org.example.tallermecanico.ui.data.repository.CitasRepository
import java.util.Date

class FirestoreCitasRepository(private val db: FirebaseFirestore) : CitasRepository() {

    private val _citas = MutableStateFlow<List<Cita>>(emptyList())
    val citas: Flow<List<Cita>> = _citas.asStateFlow()

    private val TAG = "FirestoreCitasRepo"
    private val COLLECTION_NAME = "citas"

    init {
        // Cargar citas al inicializar
        loadCitas()
    }

    private fun loadCitas() {
        db.collection(COLLECTION_NAME)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error al escuchar cambios en citas", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val citasList = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.id
                            val vehiculoId = doc.getString("vehiculoId") ?: ""
                            val fecha = doc.getTimestamp("fecha")?.toDate() ?: Date()
                            val servicio = doc.getString("servicio") ?: ""
                            val estado = doc.getString("estado") ?: "pendiente"
                            val notas = doc.getString("notas") ?: ""
                            val usuarioId = doc.getString("usuarioId") ?: ""

                            Cita(
                                id = id,
                                vehiculoId = vehiculoId,
                                fecha = fecha.toString(),
                                servicio = servicio,
                                estado = estado,
                                notas = notas,
                                usuarioId = usuarioId,
                                userId = "", // Valores predeterminados para todos los campos faltantes
                                nombre = "",
                                telefono = "",
                                email = "",
                                marca = "",
                                modelo = "",
                                anio = "",
                                placa = "",
                                kilometraje = "",
                                descripcionProblema = "",
                                fechaSolicitud = "",
                                fechaDeseada = "",
                                horaDeseada = "",
                                servicioId = "",
                                fechaCreacion = "",
                                nombreCliente = "",
                                marcaVehiculo = "",
                                modeloVehiculo = "",
                                anioVehiculo = "",
                                anoVehiculo = "",
                                placaVehiculo = "",
                                kilometrajeVehiculo = "",
                                descripcionProblemaVehiculo = "",
                                time = "",
                                name = "",
                                citaId = "",
                                servicioid = "",
                                propietario = "",
                                clienteId = "",
                                diagnostico = "",
                                hora = "",
                                needsSync = false,
                                syncId = "",
                                selectedDate = "",
                                idSeguimiento = ""
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error al parsear cita: ${doc.id}", e)
                            null
                        }
                    }
                    _citas.value = citasList
                }
            }
    }

    override suspend fun getCitaById(id: String): Cita? {
        return try {
            val docSnapshot = db.collection(COLLECTION_NAME).document(id).get().await()
            if (docSnapshot.exists()) {
                val vehiculoId = docSnapshot.getString("vehiculoId") ?: ""
                val fecha = docSnapshot.getTimestamp("fecha")?.toDate() ?: Date()
                val servicio = docSnapshot.getString("servicio") ?: ""
                val estado = docSnapshot.getString("estado") ?: "pendiente"
                val notas = docSnapshot.getString("notas") ?: ""
                val usuarioId = docSnapshot.getString("usuarioId") ?: ""

                Cita(
                    id = id,
                    vehiculoId = vehiculoId,
                    fecha = fecha.toString(),
                    servicio = servicio,
                    estado = estado,
                    notas = notas,
                    usuarioId = usuarioId,
                    userId = "", // Valores predeterminados para todos los campos faltantes
                    nombre = "",
                    telefono = "",
                    email = "",
                    marca = "",
                    modelo = "",
                    anio = "",
                    placa = "",
                    kilometraje = "",
                    descripcionProblema = "",
                    fechaSolicitud = "",
                    fechaDeseada = "",
                    horaDeseada = "",
                    servicioId = "",
                    fechaCreacion = "",
                    nombreCliente = "",
                    marcaVehiculo = "",
                    modeloVehiculo = "",
                    anioVehiculo = "",
                    anoVehiculo = "",
                    placaVehiculo = "",
                    kilometrajeVehiculo = "",
                    descripcionProblemaVehiculo = "",
                    time = "",
                    name = "",
                    citaId = "",
                    servicioid = "",
                    propietario = "",
                    clienteId = "",
                    diagnostico = "",
                    hora = "",
                    needsSync = false,
                    syncId = "",
                    selectedDate = "",
                    idSeguimiento = ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener cita por ID: $id", e)
            null
        }
    }

    suspend fun saveCita(cita: Cita): Boolean {
        return try {
            val citaMap = hashMapOf(
                "vehiculoId" to cita.vehiculoId,
                "fecha" to cita.fecha,
                "servicio" to cita.servicio,
                "estado" to cita.estado,
                "notas" to cita.notas,
                "usuarioId" to cita.usuarioId
            )

            if (cita.id.isBlank()) {
                // Nueva cita
                db.collection(COLLECTION_NAME)
                    .add(citaMap)
                    .await()
            } else {
                // Actualizar cita existente
                db.collection(COLLECTION_NAME)
                    .document(cita.id)
                    .set(citaMap)
                    .await()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar cita", e)
            false
        }
    }

    suspend fun deleteCita(id: String): Boolean {
        return try {
            db.collection(COLLECTION_NAME)
                .document(id)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar cita: $id", e)
            false
        }
    }

    suspend fun getCitasByUsuarioId(usuarioId: String): List<Cita> {
        return try {
            val snapshot = db.collection(COLLECTION_NAME)
                .whereEqualTo("usuarioId", usuarioId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    val id = doc.id
                    val vehiculoId = doc.getString("vehiculoId") ?: ""
                    val fecha = doc.getTimestamp("fecha")?.toDate() ?: Date()
                    val servicio = doc.getString("servicio") ?: ""
                    val estado = doc.getString("estado") ?: "pendiente"
                    val notas = doc.getString("notas") ?: ""

                    Cita(
                        id = id,
                        vehiculoId = vehiculoId,
                        fecha = fecha.toString(),
                        servicio = servicio,
                        estado = estado,
                        notas = notas,
                        usuarioId = usuarioId,
                        userId = "", // Valores predeterminados para todos los campos faltantes
                        nombre = "",
                        telefono = "",
                        email = "",
                        marca = "",
                        modelo = "",
                        anio = "",
                        placa = "",
                        kilometraje = "",
                        descripcionProblema = "",
                        fechaSolicitud = "",
                        fechaDeseada = "",
                        horaDeseada = "",
                        servicioId = "",
                        fechaCreacion = "",
                        nombreCliente = "",
                        marcaVehiculo = "",
                        modeloVehiculo = "",
                        anioVehiculo = "",
                        anoVehiculo = "",
                        placaVehiculo = "",
                        kilometrajeVehiculo = "",
                        descripcionProblemaVehiculo = "",
                        time = "",
                        name = "",
                        citaId = "",
                        servicioid = "",
                        propietario = "",
                        clienteId = "",
                        diagnostico = "",
                        hora = "",
                        needsSync = false,
                        syncId = "",
                        selectedDate = "",
                        idSeguimiento = ""
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error al parsear cita: ${doc.id}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener citas por usuario: $usuarioId", e)
            emptyList()
        }
    }
}