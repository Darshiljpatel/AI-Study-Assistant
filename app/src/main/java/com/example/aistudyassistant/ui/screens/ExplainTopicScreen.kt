package com.example.aistudyassistant.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aistudyassistant.ui.viewmodel.ExplainState
import com.example.aistudyassistant.ui.viewmodel.ExplainTopicUiState
import com.example.aistudyassistant.ui.viewmodel.ExplainTopicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplainTopicScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExplainTopicViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explain Topic") },
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
                ExplainState.IDLE -> ExplainSetupView(viewModel, uiState)
                ExplainState.LOADING -> LoadingView("Explaining Topic...")
                ExplainState.SUCCESS -> ExplainResultView(viewModel, uiState)
                ExplainState.ERROR -> ErrorView(
                    message = uiState.errorMessage ?: "Unknown Error",
                    onDismiss = { viewModel.clearError() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplainSetupView(viewModel: ExplainTopicViewModel, uiState: ExplainTopicUiState) {
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
            label = { Text("Topic to explain") },
            placeholder = { Text("E.g., Quantum Computing, Polymorphism") },
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Target Audience Level", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            val levels = listOf("Beginner", "College Student", "Advanced")
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                levels.forEachIndexed { index, level ->
                    SegmentedButton(
                        selected = uiState.selectedLevel == level,
                        onClick = { viewModel.updateLevel(level) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = levels.size)
                    ) {
                        Text(level)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.explainTopic() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Text("Explain Topic", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun ExplainResultView(viewModel: ExplainTopicViewModel, uiState: ExplainTopicUiState) {
    val explanation = uiState.explanationResult
    
    if (explanation == null) {
        ErrorView(message = "Explanation missing", onDismiss = { viewModel.clearError() })
        return
    }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = uiState.topicInput,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

        ExpandableSection(title = "Definition", content = explanation.definition, defaultExpanded = true)
        ExpandableSection(title = "Core Concept", content = explanation.coreConcept)
        ExpandableSection(title = "How It Works", content = explanation.howItWorks)
        ExpandableSection(title = "Real-World Example", content = explanation.realWorldExample)
        
        if (explanation.codeExample != "Not applicable" && explanation.codeExample.isNotBlank()) {
            ExpandableSection(title = "Code Example", content = explanation.codeExample)
        }
        
        ExpandableSection(title = "Common Mistakes", content = explanation.commonMistakes)
        ExpandableSection(title = "Quick Revision Notes", content = explanation.quickRevisionNotes)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.clearAll() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Text("Explain Another Topic", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun ExpandableSection(title: String, content: String, defaultExpanded: Boolean = false) {
    var expanded by remember { mutableStateOf(defaultExpanded) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (expanded) 4.dp else 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                androidx.compose.foundation.text.selection.SelectionContainer {
                    Text(
                        text = content,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
