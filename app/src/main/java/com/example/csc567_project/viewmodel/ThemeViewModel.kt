package com.example.csc567_project.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {
    var isDarkTheme = mutableStateOf(false)
        private set

    fun toggleTheme(enabled: Boolean) {
        isDarkTheme.value = enabled
    }
}