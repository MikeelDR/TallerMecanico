package org.example.tallermecanico.ui.screens.PerfilCliente

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import org.example.tallermecanico.navigation.Screen
import android.widget.Toast

// MODELO
data class Usuario(
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = ""
)

// FUNCIÓN PARA OBTENER DATOS DE FIRESTORE
@Composable
fun rememberUsuarioState(): State<Usuario?> {
    val usuarioState = remember { mutableStateOf<Usuario?>(null) }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(Usuario::class.java)
                        usuarioState.value = user
                    } else {
                        usuarioState.value = Usuario(nombre = "Invitado", correo = "N/A", telefono = "N/A")
                    }
                }
                .addOnFailureListener {
                    usuarioState.value = Usuario(nombre = "Error", correo = "No disponible", telefono = "No disponible")
                }
        } else {
            usuarioState.value = Usuario(nombre = "No autenticado", correo = "-", telefono = "-")
        }
    }

    return usuarioState
}

// UI PRINCIPAL
@Composable
fun PerfilClienteScreen(navController: NavHostController) {
    val usuario = rememberUsuarioState().value
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var editando by remember { mutableStateOf(false) }

    LaunchedEffect(usuario) {
        if (usuario != null) {
            nombre = usuario.nombre
            correo = usuario.correo
            telefono = usuario.telefono
        }
    }

    if (usuario == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FF))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color(0xFF1F41BB))
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Mi Perfil",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F41BB)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2196F3))
                    .align(Alignment.CenterHorizontally)
                    .clickable { Toast.makeText(context, "Funcionalidad de imagen no implementada", Toast.LENGTH_SHORT).show() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Foto de perfil",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (editando) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Cliente Registrado",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(32.dp))

                ProfileItem(icon = Icons.Default.Email, label = "Correo", value = correo)
                ProfileItem(icon = Icons.Default.Phone, label = "Teléfono", value = telefono)
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (editando) {
                Button(
                    onClick = {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null) {
                            val db = FirebaseFirestore.getInstance()
                            val userMap = mapOf(
                                "nombre" to nombre,
                                "correo" to correo,
                                "telefono" to telefono
                            )
                            db.collection("usuarios").document(uid)
                                .set(userMap, SetOptions.merge())
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                                    editando = false
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        } else {
                            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Guardar", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Cambios", color = Color.White)
                }
            } else {
                Button(
                    onClick = { editando = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar Perfil", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.PantallaBienvenida.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cerrar Sesión", color = Color.White)
            }
        }
    }
}

@Composable
fun ProfileItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF2196F3),
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = label, fontSize = 14.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PerfilClientePreview() {
    PerfilClienteScreen(navController = rememberNavController())
}
