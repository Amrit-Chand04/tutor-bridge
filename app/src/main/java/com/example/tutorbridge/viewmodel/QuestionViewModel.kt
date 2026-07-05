package com.example.tutorbridge.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tutorbridge.model.QuestionModel
import com.example.tutorbridge.repo.QuestionRepo
import com.example.tutorbridge.repo.QuestionRepoImpl
import com.example.tutorbridge.repo.UserRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QuestionViewModel : ViewModel() {

    private val repo: QuestionRepo = QuestionRepoImpl()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun postQuestion(
        title: String,
        subject: String,
        details: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (title.isBlank()) { callback(false, "Question title is required"); return }
        if (subject.isBlank()) { callback(false, "Subject is required"); return }
        if (details.isBlank()) { callback(false, "Question details are required"); return }

        _isLoading.value = true

        UserRepoImpl().getCurrentUser { _, userData ->
            val model = QuestionModel(
                title = title.trim(),
                subject = subject.trim(),
                details = details.trim(),
                askedBy = userData?.fullName ?: "",
                timestamp = System.currentTimeMillis()
            )
            repo.postQuestion(model) { success, msg ->
                _isLoading.value = false
                callback(success, msg)
            }
        }
    }
}
