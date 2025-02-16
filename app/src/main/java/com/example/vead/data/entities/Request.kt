package com.example.vead.data.entities

class Request {

    var folio: Int = 0
    var userCode: Long = 0
    var bookTitle: String = ""
    var requestDate: String = ""
    var returnDate: String = ""
    var status: String = ""

    constructor()

    constructor(
        folio: Int,
        userCode: Long,
        bookTitle: String,
        requestDate: String,
        returnDate: String,
        status: String
    ) {
        this.folio = folio
        this.userCode = userCode
        this.bookTitle = bookTitle
        this.requestDate = requestDate
        this.returnDate = returnDate
        this.status = status
    }
}
