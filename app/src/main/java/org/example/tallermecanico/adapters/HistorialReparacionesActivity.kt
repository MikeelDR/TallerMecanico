package org.example.tallermecanico.adapters
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.example.tallermecanico.R
import org.example.tallermecanico.data.models.Reparacion


class HistorialReparacionesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReparacionAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_reparaciones)

        recyclerView = findViewById(R.id.recyclerViewReparaciones)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Asegúrate de usar ListAdapter para que puedas usar submitList
        adapter = ReparacionAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Cargar el historial de reparaciones
        cargarHistorialReparaciones()
    }

    private fun cargarHistorialReparaciones() {
        val usuarioUid = auth.currentUser?.uid

        // Obtener las reparaciones del usuario actual
        usuarioUid?.let {
            db.collection("usuarios").document(it)
                .collection("vehiculos")
                .get()
                .addOnSuccessListener { vehiculos ->
                    vehiculos.forEach { vehiculo ->
                        val vehiculoId = vehiculo.id

                        // Obtener las reparaciones de ese vehículo
                        db.collection("vehiculos").document(vehiculoId)
                            .collection("reparaciones")
                            .orderBy("fecha_reparacion", Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener { reparaciones ->
                                val listaReparaciones = mutableListOf<Reparacion>()
                                reparaciones.forEach { reparacion ->
                                    val rep = reparacion.toObject(Reparacion::class.java)
                                    listaReparaciones.add(rep)
                                }

                                // Usar submitList para actualizar el adaptador
                                adapter.submitList(listaReparaciones)
                            }
                            .addOnFailureListener { e ->
                                Log.w("Historial", "Error al cargar las reparaciones", e)
                            }
                    }
                }
        }
    }
}

