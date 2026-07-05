package com.example.tutorbridge.repo

import com.example.tutorbridge.model.NotificationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationRepoImpl : NotificationRepo {

    private val auth = FirebaseAuth.getInstance()
    private val ref = FirebaseDatabase.getInstance().getReference("notifications")
    private val usersRef = FirebaseDatabase.getInstance().getReference("users")

    override fun sendNotification(userId: String, title: String, message: String, callback: (Boolean, String) -> Unit) {
        val notificationId = ref.child(userId).push().key ?: run {
            callback(false, "Failed to generate notification ID")
            return
        }

        val notification = NotificationModel(
            notificationId = notificationId,
            userId = userId,
            title = title,
            message = message,
            timestamp = System.currentTimeMillis()
        )

        ref.child(userId).child(notificationId).setValue(notification)
            .addOnSuccessListener { callback(true, "Notification sent") }
            .addOnFailureListener { callback(false, it.message ?: "Failed to send notification") }
    }

    override fun sendNotificationToTutors(title: String, message: String, callback: (Boolean, String) -> Unit) {
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { userSnapshot ->
                    val role = userSnapshot.child("role").getValue(String::class.java)
                    val tutorId = userSnapshot.key
                    if (role == "Teacher" && tutorId != null) {
                        sendNotification(tutorId, title, message) { _, _ -> }
                    }
                }
                callback(true, "Notifications sent")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message)
            }
        })
    }

    override fun getMyNotifications(callback: (Boolean, List<NotificationModel>) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            callback(false, emptyList())
            return
        }

        ref.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(NotificationModel::class.java) }
                callback(true, list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, emptyList())
            }
        })
    }
}
