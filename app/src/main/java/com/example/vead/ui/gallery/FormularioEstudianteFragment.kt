package com.example.vead.ui.gallery

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
import com.example.vead.data.entities.Estudiante
import com.example.vead.data.repositories.EstudianteRepository
import java.util.Calendar

class FormularioEstudianteFragment : Fragment() {

    private val repository = EstudianteRepository()

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
        val etFechaCreacion = root.findViewById<EditText>(R.id.etFechaCreacion)
        val etFechaExpiracion = root.findViewById<EditText>(R.id.etFechaExpiracion)
        val etGrado = root.findViewById<EditText>(R.id.etGrado)
        val etCarrera = root.findViewById<EditText>(R.id.etCarrera)
        val etNombreTutor = root.findViewById<EditText>(R.id.etNombreTutor)
        val etGrupo = root.findViewById<EditText>(R.id.etGrupo)
        val btnGuardar = root.findViewById<Button>(R.id.btnGuardar)

        // Configurar selectores de fecha
        etFechaCreacion.setOnClickListener {
            mostrarSelectorDeFecha { fecha -> etFechaCreacion.setText(fecha) }
        }
        etFechaExpiracion.setOnClickListener {
            mostrarSelectorDeFecha { fecha -> etFechaExpiracion.setText(fecha) }
        }

        // Acción del botón Guardar
        btnGuardar.setOnClickListener {
            val estudiante = Estudiante(
                email = etEmail.text.toString(),
                contrasena = etContrasena.text.toString(),
                nombre = etNombre.text.toString(),
                fechaCreacion = etFechaCreacion.text.toString(),
                fechaExpiracion = etFechaExpiracion.text.toString(),
                tipo = "Estudiante",
                registro = etRegistro.text.toString(),
                grado = etGrado.text.toString().toIntOrNull() ?: 0,
                carrera = etCarrera.text.toString(),
                nombreTutor = etNombreTutor.text.toString(),
                grupo = etGrupo.text.toString()
            )
            repository.agregar(estudiante)
            Toast.makeText(requireContext(), "Estudiante registrado", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack() // Regresar al fragment anterior
        }

        return root
    }

    private fun mostrarSelectorDeFecha(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val fecha = "$year-${month + 1}-$dayOfMonth"
                onDateSelected(fecha)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
