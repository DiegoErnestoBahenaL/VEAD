package com.example.vead

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vead.data.repositories.AdministradorRepository
import com.example.vead.data.repositories.EstudianteRepository

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val emailField = findViewById<EditText>(R.id.editTextEmail)
        val passwordField = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)

        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val contrasena = passwordField.text.toString()

            if (email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, complete ambos campos", Toast.LENGTH_SHORT).show()
            } else {

                val adminisitradorRepo = AdministradorRepository()
                val estudianteRepo = EstudianteRepository()

                val administrador = adminisitradorRepo.buscarUno(email)
                val estudiante = estudianteRepo.buscarUno(email)

                when {
                    administrador != null && administrador.contrasena == contrasena -> {
                        navegarADashboard(administrador.tipo, administrador.email)
                    }
                    estudiante != null && estudiante.contrasena == contrasena -> {
                        navegarADashboard(estudiante.tipo, estudiante.email)
                    }
                    else -> {
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun navegarADashboard(tipoUsuario: String, email: String) {

        val intent = Intent(this, MainActivity::class.java)
            .putExtra("TipoUsuario", tipoUsuario)
            .putExtra("Email", email)
        startActivity(intent)
        finish()
    }
}