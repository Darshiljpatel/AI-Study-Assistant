package com.example.aistudyassistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aistudyassistant.data.ChatMessage
import com.example.aistudyassistant.data.NebiusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * State representing the UI of the Ask AI screen.
 */
data class AskAiUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AskAiViewModel : ViewModel() {

    private val repository = NebiusRepository()

    private val _uiState = MutableStateFlow(AskAiUiState())
    val uiState: StateFlow<AskAiUiState> = _uiState.asStateFlow()

    /**
     * Sends the given [prompt] to Gemini and updates the chat history.
     */
    fun sendMessage(prompt: String) {
        val trimmedPrompt = prompt.trim()
        if (trimmedPrompt.isBlank()) return
        if (_uiState.value.isLoading) return

        // 1. Add user message to UI state and set loading to true, clear any previous errors
        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + ChatMessage(trimmedPrompt, isUser = true),
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            // 2. Send the message to the Chat session in the background
            repository.sendMessage(trimmedPrompt)
                .onSuccess { responseText ->
                    // 3. On success, add AI message and set loading to false
                    _uiState.update { currentState ->
                        currentState.copy(
                            messages = currentState.messages + ChatMessage(responseText, isUser = false),
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    // 4. On failure, set the error message and loading to false
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "An unexpected error occurred."
                        )
                    }
                }
        }
    }

    /**
     * Clears the current error message.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
