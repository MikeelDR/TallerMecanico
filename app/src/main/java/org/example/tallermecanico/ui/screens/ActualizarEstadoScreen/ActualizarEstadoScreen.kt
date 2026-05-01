package org.example.tallermecanico.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import org.example.tallermecanico.ui.data.repository.NotificacionesRepository
import org.example.tallermecanico.ui.data.repository.VehiculosRepository
import org.example.tallermecanico.viewmodel.TrabajadorViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActualizarEstadoScreen(
    navController: NavHostController,
    trabajadorViewModel: TrabajadorViewModel,
    vehiculoId: String,
    vehiculosRepository: VehiculosRepository,
    notificacionesRepository: NotificacionesRepository
) {
    // Colores del tema
    val primaryColor = Color(0xFF1F41BB)
    val backgroundColor = Color(0xFFF8F9FF)
    val cardColor = Color(0xFFFFFFFF)
    val textSecondaryColor = Color(0xFF555555)
    val pendingColor = Color(0xFFFFA000)
    val acceptedColor = Color(0xFF4CAF50)
    val rejectedColor = Color(0xFFE53935)
    val inProgressColor = Color(0xFF2196F3)
    val completedColor = Color(0xFF4CAF50)

    // Estados para gestionar las citas
    var citas by remember { mutableStateOf<List<Pair<String, CitaTrabajador>>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var filtroEstado by remember { mutableStateOf("Todos") }
    var expandedItemId by remember { mutableStateOf<String?>(null) }
    var showDialogActualizarEstado by remember { mutableStateOf(false) }
    var citaSeleccionadaId by remember { mutableStateOf<String?>(null) }
    var nuevoEstadoSeleccionado by remember { mutableStateOf("") }

    // Estado para búsqueda
    var searchQuery by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Firestore instance
    val db = FirebaseFirestore.getInstance()
    var listener: ListenerRegistration? = null

    // Lista de estados para filtrar
    val estadosFiltro = listOf("Todos", "Pendiente", "Aceptada", "En progreso", "Completada", "Rechazada")

    // Lista de estados para actualizar
    val estadosActualizar = listOf("Pendiente", "Aceptada", "En progreso", "Completada", "Rechazada")

    // Escuchar cambios en tiempo real de Firestore
    LaunchedEffect(filtroEstado, searchQuery) {
        listener?.remove() // Elimina el listener anterior si existe

        var query = db.collection("citas")
            .orderBy("fecha_hora", Query.Direction.DESCENDING)

        // Aplica filtro por estado si no es "Todos"
        if (filtroEstado != "Todos") {
            query = query.whereEqualTo("estado", filtroEstado)
        }

        listener = query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                println("Error al obtener citas: ${exception.localizedMessage}")
                loading = false
                return@addSnapshotListener
            }

            if (snapshot != null) {
                var citasList = snapshot.documents.map { doc ->
                    Pair(doc.id, doc.toObject(CitaTrabajador::class.java) ?: CitaTrabajador())
                }

                // Aplica filtro de búsqueda si hay alguno
                if (searchQuery.isNotEmpty()) {
                    citasList = citasList.filter { (_, cita) ->
                        cita.cliente_nombre.contains(searchQuery, ignoreCase = true) ||
                                cita.placa.contains(searchQuery, ignoreCase = true) ||
                                cita.marca.contains(searchQuery, ignoreCase = true) ||
                                cita.servicio.contains(searchQuery, ignoreCase = true)
                    }
                }

                citas = citasList
                loading = false
            }
        }
    }

    // Para limpiar el listener cuando la composición termine
    DisposableEffect(Unit) {
        onDispose {
            listener?.remove()
        }
    }

    // Función para actualizar el estado de una cita
    fun actualizarEstadoCita(docId: String, nuevoEstado: String) {
        // Obtén la referencia al usuario actual
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            scope.launch {
                snackbarHostState.showSnackbar("Error: Usuario no autenticado")
            }
            return
        }

        // Mostrar información de debug
        println("Actualizando cita $docId con estado $nuevoEstado por usuario ${currentUser.uid}")

        // Obtener información de la cita actual antes de actualizar
        val citaActual = citas.find { it.first == docId }?.second

        db.collection("citas").document(docId)
            .update("estado", nuevoEstado)
            .addOnSuccessListener {
                // Acciones adicionales según el estado
                if (nuevoEstado == "En progreso" && citaActual?.vehiculo_id?.isEmpty() == true) {
                    // Si se cambia a "En progreso" pero no tiene ID de vehículo, mostramos advertencia
                    scope.launch {
                        snackbarHostState.showSnackbar("Advertencia: Esta cita necesita un ID de vehículo")
                    }
                } else if (nuevoEstado == "Completada" && citaActual?.vehiculo_id?.isNotEmpty() == true) {
                    // Si se marca como completada y tiene ID de vehículo, actualizamos su estado
                    db.collection("estados_vehiculos").document(citaActual.vehiculo_id)
                        .update(
                            mapOf(
                                "estadoActual" to "Finalizado",
                                "porcentajeCompletado" to 100,
                                "comentarios" to "Servicio completado satisfactoriamente."
                            )
                        )
                }

                scope.launch {
                    snackbarHostState.showSnackbar("Estado actualizado a: $nuevoEstado")
                }
            }
            .addOnFailureListener { e ->
                println("Error al actualizar: ${e.message}")
                println("Error completo: $e")

                scope.launch {
                    when {
                        e.message?.contains("PERMISSION_DENIED") == true -> {
                            snackbarHostState.showSnackbar("Error de permisos: Verifica que estás autenticado como trabajador")
                        }
                        else -> {
                            snackbarHostState.showSnackbar("Error al actualizar: ${e.message}")
                        }
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Actualizar Estado de Citas")
                },
                actions = {
                    IconButton(onClick = {
                        // Lógica para actualizar/refrescar
                        loading = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar"
                        )
                    }

                    IconButton(onClick = {
                        // Volver a la pantalla anterior
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
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
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar por cliente, placa, marca o servicio") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = textSecondaryColor.copy(alpha = 0.5f)
                )
            )

            // Filtros de estado
            ScrollableTabRow(
                selectedTabIndex = estadosFiltro.indexOf(filtroEstado),
                containerColor = backgroundColor,
                contentColor = primaryColor,
                edgePadding = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                estadosFiltro.forEachIndexed { index, estado ->
                    Tab(
                        selected = filtroEstado == estado,
                        onClick = { filtroEstado = estado },
                        text = {
                            Text(
                                text = estado,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        selectedContentColor = primaryColor,
                        unselectedContentColor = textSecondaryColor
                    )
                }
            }

            // Tarjeta informativa
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = primaryColor.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = primaryColor
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "En esta pantalla puede actualizar el estado de cualquier cita, independientemente de su estado actual.",
                        color = primaryColor
                    )
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
                            text = "Cargando citas...",
                            color = textSecondaryColor
                        )
                    }
                }
            } else if (citas.isEmpty()) {
                // Mensaje sin citas
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
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = textSecondaryColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isEmpty() && filtroEstado == "Todos")
                                "No hay citas registradas"
                            else
                                "No se encontraron citas con los filtros aplicados",
                            color = textSecondaryColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Mostrar las citas
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(citas, key = { it.first }) { (docId, cita) ->
                        val isExpanded = expandedItemId == docId

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedItemId = if (isExpanded) null else docId
                                },
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Cabecera con badge de estado
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${cita.marca} ${cita.modelo}",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = primaryColor
                                        )
                                        Text(
                                            text = "Cliente: ${cita.cliente_nombre}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = textSecondaryColor
                                        )
                                    }

                                    Surface(
                                        color = when(cita.estado) {
                                            "Pendiente" -> pendingColor
                                            "Aceptada" -> acceptedColor
                                            "En progreso" -> inProgressColor
                                            "Completada" -> completedColor
                                            "Rechazada" -> rejectedColor
                                            else -> textSecondaryColor
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.padding(start = 8.dp)
                                    ) {
                                        Text(
                                            text = cita.estado,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Información principal
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(primaryColor.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsCar,
                                            contentDescription = null,
                                            tint = primaryColor
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = "Placa: ${cita.placa}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Fecha: ${cita.fecha_hora}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = textSecondaryColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(acceptedColor.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Handyman,
                                            contentDescription = null,
                                            tint = acceptedColor
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = cita.servicio,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                // Botón para cambiar estado
                                Button(
                                    onClick = {
                                        citaSeleccionadaId = docId
                                        nuevoEstadoSeleccionado = cita.estado
                                        showDialogActualizarEstado = true
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = primaryColor
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Cambiar Estado"
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Cambiar Estado")
                                }

                                // Información adicional expandible
                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = fadeIn(animationSpec = tween(200)) +
                                            expandVertically(animationSpec = tween(200)),
                                    exit = fadeOut(animationSpec = tween(200)) +
                                            shrinkVertically(animationSpec = tween(200))
                                ) {
                                    Column(modifier = Modifier.padding(top = 16.dp)) {
                                        Divider()

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Información de contacto
                                        Text(
                                            text = "Información de contacto",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Email,
                                                contentDescription = null,
                                                tint = textSecondaryColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = cita.cliente_email,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Phone,
                                                contentDescription = null,
                                                tint = textSecondaryColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (cita.telefono.isNotEmpty()) cita.telefono else "No disponible",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // ID del vehículo si existe
                                        if (cita.vehiculo_id.isNotEmpty()) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(inProgressColor.copy(alpha = 0.1f))
                                                    .padding(12.dp)
                                                    .clickable {
                                                        // Navegar a la pantalla de consulta de vehículo con este ID
                                                        navController.navigate("consultaVehiculo/${cita.vehiculo_id}")
                                                    },
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.QrCode,
                                                    contentDescription = null,
                                                    tint = inProgressColor
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = "ID de Vehículo: ${cita.vehiculo_id}",
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                    Text(
                                                        text = "Toca para ver detalles",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = textSecondaryColor
                                                    )
                                                }
                                                Spacer(modifier = Modifier.weight(1f))
                                                Icon(
                                                    imageVector = Icons.Default.ArrowForward,
                                                    contentDescription = null,
                                                    tint = inProgressColor
                                                )
                                            }
                                        } else {
                                            // Mensaje cuando no tiene ID asignado
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(pendingColor.copy(alpha = 0.1f))
                                                    .padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = pendingColor
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Vehículo sin ID asignado. Se asignará un ID cuando la cita esté en progreso.",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = textSecondaryColor
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Historial de cambios de estado (esto sería ideal tenerlo en la DB)
                                        Text(
                                            text = "Estado actual: ${cita.estado}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog para actualizar estado
    if (showDialogActualizarEstado && citaSeleccionadaId != null) {
        val citaActual = citas.find { it.first == citaSeleccionadaId }?.second

        if (citaActual != null) {
            AlertDialog(
                onDismissRequest = { showDialogActualizarEstado = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Update,
                        contentDescription = null,
                        tint = primaryColor
                    )
                },
                title = {
                    Text(
                        text = "Actualizar Estado de Cita",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Vehículo: ${citaActual.marca} ${citaActual.modelo}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = "Cliente: ${citaActual.cliente_nombre}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = "Estado actual: ${citaActual.estado}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Seleccione el nuevo estado:",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Radio buttons para seleccionar el nuevo estado
                        estadosActualizar.forEach { estado ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { nuevoEstadoSeleccionado = estado }
                                    .background(
                                        if (nuevoEstadoSeleccionado == estado) {
                                            when(estado) {
                                                "Pendiente" -> pendingColor.copy(alpha = 0.1f)
                                                "Aceptada" -> acceptedColor.copy(alpha = 0.1f)
                                                "En progreso" -> inProgressColor.copy(alpha = 0.1f)
                                                "Completada" -> completedColor.copy(alpha = 0.1f)
                                                "Rechazada" -> rejectedColor.copy(alpha = 0.1f)
                                                else -> primaryColor.copy(alpha = 0.1f)
                                            }
                                        } else {
                                            Color.Transparent
                                        }
                                    )
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = nuevoEstadoSeleccionado == estado,
                                    onClick = { nuevoEstadoSeleccionado = estado }
                                )

                                val estadoColor = when(estado) {
                                    "Pendiente" -> pendingColor
                                    "Aceptada" -> acceptedColor
                                    "En progreso" -> inProgressColor
                                    "Completada" -> completedColor
                                    "Rechazada" -> rejectedColor
                                    else -> textSecondaryColor
                                }

                                Surface(
                                    color = estadoColor,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(
                                        text = estado,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Advertencias según el estado seleccionado
                        if (nuevoEstadoSeleccionado == "En progreso" && citaActual.vehiculo_id.isEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(pendingColor.copy(alpha = 0.1f))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = pendingColor
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Esta cita no tiene ID de vehículo. Se recomienda asignar uno antes de cambiar el estado a 'En progreso'.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textSecondaryColor
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            actualizarEstadoCita(citaSeleccionadaId!!, nuevoEstadoSeleccionado)
                            showDialogActualizarEstado = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor
                        )
                    ) {
                        Text("Actualizar Estado")
                    }
                },

                dismissButton = {
                    OutlinedButton(
                        onClick = { showDialogActualizarEstado = false },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = primaryColor
                        )
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

