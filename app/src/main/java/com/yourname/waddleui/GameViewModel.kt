package com.yourname.waddleui

import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    val dictionary = Dictionary(application)
    
    private var _streak by mutableStateOf(0)
    val streak: Int get() = _streak
    
    private var _record by mutableStateOf(0)
    val record: Int get() = _record
    
    private var _message by mutableStateOf("")
    val message: String get() = _message
    
    private var _grid by mutableStateOf(List(6) { GridRow() })
    val grid: List<GridRow> get() = _grid
    
    private var _keyboard by mutableStateOf(createInitialKeyboard())
    val keyboard: List<KeyRow> get() = _keyboard
    
    private var game = Gamer()
    
    private val praises = listOf(
        listOf("INCREDULOUS", "FANTASTIC"),
        listOf("WOW", "STUPENDOUS", "EXCEPTIONAL"),
        listOf("EXCELLENT", "SUPERB", "WONDERFUL"),
        listOf("WELL DONE"),
        listOf("CORRECT"),
        listOf("CORRECT (PHEW)")
    )
    
    init {
        loadGame()
    }
    
    fun keyPressed(key: String) {
        when (key) {
            "⌫" -> handleBackspace()
            "⏎" -> handleEnter()
            else -> handleLetter(key)
        }
        highlightCursor()
    }
    
    private fun handleBackspace() {
        if (game.cursorX >= 5) {
            game.cursorX -= 1
        }
        
        if (game.cursorX > 0 && _grid[game.cursorY].entries[game.cursorX].value.isEmpty()) {
            game.cursorX -= 1
        }
        
        val newGrid = _grid.toMutableList()
        newGrid[game.cursorY] = newGrid[game.cursorY].copy(
            entries = newGrid[game.cursorY].entries.toMutableList().apply {
                this[game.cursorX] = this[game.cursorX].copy(value = "")
            }
        )
        _grid = newGrid
    }
    
    private fun handleEnter() {
        if (game.cursorY < 6 && getWord(game.cursorY).length == 5) {
            analyzeWord()
        }
    }
    
    private fun handleLetter(key: String) {
        if (game.cursorX < 5) {
            val newGrid = _grid.toMutableList()
            newGrid[game.cursorY] = newGrid[game.cursorY].copy(
                entries = newGrid[game.cursorY].entries.toMutableList().apply {
                    this[game.cursorX] = this[game.cursorX].copy(value = key)
                }
            )
            _grid = newGrid
            game.cursorX += 1
        }
    }
    
    private fun getWord(row: Int): String {
        return _grid[row].entries.joinToString("") { it.value }
    }
    
    private fun analyzeWord() {
        val word = getWord(game.cursorY)
        
        if (!dictionary.isValidWord(word)) {
            showMessage("NOT IN WORD LIST")
            return
        }
        
        updateLetter(word, game.cursorY)
        game.cursorY += 1
        game.cursorX = 0
        
        if (word == game.answer) {
            handleWin()
        } else if (game.cursorY >= 6) {
            handleLoss()
        }
        
        saveGame()
    }
    
    private fun updateLetter(word: String, row: Int) {
        val newGrid = _grid.toMutableList()
        val newEntries = newGrid[row].entries.toMutableList()
        
        // Create a map to track remaining letters in the answer
        val answerLetterCounts = mutableMapOf<Char, Int>()
        for (char in game.answer) {
            answerLetterCounts[char] = answerLetterCounts.getOrDefault(char, 0) + 1
        }
        
        // First pass: mark exact matches (green) and update counts
        val statuses = Array(5) { LetterStatus.OUTCAST }
        for (i in 0..4) {
            val guessLetter = word[i]
            val answerLetter = game.answer[i]
            
            if (guessLetter == answerLetter) {
                statuses[i] = LetterStatus.POSITIONED
                answerLetterCounts[guessLetter] = answerLetterCounts[guessLetter]!! - 1
            }
        }
        
        // Second pass: mark misplaced letters (yellow) if there are still remaining instances
        for (i in 0..4) {
            val guessLetter = word[i]
            
            if (statuses[i] != LetterStatus.POSITIONED && answerLetterCounts.getOrDefault(guessLetter, 0) > 0) {
                statuses[i] = LetterStatus.ILLPOSITIONED
                answerLetterCounts[guessLetter] = answerLetterCounts[guessLetter]!! - 1
            }
        }
        
        // Apply the statuses to the grid
        for (i in 0..4) {
            val letter = word[i].toString()
            val status = statuses[i]
            
            newEntries[i] = newEntries[i].copy(
                foreground = when (status) {
                    LetterStatus.POSITIONED -> Color.White
                    LetterStatus.ILLPOSITIONED -> Color.White
                    LetterStatus.OUTCAST -> Color.White
                    else -> Color.Black
                },
                background = when (status) {
                    LetterStatus.POSITIONED -> Color(0xFF4CAF50) // Green
                    LetterStatus.ILLPOSITIONED -> Color(0xFFFF9800) // Orange
                    LetterStatus.OUTCAST -> Color(0xFF9E9E9E) // Dark gray
                    else -> Color.White
                }
            )
            
            updateKeyboard(letter, status)
        }
        
        newGrid[row] = newGrid[row].copy(entries = newEntries)
        _grid = newGrid
    }
    
    private fun updateKeyboard(value: String, status: LetterStatus) {
        val newKeyboard = _keyboard.toMutableList()
        
        for (rowIndex in newKeyboard.indices) {
            val newKeys = newKeyboard[rowIndex].keys.toMutableList()
            for (keyIndex in newKeys.indices) {
                if (newKeys[keyIndex].value == value) {
                    if (newKeys[keyIndex].status < status) {
                        newKeys[keyIndex] = newKeys[keyIndex].copy(
                            status = status,
                            foreground = when (status) {
                                LetterStatus.PENDING -> Color.Black
                                LetterStatus.POSITIONED -> Color.White
                                LetterStatus.ILLPOSITIONED -> Color.White
                                LetterStatus.OUTCAST -> Color.White
                            },
                            background = when (status) {
                                LetterStatus.PENDING -> Color(0xFFD3D3D3) // Light gray
                                LetterStatus.POSITIONED -> Color(0xFF4CAF50) // Green
                                LetterStatus.ILLPOSITIONED -> Color(0xFFFF9800) // Orange
                                LetterStatus.OUTCAST -> Color(0xFF9E9E9E) // Dark gray
                            }
                        )
                    }
                }
            }
            newKeyboard[rowIndex] = newKeyboard[rowIndex].copy(keys = newKeys)
        }
        
        _keyboard = newKeyboard
    }
    
    private fun highlightCursor() {
        if (game.cursorY > 5 || game.over) return
        
        val newGrid = _grid.toMutableList()
        val newEntries = newGrid[game.cursorY].entries.toMutableList()
        
        for (i in 0..4) {
            newEntries[i] = newEntries[i].copy(border = Color.Gray)
        }
        
        if (game.cursorX < 5) {
            newEntries[game.cursorX] = newEntries[game.cursorX].copy(border = Color.Blue)
        }
        
        newGrid[game.cursorY] = newGrid[game.cursorY].copy(entries = newEntries)
        _grid = newGrid
    }
    
    fun selectEntry(row: Int, col: Int) {
        game.cursorX = col
        highlightCursor()
    }
    
    fun copyRow(row: Int) {
        // Implementation for copying row logic
    }
    
    fun handleMessageClick() {
        _message = ""
        if (game.over) {
            resetGame()
        }
    }
    
    private fun handleWin() {
        val praise = praises[game.cursorY - 1].random()
        showMessage(praise)

        if (++_streak > _record){
            _record = _streak
        }
        game.over = true
    }
    
    private fun handleLoss() {
        showMessage(game.answer)
        game.over = true
    }
    
    private fun resetGame() {
        game.answer = dictionary.randomWord()
        game.over = false
        game.cursorX = 0
        game.cursorY = 0
        
        val newGrid = List(6) { GridRow() }.toMutableList()
        newGrid[0] = newGrid[0].copy(disabled = false)
        _grid = newGrid
        
        // Reset keyboard
        _keyboard = createInitialKeyboard()
    }
    
    private fun showMessage(text: String) {
        _message = text
    }
    
    private fun createInitialKeyboard(): List<KeyRow> {
        return listOf(
            KeyRow("QWERTYUIOP".map { Key(it.toString(), Color.Black, Color(0xFFD3D3D3)) }),
            KeyRow("ASDFGHJKL".map { Key(it.toString(), Color.Black, Color(0xFFD3D3D3)) }),
            KeyRow(
                listOf(Key("⏎", Color.White, Color(0xFF2196F3))) + 
                "ZXCVBNM".map { Key(it.toString(), Color.Black, Color(0xFFD3D3D3)) } + 
                listOf(Key("⌫", Color.White, Color(0xFFF44336)))
            )
        )
    }
    
    private fun loadGame() {
        // Load from SharedPreferences
        resetGame()
    }
    
    private fun saveGame() {
        // Save to SharedPreferences
    }
}

