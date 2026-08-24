package com.example.aistudyassistant.data

import com.example.aistudyassistant.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository that wraps interactions with the Nebius AI API.
 */
class NebiusRepository {

    private val apiService: NebiusApiService
    private val conversationHistory = mutableListOf<NebiusMessage>()
    private val systemInstruction = NebiusMessage(
        role = "system",
        content = "You are a helpful AI Study Assistant. Explain concepts clearly, use simple language, give examples, and help with exam prep. Break difficult topics into steps and provide code/formulas when applicable. Do not just give answers; encourage understanding. Keep responses concise."
    )

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.studio.nebius.ai/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(NebiusApiService::class.java)
        
        resetChat()
    }

    /**
     * Resets the chat history.
     */
    fun resetChat() {
        conversationHistory.clear()
        conversationHistory.add(systemInstruction)
    }

    /**
     * Sends a prompt to the Nebius API and returns the response text.
     */
    suspend fun sendMessage(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Add user message to history
            conversationHistory.add(NebiusMessage(role = "user", content = prompt))
            
            val request = NebiusChatRequest(messages = conversationHistory)
            val authHeader = "Bearer ${BuildConfig.NEBIUS_API_KEY}"
            
            val response = apiService.createChatCompletion(authHeader, request)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                val assistantMessage = responseBody?.choices?.firstOrNull()?.message?.content
                
                if (assistantMessage != null) {
                    // Add assistant response to history
                    conversationHistory.add(NebiusMessage(role = "assistant", content = assistantMessage))
                    Result.success(assistantMessage)
                } else {
                    Result.failure(Exception("Empty response from AI"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
