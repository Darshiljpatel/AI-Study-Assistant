package com.example.aistudyassistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aistudyassistant.data.NebiusRepository
import com.example.aistudyassistant.data.TopicExplanation
import com.example.aistudyassistant.AIStudyAssistantApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ExplainState {
    IDLE, LOADING, SUCCESS, ERROR
}

data class ExplainTopicUiState(
    val topicInput: String = "",
    val selectedLevel: String = "College Student",
    val state: ExplainState = ExplainState.IDLE,
    val explanationResult: TopicExplanation? = null,
    val errorMessage: String? = null
)

class ExplainTopicViewModel : ViewModel() {

    private val repository = NebiusRepository()
    private val historyRepo = AIStudyAssistantApp.historyRepository
    
    private val _uiState = MutableStateFlow(ExplainTopicUiState())
    val uiState: StateFlow<ExplainTopicUiState> = _uiState.asStateFlow()

    fun updateTopic(topic: String) {
        _uiState.update { it.copy(topicInput = topic) }
    }

    fun updateLevel(level: String) {
        _uiState.update { it.copy(selectedLevel = level) }
    }

    fun explainTopic() {
        val currentState = _uiState.value
        val topic = currentState.topicInput.trim()
        
        if (topic.isEmpty()) {
            _uiState.update { it.copy(state = ExplainState.ERROR, errorMessage = "Please enter a topic.") }
            return
        }

        _uiState.update { 
            it.copy(
                state = ExplainState.LOADING,
                errorMessage = null,
                explanationResult = null
            ) 
        }

        viewModelScope.launch {
            val result = repository.explainTopic(topic, currentState.selectedLevel)
            
            result.onSuccess { explanation ->
                _uiState.update { 
                    it.copy(
                        state = ExplainState.SUCCESS,
                        explanationResult = explanation
                    ) 
                }
                val formatted = "Definition:\n${explanation.definition}\n\n" +
                                "Core Concept:\n${explanation.coreConcept}\n\n" +
                                "How It Works:\n${explanation.howItWorks}\n\n" +
                                "Example:\n${explanation.realWorldExample}\n\n" +
                                (explanation.codeExample?.let { "Code Example:\n$it" } ?: "")
                historyRepo.saveHistory("Explain: $topic (${currentState.selectedLevel})", formatted.trim(), "Explain Topic")
            }.onFailure { exception ->
                _uiState.update { 
                    it.copy(
                        state = ExplainState.ERROR,
                        errorMessage = exception.message ?: "An unexpected error occurred."
                    ) 
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(state = ExplainState.IDLE, errorMessage = null) }
    }
    
    fun clearAll() {
        _uiState.update { 
            it.copy(
                topicInput = "",
                state = ExplainState.IDLE,
                explanationResult = null,
                errorMessage = null
            ) 
        }
    }
}
