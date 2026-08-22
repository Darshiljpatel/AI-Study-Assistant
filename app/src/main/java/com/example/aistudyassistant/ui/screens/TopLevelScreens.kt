package com.example.aistudyassistant.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.example.aistudyassistant.History
import com.example.aistudyassistant.Profile
import com.example.aistudyassistant.ui.components.AppBottomNavigation

@Composable
fun HistoryScreen(
    onNavigateTo: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        bottomBar = { AppBottomNavigation(currentRoute = History, onNavigate = onNavigateTo) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "History coming soon",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
fun ProfileScreen(
    onNavigateTo: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        bottomBar = { AppBottomNavigation(currentRoute = Profile, onNavigate = onNavigateTo) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Profile coming soon",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
