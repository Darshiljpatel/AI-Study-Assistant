package com.example.aistudyassistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aistudyassistant.data.NebiusRepository
import android.content.Context
import android.net.Uri
import com.example.aistudyassistant.AIStudyAssistantApp
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SummarizerUiState(
    val notesInput: String = "",
    val selectedStyle: String = "Quick",
    val summaryResult: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SummarizerViewModel : ViewModel() {

    private val repository = NebiusRepository()
    private val historyRepo = AIStudyAssistantApp.historyRepository
    
    private val _uiState = MutableStateFlow(SummarizerUiState())
    val uiState: StateFlow<SummarizerUiState> = _uiState.asStateFlow()

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notesInput = notes) }
    }

    fun updateStyle(style: String) {
        _uiState.update { it.copy(selectedStyle = style) }
    }

    fun clearResult() {
        _uiState.update { it.copy(summaryResult = null, errorMessage = null) }
    }
    
    fun clearAll() {
        _uiState.update { 
            it.copy(
                notesInput = "",
                summaryResult = null, 
                errorMessage = null
            ) 
        }
    }

    fun summarizeNotes() {
        val currentState = _uiState.value
        val notes = currentState.notesInput.trim()
        
        if (notes.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter some notes to summarize.") }
            return
        }
        
        if (notes.length < 10) {
            _uiState.update { it.copy(errorMessage = "Notes are too short to summarize.") }
            return
        }

        _uiState.update { 
            it.copy(
                isLoading = true,
                errorMessage = null,
                summaryResult = null
            ) 
        }

        viewModelScope.launch {
            val result = repository.generateSummary(notes, currentState.selectedStyle)
            
            result.onSuccess { summary ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        summaryResult = summary
                    ) 
                }
                historyRepo.saveHistory("Summarize (Style: ${currentState.selectedStyle}):\n$notes", summary, "Summarizer")
            }.onFailure { exception ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "An unexpected error occurred."
                    ) 
                }
            }
        }
    }

    fun extractTextFromPdf(context: Context, uri: Uri) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val text = withContext(Dispatchers.IO) {
                    PDFBoxResourceLoader.init(context.applicationContext)
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        PDDocument.load(inputStream).use { document ->
                            val stripper = PDFTextStripper()
                            stripper.getText(document)
                        }
                    } ?: throw Exception("Failed to open file")
                }
                
                _uiState.update { 
                    it.copy(
                        notesInput = (it.notesInput + "\n\n" + text.trim()).trim(),
                        isLoading = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error reading PDF: ${e.message}"
                    ) 
                }
            }
        }
    }
}
