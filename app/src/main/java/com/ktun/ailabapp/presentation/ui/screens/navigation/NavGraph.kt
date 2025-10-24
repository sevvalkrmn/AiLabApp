package com.ktun.ailabapp.presentation.ui.screens.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ktun.ailabapp.presentation.ui.navigation.Screen
import com.ktun.ailabapp.presentation.ui.screens.announcement.AnnouncementScreen
import com.ktun.ailabapp.presentation.ui.screens.home.HomeScreen
import com.ktun.ailabapp.presentation.ui.screens.login.LoginScreen
import com.ktun.ailabapp.presentation.ui.screens.profile.ProfileScreen
import com.ktun.ailabapp.presentation.ui.screens.projects.ProjectDetailScreen
import com.ktun.ailabapp.presentation.ui.screens.projects.ProjectsScreen
import com.ktun.ailabapp.screens.RegisterScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login Screen
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Register Screen
        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
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
                }
            )
        }

        // Projects Screen
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
                }
            )
        }

        // ProjectDetail Screen
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

        // Announcements Screen
        composable(Screen.Announcements.route) {
            AnnouncementScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route)
                },
                onNavigateToProjects = {
                    navController.navigate(Screen.Projects.route)
                },
                onNavigateToChat = { /* Zaten announcements'tayız */ },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        // Profile Screen
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
                onNavigateToProfile = { /* Zaten profile'dayız */ },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}