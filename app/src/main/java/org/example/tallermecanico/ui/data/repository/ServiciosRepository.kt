package org.example.tallermecanico.ui.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.example.tallermecanico.data.models.Servicio

class ServiciosRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val serviciosCollection = firestore.collection("servicios")


    // Obtiene todos los servicios disponibles como Flow
    fun getAllServiciosFlow(): Flow<List<Servicio>> = callbackFlow {
        val subscription = serviciosCollection
            .orderBy("nombre")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val servicios = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Servicio::class.java)
                } ?: emptyList()

                trySend(servicios)
            }

        awaitClose { subscription.remove() }
    }

    // Obtiene todos los servicios disponibles
    suspend fun getAllServicios(): List<Servicio> = withContext(Dispatchers.IO) {
        val snapshot = serviciosCollection.get().await()
        snapshot.toObjects(Servicio::class.java)
    }

    // Obtiene un servicio por su ID
    suspend fun getServicioPorId(servicioId: String): Servicio? = withContext(Dispatchers.IO) {
        val document = serviciosCollection.document(servicioId).get().await()
        document.toObject(Servicio::class.java)
    }



    // Obtiene servicios por categoría
    suspend fun getServiciosPorCategoria(categoria: String): List<Servicio> = withContext(Dispatchers.IO) {
        val snapshot = serviciosCollection.whereEqualTo("categoria", categoria).get().await()
        snapshot.toObjects(Servicio::class.java)
    }
}


