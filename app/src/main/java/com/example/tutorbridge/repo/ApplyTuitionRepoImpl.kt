package com.example.tutorbridge.repo

import com.example.tutorbridge.model.ApplyTuitionModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplyTuitionRepoImpl : ApplyTuitionRepo {

    private val auth = FirebaseAuth.getInstance()
    private val ref = FirebaseDatabase.getInstance().getReference("applications")

    override fun applyForRequest(model: ApplyTuitionModel, callback: (Boolean, String) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            callback(false, "User not logged in")
            return
        }
        val appId = ref.child(uid).push().key ?: run {
            callback(false, "Failed to generate ID")
            return
        }
        val application = model.copy(applicationId = appId, tutorId = uid)
        ref.child(uid).child(appId).setValue(application)
            .addOnSuccessListener { callback(true, "Applied successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Apply failed") }
    }

    override fun getAppliedRequestIds(callback: (List<String>) -> Unit) {
        val uid = auth.currentUser?.uid ?: run { callback(emptyList()); return }
        ref.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ids = snapshot.children.mapNotNull {
                    it.getValue(ApplyTuitionModel::class.java)?.requestId
                }
                callback(ids)
            }
            override fun onCancelled(error: DatabaseError) { callback(emptyList()) }
        })
    }
}
