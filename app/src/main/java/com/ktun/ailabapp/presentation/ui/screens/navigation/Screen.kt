// presentation/ui/navigation/Screen.kt

package com.ktun.ailabapp.presentation.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Projects : Screen("projects")
    object ProjectDetail : Screen("project_detail/{projectId}") {
        fun createRoute(projectId: String) = "project_detail/$projectId"
    }

    object ManageRoles : Screen("manage_roles/{userId}") {
        fun createRoute(userId: String) = "manage_roles/$userId"
    }
    object Announcements : Screen("announcements")
    object Profile : Screen("profile")
    object AdminPanel : Screen("admin_panel")
    object UsersList : Screen("users_list")

    object CreateProject : Screen("create_project")
    object AllProjects : Screen("all_projects")

    object TaskHistory : Screen("task_history/{userId}/{userName}") {
        fun createRoute(userId: String, userName: String) = "task_history/$userId/$userName"
    }

    // ✅ YENİ ROUTE
    object SendAnnouncement : Screen("send_announcement/{userId}/{userName}") {
        fun createRoute(userId: String, userName: String) = "send_announcement/$userId/$userName"
    }

    object LabPeople : Screen("lab_people")
    object PendingTasks : Screen("pending_tasks")
}