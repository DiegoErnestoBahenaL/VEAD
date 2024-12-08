package com.example.vead.data.repositories

import com.example.vead.data.collections.AdministradoresCollection
import com.example.vead.data.entities.Administrador

public class AdministradorRepository {

    // Método para obtener todos los administradores
    fun obtenerTodos(): List<Administrador> {
        return AdministradoresCollection.administradores
    }

    // Método para buscar un administrador por email (el primero que coincida)
    fun buscarUno(email: String): Administrador? {
        return AdministradoresCollection.administradores.find { it.email == email }
    }

    // Método para agregar un nuevo administrador
    fun agregar(administrador: Administrador) {
        AdministradoresCollection.administradores.add(administrador)
    }

    fun actualizar(email: String, nuevoAdministrador: Administrador): Boolean {
        val indice = AdministradoresCollection.administradores.indexOfFirst { it.email == email }
        if (indice != -1) {
            // Reemplazamos el administrador existente con el nuevo
            AdministradoresCollection.administradores[indice] = nuevoAdministrador
            return true
        }
        return false // Si no encuentra el administrador, retorna false
    }

    // Método para eliminar un administrador por email
    fun eliminar(email: String): Boolean {
        return AdministradoresCollection.administradores.removeIf { it.email == email }
    }
}
