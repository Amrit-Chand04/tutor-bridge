package com.example.tutorbridge.model

data class NotificationModel(
    val notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)
