package com.example.tutorbridge.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tutorbridge.model.AnswerModel
import com.example.tutorbridge.model.QuestionModel
import com.example.tutorbridge.repo.AnswerRepo
import com.example.tutorbridge.repo.AnswerRepoImpl
import com.example.tutorbridge.repo.NotificationRepoImpl
import com.example.tutorbridge.repo.UserRepoImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AnswerViewModel : ViewModel() {

    private val repo: AnswerRepo = AnswerRepoImpl()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _answers = MutableStateFlow<List<AnswerModel>>(emptyList())
    val answers: StateFlow<List<AnswerModel>> = _answers

    fun currentTutorId(): String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun loadAnswers(questionId: String) {
        _isLoading.value = true
        repo.getAnswersForQuestion(questionId) { _, list ->
            _isLoading.value = false
            _answers.value = list.sortedBy { it.timestamp }
        }
    }

    fun postAnswer(question: QuestionModel, answerText: String, callback: (Boolean, String) -> Unit) {
        if (answerText.isBlank()) { callback(false, "Answer cannot be empty"); return }

        _isSubmitting.value = true

        UserRepoImpl().getCurrentUser { _, userData ->
            val model = AnswerModel(
                questionId = question.questionId,
                answerText = answerText.trim(),
                tutorName = userData?.fullName ?: "",
                timestamp = System.currentTimeMillis()
            )
            repo.postAnswer(model) { success, msg ->
                _isSubmitting.value = false
                if (success) {
                    loadAnswers(question.questionId)
                    NotificationRepoImpl().sendNotification(
                        question.userId,
                        "Your Question was Answered",
                        "${model.tutorName.ifBlank { "A tutor" }} answered: ${question.title}"
                    ) { _, _ -> }
                }
                callback(success, msg)
            }
        }
    }

    fun updateAnswer(answer: AnswerModel, newText: String, callback: (Boolean, String) -> Unit) {
        if (newText.isBlank()) { callback(false, "Answer cannot be empty"); return }
        if (newText.trim() == answer.answerText) { callback(false, "No changes made"); return }

        _isSubmitting.value = true
        val updated = answer.copy(answerText = newText.trim())

        repo.updateAnswer(updated) { success, msg ->
            _isSubmitting.value = false
            if (success) loadAnswers(answer.questionId)
            callback(success, msg)
        }
    }

    fun deleteAnswer(answer: AnswerModel, callback: (Boolean, String) -> Unit) {
        repo.deleteAnswer(answer.questionId, answer.answerId) { success, msg ->
            if (success) loadAnswers(answer.questionId)
            callback(success, msg)
        }
    }
}
