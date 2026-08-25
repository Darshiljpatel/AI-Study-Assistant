package com.example.aistudyassistant.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Warning
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
            .padding(24.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        OutlinedTextField(
            value = uiState.topicInput,
            onValueChange = { viewModel.updateTopic(it) },
            label = { Text("Topic to test on") },
            placeholder = { Text("E.g., World War 2, Cellular Biology") },
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.generateQuiz() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Text("Generate Quiz", style = MaterialTheme.typography.titleMedium)
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
        CircularProgressIndicator(strokeWidth = 3.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ActiveQuizView(viewModel: QuizViewModel, uiState: com.example.aistudyassistant.ui.viewmodel.QuizUiState) {
    val question = uiState.questions[uiState.currentQuestionIndex]
    val selectedAnswer = uiState.userAnswers[uiState.currentQuestionIndex]

    Column(
        modifier = Modifier
            .padding(24.dp)
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
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(32.dp))

        question.options.forEach { option ->
            val isSelected = option == selectedAnswer
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { viewModel.selectAnswer(option) },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = option, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { viewModel.previousQuestion() },
                enabled = uiState.currentQuestionIndex > 0,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Text("Previous")
            }
            Spacer(modifier = Modifier.width(16.dp))
            if (uiState.currentQuestionIndex < uiState.questions.size - 1) {
                Button(
                    onClick = { viewModel.nextQuestion() },
                    enabled = selectedAnswer != null,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Next")
                }
            } else {
                Button(
                    onClick = { viewModel.finishQuiz() },
                    enabled = selectedAnswer != null,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
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
            .padding(24.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, shape = androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${uiState.score}/${uiState.questions.size}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
        
        Text(
            text = "Quiz Completed!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        
        uiState.questions.forEachIndexed { index, question ->
            val userAnswer = uiState.userAnswers[index]
            val isCorrect = userAnswer == question.correctAnswer
            
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = if (isCorrect) "Correct" else "Incorrect",
                            tint = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Q${index + 1}: ${question.question}", 
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Your answer: ${userAnswer ?: "Skipped"}", style = MaterialTheme.typography.bodyMedium)
                    if (!isCorrect) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Correct answer: ${question.correctAnswer}", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Explanation: ${question.explanation}", 
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { viewModel.restartQuiz() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Text("Create New Quiz", style = MaterialTheme.typography.titleMedium)
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
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Oops!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onErrorContainer,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onDismiss) {
            Text("Go Back")
        }
    }
}
