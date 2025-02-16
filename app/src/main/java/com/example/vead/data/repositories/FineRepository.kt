package com.example.vead.data.repositories

import com.example.vead.data.entities.Fine
import com.google.firebase.database.*

public class FineRepository {

    private val database = FirebaseDatabase.getInstance()
    private val fineRef: DatabaseReference = database.getReference("Fines")

    /**
     * Add a new fine using a Firebase-generated key.
     */
    fun addFine(fine: Fine, callback: (Boolean) -> Unit) {
        val newFineKey = fineRef.push().key
        if (newFineKey == null) {
            callback(false)
            return
        }
        fineRef.child(newFineKey).setValue(fine)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    /**
     * Retrieve all fines (one-time fetch).
     */
    fun getAllFines(callback: (List<Fine>) -> Unit) {
        fineRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fineList = mutableListOf<Fine>()
                for (childSnapshot in snapshot.children) {
                    val fine = childSnapshot.getValue(Fine::class.java)
                    fine?.let { fineList.add(it) }
                }
                callback(fineList)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList()) // Handle error if needed
            }
        })
    }

    /**
     * Find all fines related to a specific userCode (one-time fetch).
     */
    fun getFinesByUserCode(userCode: Long, callback: (List<Fine>) -> Unit) {
        fineRef.orderByChild("userCode").equalTo(userCode.toDouble()) // Firebase stores numbers as Double
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userFines = mutableListOf<Fine>()
                    for (childSnapshot in snapshot.children) {
                        val fine = childSnapshot.getValue(Fine::class.java)
                        fine?.let { userFines.add(it) }
                    }
                    callback(userFines)
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    /**
     * Find a fine by its folio (one-time fetch).
     */
    fun getFineByFolio(folio: Int, callback: (Fine?) -> Unit) {
        fineRef.orderByChild("folio").equalTo(folio.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var foundFine: Fine? = null
                    for (childSnapshot in snapshot.children) {
                        foundFine = childSnapshot.getValue(Fine::class.java)
                        if (foundFine != null) break
                    }
                    callback(foundFine)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    /**
     * Update an existing fine by folio.
     */
    fun updateFineByFolio(folio: Int, updatedFine: Fine, callback: (Boolean) -> Unit) {
        getFineByFolio(folio) { existingFine ->
            if (existingFine == null) {
                callback(false) // No fine found
                return@getFineByFolio
            }
            fineRef.orderByChild("folio").equalTo(folio.toDouble())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var updatedSuccessfully = false
                            for (childSnapshot in snapshot.children) {
                                childSnapshot.ref.setValue(updatedFine)
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
     * Delete a fine by folio.
     */
    fun deleteFineByFolio(folio: Int, callback: (Boolean) -> Unit) {
        getFineByFolio(folio) { existingFine ->
            if (existingFine == null) {
                callback(false) // No fine found
                return@getFineByFolio
            }
            fineRef.orderByChild("folio").equalTo(folio.toDouble())
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
