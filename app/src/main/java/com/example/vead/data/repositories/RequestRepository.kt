package com.example.vead.data.repositories

import com.example.vead.data.entities.Request
import com.google.firebase.database.*

public class RequestRepository {

    private val database = FirebaseDatabase.getInstance()
    private val requestRef: DatabaseReference = database.getReference("Requests")

    /**
     * Add a new request using a Firebase-generated key.
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
     */
    fun getAllRequests(callback: (List<Request>) -> Unit) {
        requestRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val requestList = mutableListOf<Request>()
                for (childSnapshot in snapshot.children) {
                    val request = childSnapshot.getValue(Request::class.java)
                    request?.let { requestList.add(it) }
                }
                callback(requestList)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList()) // Handle error if needed
            }
        })
    }

    /**
     * Find all requests made by a specific userCode (one-time fetch).
     */
    fun getRequestsByUserCode(userCode: String, callback: (List<Request>) -> Unit) {
        requestRef.orderByChild("userCode").equalTo(userCode)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userRequests = mutableListOf<Request>()
                    for (childSnapshot in snapshot.children) {
                        val request = childSnapshot.getValue(Request::class.java)
                        request?.let { userRequests.add(it) }
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
     */
    fun getRequestByFolio(folio: Int, callback: (Request?) -> Unit) {
        requestRef.orderByChild("folio").equalTo(folio.toDouble()) // Firebase stores numbers as Double
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var foundRequest: Request? = null
                    for (childSnapshot in snapshot.children) {
                        foundRequest = childSnapshot.getValue(Request::class.java)
                        if (foundRequest != null) break
                    }
                    callback(foundRequest)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    /**
     * Update an existing request by folio.
     */
    fun updateRequestByFolio(folio: Int, updatedRequest: Request, callback: (Boolean) -> Unit) {
        getRequestByFolio(folio) { existingRequest ->
            if (existingRequest == null) {
                callback(false) // No request found
                return@getRequestByFolio
            }
            requestRef.orderByChild("folio").equalTo(folio.toDouble())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var updatedSuccessfully = false
                            for (childSnapshot in snapshot.children) {
                                childSnapshot.ref.setValue(updatedRequest)
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
     * Delete a request by folio.
     */
    fun deleteRequestByFolio(folio: Int, callback: (Boolean) -> Unit) {
        getRequestByFolio(folio) { existingRequest ->
            if (existingRequest == null) {
                callback(false) // No request found
                return@getRequestByFolio
            }
            requestRef.orderByChild("folio").equalTo(folio.toDouble())
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
