package com.example.aistudyassistant.data

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import java.io.IOException

/**
 * Repository that wraps all Gemini AI interactions via Firebase AI Logic.
 *
 * Uses the Gemini Developer API backend (free tier, no billing required).
 * The model instance is created once and reused across all calls.
 */
class GeminiRepository {

    private val generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.0-flash")

    /**
     * Sends a prompt to Gemini and returns the response text.
     *
     * @param prompt The user's input text.
     * @return [Result.success] with the response string, or [Result.failure] with a
     *         descriptive exception.
     */
    suspend fun generateResponse(prompt: String): Result<String> {
        return try {
            val response = generativeModel.generateContent(prompt)
            val text = response.text

            if (text.isNullOrBlank()) {
                Result.failure(Exception("The AI returned an empty response. Please try rephrasing your question."))
            } else {
                Result.success(text)
            }
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection. Please check your network and try again."))
        } catch (e: com.google.firebase.ai.type.ServerException) {
            Result.failure(Exception("The AI service is temporarily unavailable. Please try again in a moment."))
        } catch (e: com.google.firebase.ai.type.InvalidAPIKeyException) {
            Result.failure(Exception("Firebase configuration error. Please verify your Firebase setup."))
        } catch (e: com.google.firebase.ai.type.QuotaExceededException) {
            Result.failure(Exception("You've reached the usage limit. Please wait a moment before trying again."))
        } catch (e: com.google.firebase.ai.type.PromptBlockedException) {
            Result.failure(Exception("Your request was blocked by safety filters. Please rephrase your question."))
        } catch (e: com.google.firebase.ai.type.ResponseStoppedException) {
            Result.failure(Exception("The response was stopped early. Please try a shorter or simpler question."))
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }
}
