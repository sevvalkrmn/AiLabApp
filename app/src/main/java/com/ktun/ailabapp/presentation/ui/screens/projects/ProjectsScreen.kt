package com.ktunailab.ailabapp.presentation.ui.screens.projects

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.ktunailab.ailabapp.presentation.ui.components.DebugButton
import com.ktunailab.ailabapp.presentation.ui.components.FeedbackDialog
import com.ktunailab.ailabapp.presentation.ui.screens.announcement.AnnouncementViewModel
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
    viewModel: ProjectsViewModel = hiltViewModel(),
    announcementViewModel: AnnouncementViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val announcementUiState by announcementViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    var showFeedbackDialog by remember { mutableStateOf(false) }  // ✅ YENİ

    val unreadCount = remember(announcementUiState.announcements) {
        announcementUiState.announcements.count { !it.isRead }
    }

    // ✅ YENİ: Feedback Dialog
    if (showFeedbackDialog) {
        FeedbackDialog(
            onDismiss = { showFeedbackDialog = false },
            onSubmit = { feedback ->
                android.util.Log.d("ProjectsScreen", "📝 Feedback: $feedback")
                Toast.makeText(context, "Geri bildiriminiz alındı!", Toast.LENGTH_SHORT).show()
                showFeedbackDialog = false
            }
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 1,
                onHomeClick = onNavigateToHome,
                onProjectsClick = {  },
                onChatClick = onNavigateToChat,
                onProfileClick = onNavigateToProfile,
                unreadAnnouncementCount = unreadCount
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = screenHeight * 0.02f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(screenWidth * 0.1f))  // Sol boşluk (dengeleme için)

                    Text(
                        text = "Projelerim",
                        fontSize = (screenWidth.value * 0.06f).sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )

                    // ✅ GÜNCELLEME: Debug butonu
                    DebugButton(
                        onClick = { showFeedbackDialog = true }
                    )
                }
            }

            // CONTENT - Filtreler kaldırıldı
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
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenWidth * 0.04f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol: Proje İkonu
            Box(
                modifier = Modifier
                    .size(screenWidth * 0.12f)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Folder,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(screenWidth * 0.06f)
                )
            }

            Spacer(modifier = Modifier.width(screenWidth * 0.03f))

            // Orta: Proje Bilgileri
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name.ifEmpty { "İsimsiz Proje" },
                    fontSize = (screenWidth.value * 0.04f).sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    maxLines = 1
                )

                if (!project.description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(screenHeight * 0.003f))
                    Text(
                        text = project.description,
                        fontSize = (screenWidth.value * 0.032f).sp,
                        color = PrimaryBlue.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.005f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = PrimaryBlue.copy(alpha = 0.4f),
                        modifier = Modifier.size(screenWidth * 0.035f)
                    )
                    Spacer(modifier = Modifier.width(screenWidth * 0.01f))
                    Text(
                        text = formatDate(project.createdAt),
                        fontSize = (screenWidth.value * 0.028f).sp,
                        color = PrimaryBlue.copy(alpha = 0.5f)
                    )
                }
            }

            // Sağ: Rol Badge
            Surface(
                color = if (project.userRole == "Captain")
                    PrimaryBlue else PrimaryBlue.copy(alpha = 0.2f),
                shape = RoundedCornerShape(screenWidth * 0.02f)
            ) {
                Text(
                    text = project.userRole,
                    color = if (project.userRole == "Captain") White else PrimaryBlue,
                    fontSize = (screenWidth.value * 0.03f).sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(
                        horizontal = screenWidth * 0.025f,
                        vertical = screenHeight * 0.006f
                    )
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