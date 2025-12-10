package com.ktun.ailabapp.presentation.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Projects : Screen("projects")
    object ProjectDetail : Screen("project_detail/{projectId}") {
        fun createRoute(projectId: String) = "project_detail/$projectId"
    }
    object Announcements : Screen("announcements")
    object Profile : Screen("profile")
}