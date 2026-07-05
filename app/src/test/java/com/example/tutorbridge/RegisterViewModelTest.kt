package com.example.tutorbridge

import android.app.Application
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

class RegisterViewModelTest {

    @Test
    fun register_success_test() {
        val repo = mock<UserRepo>()
        val sessionRepo = mock<SessionRepo>()
        val application = mock<Application>()
        val viewModel = UserViewModel(application, repo, sessionRepo)

        val fullName = "Amrit Chand"
        val email = "test@gmail.com"
        val password = "123456"
        val uid = "newUid"

        // Fake the repo's register call responding with success
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, String) -> Unit>(2)
            callback(true, "Account created successfully", uid)
            null
        }.`when`(repo).register(eq(email), eq(password), any())

        // register() also saves the user's profile afterwards
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Signup Successful")
            null
        }.`when`(repo).addUser(eq(uid), any(), any())

        var successResult = false
        var messageResult = ""

        viewModel.register(fullName, email, password, password, "Student") { success, message ->
            successResult = success
            messageResult = message
        }

        assertTrue(successResult)
        assertEquals("Signup Successful", messageResult)

        verify(repo).register(eq(email), eq(password), any())
        verify(repo).addUser(eq(uid), any(), any())
    }
}
