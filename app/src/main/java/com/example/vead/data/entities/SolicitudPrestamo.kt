package com.example.vead.data.entities

class SolicitudPrestamo {

    var folio: Int = 0
    var registroEstudiante: String = ""
    var tituloLibro: String = ""
    var fechaPrestamo: String = ""
    var fechaDevolucion: String = ""
    var estado: String = ""

    constructor()

    constructor(folio: Int, registroEstudiante: String, tituloLibro: String, fechaPrestamo: String, fechaDevolucion: String, estado: String) {
        this.folio = folio
        this.registroEstudiante = registroEstudiante
        this.tituloLibro = tituloLibro
        this.fechaPrestamo = fechaPrestamo
        this.fechaDevolucion = fechaDevolucion
        this.estado = estado
    }
}
