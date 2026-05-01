package org.example.tallermecanico.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.example.tallermecanico.R
import org.example.tallermecanico.data.models.Cita

@Composable
fun CitasRealizadasScreen(navController: NavHostController, userEmail: String) {
    val db = FirebaseFirestore.getInstance()
    var citas by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var loading by remember { mutableStateOf(true) }
    var listener: ListenerRegistration? = null

    // Colores del tema
    val primaryColor = Color(0xFF1F41BB)
    val backgroundColor = Color(0xFFF8F9FF)
    val cardColor = Color(0xFFFFFFFF)
    val textSecondaryColor = Color(0xFF555555)
    val accentColor = Color(0xFF4CAF50)

    // Escuchar cambios en tiempo real
    LaunchedEffect(userEmail) {
        listener = db.collection("citas")
            .whereEqualTo("email", userEmail)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Maneja el error si ocurre
                    println("Error al obtener citas: ${exception.localizedMessage}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Convertimos directamente a Map en lugar de usar el modelo Cita
                    citas = snapshot.documents.mapNotNull { doc ->
                        doc.data
                    }
                    loading = false // Ya terminamos de cargar los datos
                }
            }
    }

    // Para limpiar el listener cuando la composición termine
    DisposableEffect(Unit) {
        onDispose {
            listener?.remove() // Elimina el listener cuando la pantalla se descarta
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header mejorado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Icono decorativo
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CarRepair,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Título con subtítulo
                    Column {
                        Text(
                            text = "Citas Realizadas",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = primaryColor
                        )
                        Text(
                            text = "Historial de servicios agendados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textSecondaryColor
                        )
                    }
                }
            }

            // Indicador de carga
            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = primaryColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando tus citas...",
                            color = textSecondaryColor
                        )
                    }
                }
            } else {
                if (citas.isEmpty()) {
                    // Mensaje sin citas mejorado
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = textSecondaryColor.copy(alpha = 0.5f),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No tienes citas registradas",
                                color = textSecondaryColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Cuando agendes un servicio, aparecerá aquí",
                                color = textSecondaryColor.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    // Mostrar las citas con mejor diseño
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(citas) { cita ->
                            CitaCard(cita, primaryColor, cardColor, textSecondaryColor, accentColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CitaCard(
    cita: Map<String, Any>,  // Cambiado a Map<String, Any> para mayor flexibilidad
    primaryColor: Color,
    cardColor: Color,
    textSecondaryColor: Color,
    accentColor: Color
) {
    // Extraemos los valores de manera segura del Map
    val marca = cita["marca"] as? String ?: "No especificada"
    val placaVehiculo = cita["placa"] as? String ?: "No especificada"

    // Intentamos obtener la fecha_hora, si no existe intentamos con otros campos posibles
    val fechaHora = cita["fecha_hora"] as? String
        ?: cita["fechaSolicitada"] as? String
        ?: "${cita["fecha"] as? String ?: ""} ${cita["hora"] as? String ?: ""}"
        ?: "No especificada"

    // Para el año, intentamos varias posibilidades
    val anio = cita["año"] as? String
        ?: cita["anio"] as? String
        ?: cita["anoVehiculo"] as? String
        ?: cita["anioVehiculo"] as? String
        ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Cabecera de la tarjeta
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = marca,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = primaryColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Icono de carro
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(primaryColor)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_car),
                        contentDescription = "Vehículo",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(
                icon = Icons.Default.CalendarToday,
                label = "Fecha Solicitada:",
                value = fechaHora,
                primaryColor = accentColor,
                textColor = textSecondaryColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(
                icon = Icons.Default.DirectionsCar,
                label = "Modelo:",
                value = if (anio.isNotEmpty()) "$marca $anio" else marca,
                primaryColor = primaryColor,
                textColor = textSecondaryColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(
                icon = Icons.Default.Numbers,
                label = "Placa:",
                value = placaVehiculo,
                primaryColor = primaryColor,
                textColor = textSecondaryColor
            )

            // Mostrar el servicio si está disponible
            cita["servicio"]?.let { servicio ->
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Default.CarRepair,
                    label = "Servicio:",
                    value = servicio.toString(),
                    primaryColor = primaryColor,
                    textColor = textSecondaryColor
                )
            }

            // Mostrar el estado si está disponible
            cita["estado"]?.let { estado ->
                Spacer(modifier = Modifier.height(8.dp))
                val estadoColor = when(estado.toString().lowercase()) {
                    "pendiente" -> Color(0xFFFFA000)
                    "completado" -> Color(0xFF4CAF50)
                    "cancelado" -> Color(0xFFF44336)
                    else -> textSecondaryColor
                }

                InfoRow(
                    icon = Icons.Default.DirectionsCar,
                    label = "Estado:",
                    value = estado.toString(),
                    primaryColor = estadoColor,
                    textColor = textSecondaryColor
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    primaryColor: Color,
    textColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = primaryColor,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = textColor
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = FontWeight.Normal
        )
    }
}