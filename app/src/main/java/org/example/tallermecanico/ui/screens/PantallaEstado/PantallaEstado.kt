package org.example.tallermecanico.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.example.tallermecanico.viewmodel.CitasViewModel
import org.example.tallermecanico.viewmodel.EstadoServicio
import org.example.tallermecanico.viewmodel.EstadoVehiculo

// Definimos los colores de la aplicación con una paleta más moderna
private val AzulPrimario = Color(0xFF0F4C81)
private val AzulSecundario = Color(0xFF3282B8)
private val AzulClaro = Color(0xFFBBE1FA)
private val Blanco = Color(0xFFFFFFFF)
private val GrisClaro = Color(0xFFF5F5F5)
private val GrisOscuro = Color(0xFF424242)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEstadoScreen(
    navController: NavHostController,
    vehiculoId: Int? = null,
    citasViewModel: CitasViewModel = viewModel(),
    serviceId: String = ""
) {
    // Estado para gestionar la carga de datos
    var isLoading by remember { mutableStateOf(vehiculoId != null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Observar el estado del vehículo desde el ViewModel
    val estadoVehiculoStateFlow by citasViewModel.estadoVehiculo.collectAsState()

    // Estado local para la UI
    var estadoVehiculo by remember { mutableStateOf<EstadoVehiculo?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Efecto para cargar el vehículo cuando cambie el ID
    LaunchedEffect(vehiculoId) {
        if (vehiculoId != null && vehiculoId > 0) {
            isLoading = true
            errorMessage = null

            try {
                citasViewModel.obtenerEstadoVehiculoPorId(vehiculoId.toString())
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Error al cargar los datos: ${e.message}"
                isLoading = false
            }
        }
    }

    // Efecto para actualizar el estado local cuando cambie el flujo
    LaunchedEffect(estadoVehiculoStateFlow) {
        estadoVehiculo = estadoVehiculoStateFlow
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (estadoVehiculo != null) "Estado del Vehículo" else "Consulta de Estado",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulPrimario,
                    titleContentColor = Blanco
                ),
                navigationIcon = {
                    if (estadoVehiculo != null) {
                        IconButton(onClick = {
                            estadoVehiculo = null
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = Blanco
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(GrisClaro)
        ) {
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AzulPrimario,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = !isLoading && errorMessage != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut()
            ) {
                ErrorMessage(
                    message = errorMessage ?: "Ha ocurrido un error",
                    onDismiss = { errorMessage = null }
                )
            }

            AnimatedVisibility(
                visible = !isLoading && errorMessage == null && estadoVehiculo != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }, animationSpec = tween(300)),
                exit = fadeOut()
            ) {
                estadoVehiculo?.let {
                    MostrarEstadoVehiculo(
                        estadoVehiculo = it,
                        onBackClick = {
                            estadoVehiculo = null
                        }
                    )
                }
            }

            AnimatedVisibility(
                visible = !isLoading && errorMessage == null && estadoVehiculo == null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ContenidoGeneralEstado(
                    navController = navController,
                    onBuscarClick = { id ->
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                errorMessage = null
                                // En lugar de navegar, actualizamos el estado local
                                citasViewModel.obtenerEstadoVehiculoPorId(id)
                                isLoading = false
                            } catch (e: Exception) {
                                errorMessage = "No se encontró el vehículo con ID: $id"
                                isLoading = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(
                containerColor = Blanco
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulPrimario
                    )
                ) {
                    Text("Entendido")
                }
            }
        }
    }
}

@Composable
fun MostrarEstadoVehiculo(
    estadoVehiculo: EstadoVehiculo,
    onBackClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            EstadoCard(estadoVehiculo)
        }

        item {
            InformacionCard(estadoVehiculo)
        }

        item {
            ServiciosCard(estadoVehiculo)
        }
    }
}

@Composable
fun EstadoCard(estadoVehiculo: EstadoVehiculo) {
    // Configuración del estado y sus colores visuales
    val estadoColor = when (estadoVehiculo.estado) {
        EstadoServicio.PENDIENTE -> Color(0xFFFFA000)
        EstadoServicio.EN_PROCESO -> AzulSecundario
        EstadoServicio.COMPLETADO -> Color(0xFF4CAF50)
        EstadoServicio.CANCELADO -> Color(0xFFF44336)
    }

    val estadoTexto = when (estadoVehiculo.estado) {
        EstadoServicio.PENDIENTE -> "Pendiente"
        EstadoServicio.EN_PROCESO -> "En Proceso"
        EstadoServicio.COMPLETADO -> "Completado"
        EstadoServicio.CANCELADO -> "Cancelado"
    }

    val estadoIcon = when (estadoVehiculo.estado) {
        EstadoServicio.PENDIENTE -> Icons.Rounded.CalendarToday
        EstadoServicio.EN_PROCESO -> Icons.Rounded.Build
        EstadoServicio.COMPLETADO -> Icons.Rounded.DirectionsCar
        EstadoServicio.CANCELADO -> Icons.Rounded.Info
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "ID del Vehículo: ${estadoVehiculo.idSeguimiento}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AzulPrimario
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(estadoColor.copy(alpha = 0.15f))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(estadoColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = estadoIcon,
                        contentDescription = estadoTexto,
                        tint = estadoColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Estado Actual",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrisOscuro
                    )

                    Text(
                        text = estadoTexto,
                        style = MaterialTheme.typography.titleLarge,
                        color = estadoColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InformacionCard(estadoVehiculo: EstadoVehiculo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Información",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AzulPrimario
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoItem(
                titulo = "Descripción:",
                contenido = estadoVehiculo.descripcion
            )

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = GrisClaro
            )

            InfoItem(
                titulo = "Fecha de Ingreso:",
                contenido = estadoVehiculo.fechaIngreso
            )

            Spacer(modifier = Modifier.height(8.dp))

            InfoItem(
                titulo = "Fecha Estimada de Entrega:",
                contenido = estadoVehiculo.fechaEstimadaEntrega,
                destacado = true
            )
        }
    }
}

@Composable
fun InfoItem(
    titulo: String,
    contenido: String,
    destacado: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodyMedium,
            color = GrisOscuro
        )

        Text(
            text = contenido,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal,
            color = if (destacado) AzulPrimario else Color.Black
        )
    }
}

