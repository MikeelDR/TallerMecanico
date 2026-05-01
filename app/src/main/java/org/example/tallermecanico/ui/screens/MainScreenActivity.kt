package org.example.tallermecanico

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.Firebase
import com.google.firebase.initialize
import org.example.tallermecanico.navigation.TallerMecanicoNavGraph
import org.example.tallermecanico.ui.theme.TallerMecanicoTheme

// ✅ Importaciones necesarias
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.tallermecanico.viewmodel.CitasViewModel
import org.example.tallermecanico.viewmodel.CitasViewModelFactory
import org.example.tallermecanico.ui.data.repository.CitasRepository
import org.example.tallermecanico.ui.data.repository.VehiculosRepository
import org.example.tallermecanico.ui.data.repository.impl.FirestoreCitasRepository
import org.example.tallermecanico.ui.data.repository.impl.FirestoreVehiculosRepository

class MainScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 INICIALIZAR FIREBASE PRIMERO - ANTES DE USAR CUALQUIER SERVICIO
        Firebase.initialize(this)

        // Obtener el token de Firebase Cloud Messaging
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "Nuevo token: $token")

                // Guarda el token en Firestore
                val usuario = FirebaseAuth.getInstance().currentUser
                usuario?.let {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("usuarios").document(it.uid)
                        .update("fcm_token", token)
                        .addOnSuccessListener {
                            Log.d("FCM", "Token guardado exitosamente.")
                        }
                        .addOnFailureListener { e ->
                            Log.w("FCM", "Error al guardar el token", e)
                        }
                }
            } else {
                Log.e("FCM", "Error al obtener el token", task.exception)
            }
        }

        // Inicializar los repositorios
        val db = FirebaseFirestore.getInstance()
        val citasRepository = FirestoreCitasRepository(db)
        val vehiculosRepository = FirestoreVehiculosRepository(db)

        // Configurar la UI con Compose
        setContent {
            TallerMecanicoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController: NavHostController = rememberNavController()
                    val citasViewModel: CitasViewModel = viewModel(factory = CitasViewModelFactory(
                        citasRepository = citasRepository,
                        vehiculosRepository = vehiculosRepository
                    ))
                    TallerMecanicoNavGraph(navController, citasViewModel)
                }
            }
        }
    }
}
