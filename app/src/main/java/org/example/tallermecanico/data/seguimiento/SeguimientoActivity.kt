package org.example.tallermecanico.data.seguimiento
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.example.tallermecanico.R
import com.google.firebase.firestore.FirebaseFirestore
import org.example.tallermecanico.adapters.HistorialAdapter
import org.example.tallermecanico.data.models.Vehiculo // Asegú

class SeguimientoActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var rvHistorial: RecyclerView
    private lateinit var historialAdapter: HistorialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguimiento)

        val tvModelo = findViewById<TextView>(R.id.tvModelo)
        val tvEstado = findViewById<TextView>(R.id.tvEstado)
        val tvFechaIngreso = findViewById<TextView>(R.id.tvFechaIngreso)
        rvHistorial = findViewById(R.id.rvHistorial)
        rvHistorial.layoutManager = LinearLayoutManager(this)

        val placaVehiculo = intent.getStringExtra("placa") ?: return

        db.collection("vehiculos").document(placaVehiculo).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val vehiculo = document.toObject(Vehiculo::class.java)

                    tvModelo.text = "Modelo: ${vehiculo?.modelo}"
                    tvEstado.text = "Estado: ${vehiculo?.estado}"
                    tvFechaIngreso.text = "Fecha de Ingreso: ${vehiculo?.fecha_ingreso}"

                    // Cargar historial en el RecyclerView
                    historialAdapter = HistorialAdapter(vehiculo?.historial ?: listOf())
                    rvHistorial.adapter = historialAdapter
                } else {
                    Toast.makeText(this, "No se encontró el vehículo", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
    }
}
