package org.example.tallermecanico.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.tallermecanico.R
import org.example.tallermecanico.navigation.AppScreens


// Clase para representar un servicio del taller
data class ServicioTaller1(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: String,
    val categoria: String,
    val nombreCategoria: String,
    val duracionEstimada: String
)

// Enumeración para las categorías de servicios
enum class CategoriaServicio(val nombreCategoria: String) {
    MANTENIMIENTO_BASICO("Mantenimiento Básico"),
    FRENOS("Sistema de Frenos"),
    NEUMATICOS("Neumáticos y Suspensión"),
    MOTOR("Motor y Rendimiento"),
    ELECTRICO("Sistema Eléctrico"),
    CLIMATIZACION("Aire Acondicionado"),
    ESTETICA("Estética y Limpieza"),
    DIAGNOSTICO("Diagnóstico"),
    TRANSMISION("Transmisión"),
    OTROS("Otros Servicios"),
    MANTENIMIENTO("Mantenimiento"),
    REPARACION("Reparación"),
    SERVICIO_GENERAL("Servicio General"),
    ELECTRICIDAD("Electricidad"),
    LIMPIEZA("Limpieza"),
    REPARACION_MECANICA("Reparación Mecánica"),
    SUSPENSION("Suspensión"),
    MOTORIZACION("Motorización")
}

