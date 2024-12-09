package com.example.vead.ui.slideshow

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.vead.R
import com.example.vead.data.entities.Libro

class DialogAgregarLibro(private val onLibroAgregado: (Libro) -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_agregar_libro, null)

        val etTitulo = view.findViewById<EditText>(R.id.etTitulo)
        val etAutor = view.findViewById<EditText>(R.id.etAutor)
        val etGenero = view.findViewById<EditText>(R.id.etGenero)
        val etNumeroCopias = view.findViewById<EditText>(R.id.etNumeroCopias)
        val etUbicacion = view.findViewById<EditText>(R.id.etUbicacion)

        builder.setView(view)
            .setTitle("Agregar Libro")
            .setPositiveButton("Agregar") { _, _ ->
                val libro = Libro(
                    titulo = etTitulo.text.toString(),
                    autor = etAutor.text.toString(),
                    genero = etGenero.text.toString(),
                    numeroCopias = etNumeroCopias.text.toString().toIntOrNull() ?: 0,
                    ubicacion = etUbicacion.text.toString()
                )
                onLibroAgregado(libro)
            }
            .setNegativeButton("Cancelar", null)

        return builder.create()
    }
}
