package com.example.tutorbridge.repo

import com.example.tutorbridge.model.ReviewModel

interface ReviewRepo {
    fun addReview(model: ReviewModel, callback: (Boolean, String, ReviewModel?) -> Unit)
    fun getReviewsForTutor(tutorId: String, callback: (Boolean, List<ReviewModel>) -> Unit)
    fun hasReviewed(tutorId: String, callback: (Boolean) -> Unit)
    fun getMyReviewForTutor(tutorId: String, callback: (ReviewModel?) -> Unit)
    fun updateReview(model: ReviewModel, callback: (Boolean, String) -> Unit)
    fun deleteReview(tutorId: String, reviewId: String, callback: (Boolean, String) -> Unit)
}
