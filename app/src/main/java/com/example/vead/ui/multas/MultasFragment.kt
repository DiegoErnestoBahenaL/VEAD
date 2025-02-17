package com.example.vead.ui.multas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vead.R
import com.example.vead.data.entities.Fine
import com.example.vead.data.repositories.FineRepository
import com.example.vead.data.repositories.UserRepository
import kotlinx.coroutines.launch

class MultasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MultaAdapter

    private val repository = FineRepository()
    private val userRepository = UserRepository()
    private var tipoUsuario: String? = null
    private var registroEstudiante: Long? = null
    private var emailUser : String? = null
    private var estudiantes = emptyList<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_multas, container, false)

        tipoUsuario = activity?.intent?.getStringExtra("UserType")

        emailUser = activity?.intent?.getStringExtra("Email")

        registroEstudiante = requireActivity().intent.getLongExtra("Code", 0)


        userRepository.getAllUsers { data ->
            estudiantes =  data.map {it.code.toString()}
        }



        recyclerView = root.findViewById(R.id.recyclerMultas)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (tipoUsuario == "Administrador"){
            repository.getAllFines { data ->
                adapter = MultaAdapter(
                    data,
                    tipoUsuario ?: "",
                    onEliminarClick = { eliminarMulta(it) }
                )
                recyclerView.adapter = adapter
            }
        }
        else {
            repository.getFinesByUserCode(registroEstudiante!!){ data ->
                adapter = MultaAdapter(
                    data,
                    tipoUsuario ?: "",
                    onEliminarClick = { eliminarMulta(it) }
                )
                recyclerView.adapter = adapter
            }
        }


        val btnAgregarMulta = root.findViewById<Button>(R.id.btnAgregarMulta)
        if (tipoUsuario == "Administrador") {
            btnAgregarMulta.visibility = View.VISIBLE
            btnAgregarMulta.setOnClickListener { mostrarDialogAgregarMulta() }
        }


        return root
    }

    private fun cargarMultas(){
        if (tipoUsuario == "Administrador"){
            repository.getAllFines { data ->
                adapter = MultaAdapter(
                    data,
                    tipoUsuario ?: "",
                    onEliminarClick = { eliminarMulta(it) }
                )
                recyclerView.adapter = adapter
            }
        }
        else {
            repository.getFinesByUserCode(registroEstudiante!!){ data ->
                adapter = MultaAdapter(
                    data,
                    tipoUsuario ?: "",
                    onEliminarClick = { eliminarMulta(it) }
                )
                recyclerView.adapter = adapter
            }
        }
    }

    private fun mostrarDialogAgregarMulta() {

        val dialog = DialogAgregarMulta(estudiantes) { multa ->

            repository.addFine(multa) { successful ->
                if (successful){
                    cargarMultas()

                }
            }
        }
        dialog.show(parentFragmentManager, "DialogAgregarMulta")
    }

    private fun eliminarMulta(fine: Fine) {

        repository.deleteFineByFolio(fine.folio){ successful ->
            if (successful){
                cargarMultas()
            }
        }
    }
}
