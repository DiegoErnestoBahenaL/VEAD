package com.example.vead.ui.multas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vead.R
import com.example.vead.data.entities.Multa
import com.example.vead.data.repositories.EstudianteRepository
import com.example.vead.data.repositories.MultaRepository

class MultasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MultaAdapter

    private val repository = MultaRepository()
    private var tipoUsuario: String? = null
    private var registroEstudiante: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_multas, container, false)

        tipoUsuario = activity?.intent?.getStringExtra("TipoUsuario")

        registroEstudiante = activity?.intent?.getStringExtra("Email")?.let {
            EstudianteRepository().buscarUno(it)?.registro
        }



        recyclerView = root.findViewById(R.id.recyclerMultas)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val btnAgregarMulta = root.findViewById<Button>(R.id.btnAgregarMulta)
        if (tipoUsuario == "Administrador") {
            btnAgregarMulta.visibility = View.VISIBLE
            btnAgregarMulta.setOnClickListener { mostrarDialogAgregarMulta() }
        }

        cargarMultas()
        return root
    }

    private fun cargarMultas() {

        val multas = if (tipoUsuario == "Administrador") {
            repository.obtenerTodos()
        } else {
            registroEstudiante?.let { repository.buscarPorEstudiante(it) } ?: emptyList()
        }

        adapter = MultaAdapter(
            multas,
            tipoUsuario ?: "",
            onEliminarClick = { eliminarMulta(it) }
        )
        recyclerView.adapter = adapter
    }

    private fun mostrarDialogAgregarMulta() {
        val estudiantes = EstudianteRepository().obtenerTodos().map { it.registro }
        val dialog = DialogAgregarMulta(estudiantes) { multa ->
            repository.agregar(multa)
            cargarMultas()
        }
        dialog.show(parentFragmentManager, "DialogAgregarMulta")
    }

    private fun eliminarMulta(multa: Multa) {
        repository.eliminar(multa.folio)
        cargarMultas()
    }
}
