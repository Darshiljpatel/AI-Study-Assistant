package com.example.aistudyassistant.data

import com.example.aistudyassistant.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    /**
     * Generates a summary for the given notes using the specified style.
     * This is a one-shot request and does not use or modify the chat history.
     */
    suspend fun generateSummary(notes: String, style: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val systemInstruction = "You are an academic study assistant. Summarize only the information provided by the student. Do not invent facts. Organize information clearly for studying."
            val styleInstruction = when (style) {
                "Quick" -> "Style: QUICK. Provide a very concise summary. Focus only on essential information."
                "Detailed" -> "Style: DETAILED. Explain important concepts in detail. Include relevant examples and preserve important details."
                "Exam Revision" -> "Style: EXAM REVISION. Focus on definitions, important concepts, formulas, key facts, and provide quick revision points."
                else -> ""
            }
            
            val formattingInstruction = """
                Use the following format:
                ## Summary
                [A concise explanation of the notes]
                
                ## Key Points
                - [Important point]
                
                ## Important Definitions
                - [Term] — [definition]
                
                ## Important Formulas
                [Include formulas only if they appear in the notes. If no formulas exist, output: "No specific formulas found in the provided notes."]
                
                ## Important Examples
                [Include examples from the notes when available]
                
                ## Exam Revision
                [Short, high-value revision points]
            """.trimIndent()
            
            val fullSystemPrompt = "$systemInstruction\n\n$styleInstruction\n\n$formattingInstruction"
            
            val messages = listOf(
                NebiusMessage(role = "system", content = fullSystemPrompt),
                NebiusMessage(role = "user", content = notes)
            )
            
            val request = NebiusChatRequest(messages = messages)
            val authHeader = "Bearer ${BuildConfig.NEBIUS_API_KEY}"
            
            val response = apiService.createChatCompletion(authHeader, request)
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                val assistantMessage = responseBody?.choices?.firstOrNull()?.message?.content
                
                if (assistantMessage != null) {
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

    /**
     * Helper function to extract JSON block if the model wraps it in markdown.
     */
    private fun extractJson(response: String): String {
        var cleanResponse = response.trim()
        if (cleanResponse.startsWith("```json")) {
            cleanResponse = cleanResponse.removePrefix("```json")
        } else if (cleanResponse.startsWith("```")) {
            cleanResponse = cleanResponse.removePrefix("```")
        }
        if (cleanResponse.endsWith("```")) {
            cleanResponse = cleanResponse.removeSuffix("```")
        }
        return cleanResponse.trim()
    }

    /**
     * Generates a quiz with structured JSON.
     */
    suspend fun generateQuiz(topic: String, difficulty: String, count: Int): Result<List<QuizQuestion>> = withContext(Dispatchers.IO) {
        try {
            val systemPrompt = """
                You are a precise Quiz Generator. Generate exactly $count multiple-choice questions about "$topic".
                Difficulty level: $difficulty.
                You must respond ONLY with a valid JSON array of objects. No other text.
                Format of each object:
                {
                  "question": "The question text",
                  "options": ["Option A", "Option B", "Option C", "Option D"],
                  "correctAnswer": "The exact string from options that is correct",
                  "explanation": "Short explanation of why it is correct"
                }
            """.trimIndent()

            val messages = listOf(NebiusMessage(role = "system", content = systemPrompt))
            val request = NebiusChatRequest(messages = messages)
            val authHeader = "Bearer ${BuildConfig.NEBIUS_API_KEY}"
            
            val response = apiService.createChatCompletion(authHeader, request)
            
            if (response.isSuccessful) {
                val assistantMessage = response.body()?.choices?.firstOrNull()?.message?.content
                if (assistantMessage != null) {
                    val json = extractJson(assistantMessage)
                    val listType = object : TypeToken<List<QuizQuestion>>() {}.type
                    val questions: List<QuizQuestion> = Gson().fromJson(json, listType)
                    Result.success(questions)
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

    /**
     * Explains a topic returning structured JSON.
     */
    suspend fun explainTopic(topic: String, level: String): Result<TopicExplanation> = withContext(Dispatchers.IO) {
        try {
            val systemPrompt = """
                You are an expert tutor. Explain the topic "$topic" for a "$level" audience.
                You must respond ONLY with a valid JSON object. No other text.
                Format:
                {
                  "definition": "Clear definition",
                  "coreConcept": "The main concept explained simply",
                  "howItWorks": "Step by step or logical flow",
                  "realWorldExample": "An analogy or real-world example",
                  "codeExample": "A short code snippet if applicable, otherwise a string saying 'Not applicable'",
                  "commonMistakes": "Common pitfalls or misconceptions",
                  "quickRevisionNotes": "Bullet points for quick revision"
                }
            """.trimIndent()

            val messages = listOf(NebiusMessage(role = "system", content = systemPrompt))
            val request = NebiusChatRequest(messages = messages)
            val authHeader = "Bearer ${BuildConfig.NEBIUS_API_KEY}"
            
            val response = apiService.createChatCompletion(authHeader, request)
            
            if (response.isSuccessful) {
                val assistantMessage = response.body()?.choices?.firstOrNull()?.message?.content
                if (assistantMessage != null) {
                    val json = extractJson(assistantMessage)
                    val explanation: TopicExplanation = Gson().fromJson(json, TopicExplanation::class.java)
                    Result.success(explanation)
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
