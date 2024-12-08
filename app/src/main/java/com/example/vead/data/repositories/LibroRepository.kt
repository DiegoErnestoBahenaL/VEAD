package com.example.vead.data.repositories

import com.example.vead.data.collections.LibrosCollection
import com.example.vead.data.entities.Libro

public class LibroRepository {

    // Método para obtener todos los libros
    fun obtenerTodos(): List<Libro> {
        return LibrosCollection.libros
    }

    // Método para buscar un libro por título (el primero que coincida)
    fun buscarUno(titulo: String): Libro? {
        return LibrosCollection.libros.find { it.titulo == titulo }
    }

    // Método para agregar un nuevo libro
    fun agregar(libro: Libro) {
        LibrosCollection.libros.add(libro)
    }

    // Método para actualizar un libro basado en el título
    fun actualizar(titulo: String, nuevoLibro: Libro): Boolean {
        val indice = LibrosCollection.libros.indexOfFirst { it.titulo == titulo }
        if (indice != -1) {
            LibrosCollection.libros[indice] = nuevoLibro
            return true
        }
        return false
    }

    // Método para eliminar un libro por título
    fun eliminar(titulo: String): Boolean {
        return LibrosCollection.libros.removeIf { it.titulo == titulo }
    }
}
