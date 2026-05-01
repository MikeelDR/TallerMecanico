package org.example.tallermecanico.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.example.tallermecanico.ui.agendar.AgendarCitaScreen
import org.example.tallermecanico.ui.data.repository.CitasRepository
import org.example.tallermecanico.ui.data.repository.NotificacionesRepository
import org.example.tallermecanico.ui.data.repository.ServiciosRepository
import org.example.tallermecanico.ui.data.repository.VehiculosRepository
import org.example.tallermecanico.ui.screens.*
import org.example.tallermecanico.ui.screens.PerfilCliente.PerfilClienteScreen
import org.example.tallermecanico.viewmodel.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
/**
 * Definición de rutas para la navegación en la aplicación
 * Organizadas por secciones: Autenticación, Cliente, Administrador/Trabajador
 */
sealed class AppScreens(val route: String) {
    // ========================= AUTENTICACIÓN =========================
    object PantallaBienvenida : AppScreens("pantalla_bienvenida")
    object PantallaInicio : AppScreens("pantalla_inicio")
    object PantallaRegistro : AppScreens("pantalla_registro")

    // ========================= CLIENTE =========================
    object MenuPrincipalCliente : AppScreens("menu_principal_cliente")
    object PerfilCliente : AppScreens("perfil_cliente")
    object CitasRealizadas : AppScreens("citas_realizadas") {
        fun createRoute(userEmail: String) = "$route/$userEmail"
    }
    object AgendarCita : AppScreens("agendar_cita")
    object PantallaEstado : AppScreens("pantalla_estado")
    object ServiciosDisponibles : AppScreens("servicios_cliente")
    object EstadoVehiculo : AppScreens("estado_vehiculo") {
        fun createRoute(vehiculoId: String) = "$route/$vehiculoId"
    }
    object DatosAutoCliente : AppScreens("datos_auto_cliente")

    // ========================= ADMINISTRADOR/TRABAJADOR =========================
    object TrabajadorMenuPrincipal : AppScreens("trabajador_menu_principal")
    object GestionCitasTrabajadorScreen : AppScreens("gestion_citas_trabajador")
    object ActualizarEstadoScreen : AppScreens("actualizar_estado") {
        const val vehiculoId = "vehiculoId"
        fun createRoute(vehiculoId: String) = "$route/$vehiculoId"
    }
    object ConsultaVehiculoScreen : AppScreens("consulta_vehiculo") {
        fun createRoute(vehiculoId: String) = "$route/$vehiculoId"
    }
    object TrabajadorRegistroAuto : AppScreens("trabajador_registro_auto")

    // ========================= RUTAS CON PARÁMETROS =========================
    object ConsultaVehiculoCliente : AppScreens("consulta_vehiculo_cliente") {
        fun createRoute(vehiculoId: String) = "$route/$vehiculoId"
    }

    // Para mantener compatibilidad con código existente
    object HistorialDeServicios : AppScreens("historial_servicios")
    object SplashScreen : AppScreens("splash_screen")
    object Login : AppScreens("login_screen")
    object Registro : AppScreens("registro_screen")
    object DetallesServicio : AppScreens("detalles_servicio/{servicioId}") {
        fun createRoute(servicioId: String) = "detalles_servicio/$servicioId"
    }
    object DetallesVehiculo : AppScreens("detalles_vehiculo/{vehiculoId}") {
        fun createRoute(vehiculoId: String) = "detalles_vehiculo/$vehiculoId"
    }
    // Mantener compatibilidad con la versión anterior
    object WorkOrderScreen : AppScreens("work_order_screen")
    object ServiceHistoryScreen : AppScreens("service_history_screen")
    object VehicleConsultationScreen : AppScreens("vehicle_consultation_screen")
    object UpdateStatusScreen : AppScreens("update_status_screen") {
        const val serviceIdArg = "serviceId"
        fun createRoute(serviceId: String) = "$route/$serviceId"
    }
    object EstadoVehiculoConDetalles : AppScreens("estado_vehiculo_con_detalles") {
        fun createRoute(vehiculoId: Int) = "$route/$vehiculoId"
    }
}

/**
 * Función principal de navegación para la aplicación
 */
