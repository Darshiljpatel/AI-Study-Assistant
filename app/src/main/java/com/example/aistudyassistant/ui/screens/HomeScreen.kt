package com.example.aistudyassistant.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.example.aistudyassistant.AskAi
import com.example.aistudyassistant.ExplainTopic
import com.example.aistudyassistant.GenerateQuiz
import com.example.aistudyassistant.Home
import com.example.aistudyassistant.SummarizeNotes
import com.example.aistudyassistant.ui.components.AppBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateTo: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        bottomBar = { AppBottomNavigation(currentRoute = Home, onNavigate = onNavigateTo) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AppHeader()
            WelcomeCard()
            FeaturesSection(onNavigateTo = onNavigateTo)
            StudyPlanSection()
        }
    }
}

@Composable
private fun AppHeader() {
    Column {
        Text(
            text = "AI Study Assistant",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Learn smarter. Study better.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Welcome back! \uD83D\uDC4B",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "What would you like to study today?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun FeaturesSection(onNavigateTo: (NavKey) -> Unit) {
    val features = listOf(
        FeatureItem("Ask AI", Icons.AutoMirrored.Filled.Chat, AskAi),
        FeatureItem("Summarize Notes", Icons.AutoMirrored.Filled.MenuBook, SummarizeNotes),
        FeatureItem("Generate Quiz", Icons.Default.Quiz, GenerateQuiz),
        FeatureItem("Explain Topic", Icons.Default.Lightbulb, ExplainTopic)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        
        // Using a Row with nested Columns to simulate a Grid as we are inside a scrollable Column
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(
                    feature = features[0],
                    onClick = { onNavigateTo(features[0].route) },
                    modifier = Modifier.weight(1f)
                )
                FeatureCard(
                    feature = features[1],
                    onClick = { onNavigateTo(features[1].route) },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(
                    feature = features[2],
                    onClick = { onNavigateTo(features[2].route) },
                    modifier = Modifier.weight(1f)
                )
                FeatureCard(
                    feature = features[3],
                    onClick = { onNavigateTo(features[3].route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    feature: FeatureItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun StudyPlanSection() {
    val tasks = listOf(
        "Review Biology Chapter 4",
        "Complete Math Assignment",
        "Read History Article"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Today's Study Plan",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                tasks.forEach { task ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Task",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = task,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private data class FeatureItem(val title: String, val icon: ImageVector, val route: NavKey)
