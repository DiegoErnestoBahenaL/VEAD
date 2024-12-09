package com.example.vead.ui.slideshow

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.vead.R
import com.example.vead.data.entities.Libro

class DialogActualizarLibro(
    private val libro: Libro,
    private val onLibroActualizado: (Libro) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_actualizar_libro, null)

        val etTitulo = view.findViewById<EditText>(R.id.etTituloActualizar)
        val etAutor = view.findViewById<EditText>(R.id.etAutorActualizar)
        val etGenero = view.findViewById<EditText>(R.id.etGeneroActualizar)
        val etNumeroCopias = view.findViewById<EditText>(R.id.etNumeroCopiasActualizar)
        val etUbicacion = view.findViewById<EditText>(R.id.etUbicacionActualizar)

        // Pre-cargar datos del libro
        etTitulo.setText(libro.titulo)
        etAutor.setText(libro.autor)
        etGenero.setText(libro.genero)
        etNumeroCopias.setText(libro.numeroCopias.toString())
        etUbicacion.setText(libro.ubicacion)

        builder.setView(view)
            .setTitle("Actualizar Libro")
            .setPositiveButton("Guardar") { _, _ ->
                val libroActualizado = Libro(
                    titulo = etTitulo.text.toString(),
                    autor = etAutor.text.toString(),
                    genero = etGenero.text.toString(),
                    numeroCopias = etNumeroCopias.text.toString().toIntOrNull() ?: 0,
                    ubicacion = etUbicacion.text.toString()
                )
                onLibroActualizado(libroActualizado)
            }
            .setNegativeButton("Cancelar", null)

        return builder.create()
    }
}
