package org.example.tallermecanico.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.example.tallermecanico.navigation.Screen
import org.example.tallermecanico.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBienvenidaScreen(
    navController: NavHostController,
    usuarioViewModel: UsuarioViewModel = viewModel()
) {
    val correo = remember { mutableStateOf("") }
    val contrasena = remember { mutableStateOf("") }
    val codigoAcceso = remember { mutableStateOf("") }
    // Establecer "Cliente" como valor predeterminado
    val selectedRole = remember { mutableStateOf("Cliente") }
    val expanded = remember { mutableStateOf(false) }
    val roles = listOf("Trabajador", "Cliente")
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isAccessCodeVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showResetPasswordDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var showAccessCodeField by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Actualizar la visibilidad del campo de código de acceso cuando cambia el rol
    LaunchedEffect(selectedRole.value) {
        showAccessCodeField = selectedRole.value == "Trabajador"
    }

    // Función local para iniciar sesión - Definida aquí para acceder a todas las variables necesarias
    fun iniciarSesion() {
        usuarioViewModel.iniciarSesion(
            correo.value,
            contrasena.value,
            onSuccess = {
                isLoading = false
                // Navegar según el rol seleccionado
                if (selectedRole.value == "Trabajador") {
                    navController.navigate(Screen.TrabajadorMenuPrincipal.route) {
                        popUpTo(Screen.PantallaBienvenida.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.MenuPrincipalCliente.route) {
                        popUpTo(Screen.PantallaBienvenida.route) { inclusive = true }
                    }
                }
            },
            onError = { mensaje ->
                isLoading = false
                errorMessage = mensaje
            }
        )
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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Taller Mecánico",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F41BB)
                )
                Text(
                    text = "Inicia sesión en tu cuenta",
                    fontSize = 16.sp,
                    color = Color(0xFF1F41BB),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Menú desplegable de roles
            ExposedDropdownMenuBox(
                expanded = expanded.value,
                onExpandedChange = { expanded.value = !expanded.value }
            ) {
                OutlinedTextField(
                    value = selectedRole.value,
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
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role) },
                            onClick = {
                                selectedRole.value = role
                                expanded.value = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Campos de formulario
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = correo.value,
                    onValueChange = { correo.value = it.trim() },
                    label = { Text("Correo Electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
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

                // Campo de código de acceso para trabajadores
                if (showAccessCodeField) {
                    OutlinedTextField(
                        value = codigoAcceso.value,
                        onValueChange = { codigoAcceso.value = it },
                        label = { Text("Código de Acceso") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                        visualTransformation = if (isAccessCodeVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isAccessCodeVisible = !isAccessCodeVisible }) {
                                Icon(
                                    imageVector = if (isAccessCodeVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Mostrar/Ocultar Código"
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
                }

                // Botón para restablecer contraseña
                TextButton(
                    onClick = { showResetPasswordDialog = true },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = Color(0xFF1F41BB),
                        fontWeight = FontWeight.Bold
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
                        .padding(top = 8.dp)
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
                        if (correo.value.isEmpty() || contrasena.value.isEmpty()) {
                            errorMessage = "Por favor completa todos los campos"
                            return@Button
                        }

                        // Validación del código de acceso para trabajadores
                        if (selectedRole.value == "Trabajador" && codigoAcceso.value.isEmpty()) {
                            errorMessage = "Por favor ingresa el código de acceso"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = ""

                        // Si es un trabajador, verificar el código de acceso
                        if (selectedRole.value == "Trabajador") {
                            usuarioViewModel.verificarCodigoAcceso(
                                codigoAcceso.value,
                                onSuccess = {
                                    // Si el código es válido, continuar con el inicio de sesión
                                    iniciarSesion()
                                },
                                onError = { mensaje ->
                                    isLoading = false
                                    errorMessage = mensaje
                                }
                            )
                        } else {
                            // Si es cliente, iniciar sesión directamente
                            iniciarSesion()
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
                        Text("Iniciar sesión", fontSize = 16.sp, color = Color.White)
                    }
                }

                TextButton(
                    onClick = {
                        navController.navigate(Screen.PantallaRegistro.route)
                    }
                ) {
                    Text(
                        text = "¿No tienes cuenta? Regístrate",
                        color = Color(0xFF1F41BB),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Diálogo para restablecer contraseña
        if (showResetPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showResetPasswordDialog = false },
                title = { Text("Restablecer Contraseña") },
                text = {
                    Column {
                        Text("Ingresa tu correo electrónico para recibir instrucciones de restablecimiento")
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it.trim() },
                            label = { Text("Correo Electrónico") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (resetEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()) {
                                Toast.makeText(context, "Introduce un correo válido", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            usuarioViewModel.enviarCorreoRestablecerContrasena(
                                resetEmail,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Se ha enviado un correo para restablecer tu contraseña",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    showResetPasswordDialog = false
                                    resetEmail = ""
                                },
                                onError = { mensaje ->
                                    Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F41BB))
                    ) {
                        Text("Enviar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetPasswordDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaBienvenidaScreenPreview() {
    PantallaBienvenidaScreen(navController = rememberNavController())
}