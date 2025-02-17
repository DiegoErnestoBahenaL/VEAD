package com.example.vead.ui.slideshow

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.vead.R
import com.example.vead.data.entities.Book

class DialogActualizarLibro(
    private val book: Book,
    private val onLibroActualizado: (Book) -> Unit
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
        etTitulo.setText(book.title)
        etAutor.setText(book.author)
        etGenero.setText(book.genre)
        etNumeroCopias.setText(book.copiesNumber.toString())
        etUbicacion.setText(book.location)

        builder.setView(view)
            .setTitle("Actualizar Libro")
            .setPositiveButton("Guardar") { _, _ ->
                val bookActualizado = Book(
                    etTitulo.text.toString(),
                    etAutor.text.toString(),
                    etGenero.text.toString(),
                    etNumeroCopias.text.toString().toIntOrNull() ?: 0,
                    etUbicacion.text.toString(),
                    ""
                )
                onLibroActualizado(bookActualizado)
            }
            .setNegativeButton("Cancelar", null)

        return builder.create()
    }
}
