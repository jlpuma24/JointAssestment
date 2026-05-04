package com.joist.assestment.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joist.assestment.R
import com.joist.assestment.ui.theme.EchoAppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun EchoScreen(
    viewModel: EchoViewModel = koinViewModel(),
    onNavigateToDetail: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    EchoScreenContent(
        uiState = uiState,
        inputText = inputText,
        onTextChanged = viewModel::onTextChanged,
        onSubmit = viewModel::submit
    )
}

@Composable
internal fun EchoScreenContent(
    uiState: EchoUiState,
    inputText: String,
    onTextChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = stringResource(R.string.title_echo_app),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.subtitle_instructions),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = onTextChanged,
                label = { Text(stringResource(R.string.label_your_text)) },
                singleLine = true,
                isError = uiState is EchoUiState.Error,
                supportingText = {
                    if (uiState is EchoUiState.Error) {
                        Text(
                            text = (uiState as EchoUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    onSubmit()
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_field")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    keyboardController?.hide()
                    onSubmit()
                },
                enabled = uiState !is EchoUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("submit_button")
            ) {
                if (uiState is EchoUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.action_submit))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = uiState is EchoUiState.Success,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val successText = (uiState as? EchoUiState.Success)?.text ?: ""
                OutputCard(text = successText)
            }
        }
    }
}

@Preview(name = "Idle", showBackground = true)
@Composable
private fun EchoScreenPreviewIdle() {
    EchoAppTheme {
        EchoScreenContent(
            uiState = EchoUiState.Idle,
            inputText = "",
            onTextChanged = {},
            onSubmit = {}
        )
    }
}

@Preview(name = "Loading", showBackground = true)
@Composable
private fun EchoScreenPreviewLoading() {
    EchoAppTheme {
        EchoScreenContent(
            uiState = EchoUiState.Loading,
            inputText = "Hello World",
            onTextChanged = {},
            onSubmit = {}
        )
    }
}

@Preview(name = "Success", showBackground = true)
@Composable
private fun EchoScreenPreviewSuccess() {
    EchoAppTheme {
        EchoScreenContent(
            uiState = EchoUiState.Success("Hello World"),
            inputText = "Hello World",
            onTextChanged = {},
            onSubmit = {}
        )
    }
}

@Preview(name = "Error", showBackground = true)
@Composable
private fun EchoScreenPreviewError() {
    EchoAppTheme {
        EchoScreenContent(
            uiState = EchoUiState.Error("Input cannot be empty"),
            inputText = "",
            onTextChanged = {},
            onSubmit = {}
        )
    }
}
