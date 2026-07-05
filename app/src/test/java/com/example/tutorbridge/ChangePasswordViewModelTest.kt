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

class ChangePasswordViewModelTest {

    @Test
    fun changePassword_success_test() {
        val repo = mock<UserRepo>()
        val sessionRepo = mock<SessionRepo>()
        val application = mock<Application>()
        val viewModel = UserViewModel(application, repo, sessionRepo)

        val oldPassword = "oldPass123"
        val newPassword = "newPass456"

        // Fake the repo's changePassword call responding with success
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Password changed successfully")
            null
        }.`when`(repo).changePassword(eq(oldPassword), eq(newPassword), any())

        var successResult = false
        var messageResult = ""

        viewModel.changePassword(oldPassword, newPassword, newPassword) { success, message ->
            successResult = success
            messageResult = message
        }

        assertTrue(successResult)
        assertEquals("Password changed successfully", messageResult)

        verify(repo).changePassword(eq(oldPassword), eq(newPassword), any())
    }
}