@Composable
fun ServiciosCard(estadoVehiculo: EstadoVehiculo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Servicios Realizados",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AzulPrimario
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (estadoVehiculo.servicios.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No hay servicios registrados",
                        style = MaterialTheme.typography.bodyLarge,
                        color = GrisOscuro
                    )
                }
            } else {
                estadoVehiculo.servicios.forEachIndexed { index, servicio ->
                    ServicioItem(servicio = servicio, index = index)

                    if (index < estadoVehiculo.servicios.size - 1) {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = GrisClaro
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServicioItem(servicio: String, index: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Círculo con número
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(AzulSecundario),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${index + 1}",
                color = Blanco,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = servicio,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoGeneralEstado(
    navController: NavHostController,
    onBuscarClick: (String) -> Unit
) {
    var inputId by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Imagen o icono grande
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AzulPrimario),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.DirectionsCar,
                contentDescription = "Vehículo",
                tint = Blanco,
                modifier = Modifier.size(80.dp)
            )
        }

        // Tarjeta de búsqueda
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Blanco)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Consulta el estado de tu vehículo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AzulPrimario,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Ingresa el ID proporcionado al dejar tu vehículo",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = GrisOscuro
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = inputId,
                    onValueChange = {
                        inputId = it
                        inputError = null
                    },
                    label = { Text("ID del vehículo") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = AzulPrimario
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzulPrimario,
                        unfocusedBorderColor = AzulClaro,
                        cursorColor = AzulPrimario,
                        focusedLeadingIconColor = AzulPrimario,
                        unfocusedLeadingIconColor = AzulSecundario
                    ),
                    isError = inputError != null,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (inputError != null) {
                    Text(
                        text = inputError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (inputId.isBlank()) {
                            inputError = "Por favor, ingresa un ID"
                            return@Button
                        }

                        val id = inputId.toIntOrNull()
                        if (id == null) {
                            inputError = "El ID debe ser un número"
                            return@Button
                        }

                        // Llamamos a la función proporcionada
                        onBuscarClick(inputId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulPrimario,
                        contentColor = Blanco
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Consultar Estado",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        // Tarjeta de información
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = Blanco)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InfoIconItem(
                    icon = Icons.Rounded.Info,
                    titulo = "¿Necesitas ayuda?",
                    descripcion = "Si no recuerdas tu ID de consulta o tienes algún problema, contacta con nuestro servicio de atención al cliente."
                )
            }
        }
    }
}

@Composable
fun InfoIconItem(
    icon: ImageVector,
    titulo: String,
    descripcion: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AzulClaro),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = titulo,
                tint = AzulPrimario,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = AzulPrimario
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = GrisOscuro
            )
        }
    }
}