// Lista de servicios mejorada con más información y categorizada
val serviciosDisponibles = listOf(
    ServicioTaller1(
        id = "1",
        nombre = "Cambio de aceite y filtro",
        descripcion = "Sustitución completa del aceite del motor y del filtro de aceite",
        precio = "$720 - $1,400 MXN",
        categoria = CategoriaServicio.MANTENIMIENTO_BASICO.toString(),
        nombreCategoria = CategoriaServicio.MANTENIMIENTO_BASICO.nombreCategoria,
        duracionEstimada = "1 - 2 horas"
    ),
    ServicioTaller1(
        id = "2",
        nombre = "Revisión de niveles",
        descripcion = "Comprobación de líquidos del vehículo: aceite, refrigerante, frenos, dirección",
        precio = "$250 - $500 MXN",
        categoria = CategoriaServicio.MANTENIMIENTO_BASICO.toString(),
        nombreCategoria = CategoriaServicio.MANTENIMIENTO_BASICO.nombreCategoria,
        duracionEstimada = "30 minutos",
    ),
    ServicioTaller1(
        id = "3",
        nombre = "Cambio de filtro de aire",
        descripcion = "Sustitución del filtro de aire del motor para mejorar la eficiencia",
        precio = "$360 - $700 MXN",
        categoria = CategoriaServicio.MANTENIMIENTO_BASICO.toString(),
        nombreCategoria = CategoriaServicio.MANTENIMIENTO_BASICO.nombreCategoria,
        duracionEstimada = "20 minutos",
    ),
    ServicioTaller1(
        id = "4",
        nombre = "Cambio de bujías",
        descripcion = "Sustitución de bujías para mejorar el encendido del motor",
        precio = "$720 - $2,200 MXN",
        categoria = CategoriaServicio.MOTOR.toString(),
        nombreCategoria = CategoriaServicio.MOTOR.nombreCategoria,
        duracionEstimada = "45 minutos",
    ),
    ServicioTaller1(
        id = "5",
        nombre = "Revisión de correas",
        descripcion = "Inspección del estado y tensión de las correas del motor",
        precio = "$450 - $700 MXN",
        categoria = CategoriaServicio.MANTENIMIENTO_BASICO.toString(),
        nombreCategoria = CategoriaServicio.MANTENIMIENTO_BASICO.nombreCategoria,
        duracionEstimada = "30 minutos",
    ),
    ServicioTaller1(
        id = "6",
        nombre = "Servicio de afinación general",
        descripcion = "Puesta a punto completa del motor para un rendimiento óptimo",
        precio = "$1,800 - $4,500 MXN",
        categoria = CategoriaServicio.MOTOR.toString(),
        nombreCategoria = CategoriaServicio.MOTOR.nombreCategoria,
        duracionEstimada = " 120 minutos",
    ),
    ServicioTaller1(
        id = "7",
        nombre = "Rotación de neumáticos",
        descripcion = "Cambio de posición de neumáticos para garantizar un desgaste uniforme",
        precio = "$400 - $700 MXN",
        categoria = CategoriaServicio.NEUMATICOS.toString(),
        nombreCategoria = CategoriaServicio.NEUMATICOS.nombreCategoria,
        duracionEstimada = " 30 minutos",
    ),
    ServicioTaller1(
        id = "8",
        nombre = "Alineación y balanceo",
        descripcion = "Ajuste de la geometría de dirección y equilibrado de ruedas",
        precio = "$900 - $1,800 MXN",
        categoria = CategoriaServicio.NEUMATICOS.toString(),
        nombreCategoria = CategoriaServicio.NEUMATICOS.nombreCategoria,
        duracionEstimada = " 60 minutos",
    ),
    ServicioTaller1(
        id = "9",
        nombre = "Cambio de neumáticos",
        descripcion = "Sustitución de neumáticos por unos nuevos",
        precio = "Según neumáticos",
        categoria = CategoriaServicio.NEUMATICOS.toString(),
        nombreCategoria = CategoriaServicio.NEUMATICOS.nombreCategoria,
        duracionEstimada = " 45 minutos",
    ),
    ServicioTaller1(
        id = "10",
        nombre = "Revisión de frenos",
        descripcion = "Inspección del sistema de frenos: pastillas, discos, líquido",
        precio = "$540 - $900 MXN",
        categoria = CategoriaServicio.FRENOS.toString(),
        nombreCategoria = CategoriaServicio.FRENOS.nombreCategoria,
        duracionEstimada = "40 minutos",
    ),
    ServicioTaller1(
        id = "11",
        nombre = "Cambio de pastillas de freno",
        descripcion = "Sustitución de las pastillas de freno desgastadas",
        precio = "$1,400 - $2,700 MXN",
        categoria = CategoriaServicio.FRENOS.toString(),
        nombreCategoria = CategoriaServicio.FRENOS.nombreCategoria,
        duracionEstimada = " 90 minutos",
    ),
    ServicioTaller1(
        id = "12",
        nombre = "Cambio de discos de freno",
        descripcion = "Reemplazo de los discos de freno desgastados o dañados",
        precio = "$2,700 - $5,400 MXN",
        categoria = CategoriaServicio.FRENOS.toString(),
        nombreCategoria = CategoriaServicio.FRENOS.nombreCategoria,
        duracionEstimada = "120 minutos",
    ),
    ServicioTaller1(
        id = "13",
        nombre = "Cambio de líquido de frenos",
        descripcion = "Purga y reemplazo del líquido de frenos",
        precio = "$900 - $1,600 MXN",
        categoria = CategoriaServicio.FRENOS.toString(),
        nombreCategoria = CategoriaServicio.FRENOS.nombreCategoria,
        duracionEstimada = "45 minutos",
    ),
    ServicioTaller1(
        id = "14",
        nombre = "Revisión de amortiguadores",
        descripcion = "Inspección del estado y funcionamiento de los amortiguadores",
        precio = "$540 - $900 MXN",
        categoria = CategoriaServicio.NEUMATICOS.toString(),
        nombreCategoria = CategoriaServicio.NEUMATICOS.nombreCategoria,
        duracionEstimada = "40 minutos",
    ),
    ServicioTaller1(
        id = "15",
        nombre = "Cambio de suspensión",
        descripcion = "Sustitución de componentes de la suspensión desgastados",
        precio = "$3,600 - $10,800 MXN",
        categoria = CategoriaServicio.NEUMATICOS.toString(),
        nombreCategoria = CategoriaServicio.NEUMATICOS.nombreCategoria,
        duracionEstimada = "180 minutos",
    ),
    ServicioTaller1(
        id = "16",
        nombre = "Diagnóstico con escáner OBD",
        descripcion = "Lectura de códigos de error con escáner electrónico",
        precio = "$540 - $1,100 MXN",
        categoria = CategoriaServicio.DIAGNOSTICO.toString(),
        nombreCategoria = CategoriaServicio.DIAGNOSTICO.nombreCategoria,
        duracionEstimada = "30 minutos",
    ),
    ServicioTaller1(
        id = "17",
        nombre = "Cambio de batería",
        descripcion = "Sustitución de la batería del vehículo",
        precio = "Según batería",
        categoria = CategoriaServicio.ELECTRICO.toString(),
        nombreCategoria = CategoriaServicio.ELECTRICO.nombreCategoria,
        duracionEstimada = "30 minutos",
    ),
    ServicioTaller1(
        id = "18",
        nombre = "Revisión de luces",
        descripcion = "Comprobación del sistema de iluminación del vehículo",
        precio = "$360 - $700 MXN",
        categoria = CategoriaServicio.ELECTRICO.toString(),
        nombreCategoria = CategoriaServicio.ELECTRICO.nombreCategoria,
        duracionEstimada = "20 minutos",
    ),
    ServicioTaller1(
        id = "19",
        nombre = "Revisión del aire acondicionado",
        descripcion = "Comprobación del funcionamiento del sistema A/C",
        precio = "$540 - $1,100 MXN",
        categoria = CategoriaServicio.CLIMATIZACION.toString(),
        nombreCategoria = CategoriaServicio.CLIMATIZACION.nombreCategoria,
        duracionEstimada = "40 minutos",
    ),
    ServicioTaller1(
        id = "20",
        nombre = "Recarga de gas refrigerante",
        descripcion = "Recarga del sistema de aire acondicionado",
        precio = "$1,100 - $2,200 MXN",
        categoria = CategoriaServicio.CLIMATIZACION.toString(),
        nombreCategoria = CategoriaServicio.CLIMATIZACION.nombreCategoria,
        duracionEstimada = "60 minutos",
    ),
    ServicioTaller1(
        id = "21",
        nombre = "Revisión de sistema de escape",
        descripcion = "Inspección del estado y estanqueidad del sistema de escape",
        precio = "$540 - $900 MXN",
        categoria = CategoriaServicio.MOTOR.toString(),
        nombreCategoria = CategoriaServicio.MOTOR.nombreCategoria,
        duracionEstimada = "45 minutos",
    ),
    ServicioTaller1(
        id = "22",
        nombre = "Revisión de transmisión",
        descripcion = "Inspección del estado de la transmisión",
        precio = "$720 - $1,300 MXN",
        categoria = CategoriaServicio.TRANSMISION.toString(),
        nombreCategoria = CategoriaServicio.TRANSMISION.nombreCategoria,
        duracionEstimada = "60 minutos",
    ),
    ServicioTaller1(
        id = "23",
        nombre = "Cambio de embrague",
        descripcion = "Sustitución del kit de embrague completo",
        precio = "$7,200 - $16,000 MXN",
        categoria = CategoriaServicio.TRANSMISION.toString(),
        nombreCategoria = CategoriaServicio.TRANSMISION.nombreCategoria,
        duracionEstimada = "240 minutos",
    ),
    ServicioTaller1(
        id = "24",
        nombre = "Lavado y aspirado general",
        descripcion = "Limpieza completa interior y exterior del vehículo",
        precio = "$360 - $1,100 MXN",
        categoria = CategoriaServicio.ESTETICA.toString(),
        nombreCategoria = CategoriaServicio.ESTETICA.nombreCategoria,
        duracionEstimada = "50 minutos",
    ),
    ServicioTaller1(
        id = "25",
        nombre = "Pulido de faros",
        descripcion = "Pulido de faros opacos para mejorar la visibilidad",
        precio = "$720 - $1,400 MXN",
        categoria = CategoriaServicio.ESTETICA.toString(),
        nombreCategoria = CategoriaServicio.ESTETICA.nombreCategoria,
        duracionEstimada = "40 minutos",
    )
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialDeServiciosScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    var busquedaText by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaServicio?>(null) }
    var expandirCategorias by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFFF8F9FF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Encabezado con título y botones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2196F3))
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Servicios del Taller",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F41BB)
                        )
                    )
                }

            }

            // Campo de búsqueda mejorado
            OutlinedTextField(
                value = busquedaText,
                onValueChange = { busquedaText = it },
                placeholder = { Text("Buscar servicios") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF1F41BB),
                    unfocusedBorderColor = Color(0xFF1F41BB),
                    containerColor = Color.Transparent,
                    cursorColor = Color(0xFF1F41BB)
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color(0xFF1F41BB)
                    )
                },
                trailingIcon = {
                    if (busquedaText.isNotEmpty()) {
                        IconButton(onClick = { busquedaText = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar",
                                tint = Color(0xFF1F41BB)
                            )
                        }
                    }
                }
            )

            // Filtro por categorías
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandirCategorias = !expandirCategorias },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F7))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = categoriaSeleccionada?.nombreCategoria ?: "Todas las categorías",
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1F41BB)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expandir",
                            tint = Color(0xFF1F41BB)
                        )
                    }
                }

                // Menú desplegable de categorías
                DropdownMenu(
                    expanded = expandirCategorias,
                    onDismissRequest = { expandirCategorias = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    DropdownMenuItem(
                        text = { Text("Todas las categorías") },
                        onClick = {
                            categoriaSeleccionada = null
                            expandirCategorias = false
                        }
                    )
                    CategoriaServicio.values().forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.nombreCategoria) },
                            onClick = {
                                categoriaSeleccionada = categoria
                                expandirCategorias = false
                            }
                        )
                    }
                }
            }

            // Lista de servicios filtrados
            val serviciosFiltrados = serviciosDisponibles.filter { servicio ->
                val coincideCategoria = categoriaSeleccionada == null || servicio.categoria == categoriaSeleccionada!!.name
                val coincideBusqueda = busquedaText.isEmpty() || servicio.nombre.contains(busquedaText, ignoreCase = true)

                coincideCategoria && coincideBusqueda
            }

            // Mensajes informativos
            if (serviciosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron servicios con los criterios seleccionados",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                Text(
                    text = "Mostrando ${serviciosFiltrados.size} de ${serviciosDisponibles.size} servicios",
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            // Lista de servicios mejorada
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(serviciosFiltrados) { servicio ->
                    ServicioCard(
                        servicio = servicio,
                        onClick = {
                            navController.navigate(AppScreens.DetallesServicio.createRoute(servicio.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ServicioCard(
    servicio: ServicioTaller1,
    onClick: () -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandido = !expandido }
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono del servicio (puede ser personalizado por categoría)
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(obtenerColorCategoria(servicio.categoria)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = servicio.id,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = servicio.nombre,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )

                    // Obtenemos la categoría a partir del string
                    val categoriaEnum = try {
                        CategoriaServicio.valueOf(servicio.categoria)
                    } catch (e: IllegalArgumentException) {
                        CategoriaServicio.OTROS // Categoría por defecto si no se encuentra
                    }

                    Text(
                        text = categoriaEnum.nombreCategoria,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Indicador de precio
                Text(
                    text = servicio.precio,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F41BB)
                )
            }

            // Contenido expandido
            if (expandido) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Descripción:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = servicio.descripcion,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Duración estimada: ${servicio.duracionEstimada}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F41BB))
                    ) {
                        Text("Más detalles", color = Color.White)
                    }
                }
            }
        }
    }
}

