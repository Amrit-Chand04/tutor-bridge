package com.example.tutorbridge.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tutorbridge.model.ApplyTuitionModel
import com.example.tutorbridge.model.CreateRequestModel
import com.example.tutorbridge.repo.ApplyTuitionRepo
import com.example.tutorbridge.repo.ApplyTuitionRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ApplyTuitionViewModel : ViewModel() {

    private val repo: ApplyTuitionRepo = ApplyTuitionRepoImpl()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _appliedRequestIds = MutableStateFlow<List<String>>(emptyList())
    val appliedRequestIds: StateFlow<List<String>> = _appliedRequestIds

    private val _myApplications = MutableStateFlow<List<ApplyTuitionModel>>(emptyList())
    val myApplications: StateFlow<List<ApplyTuitionModel>> = _myApplications

    private val _requestApplications = MutableStateFlow<List<ApplyTuitionModel>>(emptyList())
    val requestApplications: StateFlow<List<ApplyTuitionModel>> = _requestApplications

    private val _isLoadingApplications = MutableStateFlow(false)
    val isLoadingApplications: StateFlow<Boolean> = _isLoadingApplications

    fun loadApplicationsForRequest(requestId: String) {
        _isLoadingApplications.value = true
        repo.getApplicationsForRequest(requestId) { _, list ->
            _isLoadingApplications.value = false
            _requestApplications.value = list
        }
    }

    fun acceptApplication(application: ApplyTuitionModel, callback: (Boolean, String) -> Unit) {
        _isLoading.value = true
        repo.updateApplicationStatus(application.tutorId, application.applicationId, "accepted") { success, msg ->
            _isLoading.value = false
            if (success) {
                _requestApplications.value = _requestApplications.value.map {
                    if (it.applicationId == application.applicationId) it.copy(status = "accepted") else it
                }
            }
            callback(success, msg)
        }
    }

    fun loadMyApplications() {
        _isLoading.value = true
        repo.getMyApplications { _, list ->
            _isLoading.value = false
            _myApplications.value = list
        }
    }

    fun deleteApplication(applicationId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteApplication(applicationId) { success, msg ->
            if (success) _myApplications.value = _myApplications.value.filter { it.applicationId != applicationId }
            callback(success, msg)
        }
    }

    fun updateApplication(application: ApplyTuitionModel, contactNumber: String, callback: (Boolean, String) -> Unit) {
        if (contactNumber.isBlank()) { callback(false, "Phone number is required"); return }
        _isLoading.value = true
        val updated = application.copy(contactNumber = contactNumber.trim())
        repo.updateApplication(updated) { success, msg ->
            _isLoading.value = false
            if (success) _myApplications.value = _myApplications.value.map { if (it.applicationId == application.applicationId) updated else it }
            callback(success, msg)
        }
    }

    fun loadAppliedRequestIds() {
        repo.getAppliedRequestIds { ids ->
            _appliedRequestIds.value = ids
        }
    }

    fun apply(request: CreateRequestModel, contactNumber: String, callback: (Boolean, String) -> Unit) {
        if (contactNumber.isBlank()) { callback(false, "Phone number is required"); return }
        _isLoading.value = true
        val model = ApplyTuitionModel(
            requestId = request.requestId,
            studentId = request.userId,
            subject = request.subject,
            grade = request.grade,
            budget = request.budget,
            contactNumber = contactNumber.trim()
        )
        repo.applyForRequest(model) { success, msg ->
            _isLoading.value = false
            callback(success, msg)
        }
    }
}
