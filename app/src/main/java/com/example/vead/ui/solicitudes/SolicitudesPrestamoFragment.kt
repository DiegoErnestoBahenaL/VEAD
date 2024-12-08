package com.example.vead.ui.solicitudes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vead.R
import com.example.vead.data.entities.SolicitudPrestamo
import com.example.vead.data.repositories.EstudianteRepository
import com.example.vead.data.repositories.SolicitudPrestamoRepository

class SolicitudesPrestamoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SolicitudPrestamoAdapter
    private val repository = SolicitudPrestamoRepository()
    private var tipoUsuario: String? = null
    private var registroEstudiante: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_solicitud_prestamo, container, false)

        tipoUsuario = activity?.intent?.getStringExtra("TipoUsuario")
        registroEstudiante = activity?.intent?.getStringExtra("Email")?.let {
            EstudianteRepository().buscarUno(it)?.registro
        }

        recyclerView = root.findViewById(R.id.recyclerSolicitudes)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        cargarSolicitudes()

        return root
    }

    private fun cargarSolicitudes() {
        val solicitudes = if (tipoUsuario == "Administrador") {
            repository.obtenerTodos()
        } else {
            registroEstudiante?.let { repository.buscarPorRegistro(it) } ?: emptyList()
        }

        adapter = SolicitudPrestamoAdapter(
            solicitudes,
            tipoUsuario ?: "",
            onAprobarClick = { actualizarEstado(it, "Aceptado") },
            onRechazarClick = { actualizarEstado(it, "Rechazado") },
            onEliminarClick = { eliminarSolicitud(it) }
        )
        recyclerView.adapter = adapter
    }

    private fun actualizarEstado(solicitud: SolicitudPrestamo, nuevoEstado: String) {
        solicitud.estado = nuevoEstado
        repository.actualizar(solicitud.folio, solicitud)
        cargarSolicitudes()
    }

    private fun eliminarSolicitud(solicitud: SolicitudPrestamo) {
        repository.eliminar(solicitud.folio)
        cargarSolicitudes()
    }
}
