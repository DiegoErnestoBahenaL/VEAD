package com.example.vead.data.collections

import com.example.vead.data.entities.Estudiante

object EstudiantesCollection {
    val estudiantes: ArrayList<Estudiante> = arrayListOf (
        Estudiante(
            "estudiante@default.com",
            "default123",
            "Estudiante Default",
            "2024-01-01",
            "2024-12-31",
            "Estudiante",
            "22310000",
            1,
            "Software",
            "Example",
            "A"
        )

    )
}