package com.example.tutorbridge.model

data class ApplyTuitionModel(
    val applicationId: String = "",
    val requestId: String = "",
    val studentId: String = "",
    val tutorId: String = "",
    val subject: String = "",
    val grade: String = "",
    val budget: String = "",
    val status: String = "pending"
)
