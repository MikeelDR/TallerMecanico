package org.example.tallermecanico.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import org.example.tallermecanico.R
import org.example.tallermecanico.navigation.AppScreens

@Composable
fun MenuPrincipalClienteScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    // Obtener el email del usuario autenticado
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

    // Colores basados en la imagen
    val primaryBlue = Color(0xFF2563EB)      // Azul principal
    val lightBlue = Color(0xFF60A5FA)        // Azul claro
    val backgroundColor = Color(0xFFF1F5F9)   // Gris muy claro
    val cardBackground = Color.White

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Header con gradiente azul
        HeaderSection(
            navController = navController,
            userEmail = userEmail
        )

        // Contenido principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Título de servicios
                Text(
                    text = "Servicios Disponibles",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "Selecciona el servicio que necesitas",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Grid de servicios mejorado
                ServiceGrid(navController = navController)

                Spacer(modifier = Modifier.weight(1f))

                // Footer con ayuda
                HelpSection()

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun HeaderSection(
    navController: NavHostController,
    userEmail: String
) {
    val userName = userEmail.split("@").firstOrNull()?.capitalize() ?: "César"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E40AF),
                        Color(0xFF3B82F6),
                        Color(0xFF60A5FA)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "¡Bienvenido, $userName!",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tu vehículo en las mejores manos",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp
                )
            }

            // Avatar del perfil
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable {
                        navController.navigate(AppScreens.PerfilCliente.route)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = Color(0xFF1E40AF),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ServiceGrid(navController: NavHostController) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Primera fila
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ServiceCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.CalendarToday,
                title = "Agendar Cita",
                subtitle = "Programa tu servicio",
                backgroundColor = Color(0xFFDCFDF7),
                iconColor = Color(0xFF10B981),
                onClick = {
                    navController.navigate(AppScreens.DatosAutoCliente.route)
                }
            )

            ServiceCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.DirectionsCar,
                title = "Estado Vehículo",
                subtitle = "Consulta tu servicio",
                backgroundColor = Color(0xFFDBEAFE),
                iconColor = Color(0xFF3B82F6),
                showNotification = true,
                onClick = {
                    try {
                        navController.navigate(AppScreens.ConsultaVehiculoScreen.route)
                    } catch (e: Exception) {
                        navController.navigate(AppScreens.MenuPrincipalCliente.route)
                    }
                }
            )
        }

        // Segunda fila
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ServiceCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Build,
                title = "Servicios",
                subtitle = "Conoce nuestros servicios",
                backgroundColor = Color(0xFFFED7AA),
                iconColor = Color(0xFFEA580C),
                onClick = {
                    try {
                        navController.navigate(AppScreens.HistorialDeServicios.route)
                    } catch (e: Exception) {
                        navController.navigate(AppScreens.MenuPrincipalCliente.route)
                    }
                }
            )

            ServiceCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.History,
                title = "Mi Historial",
                subtitle = "Servicios realizados",
                backgroundColor = Color(0xFFE9D5FF),
                iconColor = Color(0xFF8B5CF6),
                onClick = {
                    navController.navigate(
                        AppScreens.CitasRealizadas.createRoute(
                            FirebaseAuth.getInstance().currentUser?.email ?: ""
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun ServiceCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    backgroundColor: Color,
    iconColor: Color,
    showNotification: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Círculo con icono
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(backgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }

            // Indicador de notificación
            if (showNotification) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "!",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HelpSection() {
    var mostrarAyuda by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { mostrarAyuda = !mostrarAyuda },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFDBEAFE)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E40AF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Información",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "¿Necesitas ayuda?",
                        color = Color(0xFF1E40AF),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ver más",
                    tint = Color(0xFF1E40AF),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Mostrar información si el estado está activado
        if (mostrarAyuda) {
            Text(
                text = """
                    Información de Contacto:
                    
                    Número de teléfono: +52 8129135529
                    Horario de atención: Lunes a Viernes 8:00 AM - 6:00 PM
                """.trimIndent(),
                color = Color.DarkGray,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp)
            )
        }
    }
}


// Función de extensión para capitalizar la primera letra
private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

@Preview(showBackground = true)
@Composable
fun MenuPrincipalClienteScreenPreview() {
    MenuPrincipalClienteScreen(navController = rememberNavController())
}