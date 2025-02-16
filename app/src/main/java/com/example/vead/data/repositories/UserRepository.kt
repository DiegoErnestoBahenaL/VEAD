package com.example.vead.data.repositories

import com.example.vead.data.entities.User
import com.google.firebase.database.*

public class UserRepository {

    private val database = FirebaseDatabase.getInstance()

    private val userRef: DatabaseReference = database.getReference("Users")

    /**
     *  Add a new user. A Firebase-generated key will be used as the unique node ID.
     *  Callback returns true if successful, false otherwise.
     */
    fun addUser(user: User, callback: (Boolean) -> Unit) {
        // Generate a new key under "Users/" path
        val newUserKey = userRef.push().key
        if (newUserKey == null) {
            callback(false)
            return
        }
        // Write the new user data at "Users/{newUserKey}"
        userRef.child(newUserKey).setValue(user)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    /**
     *  Retrieve the full list of users, read once (no continuous listening).
     *  Callback returns a list of all the User objects found.
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
                callback(emptyList()) // or handle error
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
                        for (childSnapshot in snapshot.children) {
                            val user = childSnapshot.getValue(User::class.java)
                            // Return the first match
                            if (user != null) {
                                callback(user)
                                return
                            }
                        }
                    }
                    callback(null)
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
                        // We'll update the *first* matching record (if multiple, you can handle differently)
                        var updatedSuccessfully = false
                        for (childSnapshot in snapshot.children) {
                            childSnapshot.ref.setValue(updatedUser)
                                .addOnSuccessListener {
                                    updatedSuccessfully = true
                                    callback(true)
                                }
                                .addOnFailureListener {
                                    callback(false)
                                }
                            break // after updating the first match, stop
                        }
                        if (!updatedSuccessfully) {
                            callback(false)
                        }
                    } else {
                        // If no user was found with the given email
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
                            break // after deleting the first match, stop
                        }
                        if (!deletedSuccessfully) {
                            callback(false)
                        }
                    } else {
                        // If no user was found with the given email
                        callback(false)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }
}
