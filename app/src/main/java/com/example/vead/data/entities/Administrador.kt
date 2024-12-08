package com.example.vead.data.entities

class Administrador : Usuario {

    var codigoTrabajador: String = ""
    var rfc: String = ""
    var nombreSupervisor: String = ""
    var telefono: String = ""
    var turno: String = ""

    constructor() : super()

    constructor(
        email: String,
        contrasena: String,
        nombre: String,
        fechaCreacion: String,
        fechaExpiracion: String,
        tipo: String,
        codigoTrabajador: String,
        rfc: String,
        nombreSupervisor: String,
        telefono: String,
        turno: String
    ) : super(email, contrasena, nombre, fechaCreacion, fechaExpiracion, tipo) {
        this.codigoTrabajador = codigoTrabajador
        this.rfc = rfc
        this.nombreSupervisor = nombreSupervisor
        this.telefono = telefono
        this.turno = turno
    }
}
