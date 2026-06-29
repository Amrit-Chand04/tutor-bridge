package com.example.tutorbridge.repo

import com.example.tutorbridge.model.CreateRequestModel

interface CreateRequestRepo {
    fun addRequest(model: CreateRequestModel, callback: (Boolean, String) -> Unit)
    fun getMyRequests(callback: (Boolean, List<CreateRequestModel>) -> Unit)
    fun deleteRequest(requestId: String, callback: (Boolean, String) -> Unit)
    fun updateRequest(model: CreateRequestModel, callback: (Boolean, String) -> Unit)
}
