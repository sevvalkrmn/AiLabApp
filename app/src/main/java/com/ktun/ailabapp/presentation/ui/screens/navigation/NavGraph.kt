

package com.ktun.ailabapp.presentation.ui.screens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.presentation.ui.navigation.Screen
import com.ktun.ailabapp.presentation.ui.screens.admin.AdminPanelScreen // ✅ IMPORT EKLE
import com.ktun.ailabapp.presentation.ui.screens.announcement.AnnouncementScreen
import com.ktun.ailabapp.presentation.ui.screens.announcement.AnnouncementViewModel
import com.ktun.ailabapp.presentation.ui.screens.home.HomeScreen
import com.ktun.ailabapp.presentation.ui.screens.login.LoginScreen
import com.ktun.ailabapp.presentation.ui.screens.profile.ProfileScreen
import com.ktun.ailabapp.presentation.ui.screens.projects.ProjectDetailScreen
import com.ktun.ailabapp.presentation.ui.screens.projects.ProjectsScreen
import com.ktun.ailabapp.screens.RegisterScreen
import com.ktun.ailabapp.presentation.ui.screens.admin.users.UsersListScreen
import com.ktun.ailabapp.presentation.ui.screens.admin.users.personalAnnouncement.SendAnnouncementScreen
import com.ktun.ailabapp.presentation.ui.screens.admin.users.roles.ManageRolesScreen
import com.ktun.ailabapp.presentation.ui.screens.admin.users.tasks.TaskHistoryScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {

    val sharedAnnouncementViewModel: AnnouncementViewModel = hiltViewModel()

    LaunchedEffect(sharedAnnouncementViewModel) {
        android.util.Log.d("NavGraph", "SharedViewModel created: ${sharedAnnouncementViewModel.hashCode()}")
        android.util.Log.d("NavGraph", "📥 Loading announcements...")
        sharedAnnouncementViewModel.loadAnnouncements()
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    android.util.Log.d("NavGraph", "📥 Login successful - Reloading announcements...")
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                onRegisterSuccess = {
                    android.util.Log.d("NavGraph", "📥 Register successful - Reloading announcements...")
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToProjects = {
                    navController.navigate(Screen.Projects.route)
                },
                onNavigateToChat = {
                    navController.navigate(Screen.Announcements.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                announcementViewModel = sharedAnnouncementViewModel
            )
        }

        composable(route = Screen.Projects.route) {
            ProjectsScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToChat = {
                    navController.navigate(Screen.Announcements.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToProjectDetail = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                },
                announcementViewModel = sharedAnnouncementViewModel
            )
        }

        composable(
            route = Screen.ProjectDetail.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""

            ProjectDetailScreen(
                projectId = projectId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Announcements.route) {
            AnnouncementScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route)
                },
                onNavigateToProjects = {
                    navController.navigate(Screen.Projects.route)
                },
                onNavigateToChat = { },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                announcementViewModel = sharedAnnouncementViewModel,
                viewModel = sharedAnnouncementViewModel
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route)
                },
                onNavigateToProjects = {
                    navController.navigate(Screen.Projects.route)
                },
                onNavigateToChat = {
                    navController.navigate(Screen.Announcements.route)
                },
                onNavigateToProfile = { },
                onNavigateToAdminPanel = { // ✅ PARAMETRE EKLE
                    android.util.Log.d("NavGraph", "🔵 Navigating to Admin Panel")
                    navController.navigate(Screen.AdminPanel.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                announcementViewModel = sharedAnnouncementViewModel
            )
        }

        // ✅ YENİ ROUTE - ADMIN PANEL
        composable(Screen.AdminPanel.route) {
            android.util.Log.d("NavGraph", "🟢 AdminPanelScreen displayed")
            AdminPanelScreen(
                onNavigateBack = {
                    android.util.Log.d("NavGraph", "🔙 Back from Admin Panel")
                    navController.popBackStack()
                },
                onNavigateToUsersList = { // ✅ YENİ
                    android.util.Log.d("NavGraph", "🔵 Navigating to Users List")
                    navController.navigate(Screen.UsersList.route)
                }
            )
        }

        composable(Screen.UsersList.route) {
            UsersListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSendAnnouncement = { userId, userName ->
                    navController.navigate(Screen.SendAnnouncement.createRoute(userId, userName))
                },
                onNavigateToManageRoles = { userId ->
                    navController.navigate(Screen.ManageRoles.createRoute(userId))
                },
                onNavigateToTaskHistory = { userId, userName ->
                    navController.navigate(Screen.TaskHistory.createRoute(userId, userName))
                }
            )
        }

// ✅ YENİ ROUTE EKLE (en alta)
        composable(
            route = Screen.SendAnnouncement.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("userName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val userName = backStackEntry.arguments?.getString("userName") ?: ""

            android.util.Log.d("NavGraph", "🟢 SendAnnouncementScreen displayed for user: $userName")

            SendAnnouncementScreen(
                userId = userId,
                userName = userName,
                onNavigateBack = {
                    android.util.Log.d("NavGraph", "🔙 Back from Send Announcement")
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ManageRoles.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""

            ManageRolesScreen(
                userId = userId, // ✅ userId gönder
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.TaskHistory.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("userName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val userName = backStackEntry.arguments?.getString("userName") ?: ""

            TaskHistoryScreen(
                userId = userId,
                userName = userName,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}