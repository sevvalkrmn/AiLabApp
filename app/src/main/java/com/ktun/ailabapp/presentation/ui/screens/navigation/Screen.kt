package com.ktun.ailabapp.presentation.ui.screens.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Projects : Screen("projects")
    object Chat : Screen("chat")
    object Profile : Screen("profile")
}