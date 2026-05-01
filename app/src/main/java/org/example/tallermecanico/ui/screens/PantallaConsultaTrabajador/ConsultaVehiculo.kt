package org.example.tallermecanico.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import org.example.tallermecanico.ui.data.repository.VehiculosRepository

// Modelo de datos para el estado del vehículo
data class EstadoVehiculo(
    val id: String = "",
    val marca: String = "",
    val modelo: String = "",
    val anio: String = "",
    val placa: String = "",
    val servicio: String = "",
    val estadoActual: String = "",
    val porcentajeCompletado: Int = 0,
    val diagnostico: String = "",
    val reparacionesRealizadas: String = "",
    val piezasReemplazadas: String = "",
    val comentarios: String = "",
    val fechaIngreso: String = "",
    val cliente_uid: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultaVehiculoScreen(
    navController: NavHostController,
    vehiculoId: String? = null,
    vehiculosRepository: VehiculosRepository,
    onEditarEstado: (String) -> Unit
) {
    // Colores del tema
    val primaryColor = Color(0xFF1F41BB)
    val backgroundColor = Color(0xFFF8F9FF)
    val cardColor = Color(0xFFFFFFFF)
    val textSecondaryColor = Color(0xFF555555)
    val accentColor = Color(0xFF4CAF50)
    val errorColor = Color(0xFFE53935)

    // Estados
    var idVehiculo by remember { mutableStateOf(vehiculoId ?: "") }
    var estadoVehiculo by remember { mutableStateOf<EstadoVehiculo?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var vehiculoEncontrado by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    // Función para buscar vehículo
    fun buscarVehiculo(id: String) {
        if (id.isBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar("Por favor ingrese un ID de vehículo")
            }
            return
        }

        // Verifica si el formato del ID coincide con el esperado
        // Modificado para aceptar mayúsculas y minúsculas
        val idRegex = Regex("[A-Za-z][A-Za-z]-[a-zA-Z0-9]{6}")
        if (!id.matches(idRegex)) {
            scope.launch {
                snackbarHostState.showSnackbar("ID inválido. El formato debe ser XX-XXXXXX")
            }
            return
        }

        loading = true
        error = null
        vehiculoEncontrado = false

        FirebaseFirestore.getInstance().collection("estados_vehiculos")
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                loading = false
                if (document != null && document.exists()) {
                    val estado = document.toObject(EstadoVehiculo::class.java)
                    if (estado != null) {
                        estadoVehiculo = estado
                        vehiculoEncontrado = true
                    } else {
                        error = "Error al procesar los datos del vehículo"
                    }
                } else {
                    error = "No se encontró ningún vehículo con el ID proporcionado"
                }
                focusManager.clearFocus()
            }
            .addOnFailureListener { e ->
                loading = false
                error = "Error al buscar: ${e.message}"
                focusManager.clearFocus()
            }
    }

    // Efecto para buscar si hay un ID preestablecido
    LaunchedEffect(vehiculoId) {
        if (!vehiculoId.isNullOrBlank()) {
            idVehiculo = vehiculoId
            buscarVehiculo(vehiculoId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Consulta de Vehículo")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Búsqueda de vehículo
            OutlinedTextField(
                value = idVehiculo,
                onValueChange = { idVehiculo = it },  // Eliminado uppercase para permitir minúsculas
                label = { Text("ID del Vehículo") },
                placeholder = { Text("ej: MM-123abc") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = primaryColor
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { buscarVehiculo(idVehiculo) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = primaryColor
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        buscarVehiculo(idVehiculo)
                    }
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = textSecondaryColor.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar carga
            AnimatedVisibility(
                visible = loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = primaryColor,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Buscando vehículo...",
                        color = textSecondaryColor
                    )
                }
            }

            // Mostrar error
            AnimatedVisibility(
                visible = error != null && !loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = errorColor,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error ?: "Error desconocido",
                        color = errorColor,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (idVehiculo.isNotEmpty()) {
                                buscarVehiculo(idVehiculo)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor
                        )
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reintentar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reintentar")
                    }
                }
            }

            // Vista vacía (si no hay búsqueda todavía)
            AnimatedVisibility(
                visible = !loading && estadoVehiculo == null && error == null && vehiculoId.isNullOrEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = textSecondaryColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ingrese un ID de vehículo para consultar",
                        color = textSecondaryColor,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Mostrar información del vehículo
            AnimatedVisibility(
                visible = vehiculoEncontrado && estadoVehiculo != null && !loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Información del vehículo
                    InformacionCard(
                        titulo = "Información del Vehículo",
                        icono = Icons.Default.DirectionsCar,
                        iconoColor = primaryColor
                    ) {
                        InfoRow("ID", estadoVehiculo?.id ?: "")
                        InfoRow("Marca", estadoVehiculo?.marca ?: "")
                        InfoRow("Modelo", estadoVehiculo?.modelo ?: "")
                        InfoRow("Año", estadoVehiculo?.anio ?: "")
                        InfoRow("Placa", estadoVehiculo?.placa ?: "")
                        InfoRow("Servicio", estadoVehiculo?.servicio ?: "")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Estado actual
                    InformacionCard(
                        titulo = "Estado Actual",
                        icono = Icons.Default.BuildCircle,
                        iconoColor = accentColor
                    ) {
                        Text(
                            text = estadoVehiculo?.estadoActual ?: "Sin información",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        InfoRow("Ingreso", estadoVehiculo?.fechaIngreso ?: "")

                        Text(
                            text = "Progreso: ${estadoVehiculo?.porcentajeCompletado ?: 0}%",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        LinearProgressIndicator(
                            progress = (estadoVehiculo?.porcentajeCompletado ?: 0).toFloat() / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = accentColor,
                            trackColor = accentColor.copy(alpha = 0.2f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Detalles del servicio
                    InformacionCard(
                        titulo = "Detalles del Servicio",
                        icono = Icons.Default.Handyman,
                        iconoColor = primaryColor
                    ) {
                        // Diagnóstico
                        if (!estadoVehiculo?.diagnostico.isNullOrBlank()) {
                            SeccionDetalle("Diagnóstico", estadoVehiculo?.diagnostico ?: "")
                        }

                        // Reparaciones
                        if (!estadoVehiculo?.reparacionesRealizadas.isNullOrBlank()) {
                            SeccionDetalle("Reparaciones Realizadas", estadoVehiculo?.reparacionesRealizadas ?: "")
                        }

                        // Piezas
                        if (!estadoVehiculo?.piezasReemplazadas.isNullOrBlank()) {
                            SeccionDetalle("Piezas Reemplazadas", estadoVehiculo?.piezasReemplazadas ?: "")
                        }

                        // Comentarios
                        SeccionDetalle(
                            "Comentarios Adicionales",
                            if (estadoVehiculo?.comentarios.isNullOrBlank()) "Sin comentarios adicionales" else estadoVehiculo?.comentarios ?: ""
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                }
            }
        }
    }
}

@Composable
fun InformacionCard(
    titulo: String,
    icono: ImageVector,
    iconoColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = iconoColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = titulo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconoColor
                )
            }

            // Línea divisoria
            Divider(
                color = iconoColor.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF555555),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun SeccionDetalle(titulo: String, contenido: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = contenido,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFFDDDDDD),
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    color = Color(0xFFF9F9F9),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        )
    }
}

@Composable
fun InfoField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF555555)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}