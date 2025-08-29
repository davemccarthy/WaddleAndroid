package com.yourname.waddleui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WaddleUITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WaddleUIApp()
                }
            }
        }
    }
}

@Composable
fun WaddleUIApp() {
    val gameViewModel: GameViewModel = viewModel()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Black, // Very light blue-gray
                        Color(0xFFF0F5F8)  // Light blue-gray
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp), // Reduced padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp) // Reduced spacing
        ) {
            // Header
            HeaderView(
                streak = gameViewModel.streak,
                record = gameViewModel.record
            )
            
            // Word Grid
            WordGridView(
                grid = gameViewModel.grid,
                onEntryClick = { row, col -> gameViewModel.selectEntry(row, col) },
                onRowDoubleClick = { row -> gameViewModel.copyRow(row) }
            )
            
            // Keyboard or Message
            if (gameViewModel.message.isEmpty()) {
                KeyboardView(
                    keyboard = gameViewModel.keyboard,
                    onKeyPress = { key -> gameViewModel.keyPressed(key) }
                )
            } else {
                MessageView(
                    message = gameViewModel.message,
                    onClick = { gameViewModel.handleMessageClick() }
                )
            }
        }
    }
}

@Composable
fun WaddleUITheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
}

