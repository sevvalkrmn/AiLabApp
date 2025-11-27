package com.ktunailab.ailabapp.presentation.ui.screens.projects

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ktunailab.ailabapp.data.remote.dto.response.MyProjectsResponse
import com.ktunailab.ailabapp.presentation.ui.components.BottomNavigationBar
import com.ktunailab.ailabapp.ui.theme.BackgroundLight
import com.ktunailab.ailabapp.ui.theme.PrimaryBlue
import com.ktunailab.ailabapp.ui.theme.White
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProjectsScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToProjects: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToProjectDetail: (String) -> Unit = {},
    viewModel: ProjectsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 1,
                onHomeClick = onNavigateToHome,
                onProjectsClick = { /* Zaten buradayız */ },
                onChatClick = onNavigateToChat,
                onProfileClick = onNavigateToProfile
            )
        },
        containerColor = PrimaryBlue,
        contentWindowInsets = WindowInsets.systemBars
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // HEADER
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
                    .padding(screenWidth * 0.04f)
            ) {
                // Ortalanmış başlık
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = screenHeight * 0.01f)
                ) {
                    Text(
                        text = "Projelerim",
                        fontSize = (screenWidth.value * 0.06f).sp,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    // Sağ tarafta Yenile butonu
                    IconButton(
                        onClick = { viewModel.refreshProjects() },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Yenile",
                            tint = White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                // FILTER BUTTONS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.02f)
                ) {
                    FilterChip(
                        selected = uiState.selectedFilter == ProjectFilter.ALL,
                        onClick = { viewModel.filterProjects(ProjectFilter.ALL) },
                        label = { Text("All") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = uiState.selectedFilter == ProjectFilter.CAPTAIN,
                        onClick = { viewModel.filterProjects(ProjectFilter.CAPTAIN) },
                        label = { Text("Captain") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = uiState.selectedFilter == ProjectFilter.MEMBER,
                        onClick = { viewModel.filterProjects(ProjectFilter.MEMBER) },
                        label = { Text("Member") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // CONTENT
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        BackgroundLight,
                        RoundedCornerShape(topStart = screenWidth * 0.075f, topEnd = screenWidth * 0.075f)
                    )
            ) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryBlue)
                        }
                    }
                    uiState.errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(screenWidth * 0.15f)
                                )
                                Spacer(modifier = Modifier.height(screenHeight * 0.02f))
                                Text(
                                    text = uiState.errorMessage ?: "Hata oluştu",
                                    color = Color.Red,
                                    fontSize = (screenWidth.value * 0.04f).sp
                                )
                                Spacer(modifier = Modifier.height(screenHeight * 0.02f))
                                Button(onClick = { viewModel.refreshProjects() }) {
                                    Text("Tekrar Dene")
                                }
                            }
                        }
                    }
                    uiState.projects.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.FolderOpen,
                                    contentDescription = null,
                                    tint = PrimaryBlue.copy(alpha = 0.5f),
                                    modifier = Modifier.size(screenWidth * 0.2f)
                                )
                                Spacer(modifier = Modifier.height(screenHeight * 0.02f))
                                Text(
                                    text = "Henüz proje yok",
                                    color = PrimaryBlue,
                                    fontSize = (screenWidth.value * 0.045f).sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(screenWidth * 0.04f),
                            verticalArrangement = Arrangement.spacedBy(screenHeight * 0.015f)
                        ) {
                            items(uiState.projects) { project ->
                                ProjectCard(
                                    project = project,
                                    onClick = { onNavigateToProjectDetail(project.id) },
                                    screenWidth = screenWidth,
                                    screenHeight = screenHeight
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectCard(
    project: MyProjectsResponse,
    onClick: () -> Unit,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(screenWidth * 0.03f),
        elevation = CardDefaults.cardElevation(screenWidth * 0.005f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenWidth * 0.04f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.name.ifEmpty { "İsimsiz Proje" },
                    fontSize = (screenWidth.value * 0.045f).sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )

                // Role Badge - Backend'den gelen userRole kullanılıyor
                Surface(
                    color = if (project.userRole == "Captain") {
                        PrimaryBlue  // Captain için koyu mavi
                    } else {
                        PrimaryBlue.copy(alpha = 0.3f)  // Member için açık mavi
                    },
                    shape = RoundedCornerShape(screenWidth * 0.02f)
                ) {
                    Text(
                        text = project.userRole,  // ← Backend'den gelen role (Captain/Member)
                        color = if (project.userRole == "Captain") {
                            White  // Captain için beyaz yazı
                        } else {
                            PrimaryBlue  // Member için mavi yazı
                        },
                        fontSize = (screenWidth.value * 0.03f).sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(
                            horizontal = screenWidth * 0.025f,
                            vertical = screenHeight * 0.005f
                        )
                    )
                }
            }

            // Description
            if (!project.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                Text(
                    text = project.description,
                    fontSize = (screenWidth.value * 0.035f).sp,
                    color = PrimaryBlue.copy(alpha = 0.7f),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.01f))

            // Date
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = PrimaryBlue.copy(alpha = 0.5f),
                    modifier = Modifier.size(screenWidth * 0.04f)
                )
                Spacer(modifier = Modifier.width(screenWidth * 0.01f))
                Text(
                    text = formatDate(project.createdAt),
                    fontSize = (screenWidth.value * 0.03f).sp,
                    color = PrimaryBlue.copy(alpha = 0.5f)
                )
            }
        }
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}