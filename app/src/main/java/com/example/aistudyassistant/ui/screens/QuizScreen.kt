package com.example.aistudyassistant.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aistudyassistant.ui.viewmodel.QuizState
import com.example.aistudyassistant.ui.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Generator") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (uiState.state) {
                QuizState.SETUP -> QuizSetupView(viewModel, uiState)
                QuizState.LOADING -> LoadingView("Generating Quiz...")
                QuizState.ACTIVE -> ActiveQuizView(viewModel, uiState)
                QuizState.FINISHED -> QuizResultView(viewModel, uiState)
                QuizState.ERROR -> ErrorView(
                    message = uiState.errorMessage ?: "Unknown Error",
                    onDismiss = { viewModel.clearError() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupView(viewModel: QuizViewModel, uiState: com.example.aistudyassistant.ui.viewmodel.QuizUiState) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        OutlinedTextField(
            value = uiState.topicInput,
            onValueChange = { viewModel.updateTopic(it) },
            label = { Text("Topic to test on") },
            placeholder = { Text("E.g., World War 2, Cellular Biology") },
            modifier = Modifier.fillMaxWidth()
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Difficulty", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            val difficulties = listOf("Easy", "Medium", "Hard")
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                difficulties.forEachIndexed { index, difficulty ->
                    SegmentedButton(
                        selected = uiState.selectedDifficulty == difficulty,
                        onClick = { viewModel.updateDifficulty(difficulty) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = difficulties.size)
                    ) {
                        Text(difficulty)
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Number of Questions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            val counts = listOf(5, 10, 15)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                counts.forEachIndexed { index, count ->
                    SegmentedButton(
                        selected = uiState.selectedCount == count,
                        onClick = { viewModel.updateCount(count) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = counts.size)
                    ) {
                        Text("$count")
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.generateQuiz() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate Quiz")
        }
    }
}

@Composable
fun LoadingView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ActiveQuizView(viewModel: QuizViewModel, uiState: com.example.aistudyassistant.ui.viewmodel.QuizUiState) {
    val question = uiState.questions[uiState.currentQuestionIndex]
    val selectedAnswer = uiState.userAnswers[uiState.currentQuestionIndex]

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Question ${uiState.currentQuestionIndex + 1} of ${uiState.questions.size}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = question.question,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        question.options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (option == selectedAnswer),
                        onClick = { viewModel.selectAnswer(option) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == selectedAnswer),
                    onClick = null
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = option, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { viewModel.previousQuestion() },
                enabled = uiState.currentQuestionIndex > 0
            ) {
                Text("Previous")
            }
            
            if (uiState.currentQuestionIndex < uiState.questions.size - 1) {
                Button(
                    onClick = { viewModel.nextQuestion() },
                    enabled = selectedAnswer != null
                ) {
                    Text("Next")
                }
            } else {
                Button(
                    onClick = { viewModel.finishQuiz() },
                    enabled = selectedAnswer != null,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("Finish")
                }
            }
        }
    }
}

@Composable
fun QuizResultView(viewModel: QuizViewModel, uiState: com.example.aistudyassistant.ui.viewmodel.QuizUiState) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Quiz Completed!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Your Score: ${uiState.score} / ${uiState.questions.size}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        uiState.questions.forEachIndexed { index, question ->
            val userAnswer = uiState.userAnswers[index]
            val isCorrect = userAnswer == question.correctAnswer
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCorrect) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                     else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Q${index + 1}: ${question.question}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Your answer: ${userAnswer ?: "Skipped"}")
                    if (!isCorrect) {
                        Text("Correct answer: ${question.correctAnswer}", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Explanation: ${question.explanation}", 
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { viewModel.restartQuiz() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create New Quiz")
        }
    }
}

@Composable
fun ErrorView(message: String, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Oops!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onDismiss) {
            Text("Go Back")
        }
    }
}
