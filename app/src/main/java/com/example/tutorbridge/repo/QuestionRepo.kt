package com.example.tutorbridge.repo

import com.example.tutorbridge.model.QuestionModel

interface QuestionRepo {
    fun postQuestion(model: QuestionModel, callback: (Boolean, String) -> Unit)
    fun getAllQuestions(callback: (Boolean, List<QuestionModel>) -> Unit)
}
