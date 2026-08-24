package com.example.aistudyassistant.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aistudyassistant.ui.viewmodel.SummarizerViewModel

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummarizerScreen(
    onNavigateBack: () -> Unit,
    viewModel: SummarizerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.extractTextFromPdf(context, it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Summarize Notes") },
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            if (uiState.summaryResult == null) {
                // Input Section
                
                Button(
                    onClick = { pdfPickerLauncher.launch("application/pdf") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Upload PDF")
                }
                
                OutlinedTextField(
                    value = uiState.notesInput,
                    onValueChange = { viewModel.updateNotes(it) },
                    label = { Text("Paste your notes") },
                    placeholder = { Text("Paste your lecture notes here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    maxLines = 15
                )

                // Character/Word Counter
                Text(
                    text = "${uiState.notesInput.length} chars | ${uiState.notesInput.split("\\s+".toRegex()).count { it.isNotBlank() }} words",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )

                Text(
                    text = "Summary Style",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                val styles = listOf("Quick", "Detailed", "Exam Revision")
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    styles.forEachIndexed { index, style ->
                        SegmentedButton(
                            selected = uiState.selectedStyle == style,
                            onClick = { viewModel.updateStyle(style) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = styles.size)
                        ) {
                            Text(style)
                        }
                    }
                }

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = { viewModel.summarizeNotes() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyzing your notes...")
                    } else {
                        Text("Summarize Notes")
                    }
                }
            } else {
                // Result Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = uiState.summaryResult ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { viewModel.clearResult() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.clearResult(); viewModel.summarizeNotes() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Summarize Again")
                    }
                }
                
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("AI Summary", uiState.summaryResult)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Summary copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Copy Summary")
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.clearAll() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Start Over")
                }
            }
        }
    }
}
