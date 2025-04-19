package com.example.csc567_project.model

sealed class Screens (val screen: String){
    data object Home: Screens("home")
    data object Chat: Screens("chat")
    data object Settings: Screens("settings")
}