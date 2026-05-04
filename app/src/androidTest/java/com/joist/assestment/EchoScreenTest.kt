package com.joist.assestment

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.joist.assestment.data.ValidationRepository
import com.joist.assestment.ui.EchoScreen
import com.joist.assestment.ui.EchoViewModel
import com.joist.assestment.ui.theme.EchoAppTheme
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EchoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var repository: ValidationRepository
    private lateinit var viewModel: EchoViewModel

    @Before
    fun setUp() {
        repository = mockk()
        viewModel = EchoViewModel(repository)
        composeTestRule.setContent {
            EchoAppTheme {
                EchoScreen(viewModel = viewModel)
            }
        }
    }

    @Test
    fun inputFieldAndSubmitButtonAreVisible() {
        composeTestRule.onNodeWithTag("input_field").assertIsDisplayed()
        composeTestRule.onNodeWithTag("submit_button").assertIsDisplayed()
    }

    @Test
    fun submitButtonIsEnabledOnIdle() {
        composeTestRule.onNodeWithTag("submit_button").assertIsEnabled()
    }

    @Test
    fun emptySubmitShowsInlineError() {
        composeTestRule.onNodeWithTag("submit_button").performClick()
        composeTestRule.onNodeWithText("Input cannot be empty").assertIsDisplayed()
    }

    @Test
    fun successfulValidationShowsOutputCard() {
        coEvery { repository.validate(any()) } returns Result.success("Hello Joist")

        composeTestRule.onNodeWithTag("input_field").performTextInput("Hello Joist")
        composeTestRule.onNodeWithTag("submit_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodes(hasTestTag("output_card"))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("output_text").assertTextEquals("Hello Joist")
    }

    @Test
    fun failedValidationShowsErrorMessage() {
        coEvery { repository.validate(any()) } returns
            Result.failure(Exception("Server rejected the input. Try a different text."))

        composeTestRule.onNodeWithTag("input_field").performTextInput("error text")
        composeTestRule.onNodeWithTag("submit_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodes(hasText("Server rejected the input. Try a different text."))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Server rejected the input. Try a different text.")
            .assertIsDisplayed()
    }

    @Test
    fun submitButtonIsDisabledWhileLoading() {
        coEvery { repository.validate(any()) } coAnswers {
            delay(2_000)
            Result.success(firstArg())
        }

        composeTestRule.onNodeWithTag("input_field").performTextInput("Hello")
        composeTestRule.onNodeWithTag("submit_button").performClick()

        composeTestRule.onNodeWithTag("submit_button").assertIsNotEnabled()
    }
}
