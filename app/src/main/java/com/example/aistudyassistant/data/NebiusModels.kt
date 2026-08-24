package com.example.aistudyassistant.data

import com.google.gson.annotations.SerializedName

data class NebiusChatRequest(
    val model: String = "meta-llama/Llama-3.3-70B-Instruct",
    val messages: List<NebiusMessage>,
    val temperature: Double = 0.7
)

data class NebiusMessage(
    val role: String,
    val content: String
)

data class NebiusChatResponse(
    val id: String,
    val choices: List<NebiusChoice>
)

data class NebiusChoice(
    val index: Int,
    val message: NebiusMessage,
    @SerializedName("finish_reason") val finishReason: String
)
