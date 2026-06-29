package com.example.tutorbridge.repo

import com.example.tutorbridge.model.CreateRequestModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateRequestRepoImpl : CreateRequestRepo {

    private val auth = FirebaseAuth.getInstance()
    private val ref = FirebaseDatabase.getInstance().getReference("requests")

    override fun addRequest(model: CreateRequestModel, callback: (Boolean, String) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            callback(false, "User not logged in")
            return
        }

        val requestId = ref.push().key ?: run {
            callback(false, "Failed to generate request ID")
            return
        }

        val request = model.copy(requestId = requestId, userId = uid)

        ref.child(requestId).setValue(request)
            .addOnSuccessListener {
                callback(true, "Request submitted successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Failed to submit request")
            }
    }
}
