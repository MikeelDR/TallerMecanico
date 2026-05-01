
package org.example.tallermecanico.data.models

data class Usuariov(
    val uid: String = "",
    val id: String = "",
    val nombre: String = "",
    val email: String = "",
    val phone : String = "",
    val apellido : String = "",
    val direccion : String = "",
    val confirmarcontrasena : String = "",
    val contrasena: String = "",
    val tipo: String = "cliente", // Puede ser "cliente" o "admin"
    val tipoUsuario : String = "cliente", // Puede ser "cliente" o "admin"
    val rol: String = "cliente",
)
