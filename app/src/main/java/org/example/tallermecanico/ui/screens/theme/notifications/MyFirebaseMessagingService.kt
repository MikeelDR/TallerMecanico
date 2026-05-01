package org.example.tallermecanico.ui.theme.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.example.tallermecanico.R

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Verifica si el mensaje contiene una notificación
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    private fun showNotification(title: String?, body: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "taller_mecanico_channel"

        // Crea el canal de notificación (necesario para versiones de Android >= Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Taller Mecánico",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de citas y estado de vehículos"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Construye la notificación
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Muestra la notificación
        notificationManager.notify(0, notification)
    }
}
