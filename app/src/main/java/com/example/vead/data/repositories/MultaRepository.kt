package com.example.vead.data.repositories

import com.example.vead.data.collections.MultasCollection
import com.example.vead.data.entities.Multa

    public class MultaRepository {

        // Método para obtener todas las multas
        fun obtenerTodos(): List<Multa> {
            return MultasCollection.multas
        }

        // Método para buscar multas por registro del estudiante (todas las que coincidan)
        fun buscarPorEstudiante(registroEstudiante: String): List<Multa> {
            return MultasCollection.multas.filter { it.registroEstudiante == registroEstudiante }
        }

        // Método para buscar una multa por folio (la primera que coincida)
        fun buscarUno(folio: Int): Multa? {
            return MultasCollection.multas.find { it.folio == folio }
        }

        // Método para agregar una nueva multa
        fun agregar(multa: Multa) {
            MultasCollection.multas.add(multa)
        }

        // Método para actualizar una multa basada en el folio
        fun actualizar(folio: Int, nuevaMulta: Multa): Boolean {
            val indice = MultasCollection.multas.indexOfFirst { it.folio == folio }
            if (indice != -1) {
                MultasCollection.multas[indice] = nuevaMulta
                return true
            }
            return false
        }

        // Método para eliminar una multa por folio
        fun eliminar(folio: Int): Boolean {
            return MultasCollection.multas.removeIf { it.folio == folio }
        }
    }
