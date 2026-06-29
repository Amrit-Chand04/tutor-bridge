package com.example.tutorbridge.repo

import com.example.tutorbridge.model.CreateRequestModel

interface CreateRequestRepo {
    fun addRequest(model: CreateRequestModel, callback: (Boolean, String) -> Unit)
}
