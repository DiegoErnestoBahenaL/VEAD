package com.example.vead.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.vead.R

class DetallesLibroFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_detalles_libro, container, false)

        // Referencias a las vistas
        val tvTitulo = root.findViewById<TextView>(R.id.tvTitulo)
        val tvAutor = root.findViewById<TextView>(R.id.tvAutor)
        val tvGenero = root.findViewById<TextView>(R.id.tvGenero)
        val tvNumeroCopias = root.findViewById<TextView>(R.id.tvNumeroCopias)
        val tvUbicacion = root.findViewById<TextView>(R.id.tvUbicacion)

        // Obtener datos del libro desde los argumentos
        val titulo = arguments?.getString("titulo") ?: ""
        val autor = arguments?.getString("autor") ?: ""
        val genero = arguments?.getString("genero") ?: ""
        val numeroCopias = arguments?.getInt("numeroCopias") ?: 0
        val ubicacion = arguments?.getString("ubicacion") ?: ""

        // Establecer valores en las vistas
        tvTitulo.text = titulo
        tvAutor.text = "Autor: $autor"
        tvGenero.text = "Género: $genero"
        tvNumeroCopias.text = "Número de copias: $numeroCopias"
        tvUbicacion.text = "Ubicación: $ubicacion"

        return root
    }
}
