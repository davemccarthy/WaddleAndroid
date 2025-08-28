package com.yourname.waddleui

import androidx.compose.ui.graphics.Color

enum class LetterStatus {
    PENDING, OUTCAST, ILLPOSITIONED, POSITIONED
}

data class GridEntry(
    var value: String = "",
    var disabled: Boolean = true,
    var foreground: Color = Color.Black,
    var background: Color = Color.White,
    var border: Color = Color.Gray
)

data class GridRow(
    var disabled: Boolean = true,
    var entries: List<GridEntry> = List(5) { GridEntry() }
)

data class Key(
    val value: String,
    var foreground: Color = Color.Black,
    var background: Color = Color(0xFFD3D3D3),
    var status: LetterStatus = LetterStatus.PENDING
)

data class KeyRow(
    var keys: List<Key>
)

data class Gamer(
    var answer: String = "",
    var over: Boolean = false,
    var cursorX: Int = 0,
    var cursorY: Int = 0
)

