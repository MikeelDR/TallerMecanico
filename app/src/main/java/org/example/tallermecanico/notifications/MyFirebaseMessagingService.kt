package org.example.tallermecanico.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.example.tallermecanico.MainActivity
import org.example.tallermecanico.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM", "Mensaje recibido de: ${remoteMessage.from}")

        // Verificar si el mensaje contiene datos
        if (remoteMessage.data.isNotEmpty()) {
            val tipo = remoteMessage.data["tipo"]
            val vehiculoId = remoteMessage.data["vehiculoId"]

            when (tipo) {
                "orden_trabajo" -> {
                    val titulo = remoteMessage.notification?.title ?: "Nueva Orden de Trabajo"
                    val cuerpo = remoteMessage.notification?.body ?: "Se ha creado una orden de trabajo para su vehículo"

                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("vehiculoId", vehiculoId)
                        putExtra("navegarA", "consulta_vehiculo")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }

                    mostrarNotificacion(titulo, cuerpo, intent)
                }
                // Puedes añadir más tipos aquí
            }
        }

        // También procesar si es solo una notificación
        remoteMessage.notification?.let {
            val titulo = it.title ?: "Taller Mecánico"
            val cuerpo = it.body ?: "Tienes una nueva notificación"

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            mostrarNotificacion(titulo, cuerpo, intent)
            Log.d("FCM", "Título: $titulo, Mensaje: $cuerpo")
        }
    }

    private fun mostrarNotificacion(titulo: String, cuerpo: String, intent: Intent) {
        val channelId = "taller_mecanico_channel"
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Asegúrate de tener este ícono
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones del Taller Mecánico",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para notificaciones del taller mecánico"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token: $token")

        // Guardar el token localmente para usarlo después de la autenticación
        guardarTokenLocalmente(token)

        // Intentar actualizar en Firestore si el usuario está autenticado
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            actualizarTokenEnFirestore(currentUser.uid, token)
        } else {
            Log.d("FCM", "Usuario no autenticado. El token se actualizará cuando el usuario inicie sesión")
        }
    }

    private fun guardarTokenLocalmente(token: String) {
        // Guardar el token en SharedPreferences
        val sharedPrefs = getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("fcm_token", token).apply()
    }

    private fun actualizarTokenEnFirestore(uid: String, token: String) {
        try {
            val db = FirebaseFirestore.getInstance()

            // 1. Actualizar usando el UID como ID del documento (más seguro)
            db.collection("usuarios").document(uid)
                .update("fcmToken", token)
                .addOnSuccessListener {
                    Log.d("FCM", "Token FCM actualizado correctamente usando UID")
                }
                .addOnFailureListener { e ->
                    Log.e("FCM", "Error al actualizar token FCM por UID: ${e.message}")

                    // 2. Si falla, intentar buscar el documento por email o algún otro campo
                    buscarYActualizarUsuarioPorEmail(token)
                }
        } catch (e: Exception) {
            Log.e("FCM", "Excepción al actualizar token: ${e.message}")
        }
    }

    private fun buscarYActualizarUsuarioPorEmail(token: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email

        if (email != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val docId = documents.documents[0].id
                        db.collection("usuarios").document(docId)
                            .update("fcmToken", token)
                            .addOnSuccessListener {
                                Log.d("FCM", "Token FCM actualizado correctamente mediante búsqueda por email")
                            }
                            .addOnFailureListener { e ->
                                Log.e("FCM", "Error en actualización final: ${e.message}")
                            }
                    } else {
                        Log.e("FCM", "No se encontró usuario con email: $email")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FCM", "Error al buscar usuario por email: ${e.message}")
                }
        }
    }
}