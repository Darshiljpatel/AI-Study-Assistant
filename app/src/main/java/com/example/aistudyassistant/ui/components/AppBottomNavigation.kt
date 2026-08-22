package com.example.aistudyassistant.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.example.aistudyassistant.History
import com.example.aistudyassistant.Home
import com.example.aistudyassistant.Profile

@Composable
fun AppBottomNavigation(currentRoute: NavKey, onNavigate: (NavKey) -> Unit) {
    val items = listOf(
        Triple("Home", Icons.Default.Home, Home),
        Triple("History", Icons.Default.History, History),
        Triple("Profile", Icons.Default.Person, Profile)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { (label, icon, route) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        onNavigate(route)
                    }
                }
            )
        }
    }
}
