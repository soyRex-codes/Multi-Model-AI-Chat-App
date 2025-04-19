package com.example.csc567_project.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import com.example.csc567_project.ui.theme.NavyBlue

class TopBarViewModel: ViewModel() {
    private val _topBarTitle = mutableStateOf("AI Chat Wrapper")
    val topBarTitleState: State<String> get() = _topBarTitle

    private val _topBarTitleColor = mutableStateOf(Color.White)
    val topBarTitleColorState: State<Color> get() = _topBarTitleColor

    private val _topBarNavColor = mutableStateOf(Color.White)
    val topBarNavColorState: State<Color> get() = _topBarNavColor

    private val _topBarColor = mutableStateOf(NavyBlue)
    val topBarColorState: State<Color> get() = _topBarColor

    fun useDefaultColors() {
        _topBarTitleColor.value = Color.White
        _topBarNavColor.value = Color.White
        _topBarColor.value = NavyBlue
    }

    fun setNavColor(color: Color) {
        _topBarNavColor.value = color
    }

    fun setTitleColor(color: Color) {
        _topBarTitleColor.value = color
    }

    fun setColor(color: Color) {
        _topBarColor.value = color
    }

    fun setTitle(newTitle: String) {
        _topBarTitle.value = newTitle
    }
}