package com.joist.assestment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joist.assestment.data.ValidationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface EchoUiState {
    data object Idle : EchoUiState
    data object Loading : EchoUiState
    data class Success(val text: String) : EchoUiState
    data class Error(val message: String) : EchoUiState
}

class EchoViewModel(
    private val repository: ValidationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EchoUiState>(EchoUiState.Idle)
    val uiState: StateFlow<EchoUiState> = _uiState.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    fun onTextChanged(text: String) {
        _inputText.value = text
        if (_uiState.value is EchoUiState.Error || _uiState.value is EchoUiState.Success) {
            _uiState.value = EchoUiState.Idle
        }
    }

    fun submit() {
        val text = _inputText.value.trim()
        if (text.isBlank()) {
            _uiState.value = EchoUiState.Error("Input cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = EchoUiState.Loading
            repository.validate(text)
                .onSuccess { _uiState.value = EchoUiState.Success(it) }
                .onFailure { _uiState.value = EchoUiState.Error(it.message ?: "Validation failed") }
        }
    }
}
