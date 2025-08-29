package com.yourname.waddleui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HeaderView(streak: Int, record: Int) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .background(
                    color = Color(0xDDCCFCFD), // Cool light bluw background
                    shape = RoundedCornerShape(12.dp)
                )
                .shadow(1.5.dp, RoundedCornerShape(2.dp))
                .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderItem(title = "STREAK", value = streak)
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "WADDLE",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.Blue
            )
            Text(
                text = "Word Puzzle",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        
        HeaderItem(title = "RECORD", value = record)
    }
}

@Composable
fun HeaderItem(title: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Text(
            text = value.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun WordGridView(
    grid: List<GridRow>,
    onEntryClick: (Int, Int) -> Unit,
    onRowDoubleClick: (Int) -> Unit
) {
            Column(
            modifier = Modifier
                //.fillMaxWidth()
                .padding(8.dp)
                .background(
                    color = Color(0xDDCCFCFD), // Cool white background
                    shape = RoundedCornerShape(16.dp)
                )
                .shadow(1.5.dp, RoundedCornerShape(2.dp))
                .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        grid.forEachIndexed { rowIndex, row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickable { onRowDoubleClick(rowIndex) }
            ) {
                row.entries.forEachIndexed { colIndex, entry ->
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = entry.background,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = entry.border,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onEntryClick(rowIndex, colIndex) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = entry.value,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = entry.foreground
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KeyboardView(
    keyboard: List<KeyRow>,
    onKeyPress: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = Color(0xDDCCFCFD), // Cool white background
                shape = RoundedCornerShape(12.dp)
            )
            .shadow(1.5.dp, RoundedCornerShape(2.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        keyboard.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.keys.forEach { key ->
                    val keyWidth = when (key.value) {
                        "⏎", "⌫" -> 1.5f
                        else -> 1f
                    }
                    
                    Box(
                        modifier = Modifier
                            .weight(keyWidth)
                            .height(44.dp)
                            .background(
                                color = key.background,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color(0xFFCCCCCC),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { onKeyPress(key.value) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = key.value,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = key.foreground,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageView(
    message: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .padding(12.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Blue, Color(0xFF800080)) // Blue to purple
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .shadow(0.5.dp, RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

