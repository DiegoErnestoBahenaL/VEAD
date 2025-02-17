package com.example.vead.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vead.R
import com.example.vead.data.entities.User

class EstudianteAdapter (
    private val users: List<User>,
    private val onEliminarClick: (User) -> Unit): RecyclerView.Adapter<EstudianteAdapter.EstudianteViewHolder>() {


    class EstudianteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtCorreo: TextView = view.findViewById(R.id.txtCorreo)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstudianteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_estudiante, parent, false)
        return EstudianteViewHolder(view)
    }

    override fun onBindViewHolder(holder: EstudianteViewHolder, position: Int) {
        val estudiante = users[position]
        holder.txtCorreo.text = estudiante.email
        holder.btnEliminar.setOnClickListener { onEliminarClick(estudiante) }
    }

    override fun getItemCount(): Int = users.size
}