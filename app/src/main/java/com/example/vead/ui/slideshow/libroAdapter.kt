package com.example.vead.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.vead.R
import com.example.vead.data.entities.Libro

class LibroAdapter(
    private val libros: List<Libro>,
    private val tipoUsuario: String,
    private val onSolicitarClick: (Libro) -> Unit,
    private val onActualizarClick: (Libro) -> Unit,
    private val onEliminarClick: (Libro) -> Unit
) : RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {

    class LibroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitulo: TextView = view.findViewById(R.id.txtTitulo)
        val imgLibro: ImageView = view.findViewById(R.id.imgLibro)
        val btnSolicitar: Button = view.findViewById(R.id.btnSolicitar)
        val btnActualizar: Button = view.findViewById(R.id.btnActualizar)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminarLibro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_libro, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = libros[position]
        holder.txtTitulo.text = libro.titulo
        holder.imgLibro.setImageResource(obtenerImagenAleatoria()) // Imagen aleatoria



        holder.txtTitulo.setOnClickListener {
            val bundle = Bundle().apply {
                putString("titulo", libro.titulo)
                putString("autor", libro.autor)
                putString("genero", libro.genero)
                putInt("numeroCopias", libro.numeroCopias)
                putString("ubicacion", libro.ubicacion)
            }
            val navController = Navigation.findNavController(holder.itemView)
            navController.navigate(R.id.action_slideshowFragment_to_detallesLibroFragment, bundle)
        }

        // Configuración de botones según tipo de usuario
        if (tipoUsuario == "Administrador") {
            holder.btnSolicitar.visibility = View.GONE
            holder.btnActualizar.visibility = View.VISIBLE
            holder.btnEliminar.visibility = View.VISIBLE

            holder.btnActualizar.setOnClickListener { onActualizarClick(libro) }
            holder.btnEliminar.setOnClickListener { onEliminarClick(libro) }
        } else {
            holder.btnSolicitar.visibility = View.VISIBLE
            holder.btnActualizar.visibility = View.GONE
            holder.btnEliminar.visibility = View.GONE

            holder.btnSolicitar.setOnClickListener { onSolicitarClick(libro) }
        }
    }

    override fun getItemCount(): Int = libros.size

    private fun obtenerImagenAleatoria(): Int {
        val imagenes = listOf(R.drawable.portada1, R.drawable.portada2, R.drawable.portada3) // Agrega tus imágenes aquí
        return imagenes.random()
    }
}
