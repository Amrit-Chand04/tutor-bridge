package com.example.tutorbridge

import android.app.Application
import com.example.tutorbridge.repo.SessionRepo
import com.example.tutorbridge.repo.UserRepo
import com.example.tutorbridge.viewmodel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class UpdateProfileViewModelTest {

    @Test
    fun updateUser_success_test() {
        val repo = mock<UserRepo>()
        val sessionRepo = mock<SessionRepo>()
        val application = mock<Application>()
        val viewModel = UserViewModel(application, repo, sessionRepo)

        val uid = "studentUid"
        val fullName = "Amrit Chand Thakuri"

        // Fake the repo's updateUser call responding with success
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Profile updated successfully")
            null
        }.`when`(repo).updateUser(eq(uid), eq(fullName), any())

        // updateUser() exposes its result via profileMessage state, not a callback
        viewModel.updateUser(uid, fullName)

        assertEquals("Profile updated successfully", viewModel.profileMessage.value)

        verify(repo).updateUser(eq(uid), eq(fullName), any())
    }
}
