package com.example.tutorbridge.repo

import com.example.tutorbridge.model.ApplyTuitionModel

interface ApplyTuitionRepo {
    fun applyForRequest(model: ApplyTuitionModel, callback: (Boolean, String) -> Unit)
    fun getAppliedRequestIds(callback: (List<String>) -> Unit)
    fun getMyApplications(callback: (Boolean, List<ApplyTuitionModel>) -> Unit)
    fun deleteApplication(applicationId: String, callback: (Boolean, String) -> Unit)
    fun updateApplication(model: ApplyTuitionModel, callback: (Boolean, String) -> Unit)
}
