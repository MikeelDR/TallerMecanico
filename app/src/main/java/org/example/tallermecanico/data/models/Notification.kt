package org.example.tallermecanico.data.models


data class NotificationData(
    val title: String,
    val body: String,
    val tipo: String,
    val vehiculoId: String,
    val additionalData: Map<String, String>? = null
)


data class NotificationSender(
    val to: String,
    val title: String,
    val body: String,
    val data: Map<String, String>? = null
)