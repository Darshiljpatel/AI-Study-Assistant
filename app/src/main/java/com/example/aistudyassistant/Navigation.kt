package com.example.aistudyassistant

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.aistudyassistant.ui.screens.*

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(Home)
    
    val onNavigateTo: (androidx.navigation3.runtime.NavKey) -> Unit = { route ->
        // For top level destinations, we might want to pop up to Home, but for simplicity
        // let's just add to backstack or pop back to avoid huge stacks of top level tabs.
        // If clicking a bottom tab, we ideally clear backstack up to Home.
        if (route is Home) {
            backStack.clear()
            backStack.add(Home)
        } else if (route is History || route is Profile) {
            // Check if already in backstack, otherwise just add
            if (backStack.last() != route) {
                backStack.add(route)
            }
        } else {
            backStack.add(route)
        }
    }

    val onBack: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeLast()
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = onBack,
        entryProvider = entryProvider {
            entry<Home> {
                HomeScreen(
                    onNavigateTo = onNavigateTo,
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<History> {
                HistoryScreen(
                    onNavigateTo = onNavigateTo,
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<Profile> {
                ProfileScreen(
                    onNavigateTo = onNavigateTo,
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<AskAi> {
                AskAiScreen(
                    onBack = onBack,
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<SummarizeNotes> {
                SummarizeNotesScreen(
                    onBack = onBack,
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<GenerateQuiz> {
                GenerateQuizScreen(
                    onBack = onBack,
                    modifier = Modifier.safeDrawingPadding()
                )
            }
            entry<ExplainTopic> {
                ExplainTopicScreen(
                    onBack = onBack,
                    modifier = Modifier.safeDrawingPadding()
                )
            }
        },
    )
}
