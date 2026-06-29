package com.example.tutorbridge.model

data class ReviewModel(
    val reviewId: String = "",
    val tutorId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val rating: Int = 0,
    val comment: String = ""
)
