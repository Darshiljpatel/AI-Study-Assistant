package com.example.aistudyassistant.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
        Triple("Home", Pair(Icons.Filled.Home, Icons.Outlined.Home), Home),
        Triple("History", Pair(Icons.Filled.History, Icons.Outlined.History), History),
        Triple("Profile", Pair(Icons.Filled.Person, Icons.Outlined.Person), Profile)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        items.forEach { (label, icons, route) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = if (isSelected) icons.first else icons.second, 
                        contentDescription = label 
                    ) 
                },
                label = { 
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium
                    ) 
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        onNavigate(route)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
