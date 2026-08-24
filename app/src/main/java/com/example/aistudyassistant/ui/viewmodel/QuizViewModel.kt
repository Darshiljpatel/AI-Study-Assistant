package com.example.aistudyassistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aistudyassistant.data.NebiusRepository
import com.example.aistudyassistant.data.QuizQuestion
import com.example.aistudyassistant.AIStudyAssistantApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class QuizState {
    SETUP, LOADING, ACTIVE, FINISHED, ERROR
}

data class QuizUiState(
    val topicInput: String = "",
    val selectedDifficulty: String = "Medium",
    val selectedCount: Int = 5,
    val state: QuizState = QuizState.SETUP,
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val userAnswers: Map<Int, String> = emptyMap(),
    val errorMessage: String? = null
) {
    val score: Int
        get() = questions.withIndex().count { (index, question) ->
            userAnswers[index] == question.correctAnswer
        }
}

class QuizViewModel : ViewModel() {

    private val repository = NebiusRepository()
    private val historyRepo = AIStudyAssistantApp.historyRepository
    
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun updateTopic(topic: String) {
        _uiState.update { it.copy(topicInput = topic) }
    }

    fun updateDifficulty(difficulty: String) {
        _uiState.update { it.copy(selectedDifficulty = difficulty) }
    }

    fun updateCount(count: Int) {
        _uiState.update { it.copy(selectedCount = count) }
    }

    fun generateQuiz() {
        val currentState = _uiState.value
        val topic = currentState.topicInput.trim()
        
        if (topic.isEmpty()) {
            _uiState.update { it.copy(state = QuizState.ERROR, errorMessage = "Please enter a topic.") }
            return
        }

        _uiState.update { 
            it.copy(
                state = QuizState.LOADING,
                errorMessage = null,
                questions = emptyList(),
                currentQuestionIndex = 0,
                userAnswers = emptyMap()
            ) 
        }

        viewModelScope.launch {
            val result = repository.generateQuiz(topic, currentState.selectedDifficulty, currentState.selectedCount)
            
            result.onSuccess { questions ->
                if (questions.isEmpty()) {
                    _uiState.update { 
                        it.copy(state = QuizState.ERROR, errorMessage = "No questions generated.") 
                    }
                } else {
                    _uiState.update { 
                        it.copy(state = QuizState.ACTIVE, questions = questions) 
                    }
                    val formatted = questions.joinToString("\n\n") { q -> 
                        "Q: ${q.question}\nOptions: ${q.options.joinToString(", ")}\nAnswer: ${q.correctAnswer}\nExplanation: ${q.explanation}"
                    }
                    historyRepo.saveHistory("Quiz: $topic (${currentState.selectedDifficulty}, ${currentState.selectedCount} Qs)", formatted, "Quiz Generator")
                }
            }.onFailure { exception ->
                _uiState.update { 
                    it.copy(
                        state = QuizState.ERROR,
                        errorMessage = exception.message ?: "An unexpected error occurred."
                    ) 
                }
            }
        }
    }

    fun selectAnswer(answer: String) {
        _uiState.update { state ->
            val newAnswers = state.userAnswers.toMutableMap()
            newAnswers[state.currentQuestionIndex] = answer
            state.copy(userAnswers = newAnswers)
        }
    }

    fun nextQuestion() {
        _uiState.update { state ->
            if (state.currentQuestionIndex < state.questions.size - 1) {
                state.copy(currentQuestionIndex = state.currentQuestionIndex + 1)
            } else {
                state
            }
        }
    }

    fun previousQuestion() {
        _uiState.update { state ->
            if (state.currentQuestionIndex > 0) {
                state.copy(currentQuestionIndex = state.currentQuestionIndex - 1)
            } else {
                state
            }
        }
    }

    fun finishQuiz() {
        _uiState.update { it.copy(state = QuizState.FINISHED) }
    }

    fun clearError() {
        _uiState.update { it.copy(state = QuizState.SETUP, errorMessage = null) }
    }
    
    fun restartQuiz() {
        _uiState.update { 
            it.copy(
                state = QuizState.SETUP,
                questions = emptyList(),
                currentQuestionIndex = 0,
                userAnswers = emptyMap(),
                errorMessage = null
            ) 
        }
    }
}