@Composable
fun TallerMecanicoNavGraph(
    navController: NavHostController,
    citasViewModel: CitasViewModel
) {
    // Repositorios necesarios para las pantallas
    val citasRepository = CitasRepository()
    val serviciosRepository = ServiciosRepository()
    val vehiculosRepository = VehiculosRepository()
    val notificacionesRepository = NotificacionesRepository()

    // ViewModel para el trabajador
    val trabajadorViewModel = viewModel<TrabajadorViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TrabajadorViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return TrabajadorViewModel(
                        citasRepository = citasRepository,
                        serviciosRepository = serviciosRepository,
                        vehiculosRepository = vehiculosRepository,
                        notificacionesRepository = notificacionesRepository
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = AppScreens.PantallaBienvenida.route
    ) {
        // ========================= PANTALLAS DE AUTENTICACIÓN =========================
        composable(route = AppScreens.PantallaBienvenida.route) {
            PantallaBienvenidaScreen(navController = navController)
        }

        composable(route = AppScreens.PantallaInicio.route) {
            PantallaInicioScreen(navController = navController)
        }

        composable(route = AppScreens.PantallaRegistro.route) {
            PantallaRegistroScreen(navController = navController)
        }

        // ========================= PANTALLAS DEL CLIENTE =========================
        composable(route = AppScreens.MenuPrincipalCliente.route) {
            MenuPrincipalClienteScreen(navController = navController)
        }

        composable(route = AppScreens.PerfilCliente.route) {
            PerfilClienteScreen(navController = navController)
        }

        composable(
            route = "${AppScreens.CitasRealizadas.route}/{userEmail}",
            arguments = listOf(navArgument("userEmail") { type = NavType.StringType })
        ) { backStackEntry ->
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
            CitasRealizadasScreen(
                navController = navController,
                userEmail = userEmail
            )
        }

        composable(route = AppScreens.AgendarCita.route) {
            AgendarCitaScreen(navController = navController)
        }

        composable(AppScreens.HistorialDeServicios.route) {
            HistorialDeServiciosScreen(navController = navController)
        }


        // Definición para la pantalla de detalles de servicio con parámetro
        composable(
            AppScreens.DetallesServicio.route,
            arguments = listOf(navArgument("servicioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val servicioId = backStackEntry.arguments?.getString("servicioId") ?: "0"
            DetallesServicioScreen(servicioId = servicioId, navController = navController)
        }

        // Definición para la pantalla de detalles de vehículo con parámetro
        composable(
            AppScreens.DetallesVehiculo.route,
            arguments = listOf(navArgument("vehiculoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId") ?: "0"
            DatosAutoClienteScreen(vehiculoId = vehiculoId, navController = navController)
        }

        // Para mantener compatibilidad con código existente
        composable(route = AppScreens.HistorialDeServicios.route) {
            HistorialDeServiciosScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(route = AppScreens.ConsultaVehiculoScreen.route) {
            ConsultaVehiculoScreen(
                navController = navController,
                vehiculosRepository = TODO(),
                vehiculoId = TODO(),
                onEditarEstado = TODO()
            )
        }


        // Pantalla para consulta de estado de vehículo
        composable(
            route = "${AppScreens.EstadoVehiculo.route}/{vehiculoId}",
            arguments = listOf(navArgument("vehiculoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId")?.toIntOrNull()

            // Provisión del ViewModel
            val citasViewModel: CitasViewModel = viewModel()

            PantallaEstadoScreen(
                navController = navController,
                vehiculoId = vehiculoId,
                citasViewModel = citasViewModel,
                serviceId = ""
            )
        }

        // Pantalla simple de estado sin parámetros (usada para entrada directa)
        composable(route = AppScreens.EstadoVehiculo.route) {
            // Provisión del ViewModel
            val citasViewModel: CitasViewModel = viewModel()

            PantallaEstadoScreen(
                navController = navController,
                citasViewModel = citasViewModel
            )
        }

        // Define la ruta con el parámetro
        val consultaRoute = "${AppScreens.ConsultaVehiculoScreen.route}/{vehiculoId}"

// En la navegación
        composable(
            route = consultaRoute,
            arguments = listOf(navArgument("vehiculoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId") ?: ""
            val vehiculosRepository = // obtén el repositorio adecuadamente

                ConsultaVehiculoScreen(
                    navController = navController,
                    vehiculosRepository = vehiculosRepository,
                    onEditarEstado = { vehiculoId ->
                        navController.navigate("${AppScreens.EstadoVehiculo.route}/$vehiculoId")
                    },
                    vehiculoId = vehiculoId
                )
        }
        composable(
            route = "${AppScreens.ConsultaVehiculoCliente.route}/{vehiculoId}",
            arguments = listOf(navArgument("vehiculoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId") ?: ""

            val viewModel = viewModel<ClientVehicleStatusViewModel>(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        if (modelClass.isAssignableFrom(ClientVehicleStatusViewModel::class.java)) {
                            @Suppress("UNCHECKED_CAST")
                            return ClientVehicleStatusViewModel(vehiculoId) as T
                        }
                        throw IllegalArgumentException("Unknown ViewModel class")
                    }
                }
            )

            ClientVehicleStatusScreen(
                onBackPressed = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(route = AppScreens.DatosAutoCliente.route) {
            DatosAutoClienteScreen(navController = navController)
        }

        // ========================= PANTALLAS DEL ADMINISTRADOR/TRABAJADOR =========================
        composable(route = AppScreens.TrabajadorMenuPrincipal.route) {
            val citasViewModel: CitasViewModel = viewModel(
                factory = CitasViewModelFactory(
                    CitasRepository(),
                    VehiculosRepository()
                )
            )

            TrabajadorMenuPrincipalScreen(
                navController = navController,
                onNavigateToGestionCitasTrabajadorScreen = {
                    navController.navigate(AppScreens.GestionCitasTrabajadorScreen.route)
                },
                onNavigateToConsulta = {
                    navController.navigate(AppScreens.ConsultaVehiculoScreen.route)
                },
                onNavigateToActualizarEstadoScreen = {
                    navController.navigate(AppScreens.ActualizarEstadoScreen.route)
                },
                citasViewModel = citasViewModel
            )
        }

        composable(route = AppScreens.TrabajadorRegistroAuto.route) {
            TrabajadorRegistroAutoScreen(navController = navController)
        }

        composable(route = AppScreens.GestionCitasTrabajadorScreen.route) {
            GestionCitasTrabajadorScreen(
                navController = navController,
                trabajadorViewModel = trabajadorViewModel
            )
        }

        composable(
            route = "${AppScreens.ActualizarEstadoScreen.route}/{vehiculoId}",
            arguments = listOf(navArgument("vehiculoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId") ?: ""
            val trabajadorViewModel: TrabajadorViewModel = viewModel()

            ActualizarEstadoScreen(
                navController = navController,
                vehiculoId = vehiculoId,
                vehiculosRepository = vehiculosRepository,
                notificacionesRepository = notificacionesRepository,
                trabajadorViewModel = trabajadorViewModel
            )
        }


        composable(
            route = "${AppScreens.ConsultaVehiculoScreen.route}/{vehiculoId}",
            arguments = listOf(navArgument("vehiculoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId") ?: ""
            ConsultaVehiculoScreen(
                navController = navController,
                vehiculoId = vehiculoId,
                vehiculosRepository = vehiculosRepository,
                onEditarEstado = { idVehiculo: String ->
                    navController.navigate(AppScreens.ActualizarEstadoScreen.createRoute(idVehiculo))
                }
            )
        }

        // Ruta adicional para consulta directa del trabajador sin parámetros
        composable(route = AppScreens.ConsultaVehiculoScreen.route) {
            ConsultaVehiculoScreen(
                navController = navController,
                vehiculoId = "",
                vehiculosRepository = vehiculosRepository,
                onEditarEstado = { idVehiculo: String ->
                    navController.navigate(AppScreens.ActualizarEstadoScreen.createRoute(idVehiculo))
                }
            )
        }

        // ========================= RUTAS ADICIONALES PARA COMPATIBILIDAD =========================
        composable(route = AppScreens.WorkOrderScreen.route) {
            // TODO: Implementar WorkOrderScreen
        }

        composable(route = AppScreens.ServiceHistoryScreen.route) {
            // TODO: Implementar ServiceHistoryScreen
        }

        composable(route = AppScreens.VehicleConsultationScreen.route) {
            // TODO: Implementar VehicleConsultationScreen
        }

        composable(
            route = "${AppScreens.UpdateStatusScreen.route}/{${AppScreens.UpdateStatusScreen.serviceIdArg}}",
            arguments = listOf(navArgument(AppScreens.UpdateStatusScreen.serviceIdArg) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString(AppScreens.UpdateStatusScreen.serviceIdArg) ?: ""
            // TODO: Implementar UpdateStatusScreen
        }

        composable(
            route = "${AppScreens.EstadoVehiculoConDetalles.route}/{vehiculoId}",
            arguments = listOf(navArgument("vehiculoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getInt("vehiculoId") ?: -1
            PantallaEstadoScreen(
                navController = navController,
                vehiculoId = vehiculoId,
                serviceId = ""
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ClientVehicleStatusScreen(
    onBackPressed: () -> Unit,
    viewModel: ClientVehicleStatusViewModel
) {
    val primaryColor = Color(0xFF1F41BB)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estado del Vehículo") },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Consultando estado del vehículo...",
                color = Color.Gray
            )
        }
    }
}

private fun AnimatedContentScope.obtenerRepositorioVehiculos() {
    TODO("Not yet implemented")
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesServicioScreen(servicioId: String, navController: NavHostController) {
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
                // Encabezado con color de categoría
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

                // Precio
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Precio estimado", fontWeight = FontWeight.Medium, color = Color.Gray)
                        Text(
                            text = servicio.precio,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            fontSize = 16.sp
                        )
                    }
                }

                // Duración
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Duración estimada", fontWeight = FontWeight.Medium, color = Color.Gray)
                        Text(
                            text = servicio.duracionEstimada,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                }

                // Descripción
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Descripción",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = servicio.descripcion,
                            color = Color.DarkGray,
                            lineHeight = 22.sp
                        )
                    }
                }

                // Botón agendar
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

    @Composable
    fun ClientVehicleStatusScreen(
        onBackPressed: () -> Unit,
        viewModel: ClientVehicleStatusViewModel
    ) {
        val primaryColor = Color(0xFF1F41BB)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Estado del Vehículo") },
                    navigationIcon = {
                        IconButton(onClick = { onBackPressed() }) {
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
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Consultando estado del vehículo...",
                    color = Color.Gray
                )
            }
        }
    }

/**
 * Funciones de extensión para navegación
 * Organizadas por categorías para mejor mantenimiento
 */

// ========================= NAVEGACIÓN CLIENTE =========================
fun NavHostController.navigateToMenuCliente() {
    navigate(AppScreens.MenuPrincipalCliente.route) {
        popUpTo(AppScreens.PantallaInicio.route) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavHostController.navigateToPerfilCliente() {
    navigate(AppScreens.PerfilCliente.route) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToCitasRealizadas(userEmail: String) {
    navigate(AppScreens.CitasRealizadas.createRoute(userEmail)) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToAgendarCita() {
    navigate(AppScreens.AgendarCita.route) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToServiciosCliente() {
    navigate(AppScreens.ServiciosDisponibles.route) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToConsultaVehiculo() {
    navigate(AppScreens.ConsultaVehiculoScreen.route) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToEstadoVehiculo(vehiculoId: String) {
    navigate(AppScreens.EstadoVehiculo.createRoute(vehiculoId)) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToDatosAutoCliente() {
    navigate(AppScreens.DatosAutoCliente.route) {
        launchSingleTop = true
    }
}

// ========================= NAVEGACIÓN TRABAJADOR =========================
fun NavHostController.navigateToMenuTrabajador() {
    try {
        navigate(AppScreens.TrabajadorMenuPrincipal.route) {
            popUpTo(AppScreens.PantallaBienvenida.route) { inclusive = true }
            launchSingleTop = true
        }
    } catch (e: Exception) {
        // Registrar el error para depuración
        Log.e("NavError", "Error al navegar a MenuTrabajador: ${e.message}", e)

        // Posible navegación alternativa o manejo de errores
        navigate(AppScreens.PantallaBienvenida.route) {
            popUpTo(AppScreens.PantallaBienvenida.route) { inclusive = true }
        }
    }
}

fun NavHostController.navigateToGestionCitasTrabajadorScreen() {
    navigate(AppScreens.GestionCitasTrabajadorScreen.route) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToTrabajadorRegistroAuto() {
    navigate(AppScreens.TrabajadorRegistroAuto.route) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToActualizarEstado(vehiculoId: String = "") {
    if (vehiculoId.isNotEmpty()) {
        navigate(AppScreens.ActualizarEstadoScreen.createRoute(vehiculoId)) {
            launchSingleTop = true
        }
    } else {
        navigate(AppScreens.ActualizarEstadoScreen.route) {
            launchSingleTop = true
        }
    }
}

fun NavHostController.navigateToConsultaTrabajador(vehiculoId: String = "") {
    if (vehiculoId.isNotEmpty()) {
        navigate(AppScreens.ConsultaVehiculoScreen.createRoute(vehiculoId)) {
            launchSingleTop = true
        }
    } else {
        navigate(AppScreens.ConsultaVehiculoScreen.route) {
            launchSingleTop = true
        }
    }
}

// ========================= NAVEGACIÓN GENERAL =========================
fun NavHostController.navigateToLogin() {
    navigate(AppScreens.PantallaBienvenida.route) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavHostController.navigateToRegistro() {
    navigate(AppScreens.PantallaRegistro.route) {
        launchSingleTop = true
    }
}

// Para mantener compatibilidad con código existente
fun NavHostController.navigateTo(screen: Screen.DatosAutoCliente) {
    this.navigate(screen.route) {
        launchSingleTop = true
        restoreState = true
    }
}

// Soporte para navegación a estado de vehículo con ID entero
fun NavHostController.navegarAEstadoVehiculo(vehiculoId: Int) {
    this.navigate("${AppScreens.EstadoVehiculo.route}/$vehiculoId") {
        launchSingleTop = true
        restoreState = true
    }
}

// Compatibilidad con la versión anterior
fun NavHostController.navigateToUpdateStatus(serviceId: String) {
    this.navigate(AppScreens.UpdateStatusScreen.createRoute(serviceId)) {
        launchSingleTop = true
        restoreState = true
    }
}}