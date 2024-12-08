package com.example.vead.data.entities

class Estudiante : Usuario {

    var registro: String = ""
    var grado: Int = 0
    var carrera: String = ""
    var nombreTutor: String = ""
    var grupo: String = ""

    constructor() : super()

    constructor(
        email: String,
        contrasena: String,
        nombre: String,
        fechaCreacion: String,
        fechaExpiracion: String,
        tipo: String,
        registro: String,
        grado: Int,
        carrera: String,
        nombreTutor: String,
        grupo: String
    ) : super(email, contrasena, nombre, fechaCreacion, fechaExpiracion, tipo) {
        this.registro = registro
        this.grado = grado
        this.carrera = carrera
        this.nombreTutor = nombreTutor
        this.grupo = grupo
    }
}
