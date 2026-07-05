package com.example.tutorbridge.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tutorbridge.model.NotificationModel
import com.example.tutorbridge.repo.NotificationRepo
import com.example.tutorbridge.repo.NotificationRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationViewModel : ViewModel() {

    private val repo: NotificationRepo = NotificationRepoImpl()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _notifications = MutableStateFlow<List<NotificationModel>>(emptyList())
    val notifications: StateFlow<List<NotificationModel>> = _notifications

    fun loadNotifications() {
        _isLoading.value = true
        repo.getMyNotifications { _, list ->
            _isLoading.value = false
            _notifications.value = list.sortedByDescending { it.timestamp }
        }
    }
}
