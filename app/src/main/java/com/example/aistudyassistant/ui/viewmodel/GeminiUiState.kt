package com.example.aistudyassistant.ui.viewmodel

/**
 * Represents the UI state for Gemini AI operations.
 */
sealed class GeminiUiState {
    /** No operation has been performed yet. */
    data object Idle : GeminiUiState()

    /** A request is currently in progress. */
    data object Loading : GeminiUiState()

    /** The request completed successfully. */
    data class Success(val response: String) : GeminiUiState()

    /** The request failed with a user-friendly error message. */
    data class Error(val message: String) : GeminiUiState()
}
