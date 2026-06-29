package com.example.tutorbridge.repo

import com.example.tutorbridge.model.ReviewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReviewRepoImpl : ReviewRepo {

    private val auth = FirebaseAuth.getInstance()
    private val ref = FirebaseDatabase.getInstance().getReference("reviews")

    override fun addReview(model: ReviewModel, callback: (Boolean, String, ReviewModel?) -> Unit) {
        val uid = auth.currentUser?.uid ?: run { callback(false, "User not logged in", null); return }
        hasReviewed(model.tutorId) { already ->
            if (already) { callback(false, "You have already reviewed this tutor", null); return@hasReviewed }
            val reviewId = ref.child(model.tutorId).push().key ?: run { callback(false, "Failed to generate ID", null); return@hasReviewed }
            val review = model.copy(reviewId = reviewId, studentId = uid)
            ref.child(model.tutorId).child(reviewId).setValue(review)
                .addOnSuccessListener { callback(true, "Review submitted", review) }
                .addOnFailureListener { callback(false, it.message ?: "Failed", null) }
        }
    }

    override fun getReviewsForTutor(tutorId: String, callback: (Boolean, List<ReviewModel>) -> Unit) {
        ref.child(tutorId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(ReviewModel::class.java) }
                callback(true, list)
            }
            override fun onCancelled(error: DatabaseError) { callback(false, emptyList()) }
        })
    }

    override fun getMyReviewForTutor(tutorId: String, callback: (ReviewModel?) -> Unit) {
        val uid = auth.currentUser?.uid ?: run { callback(null); return }
        ref.child(tutorId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val review = snapshot.children.mapNotNull { it.getValue(ReviewModel::class.java) }
                    .firstOrNull { it.studentId == uid }
                callback(review)
            }
            override fun onCancelled(error: DatabaseError) { callback(null) }
        })
    }

    override fun updateReview(model: ReviewModel, callback: (Boolean, String) -> Unit) {
        val uid = auth.currentUser?.uid ?: run { callback(false, "User not logged in"); return }
        if (model.studentId != uid) { callback(false, "Not authorized"); return }
        ref.child(model.tutorId).child(model.reviewId).setValue(model)
            .addOnSuccessListener { callback(true, "Review updated") }
            .addOnFailureListener { callback(false, it.message ?: "Update failed") }
    }

    override fun deleteReview(tutorId: String, reviewId: String, callback: (Boolean, String) -> Unit) {
        val uid = auth.currentUser?.uid ?: run { callback(false, "User not logged in"); return }
        ref.child(tutorId).child(reviewId).get().addOnSuccessListener { snap ->
            val review = snap.getValue(ReviewModel::class.java)
            if (review?.studentId != uid) { callback(false, "Not authorized"); return@addOnSuccessListener }
            ref.child(tutorId).child(reviewId).removeValue()
                .addOnSuccessListener { callback(true, "Review deleted") }
                .addOnFailureListener { callback(false, it.message ?: "Delete failed") }
        }.addOnFailureListener { callback(false, it.message ?: "Failed") }
    }

    override fun hasReviewed(tutorId: String, callback: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: run { callback(false); return }
        ref.child(tutorId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val found = snapshot.children.any {
                    it.getValue(ReviewModel::class.java)?.studentId == uid
                }
                callback(found)
            }
            override fun onCancelled(error: DatabaseError) { callback(false) }
        })
    }
}
