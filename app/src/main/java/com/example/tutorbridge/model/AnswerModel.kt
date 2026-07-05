package com.example.tutorbridge.model

data class AnswerModel(
    val answerId: String = "",
    val questionId: String = "",
    val tutorId: String = "",
    val tutorName: String = "",
    val answerText: String = "",
    val timestamp: Long = 0L
)
