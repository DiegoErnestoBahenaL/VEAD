package com.example.vead.data.repositories

import com.example.vead.data.entities.Fine
import com.google.firebase.database.*

class FineRepository {

    private val database = FirebaseDatabase.getInstance()
    private val fineRef: DatabaseReference = database.getReference("Fines")

    /**
     * Add a new fine using a Firebase-generated key.
     * Callback returns true if successful, false otherwise.
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
     * Callback returns a list of Fines, or emptyList() on error.
     */
    fun getAllFines(callback: (List<Fine>) -> Unit) {
        fineRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fineList = mutableListOf<Fine>()
                for (childSnapshot in snapshot.children) {
                    val fine = childSnapshot.getValue(Fine::class.java)
                    if (fine != null) {
                        fineList.add(fine)
                    }
                }
                callback(fineList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    /**
     * Find all fines related to a specific userCode (one-time fetch).
     * userCode is stored as Double in Firebase, so we compare with userCode.toDouble().
     */
    fun getFinesByUserCode(userCode: Long, callback: (List<Fine>) -> Unit) {
        fineRef.orderByChild("userCode").equalTo(userCode.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userFines = mutableListOf<Fine>()
                    for (childSnapshot in snapshot.children) {
                        val fine = childSnapshot.getValue(Fine::class.java)
                        if (fine != null) {
                            userFines.add(fine)
                        }
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
     * Assumes unique folio. Callback returns the first matching Fine, or null if none found.
     */
    fun getFineByFolio(folio: Int, callback: (Fine?) -> Unit) {
        fineRef.orderByChild("folio").equalTo(folio.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val firstChild = snapshot.children.firstOrNull()
                    val foundFine = firstChild?.getValue(Fine::class.java)
                    callback(foundFine)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    /**
     * Update an existing fine by folio, assuming folio is unique.
     * Callback returns true if update was successful, or false otherwise.
     */
    fun updateFineByFolio(folio: Int, updatedFine: Fine, callback: (Boolean) -> Unit) {
        fineRef.orderByChild("folio").equalTo(folio.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val firstChild = snapshot.children.firstOrNull()
                        if (firstChild != null) {
                            firstChild.ref.setValue(updatedFine)
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
     * Delete a fine by folio, assuming folio is unique.
     * Callback returns true if deletion was successful, or false otherwise.
     */
    fun deleteFineByFolio(folio: Int, callback: (Boolean) -> Unit) {
        fineRef.orderByChild("folio").equalTo(folio.toDouble())
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
