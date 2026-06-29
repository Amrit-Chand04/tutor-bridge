package com.example.tutorbridge.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tutorbridge.model.ReviewModel
import com.example.tutorbridge.repo.ReviewRepo
import com.example.tutorbridge.repo.ReviewRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReviewViewModel : ViewModel() {

    private val repo: ReviewRepo = ReviewRepoImpl()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _reviews = MutableStateFlow<List<ReviewModel>>(emptyList())
    val reviews: StateFlow<List<ReviewModel>> = _reviews

    private val _myReviews = MutableStateFlow<Map<String, ReviewModel>>(emptyMap())
    val myReviews: StateFlow<Map<String, ReviewModel>> = _myReviews

    val averageRating: Float get() {
        val list = _reviews.value
        return if (list.isEmpty()) 0f else list.sumOf { it.rating }.toFloat() / list.size
    }

    fun loadMyReviewForTutor(tutorId: String) {
        repo.getMyReviewForTutor(tutorId) { review ->
            if (review != null) {
                _myReviews.value = _myReviews.value + (tutorId to review)
            }
        }
    }

    fun loadMyReviewsForTutors(tutorIds: List<String>, onDone: () -> Unit = {}) {
        if (tutorIds.isEmpty()) { onDone(); return }
        var remaining = tutorIds.size
        tutorIds.forEach { tutorId ->
            repo.getMyReviewForTutor(tutorId) { review ->
                if (review != null) {
                    _myReviews.value = _myReviews.value + (tutorId to review)
                }
                remaining--
                if (remaining == 0) onDone()
            }
        }
    }

    fun submitReview(tutorId: String, studentName: String, rating: Int, comment: String, callback: (Boolean, String) -> Unit) {
        if (rating == 0) { callback(false, "Please select a rating"); return }
        if (comment.isBlank()) { callback(false, "Please write a comment"); return }
        _isLoading.value = true
        val model = ReviewModel(tutorId = tutorId, studentName = studentName, rating = rating, comment = comment)
        repo.addReview(model) { success, msg, savedReview ->
            _isLoading.value = false
            if (success && savedReview != null) {
                _myReviews.value = _myReviews.value + (tutorId to savedReview)
            }
            callback(success, msg)
        }
    }

    fun updateReview(model: ReviewModel, rating: Int, comment: String, callback: (Boolean, String) -> Unit) {
        if (rating == 0) { callback(false, "Please select a rating"); return }
        if (comment.isBlank()) { callback(false, "Please write a comment"); return }
        _isLoading.value = true
        val updated = model.copy(rating = rating, comment = comment)
        repo.updateReview(updated) { success, msg ->
            _isLoading.value = false
            if (success) _myReviews.value = _myReviews.value + (model.tutorId to updated)
            callback(success, msg)
        }
    }

    fun deleteReview(tutorId: String, reviewId: String, callback: (Boolean, String) -> Unit) {
        _isLoading.value = true
        repo.deleteReview(tutorId, reviewId) { success, msg ->
            _isLoading.value = false
            if (success) _myReviews.value = _myReviews.value - tutorId
            callback(success, msg)
        }
    }

    fun loadReviews(tutorId: String) {
        _isLoading.value = true
        repo.getReviewsForTutor(tutorId) { _, list ->
            _isLoading.value = false
            _reviews.value = list
        }
    }
}
