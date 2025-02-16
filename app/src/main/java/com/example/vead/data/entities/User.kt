package com.example.vead.data.entities

class User {

    var code: Long = 0
    var email: String = ""
    var lastName: String = ""
    var name: String = ""
    var password: String = ""
    var phoneNumber: Long = 0
    var userType: String = ""

    constructor()

    constructor(
        code: Long,
        email: String,
        lastName: String,
        name: String,
        password: String,
        phoneNumber: Long,
        userType: String
    ) {
        this.code = code
        this.email = email
        this.lastName = lastName
        this.name = name
        this.password = password
        this.phoneNumber = phoneNumber
        this.userType = userType
    }
}
