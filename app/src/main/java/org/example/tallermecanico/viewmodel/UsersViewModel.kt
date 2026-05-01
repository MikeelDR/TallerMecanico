package org.example.tallermecanico.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsuarioViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("usuarios")
    private val accessCodeCollection = firestore.collection("codigos_acceso")

    // Inicializar en el constructor
    init {
        inicializarCodigoAcceso()
    }

    /**
     * Inicializa un código de acceso predeterminado si no existe ninguno
     */
    private fun inicializarCodigoAcceso() {
        viewModelScope.launch {
            try {
                // Comprobar si ya existe algún código de acceso
                val snapshot = withContext(Dispatchers.IO) {
                    accessCodeCollection.get().await()
                }

                // Si no hay códigos de acceso, crear uno predeterminado
                if (snapshot.isEmpty) {
                    val codigoDefault = hashMapOf(
                        "codigo" to "TALLER2025", // Este será su código de acceso predeterminado
                        "descripcion" to "Código de acceso para trabajadores",
                        "fechaCreacion" to com.google.firebase.Timestamp.now()
                    )

                    withContext(Dispatchers.IO) {
                        accessCodeCollection.add(codigoDefault).await()
                    }
                }
            } catch (e: Exception) {
                // Manejar posibles errores
                println("Error al inicializar código de acceso: ${e.message}")
            }
        }
    }

    /**
     * Verifica si el código de acceso para los trabajadores es válido
     */
    fun verificarCodigoAcceso(
        codigo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val snapshot = withContext(Dispatchers.IO) {
                    accessCodeCollection.whereEqualTo("codigo", codigo).get().await()
                }

                if (snapshot.isEmpty) {
                    onError("Código de acceso inválido")
                } else {
                    // El código de acceso es válido
                    onSuccess()
                }
            } catch (e: Exception) {
                onError("Error al verificar el código: ${e.message}")
            }
        }
    }

    /**
     * Obtiene los códigos de acceso (para administradores)
     */
    fun obtenerCodigosAcceso(
        onSuccess: (List<Map<String, Any>>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val snapshot = withContext(Dispatchers.IO) {
                    accessCodeCollection.get().await()
                }

                val codigosList = snapshot.documents.map { doc ->
                    val data = doc.data ?: mapOf()
                    data + mapOf("id" to doc.id)
                }

                onSuccess(codigosList)
            } catch (e: Exception) {
                onError("Error al obtener códigos: ${e.message}")
            }
        }
    }

    /**
     * Inicia sesión con correo y contraseña
     */
    fun iniciarSesion(
        correo: String,
        contrasena: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    auth.signInWithEmailAndPassword(correo, contrasena).await()
                }

                // Verificar si el usuario existe en Firestore
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val usuarioDoc = withContext(Dispatchers.IO) {
                        usersCollection.document(userId).get().await()
                    }

                    if (usuarioDoc.exists()) {
                        onSuccess()
                    } else {
                        // Si el usuario no existe en Firestore, cerrar sesión en Auth
                        auth.signOut()
                        onError("Usuario no encontrado en el sistema")
                    }
                } else {
                    onError("Error al iniciar sesión")
                }
            } catch (e: Exception) {
                onError(obtenerMensajeError(e.message))
            }
        }
    }

    /**
     * Envía correo para restablecer contraseña
     */
    fun enviarCorreoRestablecerContrasena(
        correo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    auth.sendPasswordResetEmail(correo).await()
                }
                onSuccess()
            } catch (e: Exception) {
                onError("No pudimos enviar el correo: ${e.message}")
            }
        }
    }

    /**
     * Registrar un nuevo usuario (cliente o trabajador)
     */
    fun registrarUsuario(
        nombre: String,
        apellido: String,
        correo: String,
        contrasena: String,
        telefono: String,
        rol: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Crear usuario en Firebase Auth
                val authResult = withContext(Dispatchers.IO) {
                    auth.createUserWithEmailAndPassword(correo, contrasena).await()
                }

                val userId = authResult.user?.uid
                if (userId != null) {
                    // Crear el documento del usuario en Firestore
                    val userData = hashMapOf(
                        "nombre" to nombre,
                        "apellido" to apellido,
                        "correo" to correo,
                        "telefono" to telefono,
                        "rol" to rol,
                        "fechaRegistro" to com.google.firebase.Timestamp.now()
                    )

                    withContext(Dispatchers.IO) {
                        usersCollection.document(userId).set(userData).await()
                    }

                    onSuccess()
                } else {
                    onError("Error al crear el usuario")
                }
            } catch (e: Exception) {
                onError(obtenerMensajeError(e.message))
            }
        }
    }

    /**
     * Cerrar sesión del usuario actual
     */
    fun cerrarSesion(onComplete: () -> Unit) {
        auth.signOut()
        onComplete()
    }

    fun crearCodigoAccesoManualmente(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val codigoDefault = hashMapOf(
                    "codigo" to "TALLER2025",
                    "descripcion" to "Código de acceso para trabajadores",
                    "fechaCreacion" to com.google.firebase.Timestamp.now()
                )

                withContext(Dispatchers.IO) {
                    accessCodeCollection.add(codigoDefault).await()
                }

                onSuccess()
            } catch (e: Exception) {
                onError("Error al crear código de acceso: ${e.message}")
            }
        }
    }

    /**
     * Obtiene el mensaje de error adecuado según el error de Firebase
     */
    private fun obtenerMensajeError(errorMessage: String?): String {
        return when {
            errorMessage?.contains("no user record") == true -> "No existe una cuenta con este correo"
            errorMessage?.contains("password is invalid") == true -> "Contraseña incorrecta"
            errorMessage?.contains("email address is badly formatted") == true -> "Formato de correo electrónico inválido"
            errorMessage?.contains("email already in use") == true -> "El correo ya está registrado"
            errorMessage?.contains("network error") == true -> "Error de conexión"
            else -> "Error en la autenticación: $errorMessage"
        }
    }
}
