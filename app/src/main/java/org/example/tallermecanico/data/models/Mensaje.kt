package org.example.tallermecanico.data.models
import com.google.firebase.Timestamp
import androidx.recyclerview.widget.DiffUtil

data class Mensaje(
    val usuario_uid: String? = null,
    val mensaje: String? = null,
    val fecha_hora: Timestamp? = null // Utilizando Timestamp para Firestore
) {
    // Clase interna para DiffCallback
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Mensaje>() {
            override fun areItemsTheSame(oldItem: Mensaje, newItem: Mensaje): Boolean {
                // Compara los IDs o alguna propiedad única que identifique a un mensaje
                return oldItem.usuario_uid == newItem.usuario_uid && oldItem.fecha_hora == newItem.fecha_hora
            }

            override fun areContentsTheSame(oldItem: Mensaje, newItem: Mensaje): Boolean {
                // Compara el contenido de los mensajes
                return oldItem == newItem
            }
        }
    }
}

