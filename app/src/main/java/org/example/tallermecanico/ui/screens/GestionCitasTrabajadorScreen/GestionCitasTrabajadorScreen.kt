package org.example.tallermecanico.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import org.example.tallermecanico.viewmodel.TrabajadorViewModel
import java.util.UUID
import org.example.tallermecanico.navigation.AppScreens

// Modelo de datos para Cita con información ampliada para el trabajador
data class CitaTrabajador(
    val id: String = "",
    val cliente_uid: String = "",
    val cliente_email: String = "",
    val cliente_nombre: String = "",
    val servicio: String = "",
    val fecha_hora: String = "",
    val estado: String = "Pendiente",
    val marca: String = "",
    val modelo: String = "",
    val año: String = "",
    val placa: String = "",
    val vehiculo_id: String = "",
    val telefono: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionCitasTrabajadorScreen(
    navController: NavHostController,
    trabajadorViewModel: TrabajadorViewModel
) {
    // Colores del tema
    val primaryColor = Color(0xFF1F41BB)
    val backgroundColor = Color(0xFFF8F9FF)
    val cardColor = Color(0xFFFFFFFF)
    val textSecondaryColor = Color(0xFF555555)
    val accentColor = Color(0xFF4CAF50)
    val pendingColor = Color(0xFFFFA000)
    val acceptedColor = Color(0xFF4CAF50)
    val rejectedColor = Color(0xFFE53935)
    val inProgressColor = Color(0xFF2196F3)

    // Estados para gestionar las citas
    var citas by remember { mutableStateOf<List<Pair<String, CitaTrabajador>>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var filtroEstado by remember { mutableStateOf("Todos") }
    var citaSeleccionadaId by remember { mutableStateOf<String?>(null) }
    var showDialogEditar by remember { mutableStateOf(false) }
    var showDialogAsignarID by remember { mutableStateOf(false) }
    var showDialogEliminar by remember { mutableStateOf(false) }
    var showCommentDialog by remember { mutableStateOf(false) }
    var comentarioAdicional by remember { mutableStateOf("") }
    var estadoSeleccionado by remember { mutableStateOf("") }
    var showProgressDialog by remember { mutableStateOf(false) }
    var progresoActual by remember { mutableStateOf(0) }
    var estadoActualVehiculo by remember { mutableStateOf("") }

    // Estado para seguimiento expandido
    var expandedItemId by remember { mutableStateOf<String?>(null) }

    // Estado para búsqueda
    var searchQuery by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Firestore instance
    val db = FirebaseFirestore.getInstance()
    var listener: ListenerRegistration? = null
    var userAuthenticated by remember { mutableStateOf(false) }
    var userRole by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }

    // Lista de estados para filtrar
    val estadosFiltro = listOf("Todos", "Pendiente", "Aceptada", "En progreso", "Completada", "Rechazada")

    // Lista de servicios mejorada con información
    val serviciosDisponibles = listOf(
        "Mantenimiento Preventivo - Revisión general del vehículo",
        "Cambio de Aceite - Sustitución de aceite y filtro",
        "Frenos - Reparación o sustitución de pastillas/discos",
        "Suspensión - Ajuste y reparación de amortiguadores",
        "Dirección - Alineación y balanceo",
        "Sistema Eléctrico - Diagnóstico y reparación",
        "Aire Acondicionado - Recarga y reparación",
        "Motor - Diagnóstico y reparación de averías",
        "Transmisión - Reparación y mantenimiento",
        "Neumáticos - Cambio y reparación",
        "Diagnóstico Computarizado - Análisis detallado con herramientas electrónicas",
        "Carrocería - Reparación de daños"
    )
    // Verifica la autenticación en LaunchedEffect
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            userId = currentUser.uid
            userAuthenticated = true

            // Verificar rol de usuario
            FirebaseFirestore.getInstance().collection("usuarios")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userRole = document.getString("rol") ?: ""
                        // Mostrar en consola para debugging
                        println("Usuario autenticado: $userId con rol: $userRole")
                    }
                }
        } else {
            // Usuario no autenticado
            println("Usuario no autenticado")
        }
    }

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

        fun actualizarProgresoVehiculo(vehiculoId: String, nuevoEstado: String, nuevoProgreso: Int) {
            // Aquí va la lógica para actualizar el progreso del vehículo en Firestore
            db.collection("estados_vehiculos").document(vehiculoId)
                .update(
                    mapOf(
                        "estado" to nuevoEstado,
                        "progreso" to nuevoProgreso
                    )
                )
                .addOnSuccessListener {
                    scope.launch {
                        snackbarHostState.showSnackbar("Progreso del vehículo actualizado")
                    }
                }
                .addOnFailureListener { e ->
                    scope.launch {
                        snackbarHostState.showSnackbar("Error al actualizar progreso: ${e.message}")
                    }
                }
        }


        // Mostrar información de debug
        println("Actualizando cita $docId con estado $nuevoEstado por usuario ${currentUser.uid}")

        // Mostrar indicador de carga
        loading = true

        db.collection("citas").document(docId)
            .update("estado", nuevoEstado)
            .addOnSuccessListener {
                scope.launch {
                    snackbarHostState.showSnackbar("Estado actualizado a: $nuevoEstado")
                }

                // Si el estado cambia a "Completada", actualizar también el estado del vehículo
                if (nuevoEstado == "Completada") {
                    val citaActual = citas.find { it.first == docId }?.second
                    if (citaActual != null && citaActual.vehiculo_id.isNotEmpty()) {
                        actualizarProgresoVehiculo(citaActual.vehiculo_id, "Completado", 100)
                    }
                }

                loading = false
            }
            .addOnFailureListener { e ->
                loading = false
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

    // Función para asignar un ID de vehículo a una cita
    fun asignarIdVehiculo(docId: String, vehiculoId: String) {
        db.collection("citas").document(docId)
            .update("vehiculo_id", vehiculoId)
            .addOnSuccessListener {
                // Creamos el registro de estado del vehículo en la colección correspondiente
                val citaActual = citas.find { it.first == docId }?.second

                if (citaActual != null) {
                    val estadoVehiculo = hashMapOf(
                        "id" to vehiculoId,
                        "marca" to citaActual.marca,
                        "modelo" to citaActual.modelo,
                        "anio" to citaActual.año,
                        "placa" to citaActual.placa,
                        "servicio" to citaActual.servicio,
                        "estadoActual" to "En diagnóstico",
                        "porcentajeCompletado" to 10,
                        "diagnostico" to "",
                        "reparacionesRealizadas" to "",
                        "piezasReemplazadas" to "",
                        "comentarios" to "Vehículo recibido. Pendiente de diagnóstico detallado.",
                        "fechaIngreso" to citaActual.fecha_hora,
                        "cliente_uid" to citaActual.cliente_uid
                    )

                    db.collection("estados_vehiculos").document(vehiculoId)
                        .set(estadoVehiculo)
                        .addOnSuccessListener {
                            scope.launch {
                                snackbarHostState.showSnackbar("ID de vehículo asignado: $vehiculoId")
                            }
                            // Actualizamos también el estado de la cita a "En progreso"
                            actualizarEstadoCita(docId, "En progreso")
                        }
                        .addOnFailureListener { e ->
                            scope.launch {
                                snackbarHostState.showSnackbar("Error al crear registro: ${e.message}")
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                scope.launch {
                    snackbarHostState.showSnackbar("Error al asignar ID: ${e.message}")
                }
            }
    }

    // Función para guardar los cambios de una cita editada
    fun guardarCambiosCita(docId: String, citaActualizada: CitaTrabajador) {
        db.collection("citas").document(docId)
            .set(citaActualizada)
            .addOnSuccessListener {
                scope.launch {
                    snackbarHostState.showSnackbar("Cita actualizada correctamente")
                }
                showDialogEditar = false
            }
            .addOnFailureListener { e ->
                scope.launch {
                    snackbarHostState.showSnackbar("Error al actualizar: ${e.message}")
                }
            }
    }

    fun actualizarComentarioVehiculo(vehiculoId: String, comentario: String) {
        if (vehiculoId.isEmpty()) {
            scope.launch {
                snackbarHostState.showSnackbar("Error: No hay ID de vehículo asignado")
            }
            return
        }

        // Mostrar un indicador de carga
        loading = true

        db.collection("estados_vehiculos").document(vehiculoId)
            .update("comentarios", comentario)
            .addOnSuccessListener {
                scope.launch {
                    snackbarHostState.showSnackbar("Comentario actualizado correctamente")
                }
                showCommentDialog = false

                // Actualizar los comentarios en la vista
                db.collection("estados_vehiculos").document(vehiculoId)
                    .get()
                    .addOnSuccessListener { document ->
                        loading = false
                    }
            }
            .addOnFailureListener { e ->
                loading = false
                scope.launch {
                    snackbarHostState.showSnackbar("Error al actualizar comentario: ${e.message}")
                }
            }
    }


    fun actualizarProgresoVehiculo(vehiculoId: String, estado: String, porcentaje: Int) {
        if (vehiculoId.isEmpty()) {
            scope.launch {
                snackbarHostState.showSnackbar("Error: No hay ID de vehículo asignado")
            }
            return
        }

        // Mostrar un indicador de carga
        loading = true

        val updates = hashMapOf<String, Any>(
            "estadoActual" to estado,
            "porcentajeCompletado" to porcentaje
        )

        db.collection("estados_vehiculos").document(vehiculoId)
            .update(updates)
            .addOnSuccessListener {
                // Si el porcentaje es 100%, actualizar también el estado de la cita a "Completada"
                if (porcentaje >= 100) {
                    // Buscar el ID de la cita correspondiente a este vehículo
                    citas.find { it.second.vehiculo_id == vehiculoId }?.let { (docId, _) ->
                        actualizarEstadoCita(docId, "Completada")
                    }
                }

                scope.launch {
                    snackbarHostState.showSnackbar("Estado y progreso actualizados")
                }
                loading = false
            }
            .addOnFailureListener { e ->
                loading = false
                scope.launch {
                    snackbarHostState.showSnackbar("Error al actualizar progreso: ${e.message}")
                }
            }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Gestión de Citas")
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
                                            "Completada" -> acceptedColor
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
                                            .background(accentColor.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Handyman,
                                            contentDescription = null,
                                            tint = accentColor
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = cita.servicio,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

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

                                    // Estado actual y progreso (similar a la pantalla de consulta de vehículo)
                                    if (cita.estado == "En progreso" || cita.estado == "Completada") {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                            colors = CardDefaults.cardColors(containerColor = cardColor)
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(40.dp)
                                                            .clip(CircleShape)
                                                            .background(accentColor.copy(alpha = 0.1f)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Build,
                                                            contentDescription = null,
                                                            tint = accentColor
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.width(12.dp))

                                                    Text(
                                                        text = "Estado Actual",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(12.dp))

                                                // Obtener progreso desde Firestore
                                                var estadoActual by remember { mutableStateOf("En diagnóstico") }
                                                var progreso by remember { mutableStateOf(10) }

                                                // Consultar estado actual y progreso
                                                LaunchedEffect(docId) {
                                                    if (cita.vehiculo_id.isNotEmpty()) {
                                                        db.collection("estados_vehiculos").document(cita.vehiculo_id)
                                                            .get()
                                                            .addOnSuccessListener { document ->
                                                                if (document != null && document.exists()) {
                                                                    estadoActual = document.getString("estadoActual") ?: "En diagnóstico"
                                                                    progreso = (document.getLong("porcentajeCompletado") ?: 10).toInt()
                                                                }
                                                            }
                                                    }
                                                }

                                                Text(
                                                    text = estadoActual,
                                                    style = MaterialTheme.typography.headlineSmall,
                                                    color = when(estadoActual) {
                                                        "En diagnóstico" -> pendingColor
                                                        "En reparación" -> inProgressColor
                                                        "En pruebas" -> inProgressColor.copy(alpha = 0.7f)
                                                        "Completado" -> acceptedColor
                                                        else -> textSecondaryColor
                                                    },
                                                    fontWeight = FontWeight.Bold
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                // Barra de progreso
                                                LinearProgressIndicator(
                                                    progress = progreso / 100f, // ← Valor directamente, no lambda
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(12.dp)
                                                        .clip(RoundedCornerShape(6.dp)),
                                                    color = when {
                                                        progreso < 30 -> pendingColor
                                                        progreso < 70 -> inProgressColor
                                                        else -> acceptedColor
                                                    },
                                                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                                                )

                                                Spacer(modifier = Modifier.height(4.dp))

                                                Text(
                                                    text = "Progreso: $progreso%",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = textSecondaryColor,
                                                    modifier = Modifier.align(Alignment.End)
                                                )

                                                if (cita.estado == "En progreso") {
                                                    Spacer(modifier = Modifier.height(12.dp))

                                                    // Opciones para actualizar estado y progreso
                                                    // Reemplazar los botones de actualización de estado y progreso con:
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        OutlinedButton(
                                                            onClick = {
                                                                citaSeleccionadaId = docId
                                                                showProgressDialog = true // Mostrar nuevo diálogo de progreso
                                                            },
                                                            border = BorderStroke(1.dp, primaryColor),
                                                            modifier = Modifier.weight(1f)
                                                        ) {
                                                            Text("Editar estado/progreso")
                                                        }

                                                        Spacer(modifier = Modifier.width(8.dp))

                                                        Row(
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            Button(
                                                                onClick = {
                                                                    val nuevoProgreso = when (progreso) {
                                                                        in 0..30 -> 40
                                                                        in 31..60 -> 75
                                                                        in 61..90 -> 100
                                                                        else -> 100
                                                                    }
                                                                    val nuevoEstado = when (nuevoProgreso) {
                                                                        in 0..30 -> "En diagnóstico"
                                                                        in 31..60 -> "En reparación"
                                                                        in 61..90 -> "En pruebas"
                                                                        else -> "Completado"
                                                                    }
                                                                    actualizarProgresoVehiculo(cita.vehiculo_id, nuevoEstado, nuevoProgreso)
                                                                },
                                                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                                                modifier = Modifier.weight(1f)
                                                            ) {
                                                                Text("Avanzar progreso")
                                                            }

                                                            Button(
                                                                onClick = {
                                                                    val comentarios = ""
                                                                    comentarioAdicional = comentarios
                                                                    citaSeleccionadaId = docId
                                                                    showCommentDialog = true
                                                                },
                                                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                                                modifier = Modifier.weight(1f)
                                                            ) {
                                                                Text("Editar comentarios")
                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // ID del vehículo si existe
                                    if (cita.vehiculo_id.isNotEmpty()) {
                                        var comentarios by remember { mutableStateOf("") }

                                        // Cargar comentarios desde Firestore
                                        LaunchedEffect(docId) {
                                            db.collection("estados_vehiculos").document(cita.vehiculo_id)
                                                .get()
                                                .addOnSuccessListener { document ->
                                                    if (document != null && document.exists()) {
                                                        comentarios = document.getString("comentarios") ?: "Vehículo recibido. Pendiente de diagnóstico detallado."
                                                    }
                                                }
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(
                                                    width = 1.dp,
                                                    color = primaryColor.copy(alpha = 0.3f),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .padding(12.dp)
                                                .clickable {
                                                    // Navegar a la pantalla de consulta de vehículo con este ID
                                                    navController.navigate(
                                                        AppScreens.ConsultaVehiculoScreen.createRoute(cita.vehiculo_id)
                                                    )
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.QrCode,
                                                contentDescription = null,
                                                tint = primaryColor
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
                                                tint = primaryColor
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Comentarios adicionales
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                            colors = CardDefaults.cardColors(containerColor = cardColor)
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Comentarios Adicionales",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )

                                                    if (cita.estado == "En progreso") {
                                                        IconButton(
                                                            onClick = {
                                                                comentarioAdicional = comentarios
                                                                showCommentDialog = true
                                                            }
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Edit,
                                                                contentDescription = "Editar comentarios",
                                                                tint = primaryColor
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Text(
                                                    text = comentarios,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = textSecondaryColor,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .border(
                                                            width = 1.dp,
                                                            color = Color.LightGray.copy(alpha = 0.5f),
                                                            shape = RoundedCornerShape(8.dp)
                                                        )
                                                        .padding(12.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))
                                    }

                                    // Botones de acción según el estado
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        when (cita.estado) {
                                            "Pendiente" -> {
                                                // Para citas pendientes: Aceptar, Rechazar, Editar
                                                Button(
                                                    onClick = {
                                                        actualizarEstadoCita(docId, "Aceptada")
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = acceptedColor
                                                    )
                                                ) {
                                                    Text("Aceptar")
                                                }

                                                Button(
                                                    onClick = {
                                                        actualizarEstadoCita(docId, "Rechazada")
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = rejectedColor
                                                    )
                                                ) {
                                                    Text("Rechazar")
                                                }

                                                Button(
                                                    onClick = {
                                                        citaSeleccionadaId = docId
                                                        showDialogEditar = true
                                                    }
                                                ) {
                                                    Text("Editar")
                                                }
                                            }
                                            "Aceptada" -> {
                                                // Para citas aceptadas: Asignar ID, Editar
                                                Button(
                                                    onClick = {
                                                        citaSeleccionadaId = docId
                                                        showDialogAsignarID = true
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = inProgressColor
                                                    )
                                                ) {
                                                    Text("Asignar ID")
                                                }

                                                Button(
                                                    onClick = {
                                                        citaSeleccionadaId = docId
                                                        showDialogEditar = true
                                                    }
                                                ) {
                                                    Text("Editar")
                                                }
                                            }


                                            "En progreso" -> {
                                                // Para citas en progreso: Completar, Ver detalles
                                                Button(
                                                    onClick = {
                                                        actualizarEstadoCita(docId, "Completada")
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = acceptedColor
                                                    )
                                                ) {
                                                    Text("Completar")
                                                }

                                                Button(
                                                    onClick = {
                                                        // Navegar a la pantalla de consulta de vehículo
                                                        navController.navigate("consultaVehiculo/${cita.vehiculo_id}")
                                                    }
                                                ) {
                                                    Text("Ver detalles")
                                                }
                                            }
                                            "Completada", "Rechazada" -> {
                                                // Para citas completadas o rechazadas: Editar
                                                Button(
                                                    onClick = {
                                                        citaSeleccionadaId = docId
                                                        showDialogEditar = true
                                                    }
                                                ) {
                                                    Text("Editar")
                                                }

                                                // Si hay un ID de vehículo, también mostramos botón para ver detalles
                                                if (cita.vehiculo_id.isNotEmpty()) {
                                                    Button(
                                                        onClick = {
                                                            navController.navigate("consultaVehiculo/${cita.vehiculo_id}")
                                                        }
                                                    ) {
                                                        Text("Ver vehículo")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                // Dialog para editar comentarios
                                if (showCommentDialog && citaSeleccionadaId != null) {
                                    val citaActual = citas.find { it.first == citaSeleccionadaId }?.second ?: CitaTrabajador()

                                    // Cargar comentarios actuales al abrir el diálogo
                                    LaunchedEffect(Unit) {
                                        if (citaActual.vehiculo_id.isNotEmpty()) {
                                            db.collection("estados_vehiculos").document(citaActual.vehiculo_id)
                                                .get()
                                                .addOnSuccessListener { document ->
                                                    if (document != null && document.exists()) {
                                                        comentarioAdicional = document.getString("comentarios") ?: ""
                                                    }
                                                }
                                        }
                                    }

                                    Dialog(onDismissRequest = { showCommentDialog = false }) {
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = Color.White,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                    .fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "Comentarios Adicionales",
                                                    style = MaterialTheme.typography.headlineSmall,
                                                    fontWeight = FontWeight.Bold
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                Text(
                                                    text = "Vehículo: ${citaActual.marca} ${citaActual.modelo} (ID: ${citaActual.vehiculo_id})",
                                                    style = MaterialTheme.typography.bodyLarge
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                OutlinedTextField(
                                                    value = comentarioAdicional,
                                                    onValueChange = { comentarioAdicional = it },
                                                    label = { Text("Comentarios") },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(150.dp),
                                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                                        focusedBorderColor = primaryColor,
                                                        unfocusedBorderColor = textSecondaryColor.copy(alpha = 0.5f)
                                                    ),
                                                    textStyle = MaterialTheme.typography.bodyMedium
                                                )

                                                Spacer(modifier = Modifier.height(24.dp))

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.End
                                                ) {
                                                    TextButton(
                                                        onClick = { showCommentDialog = false }
                                                    ) {
                                                        Text("Cancelar")
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Button(
                                                        onClick = {
                                                            actualizarComentarioVehiculo(citaActual.vehiculo_id, comentarioAdicional)
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = primaryColor
                                                        )
                                                    ) {
                                                        Text("Guardar")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // Dialog para editar progreso y estado
                                if (showProgressDialog && citaSeleccionadaId != null) {
                                    val citaActual = citas.find { it.first == citaSeleccionadaId }?.second ?: CitaTrabajador()

                                    // Cargar valores actuales al abrir el diálogo
                                    LaunchedEffect(Unit) {
                                        if (citaActual.vehiculo_id.isNotEmpty()) {
                                            db.collection("estados_vehiculos").document(citaActual.vehiculo_id)
                                                .get()
                                                .addOnSuccessListener { document ->
                                                    if (document != null && document.exists()) {
                                                        estadoActualVehiculo = document.getString("estadoActual") ?: "En diagnóstico"
                                                        progresoActual = (document.getLong("porcentajeCompletado") ?: 10).toInt()
                                                    }
                                                }
                                        }
                                    }

                                    Dialog(onDismissRequest = { showProgressDialog = false }) {
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = Color.White,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                    .fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "Actualizar Progreso",
                                                    style = MaterialTheme.typography.headlineSmall,
                                                    fontWeight = FontWeight.Bold
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                Text(
                                                    text = "Vehículo: ${citaActual.marca} ${citaActual.modelo} (ID: ${citaActual.vehiculo_id})",
                                                    style = MaterialTheme.typography.bodyLarge
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                // Selector de estado
                                                var estadoExpanded by remember { mutableStateOf(false) }
                                                val estadosVehiculo = listOf("En diagnóstico", "En reparación", "En pruebas", "Completado")

                                                ExposedDropdownMenuBox(
                                                    expanded = estadoExpanded,
                                                    onExpandedChange = { estadoExpanded = !estadoExpanded },
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    OutlinedTextField(
                                                        value = estadoActualVehiculo,
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("Estado del vehículo") },
                                                        trailingIcon = {
                                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoExpanded)
                                                        },
                                                        modifier = Modifier
                                                            .menuAnchor()
                                                            .fillMaxWidth(),
                                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                                            focusedBorderColor = primaryColor,
                                                            unfocusedBorderColor = textSecondaryColor.copy(alpha = 0.5f)
                                                        )
                                                    )
                                                    ExposedDropdownMenu(
                                                        expanded = estadoExpanded,
                                                        onDismissRequest = { estadoExpanded = false },
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        estadosVehiculo.forEach { estado ->
                                                            DropdownMenuItem(
                                                                text = { Text(estado) },
                                                                onClick = {
                                                                    estadoActualVehiculo = estado
                                                                    // Ajustar progreso según el estado seleccionado
                                                                    progresoActual = when(estado) {
                                                                        "En diagnóstico" -> 25
                                                                        "En reparación" -> 50
                                                                        "En pruebas" -> 75
                                                                        "Completado" -> 100
                                                                        else -> progresoActual
                                                                    }
                                                                    estadoExpanded = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(16.dp))

                                                // Slider para ajustar el progreso
                                                Text(
                                                    text = "Progreso: $progresoActual%",
                                                    style = MaterialTheme.typography.bodyLarge
                                                )

                                                Slider(
                                                    value = progresoActual.toFloat(),
                                                    onValueChange = {
                                                        progresoActual = it.toInt()
                                                        // Ajustar estado según el progreso
                                                        estadoActualVehiculo = when(progresoActual) {
                                                            in 0..30 -> "En diagnóstico"
                                                            in 31..60 -> "En reparación"
                                                            in 61..90 -> "En pruebas"
                                                            else -> "Completado"
                                                        }
                                                    },
                                                    valueRange = 0f..100f,
                                                    steps = 19,
                                                    colors = SliderDefaults.colors(
                                                        thumbColor = primaryColor,
                                                        activeTrackColor = primaryColor
                                                    )
                                                )

                                                Spacer(modifier = Modifier.height(24.dp))

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.End
                                                ) {
                                                    TextButton(
                                                        onClick = { showProgressDialog = false }
                                                    ) {
                                                        Text("Cancelar")
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Button(
                                                        onClick = {
                                                            actualizarProgresoVehiculo(citaActual.vehiculo_id, estadoActualVehiculo, progresoActual)
                                                            showProgressDialog = false
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = primaryColor
                                                        )
                                                    ) {
                                                        Text("Guardar")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

    // Dialog para editar cita
    if (showDialogEditar && citaSeleccionadaId != null) {
        val citaActual = citas.find { it.first == citaSeleccionadaId }?.second ?: CitaTrabajador()
        var marca by remember { mutableStateOf(citaActual.marca) }
        var modelo by remember { mutableStateOf(citaActual.modelo) }
        var año by remember { mutableStateOf(citaActual.año) }
        var placa by remember { mutableStateOf(citaActual.placa) }
        var servicioSeleccionado by remember { mutableStateOf(citaActual.servicio) }
        var fecha by remember { mutableStateOf(citaActual.fecha_hora) }
        var telefono by remember { mutableStateOf(citaActual.telefono) }

        Dialog(onDismissRequest = { showDialogEditar = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Editar Cita",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = marca,
                        onValueChange = { marca = it },
                        label = { Text("Marca") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = textSecondaryColor.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = modelo,
                        onValueChange = { modelo = it },
                        label = { Text("Modelo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = textSecondaryColor.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = año,
                        onValueChange = { año = it },
                        label = { Text("Año") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = textSecondaryColor.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = placa,
                        onValueChange = { placa = it },
                        label = { Text("Placa") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = textSecondaryColor.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = textSecondaryColor.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Selector de servicio
                    var servicioExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = servicioExpanded,
                        onExpandedChange = { servicioExpanded = !servicioExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = servicioSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Servicio") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = servicioExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = textSecondaryColor.copy(alpha = 0.5f)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = servicioExpanded,
                            onDismissRequest = { servicioExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            serviciosDisponibles.forEach { servicioOpcion ->
                                DropdownMenuItem(
                                    text = { Text(servicioOpcion) },
                                    onClick = {
                                        servicioSeleccionado = servicioOpcion
                                        servicioExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Campo de fecha
                    OutlinedTextField(
                        value = fecha,
                        onValueChange = { fecha = it },
                        label = { Text("Fecha y Hora") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = "Fecha"
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = textSecondaryColor.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showDialogEditar = false }
                        ) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                // Actualizar los datos de la cita
                                val citaActualizada = citaActual.copy(
                                    marca = marca,
                                    modelo = modelo,
                                    año = año,
                                    placa = placa,
                                    servicio = servicioSeleccionado,
                                    fecha_hora = fecha,
                                    telefono = telefono
                                )
                                guardarCambiosCita(citaSeleccionadaId!!, citaActualizada)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor
                            )
                        ) {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }

// Dialog para asignar ID de vehículo
    if (showDialogAsignarID && citaSeleccionadaId != null) {
        val citaActual = citas.find { it.first == citaSeleccionadaId }?.second ?: CitaTrabajador()
        // Generar un ID único basado en las iniciales de la marca y modelo + un UUID corto
        val marcaInicial = if (citaActual.marca.isNotEmpty()) citaActual.marca[0].toString() else "X"
        val modeloInicial = if (citaActual.modelo.isNotEmpty()) citaActual.modelo[0].toString() else "X"
        val uuid = UUID.randomUUID().toString().substring(0, 6)
        var idSugerido by remember { mutableStateOf("$marcaInicial$modeloInicial-$uuid") }

        Dialog(onDismissRequest = { showDialogAsignarID = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Asignar ID de Vehículo",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Vehículo: ${citaActual.marca} ${citaActual.modelo} (${citaActual.año})",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "Cliente: ${citaActual.cliente_nombre}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = idSugerido,
                        onValueChange = { idSugerido = it },
                        label = { Text("ID de Vehículo") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "ID"
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = textSecondaryColor.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Este ID se usará para rastrear el vehículo durante su estancia en el taller.",
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondaryColor
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón para generar nuevo ID
                    OutlinedButton(
                        onClick = {
                            val newUuid = UUID.randomUUID().toString().substring(0, 6)
                            idSugerido = "$marcaInicial$modeloInicial-$newUuid"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, primaryColor)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Regenerar",
                            tint = primaryColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Generar nuevo ID",
                            color = primaryColor
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showDialogAsignarID = false }
                        ) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                // Asignar el ID de vehículo a la cita
                                asignarIdVehiculo(citaSeleccionadaId!!, idSugerido)
                                showDialogAsignarID = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor
                            )
                        ) {
                            Text("Confirmar")
                        }
                    }
                }
            }
        }
    }
} } } } } } } }


