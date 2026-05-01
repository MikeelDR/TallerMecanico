package org.example.tallermecanico.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.example.tallermecanico.data.models.Servicio
import org.example.tallermecanico.navigation.AppScreens
import org.example.tallermecanico.ui.data.repository.CitasRepository
import org.example.tallermecanico.ui.data.repository.ServiciosRepository
import org.example.tallermecanico.ui.data.repository.VehiculosRepository
import org.example.tallermecanico.viewmodel.CitasViewModel
import org.example.tallermecanico.viewmodel.CitasViewModelFactory

/**
 * Pantalla principal del menú del trabajador con diseño mejorado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrabajadorMenuPrincipalScreen(
    navController: NavHostController,
    citasViewModel: CitasViewModel = viewModel(
        factory = CitasViewModelFactory(
            CitasRepository(),
            VehiculosRepository()
        )
    ),
    onNavigateToGestionCitasTrabajadorScreen: () -> Unit,
    onNavigateToConsulta: () -> Unit,
    onNavigateToActualizarEstadoScreen: () -> Unit
) {
    // Estados
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var showSearchDialog by remember { mutableStateOf(false) }
    var idBusqueda by remember { mutableStateOf("") }
    var servicios by remember { mutableStateOf<List<Servicio>>(emptyList()) }
    val serviciosRepository = remember { ServiciosRepository() }

    // Paleta de colores moderna
    val primaryBlue = Color(0xFF1E3A8A)      // Azul profundo
    val lightBlue = Color(0xFF3B82F6)        // Azul medio
    val skyBlue = Color(0xFF60A5FA)          // Azul claro
    val lightGray = Color(0xFFF8FAFC)        // Gris muy claro
    val mediumGray = Color(0xFF64748B)       // Gris medio
    val cardBackground = Color(0xFFFFFFFF)   // Blanco
    val surfaceGray = Color(0xFFF1F5F9)      // Gris superficie

    val headerGradient = Brush.verticalGradient(
        colors = listOf(primaryBlue, lightBlue),
        startY = 0f,
        endY = 600f
    )

    // Cargar datos iniciales
    LaunchedEffect(key1 = Unit) {
        scope.launch {
            try {
                servicios = serviciosRepository.getAllServicios()
            } catch (e: Exception) {
                errorMessage = "Error al cargar datos: ${e.message}"
                Log.e("TrabajadorMenu", "Error de inicialización", e)
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header mejorado
            HeaderSection(
                onSearchClick = { showSearchDialog = true },
                onProfileClick = { navController.navigate(AppScreens.PerfilCliente.route) },
                gradient = headerGradient
            )

            // Espaciado
            Spacer(modifier = Modifier.height(24.dp))

            // Sección de acciones rápidas
            QuickActionsSection(
                onEstadoVehiculoClick = onNavigateToConsulta,
                onPanelTrabajadorClick = onNavigateToGestionCitasTrabajadorScreen,
                primaryColor = primaryBlue,
                surfaceColor = cardBackground
            )

            // Espaciado
            Spacer(modifier = Modifier.height(24.dp))

            // Sección de servicios
            ServicesSection(
                isLoading = isLoading,
                servicios = servicios,
                onServiciosClick = { navController.navigate("historial_servicios") },
                primaryColor = primaryBlue,
                surfaceColor = cardBackground,
                backgroundColor = surfaceGray
            )

            // Espaciado final
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Diálogo de búsqueda
        if (showSearchDialog) {
            ModernSearchDialog(
                idBusqueda = idBusqueda,
                onIdChange = { idBusqueda = it },
                onBuscarClick = {
                    if (idBusqueda.isNotBlank()) {
                        navController.navigate("${AppScreens.ConsultaVehiculoScreen.route}/${idBusqueda}")
                        showSearchDialog = false
                        idBusqueda = ""
                    }
                },
                onDismiss = { showSearchDialog = false },
                primaryColor = primaryBlue
            )
        }
    }
}

/**
 * Header mejorado con gradiente y mejor organización
 */
@Composable
fun HeaderSection(
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    gradient: Brush
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Barra superior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Menu Trabajador",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Botón de búsqueda moderno
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { onSearchClick() },
                        color = Color.White.copy(alpha = 0.15f),
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Buscar vehículo",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Botón de perfil moderno
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { onProfileClick() },
                        color = Color.White.copy(alpha = 0.15f),
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Información del trabajo
            Column {
                Text(
                    text = "¡Bienvenido de vuelta!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = "Gestiona las citas y servicios del taller",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Sección de acciones rápidas
 */
@Composable
fun QuickActionsSection(
    onEstadoVehiculoClick: () -> Unit,
    onPanelTrabajadorClick: () -> Unit,
    primaryColor: Color,
    surfaceColor: Color
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Consulta y Modifica Estado",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Estado Vehículo
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Estado Vehículo",
                subtitle = "Consultar servicios",
                icon = Icons.Outlined.DirectionsCar,
                onClick = onEstadoVehiculoClick,
                primaryColor = primaryColor,
                surfaceColor = surfaceColor
            )

            // Panel Trabajador
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Panel Trabajador",
                subtitle = "Gestión de citas",
                icon = Icons.Outlined.Dashboard,
                onClick = onPanelTrabajadorClick,
                primaryColor = primaryColor,
                surfaceColor = surfaceColor
            )
        }
    }
}

/**
 * Tarjeta de acción rápida mejorada
 */
