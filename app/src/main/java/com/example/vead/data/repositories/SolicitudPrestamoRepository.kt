package com.example.vead.data.repositories

import com.example.vead.data.collections.SolicitudesPrestamoCollection
import com.example.vead.data.entities.SolicitudPrestamo

public class SolicitudPrestamoRepository {

    // Método para obtener todas las solicitudes de préstamo
    fun obtenerTodos(): List<SolicitudPrestamo> {
        return SolicitudesPrestamoCollection.solicitudesPrestamo
    }

    // Método para buscar solicitudes de préstamo por registro de estudiante (todas las que coincidan)
    fun buscarPorRegistro(registroEstudiante: String): List<SolicitudPrestamo> {
        return SolicitudesPrestamoCollection.solicitudesPrestamo.filter { it.registroEstudiante == registroEstudiante }
    }

    // Método para buscar una solicitud de préstamo por folio (la primera que coincida)
    fun buscarUno(folio: Int): SolicitudPrestamo? {
        return SolicitudesPrestamoCollection.solicitudesPrestamo.find { it.folio == folio }
    }

    // Método para agregar una nueva solicitud de préstamo
    fun agregar(solicitud: SolicitudPrestamo) {
        SolicitudesPrestamoCollection.solicitudesPrestamo.add(solicitud)
    }

    // Método para actualizar una solicitud de préstamo basada en el folio
    fun actualizar(folio: Int, nuevaSolicitud: SolicitudPrestamo): Boolean {
        val indice = SolicitudesPrestamoCollection.solicitudesPrestamo.indexOfFirst { it.folio == folio }
        if (indice != -1) {
            SolicitudesPrestamoCollection.solicitudesPrestamo[indice] = nuevaSolicitud
            return true
        }
        return false
    }

    // Método para eliminar una solicitud de préstamo por folio
    fun eliminar(folio: Int): Boolean {
        return SolicitudesPrestamoCollection.solicitudesPrestamo.removeIf { it.folio == folio }
    }
}
