package com.example.aistudyassistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aistudyassistant.data.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Gemini-powered AI features.
 *
 * Exposes [uiState] as a [StateFlow] that the UI observes.
 * Call [sendPrompt] to send a user query to Gemini.
 */
class GeminiViewModel : ViewModel() {

    private val repository = GeminiRepository()

    private val _uiState = MutableStateFlow<GeminiUiState>(GeminiUiState.Idle)
    val uiState: StateFlow<GeminiUiState> = _uiState.asStateFlow()

    /**
     * Sends the given [prompt] to Gemini and updates [uiState] accordingly.
     *
     * If a request is already in progress, this call is ignored to prevent
     * duplicate requests.
     */
    fun sendPrompt(prompt: String) {
        // Prevent duplicate requests
        if (_uiState.value is GeminiUiState.Loading) return

        // Don't send empty prompts
        if (prompt.isBlank()) {
            _uiState.value = GeminiUiState.Error("Please enter a question or topic.")
            return
        }

        viewModelScope.launch {
            _uiState.value = GeminiUiState.Loading

            repository.generateResponse(prompt)
                .onSuccess { response ->
                    _uiState.value = GeminiUiState.Success(response)
                }
                .onFailure { error ->
                    _uiState.value = GeminiUiState.Error(
                        error.message ?: "An unexpected error occurred."
                    )
                }
        }
    }

    /**
     * Resets the UI state back to Idle.
     * Useful when navigating away or clearing the current response.
     */
    fun resetState() {
        _uiState.value = GeminiUiState.Idle
    }
}
