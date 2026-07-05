package com.example.tutorbridge

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tutorbridge.view.CreateRequestActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateRequestInstrumentTesting {

    @get:Rule
    val composeRule = createAndroidComposeRule<CreateRequestActivity>()

    @Test
    fun createRequestScreen_acceptsInput() {
        composeRule.onNodeWithTag("subject")
            .performTextInput("Math")

        composeRule.onNodeWithTag("grade")
            .performTextInput("Class 10")

        composeRule.onNodeWithTag("location")
            .performTextInput("Chabahil, KTM")

        composeRule.onNodeWithTag("budget")
            .performTextInput("8000")

        composeRule.onNodeWithTag("preferredTime")
            .performTextInput("5:00 PM - 7:00 PM")

        composeRule.onNodeWithTag("description")
            .performTextInput("Need help with algebra")

        composeRule.onNodeWithTag("contactNumber")
            .performTextInput("9800000000")
    }

    @Test
    fun submitRequestButton_canBeClicked() {
        composeRule.onNodeWithTag("submitRequest")
            .performClick()
    }
}
