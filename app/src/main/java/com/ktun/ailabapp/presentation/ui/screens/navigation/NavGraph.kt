package com.ktun.ailabapp.presentation.ui.screens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ktun.ailabapp.presentation.ui.screens.admin.AdminPanelScreen
import com.ktun.ailabapp.presentation.ui.screens.admin.createproject.AllProjectsScreen
import com.ktun.ailabapp.presentation.ui.screens.admin.createproject.CreateProjectScreen
import com.ktun.ailabapp.presentation.ui.screens.login.LoginScreen
import com.ktun.ailabapp.presentation.ui.screens.announcement.AnnouncementScreen
import com.ktun.ailabapp.presentation.ui.screens.announcement.AnnouncementViewModel
import com.ktun.ailabapp.presentation.ui.screens.home.HomeScreen
import com.ktun.ailabapp.presentation.ui.screens.home.HomeViewModel
import com.ktun.ailabapp.presentation.ui.screens.profile.ProfileScreen
import com.ktun.ailabapp.presentation.ui.screens.profile.ProfileViewModel
import com.ktun.ailabapp.presentation.ui.screens.projects.ProjectDetailScreen
import com.ktun.ailabapp.presentation.ui.screens.projects.ProjectsScreen
import com.ktun.ailabapp.presentation.ui.screens.projects.ProjectsViewModel
import com.ktun.ailabapp.presentation.ui.screens.register.RegisterScreen
import com.ktun.ailabapp.presentation.ui.screens.admin.users.UsersListScreen
import com.ktun.ailabapp.presentation.ui.screens.admin.users.personalAnnouncement.SendAnnouncementScreen
import com.ktun.ailabapp.presentation.ui.screens.admin.users.roles.ManageRolesScreen
import com.ktun.ailabapp.presentation.ui.screens.admin.users.tasks.TaskHistoryScreen
import com.ktun.ailabapp.presentation.ui.screens.admin.lab.LabPeopleScreen
import com.ktun.ailabapp.presentation.ui.screens.admin.pendingtasks.PendingTasksScreen
import com.ktun.ailabapp.presentation.ui.screens.admin.announcement.SendGlobalAnnouncementScreen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    val sharedAnnouncementViewModel: AnnouncementViewModel = hiltViewModel()
    val sharedHomeViewModel: HomeViewModel = hiltViewModel()
    val sharedProjectsViewModel: ProjectsViewModel = hiltViewModel()
    val sharedProfileViewModel: ProfileViewModel = hiltViewModel()

    // Slide + fade animasyonları (ileri navigasyon)
    val slideEnter = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) }
    val slideExit = { slideOutHorizontally(tween(300)) { -it / 3 } + fadeOut(tween(150)) }
    // Slide + fade animasyonları (geri navigasyon)
    val slidePopEnter = { slideInHorizontally(tween(300)) { -it / 3 } + fadeIn(tween(300)) }
    val slidePopExit = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300)) }
    // Fade animasyonları (sadece auth ekranları için)
    val fadeEnter = { fadeIn(tween(200)) }
    val fadeExit = { fadeOut(tween(200)) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideEnter() },
        exitTransition = { slideExit() },
        popEnterTransition = { slidePopEnter() },
        popExitTransition = { slidePopExit() }
    ) {
        // --- Auth Ekranları (fade) ---
        composable(
            route = Screen.Login.route,
            enterTransition = { fadeEnter() },
            exitTransition = { fadeExit() },
            popEnterTransition = { fadeEnter() },
            popExitTransition = { fadeExit() }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                    sharedHomeViewModel.refreshUserData()
                    sharedProjectsViewModel.refreshProjects()
                    sharedProfileViewModel.refreshProfile()
                    sharedAnnouncementViewModel.loadAnnouncements()
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(
            route = Screen.Register.route,
            enterTransition = { fadeEnter() },
            exitTransition = { fadeExit() },
            popEnterTransition = { fadeEnter() },
            popExitTransition = { fadeExit() }
        ) {
            RegisterScreen(
                navController = navController,
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                    sharedHomeViewModel.refreshUserData()
                    sharedProjectsViewModel.refreshProjects()
                    sharedProfileViewModel.refreshProfile()
                    sharedAnnouncementViewModel.loadAnnouncements()
                }
            )
        }

        // --- Tab Ekranları (anında geçiş — header stabil kalır, içerik StaggeredAnimatedItem ile gelir) ---
        composable(
            route = Screen.Home.routeWithArgs,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            HomeScreen(
                onNavigateToProjects = { navController.navigate(Screen.Projects.route) },
                onNavigateToChat = { navController.navigate(Screen.Announcements.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                viewModel = sharedHomeViewModel,
                announcementViewModel = sharedAnnouncementViewModel,
                notificationTaskId = taskId
            )
        }

        composable(
            route = Screen.Projects.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            ProjectsScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
                onNavigateToChat = { navController.navigate(Screen.Announcements.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToProjectDetail = { projectId -> navController.navigate(Screen.ProjectDetail.createRoute(projectId)) },
                viewModel = sharedProjectsViewModel,
                announcementViewModel = sharedAnnouncementViewModel
            )
        }

        composable(
            route = Screen.ProjectDetail.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            ProjectDetailScreen(projectId = projectId, onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.Announcements.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            AnnouncementScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToProjects = { navController.navigate(Screen.Projects.route) },
                onNavigateToChat = { },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                announcementViewModel = sharedAnnouncementViewModel,
                viewModel = sharedAnnouncementViewModel
            )
        }

        composable(
            route = Screen.Profile.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            ProfileScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToProjects = { navController.navigate(Screen.Projects.route) },
                onNavigateToChat = { navController.navigate(Screen.Announcements.route) },
                onNavigateToProfile = { },
                onNavigateToAdminPanel = { navController.navigate(Screen.AdminPanel.route) },
                onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } },
                viewModel = sharedProfileViewModel,
                announcementViewModel = sharedAnnouncementViewModel
            )
        }

        composable(
            route = Screen.AdminPanel.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            AdminPanelScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUsersList = { navController.navigate(Screen.UsersList.route) },
                onNavigateToCreateProject = { navController.navigate(Screen.CreateProject.route) },
                onNavigateToAllProjects = { navController.navigate(Screen.AllProjects.route) },
                onNavigateToLabPeople = { navController.navigate(Screen.LabPeople.route) },
                onNavigateToPendingTasks = { navController.navigate(Screen.PendingTasks.route) },
                onNavigateToSendAnnouncement = { navController.navigate(Screen.SendGlobalAnnouncement.route) }
            )
        }

        composable(
            route = Screen.AllProjects.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            AllProjectsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProjectDetail = { projectId -> navController.navigate(Screen.ProjectDetail.createRoute(projectId)) }
            )
        }

        composable(
            route = Screen.UsersList.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            UsersListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSendAnnouncement = { userId, userName -> navController.navigate(Screen.SendAnnouncement.createRoute(userId, userName)) },
                onNavigateToManageRoles = { userId -> navController.navigate(Screen.ManageRoles.createRoute(userId)) },
                onNavigateToTaskHistory = { userId, userName -> navController.navigate(Screen.TaskHistory.createRoute(userId, userName)) },
                onNavigateToProjectDetail = { projectId -> navController.navigate(Screen.ProjectDetail.createRoute(projectId)) }
            )
        }

        composable(
            route = Screen.SendAnnouncement.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType }, navArgument("userName") { type = NavType.StringType }),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val userName = backStackEntry.arguments?.getString("userName") ?: ""
            SendAnnouncementScreen(userId = userId, userName = userName, onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.ManageRoles.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType }),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ManageRolesScreen(userId = userId, onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.TaskHistory.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType }, navArgument("userName") { type = NavType.StringType }),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val userName = backStackEntry.arguments?.getString("userName") ?: ""
            TaskHistoryScreen(userId = userId, userName = userName, onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.CreateProject.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            CreateProjectScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.LabPeople.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            LabPeopleScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.PendingTasks.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            PendingTasksScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.SendGlobalAnnouncement.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            SendGlobalAnnouncementScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
