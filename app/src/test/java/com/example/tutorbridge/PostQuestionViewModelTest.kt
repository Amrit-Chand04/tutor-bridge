package com.example.tutorbridge

import com.example.tutorbridge.model.UserModel
import com.example.tutorbridge.repo.NotificationRepo
import com.example.tutorbridge.repo.QuestionRepo
import com.example.tutorbridge.repo.UserRepo
import com.example.tutorbridge.viewmodel.QuestionViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class PostQuestionViewModelTest {

    @Test
    fun postQuestion_success_test() {
        val repo = mock<QuestionRepo>()
        val userRepo = mock<UserRepo>()
        val notificationRepo = mock<NotificationRepo>()
        val viewModel = QuestionViewModel(repo, userRepo, notificationRepo)

        val title = "What's your question?"
        val subject = "Computer"
        val details = "What is a database?"

        // postQuestion() first looks up the current user to fill in "askedBy"
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, UserModel?) -> Unit>(0)
            callback(true, UserModel(uid = "studentUid", fullName = "Amrit Chand", email = "test@gmail.com", role = "Student"))
            null
        }.`when`(userRepo).getCurrentUser(any())

        // Fake the repo's postQuestion call responding with success
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Question posted successfully")
            null
        }.`when`(repo).postQuestion(any(), any())

        var successResult = false
        var messageResult = ""

        viewModel.postQuestion(title, subject, details) { success, message ->
            successResult = success
            messageResult = message
        }

        assertTrue(successResult)
        assertEquals("Question posted successfully", messageResult)

        verify(userRepo).getCurrentUser(any())
        verify(repo).postQuestion(any(), any())
        verify(notificationRepo).sendNotificationToTutors(any(), any(), any())
    }
}
