package org.example.tallermecanico

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import org.example.tallermecanico.navigation.TallerMecanicoNavGraph
import org.example.tallermecanico.ui.theme.TallerMecanicoTheme
import org.example.tallermecanico.viewmodel.CitasViewModel
import javax.inject.Inject
import org.example.tallermecanico.viewmodel.CitasViewModelFactory

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var citasViewModelFactory: CitasViewModelFactory

    private val citasViewModel: CitasViewModel by viewModels { citasViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener el token de Firebase Cloud Messaging
        configurarFCM()

        // Configurar la UI con Compose
        setContent {
            TallerMecanicoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    TallerMecanicoNavGraph(
                        navController = navController,
                        citasViewModel = citasViewModel
                    )
                }
            }
        }
    }

    private fun configurarFCM() {
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
    }
}