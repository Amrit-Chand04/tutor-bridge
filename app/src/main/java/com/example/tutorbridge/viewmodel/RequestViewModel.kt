package com.example.tutorbridge.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tutorbridge.model.CreateRequestModel
import com.example.tutorbridge.repo.CreateRequestRepo
import com.example.tutorbridge.repo.CreateRequestRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RequestViewModel : ViewModel() {

    private val repo: CreateRequestRepo = CreateRequestRepoImpl()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _requests = MutableStateFlow<List<CreateRequestModel>>(emptyList())
    val requests: StateFlow<List<CreateRequestModel>> = _requests

    fun submitRequest(
        subject: String,
        grade: String,
        preferredGender: String,
        location: String,
        budget: String,
        preferredTime: String,
        description: String,
        contactNumber: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (subject.isBlank()) { callback(false, "Subject is required"); return }
        if (grade.isBlank()) { callback(false, "Grade is required"); return }
        if (location.isBlank()) { callback(false, "Location is required"); return }
        if (budget.isBlank()) { callback(false, "Budget is required"); return }
        if (preferredTime.isBlank()) { callback(false, "Preferred time is required"); return }
        if (contactNumber.isBlank()) { callback(false, "Contact number is required"); return }

        _isLoading.value = true

        val model = CreateRequestModel(
            subject = subject.trim(),
            grade = grade.trim(),
            preferredGender = preferredGender,
            location = location.trim(),
            budget = budget.trim(),
            preferredTime = preferredTime.trim(),
            description = description.trim(),
            contactNumber = contactNumber.trim()
        )

        repo.addRequest(model) { success, msg ->
            _isLoading.value = false
            callback(success, msg)
        }
    }

    fun loadAllRequests() {
        _isLoading.value = true
        repo.getAllRequests { _, list ->
            _isLoading.value = false
            _requests.value = list
        }
    }

    fun loadMyRequests() {
        _isLoading.value = true
        repo.getMyRequests { _, list ->
            _isLoading.value = false
            _requests.value = list
        }
    }

    fun updateRequest(
        requestId: String,
        userId: String,
        subject: String,
        grade: String,
        preferredGender: String,
        location: String,
        budget: String,
        preferredTime: String,
        description: String,
        contactNumber: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (subject.isBlank()) { callback(false, "Subject is required"); return }
        if (grade.isBlank()) { callback(false, "Grade is required"); return }
        if (location.isBlank()) { callback(false, "Location is required"); return }
        if (budget.isBlank()) { callback(false, "Budget is required"); return }
        if (preferredTime.isBlank()) { callback(false, "Preferred time is required"); return }
        if (contactNumber.isBlank()) { callback(false, "Contact number is required"); return }

        _isLoading.value = true

        val model = CreateRequestModel(
            requestId = requestId,
            userId = userId,
            subject = subject.trim(),
            grade = grade.trim(),
            preferredGender = preferredGender,
            location = location.trim(),
            budget = budget.trim(),
            preferredTime = preferredTime.trim(),
            description = description.trim(),
            contactNumber = contactNumber.trim()
        )

        repo.updateRequest(model) { success, msg ->
            _isLoading.value = false
            callback(success, msg)
        }
    }

    fun deleteRequest(requestId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteRequest(requestId) { success, msg ->
            if (success) _requests.value = _requests.value.filter { it.requestId != requestId }
            callback(success, msg)
        }
    }
}
