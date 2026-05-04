package com.joist.assestment

import app.cash.turbine.test
import com.joist.assestment.data.ValidationRepository
import com.joist.assestment.ui.EchoUiState
import com.joist.assestment.ui.EchoViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class EchoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: ValidationRepository = mockk()
    private lateinit var viewModel: EchoViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EchoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        assertIs<EchoUiState.Idle>(viewModel.uiState.value)
    }

    @Test
    fun `submit with empty input emits Error without a network call`() {
        viewModel.submit()
        assertIs<EchoUiState.Error>(viewModel.uiState.value)
        assertEquals("Input cannot be empty", (viewModel.uiState.value as EchoUiState.Error).message)
    }

    @Test
    fun `submit with blank-only input emits Error`() {
        viewModel.onTextChanged("   ")
        viewModel.submit()
        assertIs<EchoUiState.Error>(viewModel.uiState.value)
    }

    @Test
    fun `submit valid text flows through Loading then lands on Success`() = runTest(testDispatcher) {
        coEvery { repository.validate(any()) } returns Result.success("Hello")

        viewModel.uiState.test {
            assertEquals(EchoUiState.Idle, awaitItem())

            viewModel.onTextChanged("Hello")
            viewModel.submit()

            assertEquals(EchoUiState.Loading, awaitItem())

            advanceUntilIdle()

            val success = awaitItem()
            assertIs<EchoUiState.Success>(success)
            assertEquals("Hello", success.text)
        }
    }

    @Test
    fun `submit text flows through Loading then lands on Error when server rejects`() = runTest(testDispatcher) {
        val errorMessage = "Server rejected the input. Try a different text."
        coEvery { repository.validate(any()) } returns Result.failure(Exception(errorMessage))

        viewModel.uiState.test {
            assertEquals(EchoUiState.Idle, awaitItem())

            viewModel.onTextChanged("error something")
            viewModel.submit()

            assertEquals(EchoUiState.Loading, awaitItem())

            advanceUntilIdle()

            val error = awaitItem()
            assertIs<EchoUiState.Error>(error)
            assertEquals(errorMessage, error.message)
        }
    }

    @Test
    fun `editing input after an error resets state to Idle`() = runTest(testDispatcher) {
        coEvery { repository.validate(any()) } returns Result.failure(Exception("Rejected"))

        viewModel.onTextChanged("error text")
        viewModel.submit()
        advanceUntilIdle()
        assertIs<EchoUiState.Error>(viewModel.uiState.value)

        viewModel.onTextChanged("new text")
        assertIs<EchoUiState.Idle>(viewModel.uiState.value)
    }

    @Test
    fun `editing input after a success resets state to Idle`() = runTest(testDispatcher) {
        coEvery { repository.validate(any()) } returns Result.success("Hello")

        viewModel.onTextChanged("Hello")
        viewModel.submit()
        advanceUntilIdle()
        assertIs<EchoUiState.Success>(viewModel.uiState.value)

        viewModel.onTextChanged("something else")
        assertIs<EchoUiState.Idle>(viewModel.uiState.value)
    }

    @Test
    fun `success state echoes exactly the submitted text`() = runTest(testDispatcher) {
        val inputText = "Hello World"
        coEvery { repository.validate(inputText) } returns Result.success(inputText)

        viewModel.onTextChanged(inputText)
        viewModel.submit()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<EchoUiState.Success>(state)
        assertEquals(inputText, state.text)
    }
}
