package com.example.vead.data.entities

public open class Usuario {

    var email: String = ""
    var contrasena: String = ""
    var nombre: String = ""
    var fechaCreacion: String = ""
    var fechaExpiracion: String = ""
    var tipo : String = ""

    constructor()

    constructor(
        email: String,
        contrasena: String,
        nombre: String,
        fechaCreacion: String,
        fechaExpiracion: String,
        tipo: String
    ) {
        this.email = email
        this.contrasena = contrasena
        this.nombre = nombre
        this.fechaCreacion = fechaCreacion
        this.fechaExpiracion = fechaExpiracion
        this.tipo = tipo
    }
}
