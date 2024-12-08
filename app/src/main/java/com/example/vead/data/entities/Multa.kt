package com.example.vead.data.entities

class Multa {
    var folio: Int = 0
    var fecha: String = ""
    var registroEstudiante: String = ""
    var gravedad: String = ""
    var descripcion: String = ""
    var firma: String = ""

    constructor()

    constructor(folio: Int, fecha: String, registroEstudiante: String, gravedad: String, descripcion: String, firma: String) {
        this.folio = folio
        this.fecha = fecha
        this.registroEstudiante = registroEstudiante
        this.gravedad = gravedad
        this.descripcion = descripcion
        this.firma = firma
    }
}
