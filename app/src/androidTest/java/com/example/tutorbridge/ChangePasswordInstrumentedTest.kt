package com.example.tutorbridge

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tutorbridge.view.ChangePassActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChangePasswordInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ChangePassActivity>()

    @Test
    fun changePasswordScreen_acceptsInput() {
        composeRule.onNodeWithTag("oldPassword")
            .performTextInput("oldPassword123")

        composeRule.onNodeWithTag("newPassword")
            .performTextInput("newPassword123")

        composeRule.onNodeWithTag("confirmNewPassword")
            .performTextInput("newPassword123")
    }

    @Test
    fun updatePasswordButton_canBeClicked() {
        composeRule.onNodeWithTag("updatePassword")
            .performClick()
    }
}
