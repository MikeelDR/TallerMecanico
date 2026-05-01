package org.example.tallermecanico.ui.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.example.tallermecanico.data.models.Cita
import org.example.tallermecanico.viewmodel.CitasRepository
import org.example.tallermecanico.viewmodel.EstadoVehiculo
import org.example.tallermecanico.viewmodel.VehiculoRegistrado
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CitasRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CitasRepository {

    private val citasCollection = firestore.collection("citas")
    private val vehiculosCollection = firestore.collection("vehiculos")
    private val serviciosCollection = firestore.collection("servicios")

    override fun getCitasByFecha(fecha: Date): Flow<List<Cita>> = callbackFlow {
        val subscription = citasCollection
            .whereEqualTo("fecha", fecha)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Manejar error
                    close(error)
                    return@addSnapshotListener
                }

                val citas = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Cita::class.java)?.apply {
                        id = document.id
                    }
                } ?: emptyList()

                trySend(citas)
            }

        awaitClose { subscription.remove() }
    }

    override fun getCitasPendientesFlow(): Flow<List<Cita>> = callbackFlow {
        val subscription = citasCollection
            .whereEqualTo("estado", "pendiente")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val citas = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Cita::class.java)?.apply {
                        id = document.id
                    }
                } ?: emptyList()

                trySend(citas)
            }

        awaitClose { subscription.remove() }
    }

    override fun getAllCitasFlow(): Flow<List<Cita>> = callbackFlow {
        val subscription = citasCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val citas = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Cita::class.java)?.apply {
                        id = document.id
                    }
                } ?: emptyList()

                trySend(citas)
            }

        awaitClose { subscription.remove() }
    }

    override fun getCitasByUserIdFlow(userId: String): Flow<List<Cita>> = callbackFlow {
        val subscription = citasCollection
            .whereEqualTo("clienteId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val citas = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Cita::class.java)?.apply {
                        id = document.id
                    }
                } ?: emptyList()

                trySend(citas)
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun getCitaById(citaId: String): Cita? {
        return try {
            val document = citasCollection.document(citaId).get().await()
            document.toObject(Cita::class.java)?.apply {
                id = document.id
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun crearCita(cita: Cita): String {
        val id = if (cita.id.isNullOrEmpty()) {
            citasCollection.document().id
        } else {
            cita.id!!
        }

        val citaData = cita.copy(id = id)
        citasCollection.document(id).set(citaData).await()
        return id
    }

    override suspend fun aceptarCita(citaId: String, vehiculoId: String, servicioId: String) {
        citasCollection.document(citaId)
            .update(
                mapOf(
                    "estado" to "aceptada",
                    "vehiculoId" to vehiculoId,
                    "servicioId" to servicioId
                )
            ).await()
    }

    override suspend fun rechazarCita(citaId: String) {
        citasCollection.document(citaId)
            .update("estado", "rechazada")
            .await()
    }

    override suspend fun completarCita(citaId: String) {
        citasCollection.document(citaId)
            .update("estado", "completada")
            .await()
    }

    override fun getAllCitas(): List<Cita> {
        val snapshot = firestore.collection("citas").get().result
        return snapshot.documents.mapNotNull { document ->
            document.toObject(Cita::class.java)?.apply {
                id = document.id
            }
        }
    }

    override fun getCitasPendientes() {
        // Esta función parece no devolver ningún valor según su firma
        // Asumimos que es una función de carga que posiblemente actualiza alguna propiedad
        // interna o notifica a través de callbacks
        citasCollection
            .whereEqualTo("estado", "pendiente")
            .get()
            .addOnSuccessListener { snapshot ->
                val citas = snapshot.documents.mapNotNull { document ->
                    document.toObject(Cita::class.java)?.apply {
                        id = document.id
                    }
                }
                // Aquí deberíamos hacer algo con las citas cargadas
            }
    }

    override fun getAllServicios() {
        // Similar a la función anterior, carga los servicios
        serviciosCollection
            .get()
            .addOnSuccessListener { snapshot ->
                // Procesamiento de los servicios obtenidos
            }
    }

    override fun getCitasPorFecha(fecha: Date) {
        citasCollection
            .whereEqualTo("fecha", fecha)
            .get()
            .addOnSuccessListener { snapshot ->
                // Procesamiento de las citas por fecha
            }
    }

    override fun actualizarEstadoCita(citaId: String, aceptada: Boolean, servicioId: String) {
        val estado = if (aceptada) "aceptada" else "rechazada"
        val updates = mutableMapOf<String, Any>("estado" to estado)

        if (aceptada) {
            updates["servicioId"] = servicioId
        }

        citasCollection.document(citaId)
            .update(updates)
    }

    override fun registrarVehiculo(vehiculo: VehiculoRegistrado) {
        val vehiculoId = vehiculo.id ?: generarIdVehiculo()
        val vehiculoConId = vehiculo.copy(id = vehiculoId)

        vehiculosCollection.document(vehiculoId)
            .set(vehiculoConId)
    }

    override fun getEstadoVehiculoById(vehiculoId: String) {
        vehiculosCollection.document(vehiculoId)
            .get()
            .addOnSuccessListener { document ->
                val estadoVehiculo = document.get("estado") as? String
                // Hacer algo con el estado obtenido
            }
    }

    override fun getVehiculoById(vehiculoId: String) {
        vehiculosCollection.document(vehiculoId)
            .get()
            .addOnSuccessListener { document ->
                val vehiculo = document.toObject(VehiculoRegistrado::class.java)
                // Hacer algo con el vehículo obtenido
            }
    }

    override fun actualizarEstadoVehiculo(vehiculoId: String, nuevoEstado: EstadoVehiculo) {
        vehiculosCollection.document(vehiculoId)
            .update("estado", nuevoEstado.toString())
    }

    override fun generarIdVehiculo(): String {
        return UUID.randomUUID().toString()
    }

    override fun obtenerAnoDelModelo(modelo: String): String {
        // Esta función debería extraer el año del modelo
        // Si el modelo tiene formato como "Toyota Corolla 2022", extraería "2022"
        val partes = modelo.split(" ")
        return partes.lastOrNull { it.matches(Regex("\\d{4}")) } ?: ""
    }

    override fun eliminarCita(citaId: String) {
        citasCollection.document(citaId).delete()
    }

    override fun actualizarCita(cita: Cita) {
        val id = cita.id ?: return
        citasCollection.document(id).set(cita)
    }

    override fun agregarCita(cita: Cita) {
        val id = cita.id ?: citasCollection.document().id
        val citaConId = cita.copy(id = id)
        citasCollection.document(id).set(citaConId)
    }

    override fun cargarTodasLasCitas() {
        // Similar a getAllCitas pero posiblemente para uso interno o con callbacks
        citasCollection
            .get()
            .addOnSuccessListener { snapshot ->
                // Hacer algo con todas las citas
            }
    }

    override fun cargarCitasPendientes() {
        // Similar a getCitasPendientes
        getCitasPendientes()
    }

    override fun cargarServicios() {
        // Similar a getAllServicios
        getAllServicios()
    }

    override fun seleccionarCita(cita: Cita) {
        // Esta función probablemente actualiza algún estado interno o notifica
        // a algún observador sobre la cita seleccionada
    }

    override fun cargarDatos() {
        // Cargar todos los datos necesarios
        cargarTodasLasCitas()
        cargarCitasPendientes()
        cargarServicios()
    }

    override fun cargarEstadoVehiculo(vehiculoId: String) {
        // Similar a getEstadoVehiculoById
        getEstadoVehiculoById(vehiculoId)
    }

    override fun citaAceptada(citaId: String, vehiculoId: String, servicioId: String) {
        // Similar a aceptarCita pero en versión no suspendida
        citasCollection.document(citaId)
            .update(
                mapOf(
                    "estado" to "aceptada",
                    "vehiculoId" to vehiculoId,
                    "servicioId" to servicioId
                )
            )
    }

    override fun enviarNotificacionCliente(clienteId: String, titulo: String, mensaje: String) {
        // Esta función debería enviar una notificación al cliente
        // Probablemente a través de Firebase Cloud Messaging (FCM)
        val notificacion = hashMapOf(
            "usuarioId" to clienteId,
            "titulo" to titulo,
            "mensaje" to mensaje,
            "leida" to false,
            "fechaCreacion" to Date()
        )

        firestore.collection("notificaciones")
            .add(notificacion)
    }
}


// Función auxiliar para comparar solo fechas sin considerar la hora
private fun areSameDates(date1: Date, date2: Date): Boolean {
    val cal1 = java.util.Calendar.getInstance().apply { time = date1 }
    val cal2 = java.util.Calendar.getInstance().apply { time = date2 }

    return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
            cal1.get(java.util.Calendar.MONTH) == cal2.get(java.util.Calendar.MONTH) &&
            cal1.get(java.util.Calendar.DAY_OF_MONTH) == cal2.get(java.util.Calendar.DAY_OF_MONTH)
}
