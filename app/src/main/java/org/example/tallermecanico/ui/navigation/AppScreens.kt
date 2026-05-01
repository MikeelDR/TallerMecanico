package org.example.tallermecanico.navigation

// Sealed class for defining all possible navigation destinations
sealed class Screen(val route: String) {
    class TrabajadorPanel {

    }

    // Pantallas principales
    object PantallaInicio : Screen("pantalla_inicio")
    object PantallaBienvenida : Screen("pantalla_bienvenida")
    object PantallaRegistro : Screen("pantalla_registro")
    object MenuPrincipalCliente : Screen("menu_principal_cliente")
    object PantallaConsulta : Screen("pantalla_consulta")
    object PantallaEstado : Screen("pantalla_estado")
    object HistorialDeServicios : Screen("historial_de_servicios")
    object DatosAutoCliente : Screen("datos_auto_cliente")
    object TrabajadorMenuPrincipal : Screen("trabajador_menu_principal")
    object TrabajadorRegistroAuto : Screen("trabajador_registro_auto")
    object PerfilCliente : Screen("perfil_cliente")
    object CitasRealizadas : Screen("citas_realizadas")
    object EstadoVehiculoConDetalles : Screen("estado_vehiculo/{vehiculoId}")


    // Pantallas adicionales
    object WorkOrderScreen : Screen("work_order")
    object ServiceHistoryScreen : Screen("service_history")
    object VehicleConsultationScreen : Screen("vehicle_consultation")

    // Pantalla parametrizada para actualizar estado de servicio
    object UpdateStatusScreen : Screen("update_status_screen/{serviceId}") {
        fun generaRuta(serviceId: String) = "update_status_screen/$serviceId"
        const val serviceIdArg = "serviceId"
    }
}



// Función de extensión para crear rutas con parámetros
fun Screen.createRoute(userEmail: String): String {
    return when (this) {
        is Screen.CitasRealizadas -> "${this.route}/$userEmail"
        else -> this.route
    }
}

