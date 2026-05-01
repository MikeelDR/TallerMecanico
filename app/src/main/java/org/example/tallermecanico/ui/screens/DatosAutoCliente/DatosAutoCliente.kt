package org.example.tallermecanico.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.example.tallermecanico.R
import java.time.LocalDateTime
import java.util.*

const val DATOS_AUTO_CLIENTE_ROUTE = "datos_auto_cliente"

// Datos del cliente y su vehículo
data class DatosAutoClienteModel(
    val cliente: ClienteModel,
    val vehiculo: VehiculoModel
)

data class ClienteModel(
    val id: String = "",
    val nombre: String,
    val telefono: String,
    val email: String
)

data class VehiculoModel(
    val id: String = "",
    val marca: String,
    val modelo: String,
    val anio: Int,
    val placa: String,
    val color: String
)

// Modelo para citas (renombrado para evitar conflicto)
data class CitaModel(
    val id: String = "",
    val cliente: ClienteModel,
    val vehiculo: VehiculoModel,
    val fecha: LocalDateTime,
    val descripcionProblema: String,
    val idSeguimiento: String = "" // ID para consultar estado
)

// Modelo para servicio de reparación
data class Servicio(
    val id: String = "",
    val nombre: String,
    val descripcion: String,
    val costo: Double,
    val tiempoEstimado: String
)



@Composable
fun DatosAutoClienteScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    vehiculoId: String? = null // Agregado el parámetro vehiculoId con valor predeterminado null
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var brand by remember { mutableStateOf(TextFieldValue("")) }
    var year by remember { mutableStateOf(TextFieldValue("")) }
    var plateNumber by remember { mutableStateOf(TextFieldValue("")) }
    var isNotificationChecked by remember { mutableStateOf(false) }
    var isTermsAccepted by remember { mutableStateOf(false) }

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    // Si vehiculoId no es nulo, cargar los datos del vehículo desde Firestore
    LaunchedEffect(vehiculoId) {
        vehiculoId?.let { id ->
            if (id.isNotEmpty()) {
                db.collection("citas").document(id).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            email = TextFieldValue(document.getString("email") ?: "")
                            brand = TextFieldValue(document.getString("marca") ?: "")
                            year = TextFieldValue(document.getString("año") ?: "")
                            plateNumber = TextFieldValue(document.getString("placa") ?: "")
                            isNotificationChecked = document.getBoolean("notificacion") ?: false

                            val fechaHora = document.getString("fecha_hora") ?: ""
                            if (fechaHora.isNotEmpty()) {
                                val partes = fechaHora.split(" ")
                                if (partes.size >= 2) {
                                    selectedDate = partes[0]
                                    selectedTime = partes[1]
                                }
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_account_circle),
                    contentDescription = "Account",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = if (vehiculoId != null) "Actualizar Vehículo" else "Datos del Vehículo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Text(
                text = if (vehiculoId != null)
                    "Actualice los datos de su vehículo"
                else "Ingrese los datos correspondientes\nde su vehículo a registrar",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.Gray
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Ingrese su correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Ingrese la marca de su vehículo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                label = { Text("Ingrese el año del vehículo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = plateNumber,
                onValueChange = { plateNumber = it },
                label = { Text("Ingrese el número de placa de su vehículo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            // Agenda
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Calendar",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Agende su fecha",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Button(onClick = { showDatePickerDialog = true }) {
                Text("Seleccionar fecha")
            }
            Text(text = "Fecha seleccionada: $selectedDate")

            Button(onClick = { showTimePickerDialog = true }) {
                Text("Seleccionar hora")
            }
            Text(text = "Hora seleccionada: $selectedTime")

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isNotificationChecked,
                    onCheckedChange = { isNotificationChecked = it }
                )
                Text("Deseo recibir notificación del estado de mi vehículo")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isTermsAccepted,
                    onCheckedChange = { isTermsAccepted = it }
                )
                Text("Acepto términos y condiciones")
            }

            Button(
                onClick = {
                    val uid = auth.currentUser?.uid
                    if (uid != null && selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                        val cita = hashMapOf(
                            "cliente_uid" to uid,
                            "email" to email.text,
                            "marca" to brand.text,
                            "año" to year.text,
                            "placa" to plateNumber.text,
                            "fecha_hora" to "$selectedDate $selectedTime",
                            "notificacion" to isNotificationChecked,
                            "estado" to "Pendiente"
                        )

                        // Actualizar o crear nueva cita según si tenemos vehiculoId
                        if (vehiculoId != null && vehiculoId.isNotEmpty()) {
                            // Actualizar cita existente
                            db.collection("citas").document(vehiculoId).update(cita as Map<String, Any>)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Vehículo actualizado correctamente", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // Crear nueva cita
                            db.collection("citas").add(cita)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Cita agendada correctamente", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(context, "Complete todos los campos y seleccione fecha y hora", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1F41BB)
                )
            ) {
                Text(
                    text = if (vehiculoId != null) "Actualizar" else "Aceptar",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        // DatePicker
        if (showDatePickerDialog) {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    selectedDate = "$day/${month + 1}/$year"
                    showDatePickerDialog = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // TimePicker
        if (showTimePickerDialog) {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    selectedTime = String.format("%02d:%02d", hour, minute)
                    showTimePickerDialog = false
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DatosAutoClienteScreenPreview() {
    val navController = rememberNavController()
    DatosAutoClienteScreen(navController = navController)
}