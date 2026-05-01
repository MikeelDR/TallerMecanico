package org.example.tallermecanico.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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


sealed class AppScreens(val route: String) {
    object PantallaBienvenida : AppScreens("pantalla_bienvenida")
    object PantallaInicio : AppScreens("pantalla_inicio")
    object PantallaRegistro : AppScreens("pantalla_registro")
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
    object ConsultaVehiculoCliente : AppScreens("consulta_vehiculo_cliente") {
        fun createRoute(vehiculoId: String) = "$route/$vehiculoId"
    }
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

@Composable
fun TallerMecanicoNavGraph(
    navController: NavHostController,
    citasViewModel: CitasViewModel
) {
    val citasRepository = CitasRepository()
    val serviciosRepository = ServiciosRepository()
    val vehiculosRepository = VehiculosRepository()
    val notificacionesRepository = NotificacionesRepository()

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
        composable(route = AppScreens.PantallaBienvenida.route) {
            PantallaBienvenidaScreen(navController = navController)
        }
        composable(route = AppScreens.PantallaInicio.route) {
            PantallaInicioScreen(navController = navController)
        }
        composable(route = AppScreens.PantallaRegistro.route) {
            PantallaRegistroScreen(navController = navController)
        }
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
            CitasRealizadasScreen(navController = navController, userEmail = userEmail)
        }
        composable(route = AppScreens.AgendarCita.route) {
            AgendarCitaScreen(navController = navController)
        }
        composable(route = AppScreens.HistorialDeServicios.route) {
            HistorialDeServiciosScreen(navController = navController)
        }
        composable(
            route = AppScreens.DetallesServicio.route,
            arguments = listOf(navArgument("servicioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val servicioId = backStackEntry.arguments?.getString("servicioId") ?: "0"
            DetallesServicioScreen(servicioId = servicioId, navController = navController)
        }
        composable(
            route = AppScreens.DetallesVehiculo.route,
            arguments = listOf(navArgument("vehiculoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId") ?: "0"
            DatosAutoClienteScreen(vehiculoId = vehiculoId, navController = navController)
        }
        composable(route = AppScreens.DatosAutoCliente.route) {
            DatosAutoClienteScreen(navController = navController)
        }
        composable(
            route = "${AppScreens.EstadoVehiculo.route}/{vehiculoId}",
            arguments = listOf(navArgument("vehiculoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId")?.toIntOrNull()
            val vm: CitasViewModel = viewModel()
            PantallaEstadoScreen(
                navController = navController,
                vehiculoId = vehiculoId,
                citasViewModel = vm,
                serviceId = ""
            )
        }
        composable(route = AppScreens.EstadoVehiculo.route) {
            val vm: CitasViewModel = viewModel()
            PantallaEstadoScreen(navController = navController, citasViewModel = vm)
        }
        composable(
            route = "${AppScreens.ConsultaVehiculoCliente.route}/{vehiculoId}",
            arguments = listOf(navArgument("vehiculoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId") ?: ""
            val vm = viewModel<ClientVehicleStatusViewModel>(
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
                viewModel = vm
            )
        }
        composable(route = AppScreens.TrabajadorMenuPrincipal.route) {
            val vm: CitasViewModel = viewModel(
                factory = CitasViewModelFactory(CitasRepository(), VehiculosRepository())
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
                citasViewModel = vm
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
            val vm: TrabajadorViewModel = viewModel()
            ActualizarEstadoScreen(
                navController = navController,
                vehiculoId = vehiculoId,
                vehiculosRepository = vehiculosRepository,
                notificacionesRepository = notificacionesRepository,
                trabajadorViewModel = vm
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
                onEditarEstado = { idVehiculo ->
                    navController.navigate(AppScreens.ActualizarEstadoScreen.createRoute(idVehiculo))
                }
            )
        }
        composable(route = AppScreens.ConsultaVehiculoScreen.route) {
            ConsultaVehiculoScreen(
                navController = navController,
                vehiculoId = "",
                vehiculosRepository = vehiculosRepository,
                onEditarEstado = { idVehiculo ->
                    navController.navigate(AppScreens.ActualizarEstadoScreen.createRoute(idVehiculo))
                }
            )
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
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Consultando estado del vehículo...", color = Color.Gray)
        }
    }
}

// Navegación cliente
fun NavHostController.navigateToMenuCliente() {
    navigate(AppScreens.MenuPrincipalCliente.route) {
        popUpTo(AppScreens.PantallaInicio.route) { inclusive = true }
        launchSingleTop = true
    }
}
fun NavHostController.navigateToPerfilCliente() {
    navigate(AppScreens.PerfilCliente.route) { launchSingleTop = true }
}
fun NavHostController.navigateToCitasRealizadas(userEmail: String) {
    navigate(AppScreens.CitasRealizadas.createRoute(userEmail)) { launchSingleTop = true }
}
fun NavHostController.navigateToAgendarCita() {
    navigate(AppScreens.AgendarCita.route) { launchSingleTop = true }
}
fun NavHostController.navigateToConsultaVehiculo() {
    navigate(AppScreens.ConsultaVehiculoScreen.route) { launchSingleTop = true }
}
fun NavHostController.navigateToEstadoVehiculo(vehiculoId: String) {
    navigate(AppScreens.EstadoVehiculo.createRoute(vehiculoId)) { launchSingleTop = true }
}
fun NavHostController.navigateToDatosAutoCliente() {
    navigate(AppScreens.DatosAutoCliente.route) { launchSingleTop = true }
}

// Navegación trabajador
fun NavHostController.navigateToMenuTrabajador() {
    try {
        navigate(AppScreens.TrabajadorMenuPrincipal.route) {
            popUpTo(AppScreens.PantallaBienvenida.route) { inclusive = true }
            launchSingleTop = true
        }
    } catch (e: Exception) {
        Log.e("NavError", "Error al navegar a MenuTrabajador: ${e.message}", e)
        navigate(AppScreens.PantallaBienvenida.route) {
            popUpTo(AppScreens.PantallaBienvenida.route) { inclusive = true }
        }
    }
}
fun NavHostController.navigateToGestionCitasTrabajadorScreen() {
    navigate(AppScreens.GestionCitasTrabajadorScreen.route) { launchSingleTop = true }
}
fun NavHostController.navigateToActualizarEstado(vehiculoId: String = "") {
    if (vehiculoId.isNotEmpty()) {
        navigate(AppScreens.ActualizarEstadoScreen.createRoute(vehiculoId)) { launchSingleTop = true }
    } else {
        navigate(AppScreens.ActualizarEstadoScreen.route) { launchSingleTop = true }
    }
}
fun NavHostController.navigateToConsultaTrabajador(vehiculoId: String = "") {
    if (vehiculoId.isNotEmpty()) {
        navigate(AppScreens.ConsultaVehiculoScreen.createRoute(vehiculoId)) { launchSingleTop = true }
    } else {
        navigate(AppScreens.ConsultaVehiculoScreen.route) { launchSingleTop = true }
    }
}

// Navegación general
fun NavHostController.navigateToLogin() {
    navigate(AppScreens.PantallaBienvenida.route) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}
fun NavHostController.navigateToRegistro() {
    navigate(AppScreens.PantallaRegistro.route) { launchSingleTop = true }
}
fun NavHostController.navegarAEstadoVehiculo(vehiculoId: Int) {
    navigate("${AppScreens.EstadoVehiculo.route}/$vehiculoId") {
        launchSingleTop = true
        restoreState = true
    }
}
fun NavHostController.navigateToUpdateStatus(serviceId: String) {
    navigate(AppScreens.UpdateStatusScreen.createRoute(serviceId)) {
        launchSingleTop = true
        restoreState = true
    }
}