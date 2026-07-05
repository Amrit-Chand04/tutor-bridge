package com.example.tutorbridge.model

data class QuestionModel(
    val questionId: String = "",
    val userId: String = "",
    val title: String = "",
    val subject: String = "",
    val details: String = "",
    val askedBy: String = "",
    val timestamp: Long = 0L
)
