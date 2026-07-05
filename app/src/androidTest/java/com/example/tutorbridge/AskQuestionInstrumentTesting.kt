package com.example.tutorbridge

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tutorbridge.view.AskQuestionActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AskQuestionInstrumentTesting {

    @get:Rule
    val composeRule = createAndroidComposeRule<AskQuestionActivity>()

    @Test
    fun askQuestionScreen_acceptsInput() {
        composeRule.onNodeWithTag("questionTitle")
            .performTextInput("What's your question?")

        composeRule.onNodeWithTag("questionSubject")
            .performTextInput("Computer")

        composeRule.onNodeWithTag("questionDetails")
            .performTextInput("What is a database?")
    }

    @Test
    fun postQuestionButton_canBeClicked() {
        composeRule.onNodeWithTag("postQuestion")
            .performClick()
    }
}
