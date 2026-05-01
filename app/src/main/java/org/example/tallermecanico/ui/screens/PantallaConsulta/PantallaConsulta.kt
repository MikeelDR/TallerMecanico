//PASAR A PANTALLA DE TRABAJADOR

package org.example.tallermecanico.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.tallermecanico.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Enum class to define routes for navigation
object Routes {
    const val CONSULTA_SCREEN = "consulta_screen"
    const val ESTADO_SCREEN = "estado_screen"
}

// Data class to represent vehicle information
data class VehicleInfo(
    var brand: String = "Sentra",
    var year: String = "2025",
    var clientName: String = "Ramiro Perez",
    var status: String = "En proceso",
    var services: MutableList<String> = mutableStateListOf(),
    var entryDate: String = "05/02/2025",
    var exitDate: String = "20/02/2025",
    var serviceNumber: String = "#000243"
)

// List of available services in the workshop
val availableServices = listOf(
    "Cambio de aceite y filtro",
    "Cambio de batería",
    "Reparación de aire acondicionado",
    "Alineación y balanceo",
    "Cambio de frenos",
    "Revisión general",
    "Cambio de llantas",
    "Diagnóstico electrónico",
    "Reparación de suspensión",
    "Cambio de filtro de aire",
    "Limpieza de inyectores",
    "Cambio de bujías",
    "Reparación del sistema eléctrico",
    "Cambio de correa de distribución"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConsultaScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    // Vehicle information state
    val vehicleInfo = remember {
        mutableStateOf(
            VehicleInfo(
                services = mutableStateListOf(
                    "Cambio de aceite y filtro",
                    "Cambio de batería",
                    "Reparación de aire acondicionado"
                )
            )
        )
    }

    // Edit mode state
    var isEditMode by remember { mutableStateOf(false) }

    // State variables for edit dialogs
    var showServicesDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top navigation area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pantalla De Consulta",
                    style = TextStyle(fontSize = 16.sp, color = Color.Gray)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User icon and account button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(Color(0xFF1F71FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.image),
                            contentDescription = "User Icon",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1F71FF))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Cuenta",
                            color = Color.White,
                            style = TextStyle(fontSize = 12.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(32.dp))

                // Main title
                Text(
                    text = "Consulta\nVehículo",
                    color = Color(0xFF1F41BB),
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(32.dp))

                // Home icon
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(percent = 20))
                        .background(Color(0xFF1F71FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.image4),
                        contentDescription = "Home Icon",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vehicle status text
            Text(
                text = "El estado de tu vehículo es el siguiente:",
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Vehicle info card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Vehicle name header with Edit button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF3333DD))
                            .padding(vertical = 8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${vehicleInfo.value.brand} ${vehicleInfo.value.year} \"${vehicleInfo.value.clientName}\"",
                                color = Color.White,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )

                            // Edit button
                            IconButton(
                                onClick = { isEditMode = !isEditMode },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isEditMode) Icons.Filled.Close else Icons.Filled.Edit,
                                    contentDescription = if (isEditMode) "Cancel Edit" else "Edit Info",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Edit fields section - visible only in edit mode
                    if (isEditMode) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            // Brand field
                            EditTextField(
                                label = "Marca del vehículo",
                                value = vehicleInfo.value.brand,
                                onValueChange = { vehicleInfo.value = vehicleInfo.value.copy(brand = it) }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Year field
                            EditTextField(
                                label = "Año",
                                value = vehicleInfo.value.year,
                                onValueChange = { vehicleInfo.value = vehicleInfo.value.copy(year = it) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Client name field
                            EditTextField(
                                label = "Nombre del cliente",
                                value = vehicleInfo.value.clientName,
                                onValueChange = { vehicleInfo.value = vehicleInfo.value.copy(clientName = it) }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Status dropdown
                            val statusOptions = listOf("En proceso", "Finalizado")
                            var expanded by remember { mutableStateOf(false) }

                            Column {
                                Text(
                                    text = "Estado",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(start = 4.dp)
                                )

                                OutlinedTextField(
                                    value = vehicleInfo.value.status,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        Icon(
                                            Icons.Filled.ArrowDropDown,
                                            contentDescription = "Dropdown",
                                            modifier = Modifier.clickable { expanded = true }
                                        )
                                    }
                                )

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    statusOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(text = option) },
                                            onClick = {
                                                vehicleInfo.value = vehicleInfo.value.copy(status = option)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Services selection button
                            Button(
                                onClick = { showServicesDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1F71FF)
                                )
                            ) {
                                Text("Seleccionar Servicios")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Service Number (ID) field
                            EditTextField(
                                label = "Número de servicio",
                                value = vehicleInfo.value.serviceNumber.replace("#", ""),
                                onValueChange = { vehicleInfo.value = vehicleInfo.value.copy(serviceNumber = "#$it") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Date pickers
                            DatePickerField(
                                label = "Fecha de entrada",
                                value = vehicleInfo.value.entryDate,
                                onValueChange = { vehicleInfo.value = vehicleInfo.value.copy(entryDate = it) }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            DatePickerField(
                                label = "Fecha de salida estimada",
                                value = vehicleInfo.value.exitDate,
                                onValueChange = { vehicleInfo.value = vehicleInfo.value.copy(exitDate = it) }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Save button
                            Button(
                                onClick = { isEditMode = false },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1F41BB)
                                )
                            ) {
                                Text("Guardar Cambios")
                            }
                        }
                    } else {
                        // Regular view mode
                        // Vehicle image
                        Image(
                            painter = painterResource(id = R.drawable.image),
                            contentDescription = "Vehicle Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = 2.dp,
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Status
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Estado:",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "\"${vehicleInfo.value.status}\"",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = if (vehicleInfo.value.status == "Finalizado") Color.Green else Color(0xFF1F41BB)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Services
                        Text(
                            text = "Servicios:",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            vehicleInfo.value.services.forEach { service ->
                                Text(
                                    text = "- $service",
                                    style = TextStyle(fontSize = 14.sp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dates
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Fecha de entrada:",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = vehicleInfo.value.entryDate,
                                    style = TextStyle(fontSize = 14.sp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Fecha de salida estimada:",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = vehicleInfo.value.exitDate,
                                    style = TextStyle(fontSize = 14.sp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Service number
                        Text(
                            text = "Número de servicio:",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = vehicleInfo.value.serviceNumber,
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Contact info
                        Text(
                            text = "¿Dudas? Consulta al taller mediante el número telefónico",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color(0xFF1F71FF)
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "81 1234567890",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Services selection dialog
        if (showServicesDialog) {
            ServicesSelectionDialog(
                selectedServices = vehicleInfo.value.services,
                onDismissRequest = { showServicesDialog = false }
            )
        }
    }
}

@Composable
fun EditTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = keyboardOptions,
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = { },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha"
                    )
                }
            }
        )
    }

    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Date(millis)
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            onValueChange(formatter.format(date))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun ServicesSelectionDialog(
    selectedServices: MutableList<String>,
    onDismissRequest: () -> Unit
) {
    // Create a temporary list to track changes
    val tempSelectedServices = remember {
        mutableStateListOf<String>().apply {
            addAll(selectedServices)
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Seleccionar Servicios",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(availableServices) { service ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = tempSelectedServices.contains(service),
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        if (!tempSelectedServices.contains(service)) {
                                            tempSelectedServices.add(service)
                                        }
                                    } else {
                                        tempSelectedServices.remove(service)
                                    }
                                }
                            )

                            Text(
                                text = service,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancelar")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            // Update the original list
                            selectedServices.clear()
                            selectedServices.addAll(tempSelectedServices)
                            onDismissRequest()
                        }
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaDeEstado(modifier: Modifier = Modifier, navController: NavHostController) {
    // Implementar pantalla de estado
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Pantalla de Estado - No implementada aún",
            color = Color(0xff1f41bb),
            style = TextStyle(fontSize = 20.sp)
        )
    }
}

@Preview(widthDp = 428, heightDp = 926)
@Composable
private fun PantallaConsultaScreenPreview() {
    PantallaConsultaScreen(modifier = Modifier, navController = rememberNavController())
}

// NavGraph component to handle navigation between screens
@Composable
fun TallerMecanicoNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.CONSULTA_SCREEN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.CONSULTA_SCREEN) {
            PantallaConsultaScreen(navController = navController)
        }
        composable(Routes.ESTADO_SCREEN) {
            PantallaDeEstado(navController = navController)
        }
    }
}

@Preview(widthDp = 428, heightDp = 926)
@Composable
fun TallerMecanicoNavGraphPreview(showSystemUI: Boolean = true) {
    TallerMecanicoNavGraph()
}