// Función para obtener colores según la categoría
@Composable
fun obtenerColorCategoria(categoria: String): Color {
    return try {
        when (CategoriaServicio.valueOf(categoria)) {
            CategoriaServicio.MANTENIMIENTO_BASICO -> Color(0xFF4CAF50) // Verde
            CategoriaServicio.FRENOS -> Color(0xFFE53935) // Rojo
            CategoriaServicio.NEUMATICOS -> Color(0xFF607D8B) // Azul grisáceo
            CategoriaServicio.MOTOR -> Color(0xFFFF9800) // Naranja
            CategoriaServicio.ELECTRICO -> Color(0xFFFFEB3B) // Amarillo
            CategoriaServicio.CLIMATIZACION -> Color(0xFF2196F3) // Azul
            CategoriaServicio.ESTETICA -> Color(0xFF9C27B0) // Púrpura
            CategoriaServicio.DIAGNOSTICO -> Color(0xFF795548) // Marrón
            CategoriaServicio.TRANSMISION -> Color(0xFF009688) // Verde azulado
            CategoriaServicio.MANTENIMIENTO -> Color(0xFF4CAF50) // Verde
            CategoriaServicio.REPARACION -> Color(0xFFFF5722) // Naranja oscuro
            CategoriaServicio.SERVICIO_GENERAL -> Color(0xFF3F51B5) // Índigo
            CategoriaServicio.ELECTRICIDAD -> Color(0xFFFFD600) // Amarillo oscuro
            CategoriaServicio.LIMPIEZA -> Color(0xFF00BCD4) // Cian
            CategoriaServicio.REPARACION_MECANICA -> Color(0xFFF44336) // Rojo claro
            CategoriaServicio.SUSPENSION -> Color(0xFF78909C) // Azul grisáceo oscuro
            CategoriaServicio.MOTORIZACION -> Color(0xFFE65100) // Naranja profundo
            CategoriaServicio.OTROS -> Color(0xFF9E9E9E) // Gris
        }
    } catch (e: IllegalArgumentException) {
        Color(0xFF9E9E9E) // Gris por defecto si la categoria no existe
    }
}
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DetallesServicioScreen(servicioId: String, navController: NavHostController) {
        val servicio = serviciosDisponibles.find { it.id == servicioId }
        val primaryColor = Color(0xFF1F41BB)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalle del Servicio") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = primaryColor,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            if (servicio == null) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Servicio no encontrado", color = Color.Gray)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = obtenerColorCategoria(servicio.categoria)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = servicio.nombre,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = servicio.nombreCategoria,
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Precio estimado", fontWeight = FontWeight.Medium, color = Color.Gray)
                            Text(text = servicio.precio, fontWeight = FontWeight.Bold, color = primaryColor, fontSize = 16.sp)
                        }
                    }
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Duración estimada", fontWeight = FontWeight.Medium, color = Color.Gray)
                            Text(text = servicio.duracionEstimada, fontWeight = FontWeight.Bold, color = primaryColor)
                        }
                    }
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Descripción", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
                            Text(text = servicio.descripcion, color = Color.DarkGray, lineHeight = 22.sp)
                        }
                    }
                    Button(
                        onClick = { navController.navigate(AppScreens.DatosAutoCliente.route) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        Text("Agendar este servicio", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

