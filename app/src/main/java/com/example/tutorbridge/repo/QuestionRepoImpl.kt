package com.example.tutorbridge.repo

import com.example.tutorbridge.model.QuestionModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class QuestionRepoImpl : QuestionRepo {

    private val auth = FirebaseAuth.getInstance()
    private val ref = FirebaseDatabase.getInstance().getReference("questions")

    override fun postQuestion(model: QuestionModel, callback: (Boolean, String) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            callback(false, "User not logged in")
            return
        }

        val questionId = ref.push().key ?: run {
            callback(false, "Failed to generate question ID")
            return
        }

        val question = model.copy(questionId = questionId, userId = uid)

        ref.child(questionId).setValue(question)
            .addOnSuccessListener { callback(true, "Question posted successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to post question") }
    }
}