@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    primaryColor: Color,
    surfaceColor: Color
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = primaryColor.copy(alpha = 0.25f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryColor
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

/**
 * Sección de servicios mejorada con lista completa
 */
@Composable
fun ServicesSection(
    isLoading: Boolean,
    servicios: List<Servicio>,
    onServiciosClick: () -> Unit,
    primaryColor: Color,
    surfaceColor: Color,
    backgroundColor: Color
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        // Header de servicios
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Servicios Disponibles",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
                Text(
                    text = "${servicios.size} servicios activos",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            TextButton(
                onClick = onServiciosClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = primaryColor
                )
            ) {
                Text("Lista de Servicios")
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(start = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido de servicios
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = primaryColor.copy(alpha = 0.15f)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = primaryColor,
                            strokeWidth = 3.dp
                        )
                        Text(
                            text = "Cargando servicios...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                ServicesListContent(
                    servicios = servicios,
                    primaryColor = primaryColor,
                    backgroundColor = backgroundColor
                )
            }
        }
    }
}

/**
 * Contenido de la lista de servicios
 */
@Composable
fun ServicesListContent(
    servicios: List<Servicio>,
    primaryColor: Color,
    backgroundColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        if (servicios.isEmpty()) {
            EmptyServicesState(primaryColor = primaryColor)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

            }
        }
    }
}




/**
 * Item individual de servicio en la lista
 */
@Composable
fun ServiceListItem(
    servicio: Servicio,
    primaryColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Aquí puedes agregar navegación al detalle del servicio */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono y información del servicio
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Icono del servicio basado en el tipo/nombre
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getServiceIcon(servicio.nombre ?: ""),
                        contentDescription = servicio.nombre,
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Información del servicio
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = servicio.nombre ?: "Servicio sin nombre",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = servicio.descripcion ?: "Sin descripción disponible",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Precio y estado
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "S/ ${String.format("%.2f", servicio.precio ?: 0.0)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )

                // Badge de estado (opcional - si tienes un campo de estado en tu modelo)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = primaryColor.copy(alpha = 0.1f),
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text(
                        text = "Disponible",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Estado vacío cuando no hay servicios
 */
@Composable
fun EmptyServicesState(primaryColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Build,
                contentDescription = null,
                tint = primaryColor.copy(alpha = 0.6f),
                modifier = Modifier.size(32.dp)
            )
        }

        Text(
            text = "Consulta los servicios del taller",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = primaryColor,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Los servicios aparecen en el boton de Servicios",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

/**
 * Función para obtener iconos basados en el tipo de servicio
 */
@Composable
fun getServiceIcon(serviceName: String): ImageVector {
    return when {
        serviceName.contains("Cambio", ignoreCase = true) &&
                serviceName.contains("Aceite", ignoreCase = true) -> Icons.Default.LocalGasStation

        serviceName.contains("Frenos", ignoreCase = true) -> Icons.Default.Speed

        serviceName.contains("Motor", ignoreCase = true) -> Icons.Default.Engineering

        serviceName.contains("Batería", ignoreCase = true) ||
                serviceName.contains("Bateria", ignoreCase = true) -> Icons.Default.Battery0Bar

        serviceName.contains("Llantas", ignoreCase = true) ||
                serviceName.contains("Neumáticos", ignoreCase = true) -> Icons.Default.Circle

        serviceName.contains("Aire", ignoreCase = true) &&
                serviceName.contains("Acondicionado", ignoreCase = true) -> Icons.Default.AcUnit

        serviceName.contains("Transmisión", ignoreCase = true) ||
                serviceName.contains("Transmision", ignoreCase = true) -> Icons.Default.Settings

        serviceName.contains("Suspensión", ignoreCase = true) ||
                serviceName.contains("Suspension", ignoreCase = true) -> Icons.Default.DirectionsCar

        serviceName.contains("Eléctrico", ignoreCase = true) ||
                serviceName.contains("Electrico", ignoreCase = true) -> Icons.Default.ElectricBolt

        serviceName.contains("Diagnóstico", ignoreCase = true) ||
                serviceName.contains("Diagnostico", ignoreCase = true) -> Icons.Default.Search

        serviceName.contains("Pintura", ignoreCase = true) ||
                serviceName.contains("Carrocería", ignoreCase = true) -> Icons.Default.Brush

        serviceName.contains("Limpieza", ignoreCase = true) ||
                serviceName.contains("Lavado", ignoreCase = true) -> Icons.Default.LocalCarWash

        serviceName.contains("Mantenimiento", ignoreCase = true) -> Icons.Default.Handyman

        serviceName.contains("Inspección", ignoreCase = true) ||
                serviceName.contains("Inspeccion", ignoreCase = true) -> Icons.Default.CheckCircle

        else -> Icons.Default.Build // Icono por defecto
    }
}

/**
 * Diálogo de búsqueda moderno
 */
@Composable
fun ModernSearchDialog(
    idBusqueda: String,
    onIdChange: (String) -> Unit,
    onBuscarClick: () -> Unit,
    onDismiss: () -> Unit,
    primaryColor: Color
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    "Consultar Vehículo",
                    color = primaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Ingrese el ID del vehículo para consultar su estado actual:",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                OutlinedTextField(
                    value = idBusqueda,
                    onValueChange = onIdChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Ej: VH001",
                            color = Color.Gray.copy(alpha = 0.6f)
                        )
                    },
                    label = { Text("ID del Vehículo") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        focusedLabelColor = primaryColor,
                        cursorColor = primaryColor
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onBuscarClick,
                enabled = idBusqueda.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                ),
                modifier = Modifier.height(48.dp)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buscar", fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("Cancelar", color = primaryColor)
            }
        }
    )
}