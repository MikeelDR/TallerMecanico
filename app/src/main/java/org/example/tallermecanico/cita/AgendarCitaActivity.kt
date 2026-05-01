package org.example.tallermecanico.ui.agendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import org.example.tallermecanico.data.models.Cita
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import java.util.Calendar
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendarCitaScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Inicializar Firebase
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    // Estados para los campos del formulario
    var servicioSeleccionado by remember { mutableStateOf("") }
    var fechaSeleccionada by remember { mutableStateOf("") }
    var horaSeleccionada by remember { mutableStateOf("") }
    var marcaVehiculo by remember { mutableStateOf("") }
    var añoVehiculo by remember { mutableStateOf("") }
    var placaVehiculo by remember { mutableStateOf("") }

    // Estado para los servicios disponibles
    var serviciosDisponibles by remember { mutableStateOf(listOf<String>()) }
    var cargandoServicios by remember { mutableStateOf(true) }

    // Colores del tema
    val primaryColor = Color(0xFF1F41BB)
    val backgroundColor = Color(0xFFF8F9FF)
    val cardColor = Color(0xFFFFFFFF)
    val textSecondaryColor = Color(0xFF555555)

    // Cargar lista de servicios desde Firestore
    LaunchedEffect(Unit) {
        db.collection("servicios").get()
            .addOnSuccessListener { documents ->
                val listaServicios = documents.mapNotNull { doc ->
                    doc.getString("nombre")
                }
                serviciosDisponibles = listaServicios
                cargandoServicios = false
                if (listaServicios.isNotEmpty()) {
                    servicioSeleccionado = listaServicios[0]
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AgendarCitaScreen", "Error al cargar servicios: ${exception.message}")
                cargandoServicios = false
            }
    }

    // Función para seleccionar fecha
    fun seleccionarFecha() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, day ->
                fechaSeleccionada = String.format("%02d/%02d/%d", day, month + 1, year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Función para seleccionar hora
    fun seleccionarHora() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hour, minute ->
                horaSeleccionada = String.format("%02d:%02d", hour, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    // Función para reservar la cita
    fun reservarCita() {
        val usuarioUid = auth.currentUser?.uid
        val userEmail = auth.currentUser?.email

        if (usuarioUid == null || userEmail == null || fechaSeleccionada.isEmpty() || horaSeleccionada.isEmpty()) {
            Toast.makeText(context, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar año como número
        val añoInt = try {
            if (añoVehiculo.isNotEmpty()) añoVehiculo.toInt() else null
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "El año debe ser un número válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear objeto Cita completo
        val cita = Cita(
            id = "null", // Firestore generará el ID
            cliente_uid = usuarioUid,
            email = userEmail,
            servicio = servicioSeleccionado,
            fecha_hora = "$fechaSeleccionada $horaSeleccionada",
            estado = "Pendiente",
            marca = if (marcaVehiculo.isEmpty()) "No especificada" else marcaVehiculo,
            año = añoInt.toString(),
            placa = if (placaVehiculo.isEmpty()) "No especificada" else placaVehiculo,
            userId = TODO(),
            nombre = TODO(),
            telefono = TODO(),
            modelo = TODO(),
            anio = TODO(),
            kilometraje = TODO(),
            descripcionProblema = TODO(),
            fechaSolicitud = TODO(),
            fechaDeseada = TODO(),
            horaDeseada = TODO(),
            vehiculoId = TODO(),
            servicioId = TODO(),
            fechaCreacion = TODO(),
            nombreCliente = TODO(),
            marcaVehiculo = TODO(),
            modeloVehiculo = TODO(),
            anioVehiculo = TODO(),
            anoVehiculo = TODO(),
            placaVehiculo = TODO(),
            kilometrajeVehiculo = TODO(),
            descripcionProblemaVehiculo = TODO(),
            time = TODO(),
            name = TODO(),
            citaId = TODO(),
            servicioid = TODO(),
            propietario = TODO(),
            clienteId = TODO(),
            notas = TODO(),
            diagnostico = TODO(),
            fecha = TODO(),
            hora = TODO(),
            needsSync = TODO(),
            syncId = TODO(),
            selectedDate = TODO(),
            idSeguimiento = TODO(),
            usuarioId = TODO()
        )

        // Convertir el objeto Cita a HashMap para Firestore
        val citaMap = hashMapOf(
            "cliente_uid" to cita.cliente_uid,
            "email" to cita.email,
            "servicio" to cita.servicio,
            "fecha_hora" to cita.fecha_hora,
            "estado" to cita.estado,
            "marca" to cita.marca,
            "año" to cita.año,
            "placa" to cita.placa
        )

        // Guardar en Firestore
        db.collection("citas").add(citaMap)
            .addOnSuccessListener {
                Toast.makeText(context, "Cita agendada correctamente", Toast.LENGTH_SHORT).show()
                // Navegar de vuelta a la pantalla anterior o a una pantalla de confirmación
                navController.popBackStack()
            }
            .addOnFailureListener { e ->
                Log.e("AgendarCitaScreen", "Error al agendar cita: ${e.message}")
                Toast.makeText(context, "Error al agendar la cita: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título
            Text(
                text = "Agendar Cita",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = primaryColor,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Selector de servicio
                    Text(
                        text = "Tipo de Servicio",
                        fontWeight = FontWeight.Medium,
                        color = textSecondaryColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (cargandoServicios) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterHorizontally),
                            color = primaryColor
                        )
                    } else {
                        ExposedDropdownMenuBox(
                            expanded = false,
                            onExpandedChange = { /* Implementar desplegable */ }
                        ) {
                            TextField(
                                value = servicioSeleccionado,
                                onValueChange = { /* No se edita directamente */ },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campos de fecha y hora
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Fecha",
                                fontWeight = FontWeight.Medium,
                                color = textSecondaryColor
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { seleccionarFecha() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                            ) {
                                Text(
                                    text = if (fechaSeleccionada.isEmpty()) "Seleccionar Fecha" else fechaSeleccionada
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Hora",
                                fontWeight = FontWeight.Medium,
                                color = textSecondaryColor
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { seleccionarHora() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                            ) {
                                Text(
                                    text = if (horaSeleccionada.isEmpty()) "Seleccionar Hora" else horaSeleccionada
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campos del vehículo (opcionales pero recomendados)
                    Text(
                        text = "Datos del Vehículo (Opcionales)",
                        fontWeight = FontWeight.Medium,
                        color = textSecondaryColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = marcaVehiculo,
                        onValueChange = { marcaVehiculo = it },
                        label = { Text("Marca del vehículo") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = añoVehiculo,
                        onValueChange = { añoVehiculo = it },
                        label = { Text("Año del vehículo") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = placaVehiculo,
                        onValueChange = { placaVehiculo = it },
                        label = { Text("Placa del vehículo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón para confirmar la cita
            Button(
                onClick = { scope.launch { reservarCita() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Confirmar Cita",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}