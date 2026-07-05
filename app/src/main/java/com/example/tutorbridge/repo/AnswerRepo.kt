package com.example.tutorbridge.repo

import com.example.tutorbridge.model.AnswerModel

interface AnswerRepo {
    fun postAnswer(model: AnswerModel, callback: (Boolean, String) -> Unit)
    fun updateAnswer(model: AnswerModel, callback: (Boolean, String) -> Unit)
    fun deleteAnswer(questionId: String, answerId: String, callback: (Boolean, String) -> Unit)
    fun getAnswersForQuestion(questionId: String, callback: (Boolean, List<AnswerModel>) -> Unit)
}
