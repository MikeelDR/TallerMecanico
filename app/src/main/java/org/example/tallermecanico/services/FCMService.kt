package org.example.tallermecanico.services

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.example.tallermecanico.data.models.NotificationSender
import java.io.IOException

object FCMService {

    private const val SERVER_KEY = "TALLER2025" // Sustituye esto por tu clave real
    private const val FCM_API_URL = "https://fcm.googleapis.com/fcm/send"
    private const val TAG = "FCMService"

    // Método para registrar el token de FCM
    fun registrarTokenFCM() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "Nuevo token: $token")

                // Verificamos que haya un usuario autenticado antes de guardar el token
                val usuario = FirebaseAuth.getInstance().currentUser
                if (usuario != null) {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("usuarios").document(usuario.uid)
                        .update("fcm_token", token)
                        .addOnSuccessListener {
                            Log.d(TAG, "Token guardado exitosamente.")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error al guardar el token", e)
                        }
                } else {
                    Log.d(TAG, "No hay usuario autenticado para guardar el token")
                }
            } else {
                Log.e(TAG, "Error al obtener el token", task.exception)
            }
        }
    }

    // Método mejorado para enviar notificaciones
    suspend fun enviarNotificacion(notificationSender: NotificationSender): Boolean = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()

            val json = JSONObject()
            json.put("to", notificationSender.to)

            val notification = JSONObject()
            notification.put("title", notificationSender.title)
            notification.put("body", notificationSender.body)

            // Añadimos datos adicionales si los hay
            if (notificationSender.data != null) {
                val data = JSONObject()
                notificationSender.data.forEach { (key, value) ->
                    data.put(key, value)
                }
                json.put("data", data)
            }

            json.put("notification", notification)

            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(FCM_API_URL)
                .post(body)
                .addHeader("Authorization", "key=$SERVER_KEY")
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "Error al enviar notificación: ${response.code} - ${response.body?.string()}")
                    return@withContext false
                } else {
                    Log.d(TAG, "Notificación enviada correctamente")
                    return@withContext true
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error de red al enviar notificación", e)
            return@withContext false
        } catch (e: Exception) {
            Log.e(TAG, "Error inesperado al enviar notificación", e)
            return@withContext false
        }
    }
}