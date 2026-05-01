package org.example.tallermecanico.ui.theme.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.example.tallermecanico.R
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.example.tallermecanico.MainScreenActivity
import org.example.tallermecanico.data.models.Usuario

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val name = findViewById<EditText>(R.id.etName)
        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        // Navegar a la pantalla de inicio de sesión
        tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Registrar usuario
        btnRegister.setOnClickListener {
            val userName = name.text.toString()
            val userEmail = email.text.toString()
            val userPassword = password.text.toString()

            if (userName.isNotEmpty() && userEmail.isNotEmpty() && userPassword.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnSuccessListener { authResult ->
                        val user = authResult.user
                        val usuario = Usuario(user!!.uid, userName, userEmail, "cliente")

                        // Guardar en Firestore
                        db.collection("usuarios").document(user.uid).set(usuario)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainScreenActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al guardar usuario", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
