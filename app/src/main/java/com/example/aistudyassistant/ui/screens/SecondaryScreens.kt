package com.example.aistudyassistant.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondaryScreenTemplate(
    title: String,
    message: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun AskAiScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    SecondaryScreenTemplate(
        title = "Ask AI",
        message = "Ask AI coming soon",
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun SummarizeNotesScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    SecondaryScreenTemplate(
        title = "Summarize Notes",
        message = "Summarizer coming soon",
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun GenerateQuizScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    SecondaryScreenTemplate(
        title = "Generate Quiz",
        message = "Quiz generator coming soon",
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun ExplainTopicScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    SecondaryScreenTemplate(
        title = "Explain Topic",
        message = "Topic explainer coming soon",
        onBack = onBack,
        modifier = modifier
    )
}
