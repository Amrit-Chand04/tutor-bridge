package com.example.tutorbridge.repo

import com.example.tutorbridge.model.NotificationModel

interface NotificationRepo {
    fun sendNotification(userId: String, title: String, message: String, callback: (Boolean, String) -> Unit)
    fun sendNotificationToTutors(title: String, message: String, callback: (Boolean, String) -> Unit)
    fun getMyNotifications(callback: (Boolean, List<NotificationModel>) -> Unit)
}
