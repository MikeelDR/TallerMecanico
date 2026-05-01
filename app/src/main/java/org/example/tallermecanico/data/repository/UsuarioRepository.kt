package org.example.tallermecanico.data.repository
import com.google.firebase.firestore.FirebaseFirestore
import org.example.tallermecanico.data.models.Usuario
import com.google.firebase.auth.FirebaseAuth


class UsuarioRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun registrarUsuario(usuario: Usuario, callback: (Boolean) -> Unit) {
        db.collection("usuarios").document(usuario.uid).set(usuario)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun obtenerUsuario(uid: String, callback: (Usuario?) -> Unit) {
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { document ->
                callback(document.toObject(Usuario::class.java))
            }
            .addOnFailureListener { callback(null) }
    }
}
