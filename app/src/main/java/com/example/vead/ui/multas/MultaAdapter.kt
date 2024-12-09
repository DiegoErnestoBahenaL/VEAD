package com.example.vead.ui.multas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vead.R
import com.example.vead.data.entities.Multa

class MultaAdapter(
    private val multas: List<Multa>,
    private val tipoUsuario: String,
    private val onEliminarClick: (Multa) -> Unit
) : RecyclerView.Adapter<MultaAdapter.MultaViewHolder>() {

    class MultaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFolioRegistro: TextView = view.findViewById(R.id.tvFolioRegistro)
        val tvFechaGravedad: TextView = view.findViewById(R.id.tvFechaGravedad)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvFirma: TextView = view.findViewById(R.id.tvFirma)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminarMulta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_multa, parent, false)
        return MultaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MultaViewHolder, position: Int) {
        val multa = multas[position]

        holder.tvFolioRegistro.text = "Folio: ${multa.folio} | Registro: ${multa.registroEstudiante}"
        holder.tvFechaGravedad.text = "Fecha: ${multa.fecha} | Gravedad: ${multa.gravedad}"
        holder.tvDescripcion.text = "Descripción: ${multa.descripcion}"
        holder.tvFirma.text = "Firma: ${multa.firma}"


        if (tipoUsuario == "Administrador") {
            holder.btnEliminar.visibility = View.VISIBLE
            holder.btnEliminar.setOnClickListener { onEliminarClick(multa) }
        } else {
            holder.btnEliminar.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = multas.size
}
