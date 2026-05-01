package org.example.tallermecanico.ui.theme.chat
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import org.example.tallermecanico.R
import org.example.tallermecanico.data.models.Mensaje
import org.example.tallermecanico.ui.theme.chat.ChatAdapter

class ChatActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private lateinit var etMensaje: EditText
    private lateinit var btnEnviar: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var chatId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerView = findViewById(R.id.recyclerViewChat)
        etMensaje = findViewById(R.id.etMensaje)
        btnEnviar = findViewById(R.id.btnEnviar)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Obtener el ID del chat desde la actividad anterior
        chatId = intent.getStringExtra("chatId")

        adapter = ChatAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Cargar los mensajes en tiempo real
        cargarMensajes()

        // Enviar un mensaje
        btnEnviar.setOnClickListener { enviarMensaje() }
    }

    private fun cargarMensajes() {
        // Escuchar los cambios en los mensajes en tiempo real
        chatId?.let {
            db.collection("chats").document(it).collection("mensajes")
                .orderBy("fecha_hora")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w("Chat", "Error al escuchar los mensajes", e)
                        return@addSnapshotListener
                    }

                    val mensajes = mutableListOf<Mensaje>()
                    snapshots?.forEach { document ->
                        val mensaje = document.toObject(Mensaje::class.java)
                        mensajes.add(mensaje)
                    }
                    adapter.submitList(mensajes)
                }
        }
    }

    private fun enviarMensaje() {
        val mensaje = etMensaje.text.toString().trim()
        if (mensaje.isEmpty()) return

        val nuevoMensaje = hashMapOf(
            "usuario_uid" to auth.currentUser?.uid,
            "mensaje" to mensaje,
            "fecha_hora" to FieldValue.serverTimestamp()
        )

        // Agregar el mensaje a Firestore
        chatId?.let {
            db.collection("chats").document(it).collection("mensajes")
                .add(nuevoMensaje)
                .addOnSuccessListener {
                    etMensaje.text.clear() // Limpiar el campo de texto
                }
                .addOnFailureListener { e ->
                    Log.w("Chat", "Error al enviar mensaje", e)
                }
        }
    }
}

