package com.example.aistudyassistant

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// Top Level
@Serializable data object Home : NavKey
@Serializable data object History : NavKey
@Serializable data object Profile : NavKey

// Secondary
@Serializable data object AskAi : NavKey
@Serializable data object SummarizeNotes : NavKey
@Serializable data object GenerateQuiz : NavKey
@Serializable data object ExplainTopic : NavKey
