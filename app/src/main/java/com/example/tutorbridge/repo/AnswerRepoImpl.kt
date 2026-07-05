package com.example.tutorbridge.repo

import com.example.tutorbridge.model.AnswerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AnswerRepoImpl : AnswerRepo {

    private val auth = FirebaseAuth.getInstance()
    private val ref = FirebaseDatabase.getInstance().getReference("answers")

    override fun postAnswer(model: AnswerModel, callback: (Boolean, String) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            callback(false, "User not logged in")
            return
        }

        val answerId = ref.child(model.questionId).push().key ?: run {
            callback(false, "Failed to generate answer ID")
            return
        }

        val answer = model.copy(answerId = answerId, tutorId = uid)

        ref.child(model.questionId).child(answerId).setValue(answer)
            .addOnSuccessListener { callback(true, "Answer posted successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to post answer") }
    }

    override fun updateAnswer(model: AnswerModel, callback: (Boolean, String) -> Unit) {
        ref.child(model.questionId).child(model.answerId).setValue(model)
            .addOnSuccessListener { callback(true, "Answer updated successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Update failed") }
    }

    override fun deleteAnswer(questionId: String, answerId: String, callback: (Boolean, String) -> Unit) {
        ref.child(questionId).child(answerId).removeValue()
            .addOnSuccessListener { callback(true, "Answer deleted") }
            .addOnFailureListener { callback(false, it.message ?: "Delete failed") }
    }

    override fun getAnswersForQuestion(questionId: String, callback: (Boolean, List<AnswerModel>) -> Unit) {
        ref.child(questionId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(AnswerModel::class.java) }
                callback(true, list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, emptyList())
            }
        })
    }
}
