package com.ktun.ailabapp.presentation.ui.screens.projects

import android.app.Application
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
import coil.compose.AsyncImage
import com.ktun.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktun.ailabapp.data.remote.dto.response.ProjectMember
import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import com.ktun.ailabapp.data.remote.dto.response.TaskStatistics
import com.ktun.ailabapp.presentation.ui.screens.announcement.AnnouncementViewModel
import com.ktun.ailabapp.ui.theme.BackgroundLight
import com.ktun.ailabapp.ui.theme.PrimaryBlue
import com.ktun.ailabapp.ui.theme.White
import com.ktun.ailabapp.presentation.ui.util.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: String,
    onNavigateBack: () -> Unit = {},
    viewModel: ProjectDetailViewModel = hiltViewModel()

) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    LaunchedEffect(projectId) {
        viewModel.loadProjectDetail(projectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.project?.name ?: "Proje Detayı") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshProject() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Yenile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue,
                    titleContentColor = White,
                    navigationIconContentColor = White,
                    actionIconContentColor = White
                )
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(screenHeight * 0.02f))
                        Button(onClick = { viewModel.refreshProject() }) {
                            Text("Tekrar Dene")
                        }
                    }
                }
            }
            uiState.project != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(screenWidth * 0.04f),
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.02f)
                ) {
                    // Proje Bilgileri
                    item {
                        ProjectInfoCard(
                            project = uiState.project!!,
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }

                    // Görev İstatistikleri
                    item {
                        TaskStatisticsCard(
                            statistics = uiState.project!!.taskStatistics,
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }

                    // ✅ GÖREVLER ÜSTTE
                    // Görevler Başlık
                    item {
                        Text(
                            text = "Görevler",
                            fontSize = (screenWidth.value * 0.045f).sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }

                    // Görevler Listesi
                    if (uiState.tasks.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = White)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(screenWidth * 0.08f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = PrimaryBlue.copy(alpha = 0.3f),
                                            modifier = Modifier.size(screenWidth * 0.15f)
                                        )
                                        Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                                        Text(
                                            text = "Henüz görev yok",
                                            color = PrimaryBlue.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        items(uiState.tasks) { task ->
                            TaskCard(
                                task = task,
                                onStatusChange = { newStatus ->
                                    viewModel.updateTaskStatus(task.id, newStatus)
                                },
                                screenWidth = screenWidth,
                                screenHeight = screenHeight
                            )
                        }
                    }

                    // ✅ PROJE ÜYELERİ ALTTA
                    // Proje Üyeleri Başlık
                    item {
                        Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                        Text(
                            text = "Proje Üyeleri",
                            fontSize = (screenWidth.value * 0.045f).sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }

                    // ÖNCE KAPTANLAR
                    items(uiState.project!!.captains) { captain ->
                        MemberCard(
                            member = captain,
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }

                    // SONRA ÜYELER
                    items(uiState.project!!.members) { member ->
                        MemberCard(
                            member = member,
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }
                }

            }
        }
    }
}


// ============= COMPOSABLE KARTLAR =============

@Composable
fun ProjectInfoCard(
    project: ProjectDetailResponse,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(screenWidth * 0.03f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenWidth * 0.04f)
        ) {
            Text(
                text = project.name,
                fontSize = (screenWidth.value * 0.05f).sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )

            if (!project.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                Text(
                    text = project.description,
                    fontSize = (screenWidth.value * 0.035f).sp,
                    color = PrimaryBlue.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.015f))

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
                    text = "Oluşturulma: ${formatDate(project.createdAt)}",
                    fontSize = (screenWidth.value * 0.03f).sp,
                    color = PrimaryBlue.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun TaskStatisticsCard(
    statistics: TaskStatistics,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(screenWidth * 0.03f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenWidth * 0.04f)
        ) {
            Text(
                text = "Görev İstatistikleri",
                fontSize = (screenWidth.value * 0.04f).sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.02f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Toplam", statistics.total.toString(), Color.Gray, screenWidth)
                StatItem("Yapılacak", statistics.todo.toString(), Color(0xFFFF9800), screenWidth)
                StatItem("Devam Eden", statistics.inProgress.toString(), Color(0xFF2196F3), screenWidth)
                StatItem("Tamamlanan", statistics.done.toString(), Color(0xFF4CAF50), screenWidth)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color, screenWidth: androidx.compose.ui.unit.Dp) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = (screenWidth.value * 0.06f).sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = (screenWidth.value * 0.025f).sp,
            color = PrimaryBlue.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun MemberCard(
    member: ProjectMember,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(screenWidth * 0.03f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenWidth * 0.03f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            if (member.avatarUrl != null) {
                AsyncImage(
                    model = member.avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(screenWidth * 0.12f)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(screenWidth * 0.12f)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(screenWidth * 0.06f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(screenWidth * 0.03f))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.fullName,
                    fontSize = (screenWidth.value * 0.04f).sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBlue
                )
                Text(
                    text = member.email,
                    fontSize = (screenWidth.value * 0.03f).sp,
                    color = PrimaryBlue.copy(alpha = 0.6f)
                )
            }

            Surface(
                color = if (member.role == "Captain") PrimaryBlue else PrimaryBlue.copy(alpha = 0.3f),
                shape = RoundedCornerShape(screenWidth * 0.02f)
            ) {
                Text(
                    text = member.role,
                    color = if (member.role == "Captain") White else PrimaryBlue,
                    fontSize = (screenWidth.value * 0.03f).sp,
                    modifier = Modifier.padding(
                        horizontal = screenWidth * 0.02f,
                        vertical = screenHeight * 0.005f
                    )
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    task: TaskResponse,
    onStatusChange: (String) -> Unit,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(screenWidth * 0.03f)
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
                    text = task.title,
                    fontSize = (screenWidth.value * 0.04f).sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )

                // Status Dropdown
                Box {
                    Surface(
                        color = getStatusColor(task.status),
                        shape = RoundedCornerShape(screenWidth * 0.02f),
                        modifier = Modifier.clickable { expanded = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = screenWidth * 0.02f,
                                vertical = screenHeight * 0.005f
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = getStatusText(task.status),
                                color = White,
                                fontSize = (screenWidth.value * 0.03f).sp
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(screenWidth * 0.04f)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Yapılacak") },
                            onClick = {
                                onStatusChange("Todo")
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Devam Ediyor") },
                            onClick = {
                                onStatusChange("InProgress")
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Tamamlandı") },
                            onClick = {
                                onStatusChange("Done")
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (!task.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                Text(
                    text = task.description,
                    fontSize = (screenWidth.value * 0.035f).sp,
                    color = PrimaryBlue.copy(alpha = 0.7f)
                )
            }

            if (task.assignedTo != null) {
                Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = PrimaryBlue.copy(alpha = 0.5f),
                        modifier = Modifier.size(screenWidth * 0.04f)
                    )
                    Spacer(modifier = Modifier.width(screenWidth * 0.01f))
                    Text(
                        text = task.assignedTo.fullName,
                        fontSize = (screenWidth.value * 0.03f).sp,
                        color = PrimaryBlue.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

// ============= HELPER FUNCTIONS =============

fun getStatusColor(status: String): Color {
    return when (status) {
        "Todo" -> Color(0xFFFF9800)
        "InProgress" -> Color(0xFF2196F3)
        "Done" -> Color(0xFF4CAF50)
        else -> Color.Gray
    }
}

fun getStatusText(status: String): String {
    return when (status) {
        "Todo" -> "Yapılacak"
        "InProgress" -> "Devam Ediyor"
        "Done" -> "Tamamlandı"
        else -> status
    }
}