package com.example.aistudyassistant.data

/**
 * Represents a single message in the chat conversation.
 *
 * @param text The content of the message.
 * @param isUser True if the message was sent by the user, false if it's from the AI.
 */
data class ChatMessage(
    val text: String,
    val isUser: Boolean
)
