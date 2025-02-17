package com.example.vead.ui.gallery

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vead.R
import com.example.vead.data.entities.User
import com.example.vead.data.repositories.UserRepository
import java.util.Calendar

class FormularioEstudianteFragment : Fragment() {

    private val repository = UserRepository()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_formulario_estudiante, container, false)

        // Referencias a los campos
        val etNombre = root.findViewById<EditText>(R.id.etNombre)
        val etEmail = root.findViewById<EditText>(R.id.etEmail)
        val etContrasena = root.findViewById<EditText>(R.id.etContrasena)
        val etRegistro = root.findViewById<EditText>(R.id.etRegistro)
        val etLastName = root.findViewById<EditText>(R.id.etLastName)
        val etPhoneNumber = root.findViewById<EditText>(R.id.etPhoneNumber)

        val btnGuardar = root.findViewById<Button>(R.id.btnGuardar)



        // Acción del botón Guardar
        btnGuardar.setOnClickListener {
            val estudiante = User(
                etRegistro.text.toString().toLong(),
                etEmail.text.toString(),
                etLastName.text.toString(),
                etNombre.text.toString(),
                etContrasena.text.toString(),
                etPhoneNumber.text.toString().toLong(),
                "Estudiante"
            )
            var userAdded = false

            repository.addUser(estudiante){ successful ->
                userAdded = successful
            }

            if (userAdded){
                Toast.makeText(requireContext(), "Estudiante registrado", Toast.LENGTH_SHORT).show()

            }
            else {
                Toast.makeText(requireContext(), "Sucedio un error al agregar usuario", Toast.LENGTH_SHORT).show()

            }
            findNavController().popBackStack() // Regresar al fragment anterior
        }

        return root
    }
}
