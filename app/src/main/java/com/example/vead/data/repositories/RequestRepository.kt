package com.example.vead.data.repositories

import com.example.vead.data.entities.Request
import com.google.firebase.database.*

class RequestRepository {

    private val database = FirebaseDatabase.getInstance()
    private val requestRef: DatabaseReference = database.getReference("Requests")

    /**
     * Add a new request using a Firebase-generated key.
     * Callback returns true if successful, false otherwise.
     */
    fun addRequest(request: Request, callback: (Boolean) -> Unit) {
        val newRequestKey = requestRef.push().key
        if (newRequestKey == null) {
            callback(false)
            return
        }
        requestRef.child(newRequestKey).setValue(request)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    /**
     * Retrieve all requests (one-time fetch).
     * Callback returns a list of Request objects, or emptyList() on error.
     */
    fun getAllRequests(callback: (List<Request>) -> Unit) {
        requestRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val requestList = mutableListOf<Request>()
                for (childSnapshot in snapshot.children) {
                    val request = childSnapshot.getValue(Request::class.java)
                    if (request != null) {
                        requestList.add(request)
                    }
                }
                callback(requestList)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    /**
     * Find all requests made by a specific userCode (one-time fetch).
     * userCode is stored as a Double in Firebase, so we compare with userCode.toDouble().
     */
    fun getRequestsByUserCode(userCode: Long, callback: (List<Request>) -> Unit) {
        requestRef.orderByChild("userCode").equalTo(userCode.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userRequests = mutableListOf<Request>()
                    for (childSnapshot in snapshot.children) {
                        val request = childSnapshot.getValue(Request::class.java)
                        if (request != null) {
                            userRequests.add(request)
                        }
                    }
                    callback(userRequests)
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    /**
     * Find a request by its folio (one-time fetch).
     * Assumes folio is unique. Callback returns the first matching Request, or null if none found.
     */
    fun getRequestByFolio(folio: Int, callback: (Request?) -> Unit) {
        requestRef.orderByChild("folio").equalTo(folio.toDouble()) // Firebase stores numbers as Double
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val firstChild = snapshot.children.firstOrNull()
                    val foundRequest = firstChild?.getValue(Request::class.java)
                    callback(foundRequest)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    /**
     * Update an existing request by folio, assuming folio is unique.
     * Callback returns true if update was successful, or false otherwise.
     */
    fun updateRequestByFolio(folio: Int, updatedRequest: Request, callback: (Boolean) -> Unit) {
        requestRef.orderByChild("folio").equalTo(folio.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val firstChild = snapshot.children.firstOrNull()
                        if (firstChild != null) {
                            firstChild.ref.setValue(updatedRequest)
                                .addOnSuccessListener { callback(true) }
                                .addOnFailureListener { callback(false) }
                        } else {
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

    /**
     * Delete a request by folio, assuming folio is unique.
     * Callback returns true if deletion was successful, or false otherwise.
     */
    fun deleteRequestByFolio(folio: Int, callback: (Boolean) -> Unit) {
        requestRef.orderByChild("folio").equalTo(folio.toDouble())
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
                        callback(false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }
}
