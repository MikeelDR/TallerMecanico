package org.example.tallermecanico.data.repository
import com.google.firebase.firestore.FirebaseFirestore
import org.example.tallermecanico.data.models.Cita


class CitaRepository {
    private val db = FirebaseFirestore.getInstance()

    fun agendarCita(cita: Cita, callback: (Boolean) -> Unit) {
        db.collection("citas").document(cita.id).set(cita)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun obtenerCitasPorUsuario(uid: String, callback: (List<Cita>) -> Unit) {
        db.collection("citas").whereEqualTo("usuarioId", uid).get()
            .addOnSuccessListener { result ->
                val citas = result.toObjects(Cita::class.java)
                callback(citas)
            }
            .addOnFailureListener { callback(emptyList()) }
    }
}
