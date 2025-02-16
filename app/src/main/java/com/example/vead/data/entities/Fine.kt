package com.example.vead.data.entities

class Fine {

    var folio: Int = 0
    var date: String = ""
    var userCode: Long = 0
    var severity: String = ""
    var description: String = ""
    var signature: String = ""

    constructor()

    constructor(
        folio: Int,
        date: String,
        userCode: Long,
        severity: String,
        description: String,
        signature: String
    ) {
        this.folio = folio
        this.date = date
        this.userCode = userCode
        this.severity = severity
        this.description = description
        this.signature = signature
    }
}

