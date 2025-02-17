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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vead.R
import com.example.vead.data.entities.Book
import com.example.vead.data.repositories.BookRepository
import com.example.vead.data.repositories.RequestRepository
import com.example.vead.data.repositories.UserRepository
import com.example.vead.databinding.FragmentSlideshowBinding
import kotlinx.coroutines.launch

class SlideshowFragment : Fragment() {

    private val bookRepo = BookRepository()
    private val userRepo = UserRepository()
    private val requestRepo = RequestRepository()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LibroAdapter

    private var tipoUsuario: String? = null

    private var email: String? = null
    private var code: String? = null




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

        tipoUsuario = activity?.intent?.getStringExtra("UserType")
        email = activity?.intent?.getStringExtra("Email")
        code = (requireActivity().intent.getLongExtra("Code", 0) ?: "").toString()



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

        bookRepo.getAllBooks { data ->
            adapter = LibroAdapter(
                data,
                tipoUsuario ?: "",
                onSolicitarClick = { libro -> solicitarPrestamo(libro) },
                onActualizarClick = { libro -> mostrarDialogActualizarLibro(libro) },
                onEliminarClick = { libro -> mostrarDialogConfirmarEliminacion(libro) }
            )
            recyclerView.adapter = adapter
        }

    }

    private fun solicitarPrestamo(book: Book) {

        // Mostrar el diálogo para capturar las fechas
        val dialog = DialogSolicitudPrestamo(
            tituloLibro = book.title,
            registroEstudiante = code!!.toLong()
        ) { solicitud ->
            requestRepo.addRequest(solicitud){ succesful ->
                if (succesful){
                    Toast.makeText(requireContext(), "Solicitud de préstamo creada con éxito.", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(requireContext(), "Hubo un error al crear la solicitud.", Toast.LENGTH_SHORT).show()

                }
            }
        }
        dialog.show(parentFragmentManager, "DialogSolicitudPrestamo")
    }

    private fun mostrarDialogAgregarLibro() {
        val dialog = DialogAgregarLibro { libro ->

            bookRepo.addBook(libro){ successful ->
                if (successful){
                    actualizarListaLibros()
                }
                else {
                    Toast.makeText(requireContext(), "Hubo un error al agregar el libro.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show(parentFragmentManager, "DialogAgregarLibro")
    }

    private fun mostrarDialogActualizarLibro(book: Book) {
        val dialog = DialogActualizarLibro(book) { libroActualizado ->

            bookRepo.updateBookByTitle(book.title, libroActualizado){ successful ->
                if (successful){
                    actualizarListaLibros()
                }
                else {
                    Toast.makeText(requireContext(), "Hubo un error al actualizar el libro.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show(parentFragmentManager, "DialogActualizarLibro")
    }

    private fun mostrarDialogConfirmarEliminacion(book: Book) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar '${book.title}'?")
            .setPositiveButton("Sí") { _, _ ->

                bookRepo.deleteBookByTitle(book.title){ successful ->
                    if (successful){
                        actualizarListaLibros()
                    }
                    else {
                        Toast.makeText(requireContext(), "Hubo un error al eliminar el libro.", Toast.LENGTH_SHORT).show()

                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}