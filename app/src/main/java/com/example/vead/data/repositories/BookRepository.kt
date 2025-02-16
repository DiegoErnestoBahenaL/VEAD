package com.example.vead.data.repositories

import com.example.vead.data.entities.Book
import com.google.firebase.database.*

public class BookRepository {

    private val database = FirebaseDatabase.getInstance()
    private val bookRef: DatabaseReference = database.getReference("Books")

    /**
     * Add a new book with a randomly generated Firebase key.
     */
    fun addBook(book: Book, callback: (Boolean) -> Unit) {
        val newBookKey = bookRef.push().key
        if (newBookKey == null) {
            callback(false)
            return
        }
        bookRef.child(newBookKey).setValue(book)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    /**
     * Retrieve all books (one-time fetch).
     */
    fun getAllBooks(callback: (List<Book>) -> Unit) {
        bookRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookList = mutableListOf<Book>()
                for (childSnapshot in snapshot.children) {
                    val book = childSnapshot.getValue(Book::class.java)
                    book?.let { bookList.add(it) }
                }
                callback(bookList)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList()) // Handle error if needed
            }
        })
    }

    /**
     * Find a book by its title (one-time fetch).
     */
    fun getBookByTitle(title: String, callback: (Book?) -> Unit) {
        bookRef.orderByChild("title").equalTo(title)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var foundBook: Book? = null
                    for (childSnapshot in snapshot.children) {
                        foundBook = childSnapshot.getValue(Book::class.java)
                        if (foundBook != null) break
                    }
                    callback(foundBook)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    /**
     * Update an existing book by searching for its title.
     */
    fun updateBookByTitle(title: String, updatedBook: Book, callback: (Boolean) -> Unit) {
        getBookByTitle(title) { existingBook ->
            if (existingBook == null) {
                callback(false) // No book found
                return@getBookByTitle
            }
            // Locate the actual Firebase entry and update it
            bookRef.orderByChild("title").equalTo(title)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var updatedSuccessfully = false
                            for (childSnapshot in snapshot.children) {
                                childSnapshot.ref.setValue(updatedBook)
                                    .addOnSuccessListener {
                                        updatedSuccessfully = true
                                        callback(true)
                                    }
                                    .addOnFailureListener {
                                        callback(false)
                                    }
                                break
                            }
                            if (!updatedSuccessfully) {
                                callback(false)
                            }
                        } else {
                            callback(false)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(false)
                    }
                })
        }
    }

    /**
     * Delete a book by searching for its title.
     */
    fun deleteBookByTitle(title: String, callback: (Boolean) -> Unit) {
        getBookByTitle(title) { existingBook ->
            if (existingBook == null) {
                callback(false) // No book found
                return@getBookByTitle
            }
            // Locate the Firebase entry and delete it
            bookRef.orderByChild("title").equalTo(title)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var deletedSuccessfully = false
                            for (childSnapshot in snapshot.children) {
                                childSnapshot.ref.removeValue()
                                    .addOnSuccessListener {
                                        deletedSuccessfully = true
                                        callback(true)
                                    }
                                    .addOnFailureListener {
                                        callback(false)
                                    }
                                break
                            }
                            if (!deletedSuccessfully) {
                                callback(false)
                            }
                        } else {
                            callback(false)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(false)
                    }
                })
        }
    }
}
