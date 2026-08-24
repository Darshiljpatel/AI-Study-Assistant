package com.example.aistudyassistant.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NebiusApiService {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: NebiusChatRequest
    ): Response<NebiusChatResponse>
}
