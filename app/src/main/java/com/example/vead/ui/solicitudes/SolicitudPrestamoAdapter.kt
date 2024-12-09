package com.example.vead.ui.solicitudes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vead.R
import com.example.vead.data.entities.SolicitudPrestamo

class SolicitudPrestamoAdapter(
    private val solicitudes: List<SolicitudPrestamo>,
    private val tipoUsuario: String,
    private val onAprobarClick: (SolicitudPrestamo) -> Unit,
    private val onRechazarClick: (SolicitudPrestamo) -> Unit,
    private val onEliminarClick: (SolicitudPrestamo) -> Unit
) : RecyclerView.Adapter<SolicitudPrestamoAdapter.SolicitudViewHolder>() {

    class SolicitudViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFolioRegistro: TextView = view.findViewById(R.id.tvFolioRegistro)
        val tvTituloLibro: TextView = view.findViewById(R.id.tvTituloLibro)
        val tvFechas: TextView = view.findViewById(R.id.tvFechas)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val layoutBotones: LinearLayout = view.findViewById(R.id.layoutBotones)
        val btnAprobar: Button = view.findViewById(R.id.btnAprobar)
        val btnRechazar: Button = view.findViewById(R.id.btnRechazar)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminarSolicitud)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolicitudViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_solicitud_prestamo, parent, false)
        return SolicitudViewHolder(view)
    }

    override fun onBindViewHolder(holder: SolicitudViewHolder, position: Int) {
        val solicitud = solicitudes[position]

        holder.tvFolioRegistro.text = "Folio: ${solicitud.folio} | Registro: ${solicitud.registroEstudiante}"
        holder.tvTituloLibro.text = "Título: ${solicitud.tituloLibro}"
        holder.tvFechas.text = "Préstamo: ${solicitud.fechaPrestamo} | Devolución: ${solicitud.fechaDevolucion}"
        holder.tvEstado.text = "Estado: ${solicitud.estado}"

        if (tipoUsuario == "Administrador") {
            holder.layoutBotones.visibility = View.VISIBLE

            holder.btnAprobar.setOnClickListener { onAprobarClick(solicitud) }
            holder.btnRechazar.setOnClickListener { onRechazarClick(solicitud) }
            holder.btnEliminar.setOnClickListener { onEliminarClick(solicitud) }
        } else {
            holder.layoutBotones.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = solicitudes.size
}
