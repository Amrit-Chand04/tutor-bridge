package com.example.tutorbridge

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tutorbridge.view.SignUpActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpInstrumentTesting {

    @get:Rule
    val composeRule = createAndroidComposeRule<SignUpActivity>()

    @Test
    fun signUpScreen_acceptsInput() {
        composeRule.onNodeWithTag("fullName")
            .performTextInput("Amrit Chand")

        composeRule.onNodeWithTag("signupEmail")
            .performTextInput("ammu@gmail.com")

        composeRule.onNodeWithTag("signupPassword")
            .performTextInput("password123")

        composeRule.onNodeWithTag("confirmPassword")
            .performTextInput("password123")
    }

    @Test
    fun signUpButton_canBeClicked() {
        composeRule.onNodeWithTag("signupButton")
            .performClick()
    }
}
