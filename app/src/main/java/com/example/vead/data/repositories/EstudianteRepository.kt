package com.example.vead.data.repositories

import com.example.vead.data.collections.EstudiantesCollection
import com.example.vead.data.entities.Estudiante

public class EstudianteRepository {

    // Método para obtener todos los estudiantes
    fun obtenerTodos(): List<Estudiante> {
        return EstudiantesCollection.estudiantes
    }

    // Método para buscar un estudiante por email (el primero que coincida)
    fun buscarUno(email: String): Estudiante? {
        return EstudiantesCollection.estudiantes.find { it.email == email }
    }

    // Método para agregar un nuevo estudiante
    fun agregar(estudiante: Estudiante) {
        EstudiantesCollection.estudiantes.add(estudiante)
    }

    // Método para actualizar un estudiante basado en el email
    fun actualizar(email: String, nuevoEstudiante: Estudiante): Boolean {
        val indice = EstudiantesCollection.estudiantes.indexOfFirst { it.email == email }
        if (indice != -1) {
            // Reemplazamos el estudiante existente con el nuevo
            EstudiantesCollection.estudiantes[indice] = nuevoEstudiante
            return true
        }
        return false // Si no encuentra el estudiante, retorna false
    }

    // Método para eliminar un estudiante por email
    fun eliminar(email: String): Boolean {
        return EstudiantesCollection.estudiantes.removeIf { it.email == email }
    }
}
