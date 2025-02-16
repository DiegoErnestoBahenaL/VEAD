package com.example.vead.data.entities

class Book {

    var title: String = ""
    var author: String = ""
    var genre: String = ""
    var copiesNumber: Int = 0
    var location: String = ""
    var coverUrl: String = ""

    constructor()

    constructor(
        title: String,
        author: String,
        genre: String,
        copiesNumber: Int,
        location: String,
        coverUrl: String
    ) {
        this.title = title
        this.author = author
        this.genre = genre
        this.copiesNumber = copiesNumber
        this.location = location
        this.coverUrl = coverUrl
    }
}
