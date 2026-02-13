package com.ktun.ailabapp.presentation.ui.screens.projects

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
import com.ktun.ailabapp.data.remote.dto.response.MyProjectsResponse
import com.ktun.ailabapp.presentation.ui.components.BottomNavigationBar
import com.ktun.ailabapp.presentation.ui.components.DebugButton
import com.ktun.ailabapp.presentation.ui.components.FeedbackDialog
import com.ktun.ailabapp.presentation.ui.components.ShimmerBox
import com.ktun.ailabapp.presentation.ui.components.StaggeredAnimatedItem
import com.ktun.ailabapp.presentation.ui.screens.announcement.AnnouncementViewModel
import com.ktun.ailabapp.ui.theme.BackgroundLight
import com.ktun.ailabapp.ui.theme.PrimaryBlue
import com.ktun.ailabapp.ui.theme.White
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
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

    var showFeedbackDialog by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isRefreshing = false
        }
    }

    val unreadCount = remember(announcementUiState.announcements) {
        announcementUiState.announcements.count { !it.isRead }
    }

    if (showFeedbackDialog) {
        FeedbackDialog(
            pageInfo = "projects-screen",
            onDismiss = { showFeedbackDialog = false },
            onSubmit = { feedback ->
                android.widget.Toast.makeText(context, "Geri bildiriminiz alındı!", android.widget.Toast.LENGTH_SHORT).show()
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
        containerColor = BackgroundLight, // ✅ Açık zemin
        contentWindowInsets = WindowInsets.systemBars
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding()) // ✅ Sadece alt boşluk
        ) {
            // HEADER (Kıvrımlı ve Tam Ekran)
            Surface(
                color = PrimaryBlue,
                shape = RoundedCornerShape(bottomStart = screenWidth * 0.1f, bottomEnd = screenWidth * 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars) // ✅ Beyaz çizgiyi yok eder
                        .padding(screenWidth * 0.04f)
                        .padding(top = screenHeight * 0.01f, bottom = screenHeight * 0.02f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ortalama için sol tarafa görünmez bir Spacer koyabiliriz veya Text'i ortalayabiliriz.
                    // Mevcut tasarımda sol tarafta boşluk var, sağda buton var.
                    // HomeScreen'de "Hi, User" sola yaslıydı. Burada "Projelerim" ortada olsun istenebilir ama
                    // tutarlılık için sola yaslı veya mevcut yapıyı (başlık ortada) koruyarak yapalım.
                    
                    // Mevcut yapı: Spacer - Text - Button
                    Spacer(modifier = Modifier.width(screenWidth * 0.1f)) // Sol dengeleyici

                    Text(
                        text = "Projelerim",
                        fontSize = (screenWidth.value * 0.06f).sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )

                    DebugButton(
                        onClick = { showFeedbackDialog = true }
                    )
                }
            }

            // CONTENT
            androidx.compose.material3.pulltorefresh.PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    viewModel.refreshProjects()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    uiState.isLoading && !isRefreshing -> {
                        ProjectsScreenSkeleton(screenWidth, screenHeight)
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
                            items(uiState.projects.size) { index ->
                                StaggeredAnimatedItem(index = index) {
                                    ProjectCard(
                                        project = uiState.projects[index],
                                        onClick = { onNavigateToProjectDetail(uiState.projects[index].id) },
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
}

@Composable
fun ProjectsScreenSkeleton(screenWidth: androidx.compose.ui.unit.Dp, screenHeight: androidx.compose.ui.unit.Dp) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(screenWidth * 0.04f),
        verticalArrangement = Arrangement.spacedBy(screenHeight * 0.015f)
    ) {
        repeat(6) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.1f),
                shape = RoundedCornerShape(screenWidth * 0.03f)
            )
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
            Box(
                modifier = Modifier
                    .size(screenWidth * 0.12f)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.ktun.ailabapp.R.drawable.ailablogo),
                    contentDescription = "AiLab Logo",
                    modifier = Modifier.size(screenWidth * 0.08f)
                )
            }

            Spacer(modifier = Modifier.width(screenWidth * 0.03f))

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

            Surface(
                color = if (project.userRole == "Captain")
                    PrimaryBlue else PrimaryBlue.copy(alpha = 0.2f),
                shape = RoundedCornerShape(screenWidth * 0.02f)
            ) {
                Text(
                    text = project.userRole ?: "Member",
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
