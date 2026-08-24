package com.example.aistudyassistant.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ChatHistoryRepository(private val dao: ChatHistoryDao) {

    fun getAllHistory(): Flow<List<ChatHistoryEntity>> {
        return dao.getAllHistory()
    }

    suspend fun saveHistory(prompt: String, aiResponse: String, featureType: String) = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        val entity = ChatHistoryEntity(prompt, aiResponse, featureType, timestamp)
        dao.insert(entity)
    }
}
