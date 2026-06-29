package com.example.tutorbridge.repo

import android.util.Log
import com.example.tutorbridge.model.CreateRequestModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CreateRequestRepoImpl : CreateRequestRepo {

    private val auth = FirebaseAuth.getInstance()
    private val ref = FirebaseDatabase.getInstance().getReference("requests")

    override fun addRequest(model: CreateRequestModel, callback: (Boolean, String) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            callback(false, "User not logged in")
            return
        }

        val requestId = ref.child(uid).push().key ?: run {
            callback(false, "Failed to generate request ID")
            return
        }

        val request = model.copy(requestId = requestId, userId = uid)

        ref.child(uid).child(requestId).setValue(request)
            .addOnSuccessListener { callback(true, "Request submitted successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to submit request") }
    }

    override fun getMyRequests(callback: (Boolean, List<CreateRequestModel>) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            Log.e("RequestRepo", "currentUser is null")
            callback(false, emptyList())
            return
        }

        Log.d("RequestRepo", "Fetching requests for uid: $uid")

        ref.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("RequestRepo", "snapshot childCount: ${snapshot.childrenCount}")
                val list = snapshot.children.mapNotNull {
                    it.getValue(CreateRequestModel::class.java)
                }
                callback(true, list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RequestRepo", "Firebase error: ${error.message}")
                callback(false, emptyList())
            }
        })
    }

    override fun updateRequest(model: CreateRequestModel, callback: (Boolean, String) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            callback(false, "User not logged in")
            return
        }
        ref.child(uid).child(model.requestId).setValue(model)
            .addOnSuccessListener { callback(true, "Request updated successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Update failed") }
    }

    override fun deleteRequest(requestId: String, callback: (Boolean, String) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            callback(false, "User not logged in")
            return
        }

        ref.child(uid).child(requestId).removeValue()
            .addOnSuccessListener { callback(true, "Request deleted") }
            .addOnFailureListener { callback(false, it.message ?: "Delete failed") }
    }
}
