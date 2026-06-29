package com.example.tutorbridge.model

data class CreateRequestModel(
    val requestId: String = "",
    val userId: String = "",
    val subject: String = "",
    val grade: String = "",
    val preferredGender: String = "",
    val location: String = "",
    val budget: String = "",
    val preferredTime: String = "",
    val description: String = "",
    val contactNumber: String = ""
)
