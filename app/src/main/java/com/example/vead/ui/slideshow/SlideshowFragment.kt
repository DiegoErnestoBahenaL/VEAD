package com.example.vead.ui.slideshow

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vead.R
import com.example.vead.data.entities.Book
import com.example.vead.data.repositories.BookRepository
import com.example.vead.data.repositories.RequestRepository
import com.example.vead.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LibroAdapter
    private val bookRepository = BookRepository()
    private var tipoUsuario: String? = null


    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tipoUsuario = activity?.intent?.getStringExtra("TipoUsuario")

        recyclerView = root.findViewById(R.id.recyclerLibros)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        actualizarListaLibros()

        // Configura el botón de agregar (solo para administradores)
        val btnAgregarLibro = root.findViewById<Button>(R.id.btnAgregarLibro)
        if (tipoUsuario == "Administrador") {
            btnAgregarLibro.visibility = View.VISIBLE
            btnAgregarLibro.setOnClickListener {
                mostrarDialogAgregarLibro()
            }
        } else {
            btnAgregarLibro.visibility = View.GONE
        }


        return root
    }

    private fun actualizarListaLibros() {
        val libros = bookRepository.obtenerTodos()
        adapter = LibroAdapter(
            libros,
            tipoUsuario ?: "",
            onSolicitarClick = { libro -> solicitarPrestamo(libro) },
            onActualizarClick = { libro -> mostrarDialogActualizarLibro(libro) },
            onEliminarClick = { libro -> mostrarDialogConfirmarEliminacion(libro) }
        )
        recyclerView.adapter = adapter
    }

    private fun solicitarPrestamo(book: Book) {
        // Obtener el registro del estudiante desde el repositorio
        val emailEstudiante = activity?.intent?.getStringExtra("Email") ?: ""
        val estudiante = EstudianteRepository().buscarUno(emailEstudiante)

        if (estudiante == null) {
            Toast.makeText(requireContext(), "No se pudo obtener la información del estudiante.", Toast.LENGTH_SHORT).show()
            return
        }

        // Mostrar el diálogo para capturar las fechas
        val dialog = DialogSolicitudPrestamo(
            tituloLibro = book.titulo,
            registroEstudiante = estudiante.registro
        ) { solicitud ->

            RequestRepository().agregar(solicitud)
            Toast.makeText(requireContext(), "Solicitud de préstamo creada con éxito.", Toast.LENGTH_SHORT).show()
        }

        dialog.show(parentFragmentManager, "DialogSolicitudPrestamo")
    }

    private fun mostrarDialogAgregarLibro() {
        val dialog = DialogAgregarLibro { libro ->
            bookRepository.agregar(libro)
            actualizarListaLibros()
        }
        dialog.show(parentFragmentManager, "DialogAgregarLibro")
    }

    private fun mostrarDialogActualizarLibro(book: Book) {
        val dialog = DialogActualizarLibro(book) { libroActualizado ->
            bookRepository.actualizar(book.titulo, libroActualizado)
            actualizarListaLibros()
        }
        dialog.show(parentFragmentManager, "DialogActualizarLibro")
    }

    private fun mostrarDialogConfirmarEliminacion(book: Book) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar '${book.titulo}'?")
            .setPositiveButton("Sí") { _, _ ->
                bookRepository.eliminar(book.titulo)
                actualizarListaLibros()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}