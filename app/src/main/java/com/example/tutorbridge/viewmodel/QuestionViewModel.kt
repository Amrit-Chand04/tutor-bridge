package com.example.tutorbridge.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tutorbridge.model.QuestionModel
import com.example.tutorbridge.repo.NotificationRepo
import com.example.tutorbridge.repo.NotificationRepoImpl
import com.example.tutorbridge.repo.QuestionRepo
import com.example.tutorbridge.repo.QuestionRepoImpl
import com.example.tutorbridge.repo.UserRepo
import com.example.tutorbridge.repo.UserRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QuestionViewModel @JvmOverloads constructor(
    private val repo: QuestionRepo = QuestionRepoImpl(),
    private val userRepo: UserRepo = UserRepoImpl(),
    private val notificationRepo: NotificationRepo = NotificationRepoImpl()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _questions = MutableStateFlow<List<QuestionModel>>(emptyList())
    val questions: StateFlow<List<QuestionModel>> = _questions

    private val _myQuestions = MutableStateFlow<List<QuestionModel>>(emptyList())
    val myQuestions: StateFlow<List<QuestionModel>> = _myQuestions

    fun loadAllQuestions() {
        _isLoading.value = true
        repo.getAllQuestions { _, list ->
            _isLoading.value = false
            _questions.value = list.sortedByDescending { it.timestamp }
        }
    }

    fun loadMyQuestions() {
        _isLoading.value = true
        repo.getMyQuestions { _, list ->
            _isLoading.value = false
            _myQuestions.value = list.sortedByDescending { it.timestamp }
        }
    }

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

        userRepo.getCurrentUser { _, userData ->
            val model = QuestionModel(
                title = title.trim(),
                subject = subject.trim(),
                details = details.trim(),
                askedBy = userData?.fullName ?: "",
                timestamp = System.currentTimeMillis()
            )
            repo.postQuestion(model) { success, msg ->
                _isLoading.value = false
                if (success) {
                    notificationRepo.sendNotificationToTutors(
                        "New Question",
                        "${model.askedBy.ifBlank { "A student" }} asked: ${model.title}"
                    ) { _, _ -> }
                }
                callback(success, msg)
            }
        }
    }
}
