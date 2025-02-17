package com.example.vead.data.repositories

import com.example.vead.data.entities.Book
import com.google.firebase.database.*

class BookRepository {

    private val database = FirebaseDatabase.getInstance()
    private val bookRef: DatabaseReference = database.getReference("Books")

    /**
     * Add a new book with a randomly generated Firebase key.
     * Callback returns true if successful, false otherwise.
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
     * Callback returns a list of Book objects, or emptyList() on error.
     */
    fun getAllBooks(callback: (List<Book>) -> Unit) {
        bookRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookList = mutableListOf<Book>()
                for (childSnapshot in snapshot.children) {
                    val book = childSnapshot.getValue(Book::class.java)
                    if (book != null) {
                        bookList.add(book)
                    }
                }
                callback(bookList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList()) // Return empty list on error
            }
        })
    }

    /**
     * Find a single book by its title (one-time fetch).
     * Assumes unique titles. Callback returns the first matching book, or null if none found.
     */
    fun getBookByTitle(title: String, callback: (Book?) -> Unit) {
        bookRef.orderByChild("title").equalTo(title)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val firstChild = snapshot.children.firstOrNull()
                    val foundBook = firstChild?.getValue(Book::class.java)
                    callback(foundBook)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    /**
     * Update an existing book by searching for its title.
     * Assumes unique titles. Callback returns true if successful, or false otherwise.
     */
    fun updateBookByTitle(title: String, updatedBook: Book, callback: (Boolean) -> Unit) {
        bookRef.orderByChild("title").equalTo(title)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val firstChild = snapshot.children.firstOrNull()
                        if (firstChild != null) {
                            firstChild.ref.setValue(updatedBook)
                                .addOnSuccessListener { callback(true) }
                                .addOnFailureListener { callback(false) }
                        } else {
                            // Snapshot exists but no valid child found
                            callback(false)
                        }
                    } else {
                        // No book found with this title
                        callback(false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }

    /**
     * Delete a book by searching for its title.
     * Assumes unique titles. Callback returns true if deletion was successful, or false otherwise.
     */
    fun deleteBookByTitle(title: String, callback: (Boolean) -> Unit) {
        bookRef.orderByChild("title").equalTo(title)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val firstChild = snapshot.children.firstOrNull()
                        if (firstChild != null) {
                            firstChild.ref.removeValue()
                                .addOnSuccessListener { callback(true) }
                                .addOnFailureListener { callback(false) }
                        } else {
                            callback(false)
                        }
                    } else {
                        // No book found with this title
                        callback(false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }
}
