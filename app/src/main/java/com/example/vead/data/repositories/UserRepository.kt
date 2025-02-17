package com.example.vead.data.repositories

import com.example.vead.data.entities.User
import com.google.firebase.database.*

class UserRepository {

    private val database = FirebaseDatabase.getInstance()
    private val userRef: DatabaseReference = database.getReference("Users")

    /**
     *  Add a new user. A Firebase-generated key will be used as the unique node ID.
     *  Callback returns true if successful, false otherwise.
     */
    fun addUser(user: User, callback: (Boolean) -> Unit) {
        val newUserKey = userRef.push().key
        if (newUserKey == null) {
            callback(false)
            return
        }
        userRef.child(newUserKey).setValue(user)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    /**
     *  Retrieve the full list of users, read once (no continuous listening).
     *  Callback returns a list of all the User objects found, or emptyList() on error.
     */
    fun getAllUsers(callback: (List<User>) -> Unit) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<User>()
                for (childSnapshot in snapshot.children) {
                    val user = childSnapshot.getValue(User::class.java)
                    user?.let { userList.add(it) }
                }
                callback(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Return an empty list in case of error
                callback(emptyList())
            }
        })
    }

    /**
     *  Find a single user by matching their email, read once (no continuous listening).
     *  Callback returns the first matching user or null if none found.
     */
    fun getUserByEmail(email: String, callback: (User?) -> Unit) {
        userRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Directly get the first (and only) matching child
                        val firstChild = snapshot.children.firstOrNull()
                        val user = firstChild?.getValue(User::class.java)
                        callback(user)
                    } else {
                        // No user found with this email
                        callback(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    /**
     *  Update an existing user by searching for their email, read once.
     *  Callback returns true if update was successful, or false otherwise.
     */
    fun updateUserByEmail(email: String, updatedUser: User, callback: (Boolean) -> Unit) {
        userRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Directly get the first child (unique email => only one match)
                        val firstChild = snapshot.children.firstOrNull()
                        if (firstChild != null) {
                            firstChild.ref.setValue(updatedUser)
                                .addOnSuccessListener { callback(true) }
                                .addOnFailureListener { callback(false) }
                        } else {
                            // No children despite snapshot.exists() being true
                            callback(false)
                        }
                    } else {
                        // No user found with this email
                        callback(false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }

    /**
     *  Delete a user by searching for their email, read once.
     *  Callback returns true if deletion was successful, or false otherwise.
     */
    fun deleteUserByEmail(email: String, callback: (Boolean) -> Unit) {
        userRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Directly get the first child (unique email => only one match)
                        val firstChild = snapshot.children.firstOrNull()
                        if (firstChild != null) {
                            firstChild.ref.removeValue()
                                .addOnSuccessListener { callback(true) }
                                .addOnFailureListener { callback(false) }
                        } else {
                            // No children despite snapshot.exists() being true
                            callback(false)
                        }
                    } else {
                        // No user found with this email
                        callback(false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }
}
