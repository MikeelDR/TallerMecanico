package org.example.tallermecanico.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import org.example.tallermecanico.navigation.Screen
import org.example.tallermecanico.viewmodel.UsuarioViewModel
import org.example.tallermecanico.data.models.Usuariov

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistroScreen(
    navController: NavHostController,
    usuarioViewModel: UsuarioViewModel = viewModel()
) {
    val nombre = remember { mutableStateOf("") }
    val apellido = remember { mutableStateOf("") }
    val correo = remember { mutableStateOf("") }
    val contrasena = remember { mutableStateOf("") }
    val confirmarContrasena = remember { mutableStateOf("") }
    val telefono = remember { mutableStateOf("") }
    val rol = remember { mutableStateOf("Cliente") } // Valor por defecto
    val expanded = remember { mutableStateOf(false) }
    val roles = listOf("Cliente", "Trabajador")
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var codigoAcceso = remember { mutableStateOf("") }
    var showAccessCodeField by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Actualizar la visibilidad del campo de código de acceso cuando cambia el rol
    LaunchedEffect(rol.value) {
        showAccessCodeField = rol.value == "Trabajador"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE3E6FF), Color.White)
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Registro",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F41BB)
                )
                Text(
                    text = "Crea una nueva cuenta",
                    fontSize = 16.sp,
                    color = Color(0xFF1F41BB),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Formulario de registro
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Nombre
                OutlinedTextField(
                    value = nombre.value,
                    onValueChange = { nombre.value = it.trim() },
                    label = { Text("Nombre") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF1F41BB),
                        unfocusedTextColor = Color(0xFF333333),
                        focusedLabelColor = Color(0xFF1F41BB),
                        unfocusedLabelColor = Color(0xFF666666)
                    )
                )

                // Apellido
                OutlinedTextField(
                    value = apellido.value,
                    onValueChange = { apellido.value = it.trim() },
                    label = { Text("Apellido") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF1F41BB),
                        unfocusedTextColor = Color(0xFF333333),
                        focusedLabelColor = Color(0xFF1F41BB),
                        unfocusedLabelColor = Color(0xFF666666)
                    )
                )

                // Correo
                OutlinedTextField(
                    value = correo.value,
                    onValueChange = { correo.value = it.trim() },
                    label = { Text("Correo Electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF1F41BB),
                        unfocusedTextColor = Color(0xFF333333),
                        focusedLabelColor = Color(0xFF1F41BB),
                        unfocusedLabelColor = Color(0xFF666666)
                    )
                )

                // Teléfono
                OutlinedTextField(
                    value = telefono.value,
                    onValueChange = { telefono.value = it.trim() },
                    label = { Text("Teléfono") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF1F41BB),
                        unfocusedTextColor = Color(0xFF333333),
                        focusedLabelColor = Color(0xFF1F41BB),
                        unfocusedLabelColor = Color(0xFF666666)
                    )
                )

                // Contraseña
                OutlinedTextField(
                    value = contrasena.value,
                    onValueChange = { contrasena.value = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Mostrar/Ocultar Contraseña"
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF1F41BB),
                        unfocusedTextColor = Color(0xFF333333),
                        focusedLabelColor = Color(0xFF1F41BB),
                        unfocusedLabelColor = Color(0xFF666666)
                    )
                )

                // Confirmar Contraseña
                OutlinedTextField(
                    value = confirmarContrasena.value,
                    onValueChange = { confirmarContrasena.value = it },
                    label = { Text("Confirmar Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Mostrar/Ocultar Contraseña"
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF1F41BB),
                        unfocusedTextColor = Color(0xFF333333),
                        focusedLabelColor = Color(0xFF1F41BB),
                        unfocusedLabelColor = Color(0xFF666666)
                    )
                )

                // Menú desplegable de roles
                ExposedDropdownMenuBox(
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = !expanded.value }
                ) {
                    OutlinedTextField(
                        value = rol.value,
                        onValueChange = {},
                        label = { Text("Rol") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1F41BB),
                            unfocusedTextColor = Color(0xFF333333),
                            focusedLabelColor = Color(0xFF1F41BB),
                            unfocusedLabelColor = Color(0xFF666666)
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        roles.forEach { rolOption ->
                            DropdownMenuItem(
                                text = { Text(rolOption) },
                                onClick = {
                                    rol.value = rolOption
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }

                // Campo de código de acceso para trabajadores
                if (showAccessCodeField) {
                    OutlinedTextField(
                        value = codigoAcceso.value,
                        onValueChange = { codigoAcceso.value = it },
                        label = { Text("Código de Acceso") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1F41BB),
                            unfocusedTextColor = Color(0xFF333333),
                            focusedLabelColor = Color(0xFF1F41BB),
                            unfocusedLabelColor = Color(0xFF666666)
                        )
                    )
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Botones
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        // Validación básica
                        if (nombre.value.isEmpty() || apellido.value.isEmpty() || correo.value.isEmpty() ||
                            contrasena.value.isEmpty() || confirmarContrasena.value.isEmpty() || telefono.value.isEmpty()) {
                            errorMessage = "Por favor completa todos los campos"
                            return@Button
                        }

                        // Validar formato de correo electrónico
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo.value).matches()) {
                            errorMessage = "Correo electrónico inválido"
                            return@Button
                        }

                        // Validar que las contraseñas coincidan
                        if (contrasena.value != confirmarContrasena.value) {
                            errorMessage = "Las contraseñas no coinciden"
                            return@Button
                        }

                        // Validar código de acceso para trabajadores
                        if (rol.value == "Trabajador" && codigoAcceso.value.isEmpty()) {
                            errorMessage = "Por favor ingresa el código de acceso"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = ""

                        // Si es un trabajador, verificar el código de acceso primero
                        if (rol.value == "Trabajador") {
                            usuarioViewModel.verificarCodigoAcceso(
                                codigoAcceso.value,
                                onSuccess = {
                                    // Si el código es correcto, procedemos con el registro
                                    registrarUsuario(
                                        nombre.value,
                                        apellido.value,
                                        correo.value,
                                        contrasena.value,
                                        telefono.value,
                                        rol.value,
                                        usuarioViewModel,
                                        context,
                                        navController
                                    ) { isLoading = false }
                                },
                                onError = { mensaje ->
                                    isLoading = false
                                    errorMessage = mensaje
                                }
                            )
                        } else {
                            // Si es cliente, registrar directamente
                            registrarUsuario(
                                nombre.value,
                                apellido.value,
                                correo.value,
                                contrasena.value,
                                telefono.value,
                                rol.value,
                                usuarioViewModel,
                                context,
                                navController
                            ) { isLoading = false }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F41BB)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Registrar", fontSize = 16.sp, color = Color.White)
                    }
                }

                TextButton(
                    onClick = {
                        navController.navigateUp()
                    }
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? Inicia sesión",
                        color = Color(0xFF1F41BB),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Función para registrar usuario separada para mantener el código limpio
private fun registrarUsuario(
    nombre: String,
    apellido: String,
    correo: String,
    contrasena: String,
    telefono: String,
    rol: String,
    usuarioViewModel: UsuarioViewModel,
    context: android.content.Context,
    navController: NavHostController,
    onComplete: () -> Unit
) {
    // Crear el objeto usuario con los valores de los campos
    val nuevoUsuario = Usuariov(
        id = "", // ID se generará en el backend
        nombre = nombre,
        apellido = apellido,
        email = correo,
        contrasena = contrasena,
        phone = telefono,
        rol = rol
    )

    // Registrar al usuario en Firebase Authentication y Firestore
    usuarioViewModel.registrarUsuario(
        nombre = nombre,
        apellido = apellido,
        correo = correo,
        contrasena = contrasena,
        telefono = telefono,
        rol = rol,
        onSuccess = {
            onComplete()
            Toast.makeText(
                context,
                "Cuenta creada con éxito. Por favor inicia sesión",
                Toast.LENGTH_LONG
            ).show()
            navController.navigate(Screen.PantallaBienvenida.route) {
                popUpTo(Screen.PantallaBienvenida.route) { inclusive = true }
            }
        },
        onError = { mensaje ->
            onComplete()
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
        }
    )
}