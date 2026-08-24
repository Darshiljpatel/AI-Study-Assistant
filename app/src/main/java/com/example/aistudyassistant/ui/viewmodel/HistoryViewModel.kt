package com.example.aistudyassistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aistudyassistant.AIStudyAssistantApp
import com.example.aistudyassistant.data.local.ChatHistoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val historyRepo = AIStudyAssistantApp.historyRepository
    
    private val _history = MutableStateFlow<List<ChatHistoryEntity>>(emptyList())
    val history: StateFlow<List<ChatHistoryEntity>> = _history.asStateFlow()

    init {
        viewModelScope.launch {
            historyRepo.getAllHistory().collectLatest { list ->
                _history.value = list
            }
        }
    }
}
