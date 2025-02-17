package com.example.vead.ui.solicitudes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vead.R
import com.example.vead.data.entities.Request
import com.example.vead.data.repositories.RequestRepository
import com.example.vead.data.repositories.UserRepository
import kotlinx.coroutines.launch

class SolicitudesPrestamoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SolicitudPrestamoAdapter
    private val repository = RequestRepository()

    private var tipoUsuario: String? = null
    private var registroEstudiante: Long? = null
    private var userEmail : String? = null
    private var code : Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_solicitud_prestamo, container, false)

        tipoUsuario = activity?.intent?.getStringExtra("UserType")

        userEmail = activity?.intent?.getStringExtra("Email")

        code = requireActivity().intent.getLongExtra("Code", 0)






        recyclerView = root.findViewById(R.id.recyclerSolicitudes)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        cargarSolicitudes()

        return root
    }

    private fun cargarSolicitudes() {

        if (tipoUsuario == "Administrador"){
            repository.getAllRequests { data ->
                adapter = SolicitudPrestamoAdapter(
                    data,
                    tipoUsuario ?: "",
                    onAprobarClick = { actualizarEstado(it, "Aceptado") },
                    onRechazarClick = { actualizarEstado(it, "Rechazado") },
                    onEliminarClick = { eliminarSolicitud(it) }
                )
                recyclerView.adapter = adapter
            }
        }
        else {
            repository.getRequestsByUserCode(code!!){ data ->
                adapter = SolicitudPrestamoAdapter(
                    data,
                    tipoUsuario ?: "",
                    onAprobarClick = { actualizarEstado(it, "Aceptado") },
                    onRechazarClick = { actualizarEstado(it, "Rechazado") },
                    onEliminarClick = { eliminarSolicitud(it) }
                )
                recyclerView.adapter = adapter
            }
        }
    }

    private fun actualizarEstado(solicitud: Request, nuevoEstado: String) {
        solicitud.status = nuevoEstado

        repository.updateRequestByFolio(solicitud.folio, solicitud){ successful ->
            if (successful){
                cargarSolicitudes()
            }
        }

    }

    private fun eliminarSolicitud(solicitud: Request) {

        repository.deleteRequestByFolio(solicitud.folio){ successful ->
            if (successful){
                cargarSolicitudes()
            }
        }
    }
}
