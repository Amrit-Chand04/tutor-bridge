package com.example.tutorbridge

import android.app.Application
import com.example.tutorbridge.model.UserModel
import com.example.tutorbridge.repo.SessionRepo
import com.example.tutorbridge.repo.UserRepo
import com.example.tutorbridge.viewmodel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class LoginViewModelTest {

    @Test
    fun login_success_test() {
        val repo = mock<UserRepo>()
        val sessionRepo = mock<SessionRepo>()
        val application = mock<Application>()
        val viewModel = UserViewModel(application, repo, sessionRepo)

        val email = "test@gmail.com"
        val password = "123456"

        // Fake the repo's login call responding with success
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Login successful")
            null
        }.`when`(repo).login(eq(email), eq(password), any())

        // login() also fetches the current user afterwards to read the role
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, UserModel?) -> Unit>(0)
            callback(true, UserModel(uid = "uid123", fullName = "Amrit Chand", email = email, role = "Student"))
            null
        }.`when`(repo).getCurrentUser(any())

        var successResult = false
        var messageResult = ""
        var roleResult = ""

        viewModel.login(email, password) { success, message, role ->
            successResult = success
            messageResult = message
            roleResult = role
        }

        assertTrue(successResult)
        assertEquals("Login successful", messageResult)
        assertEquals("Student", roleResult)

        verify(repo).login(eq(email), eq(password), any())
        verify(repo).getCurrentUser(any())
    }
}
