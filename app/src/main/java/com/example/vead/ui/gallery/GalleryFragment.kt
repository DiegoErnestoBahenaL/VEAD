package com.example.vead.ui.gallery

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vead.R
import com.example.vead.data.entities.User
import com.example.vead.data.repositories.UserRepository
import com.example.vead.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EstudianteAdapter
    private val repository = UserRepository()

    private var _binding: FragmentGalleryBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // Configurar RecyclerView
        recyclerView = root.findViewById(R.id.recyclerEstudiantes)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val itemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

         repository.getAllUsers{ data ->
             adapter = EstudianteAdapter(data) { estudiante ->
                 mostrarDialogoConfirmacion(estudiante)
             }
             recyclerView.adapter = adapter
        }


        // Botón para agregar estudiante
        val btnAgregarEstudiante = root.findViewById<Button>(R.id.btnAgregarEstudiante)
        btnAgregarEstudiante.setOnClickListener {
            // Ir al formulario para agregar estudiante
            findNavController().navigate(R.id.action_galleryFragment_to_formularioEstudianteFragment)
        }

        return root
    }

    private fun mostrarDialogoConfirmacion(estudiante: User) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar a ${estudiante.email}?")
            .setPositiveButton("Sí") { _, _ ->
                repository.deleteUserByEmail(estudiante.email) { succesful ->
                    if (succesful){
                        actualizarListado()
                    }
                    else {
                        Toast.makeText(requireContext(), "Sucedio un error al eliminar", Toast.LENGTH_SHORT).show()

                    }
                }

            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarListado() {
        repository.getAllUsers {data ->

                adapter = EstudianteAdapter(data) { estudiante ->
                    mostrarDialogoConfirmacion(estudiante)
                }
                recyclerView.adapter = adapter

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}