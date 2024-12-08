package com.example.vead.data.entities

class Libro {

    var titulo: String = ""
    var autor: String = ""
    var genero: String = ""
    var numeroCopias: Int = 0
    var ubicacion: String = ""

    constructor()

    constructor(titulo: String, autor: String, genero: String, numeroCopias: Int, ubicacion: String) {
        this.titulo = titulo
        this.autor = autor
        this.genero = genero
        this.numeroCopias = numeroCopias
        this.ubicacion = ubicacion
    }
}
