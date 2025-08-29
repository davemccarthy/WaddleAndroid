package com.yourname.waddleui

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class Dictionary(private val context: Context) {
    private val contenders = mutableSetOf<String>() // Words that can be answers
    private val valids = mutableSetOf<String>()     // All valid words (including contenders)

    init {
        loadDictionary()
    }
    
    private fun loadDictionary() {
        try {
            // Load contenders (answer words)
            val contendersStream = context.assets.open("contenders.txt")
            val contendersReader = BufferedReader(InputStreamReader(contendersStream))
            contendersReader.useLines { lines ->
                lines.forEach { word ->
                    if (word.length == 5) {
                        contenders.add(word.lowercase())
                        // Contenders are also valid words
                        valids.add(word.lowercase())
                    }
                }
            }
            
            // Load additional valid words
            val validsStream = context.assets.open("valids.txt")
            val validsReader = BufferedReader(InputStreamReader(validsStream))
            validsReader.useLines { lines ->
                lines.forEach { word ->
                    if (word.length == 5) {
                        valids.add(word.lowercase())
                    }
                }
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun isValidWord(word: String): Boolean {
        return valids.contains(word.lowercase())
    }
    
    fun randomWord(): String {
        return contenders.random().uppercase()
    }
}
