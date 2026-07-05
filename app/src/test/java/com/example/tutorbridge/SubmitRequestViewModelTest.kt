package com.example.tutorbridge

import com.example.tutorbridge.repo.CreateRequestRepo
import com.example.tutorbridge.repo.NotificationRepo
import com.example.tutorbridge.viewmodel.RequestViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class SubmitRequestViewModelTest {

    @Test
    fun submitRequest_success_test() {
        val repo = mock<CreateRequestRepo>()
        val notificationRepo = mock<NotificationRepo>()
        val viewModel = RequestViewModel(repo, notificationRepo)

        // Fake the repo's addRequest call responding with success
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Request submitted successfully")
            null
        }.`when`(repo).addRequest(any(), any())

        var successResult = false
        var messageResult = ""

        viewModel.submitRequest(
            subject = "Math",
            grade = "Class 10",
            preferredGender = "Any",
            location = "Chabahil, KTM",
            budget = "8000",
            preferredTime = "5:00 PM - 7:00 PM",
            description = "Need help with algebra",
            contactNumber = "9800000000"
        ) { success, message ->
            successResult = success
            messageResult = message
        }

        assertTrue(successResult)
        assertEquals("Request submitted successfully", messageResult)

        verify(repo).addRequest(any(), any())
        verify(notificationRepo).sendNotificationToTutors(any(), any(), any())
    }
}
