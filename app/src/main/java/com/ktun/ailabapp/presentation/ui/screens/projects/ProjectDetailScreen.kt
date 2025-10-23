package com.ktun.ailabapp.presentation.ui.screens.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ktun.ailabapp.data.model.Task
import com.ktun.ailabapp.data.model.TaskFilter
import com.ktun.ailabapp.data.model.TaskStatus
import com.ktun.ailabapp.presentation.ui.components.BottomNavigationBar
import androidx.compose.runtime.SideEffect

@Composable
fun ProjectDetailScreen(
    projectId: String,
    viewModel: ProjectDetailViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToProjects: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredTasks = viewModel.getFilteredTasks()

    // Ekran boyutlarını al
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    //Dialog State
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    // SideEffect - recomposition'da çalışmaz, sadece ilk kez
    SideEffect {
        println("📱 SideEffect: Loading project $projectId")
        viewModel.loadProjectDetails(projectId)
    }

    // Task detay dialogu
    selectedTask?.let { task ->
        TaskDetailDialog(
            task = task,
            onDismiss = { selectedTask = null },
            onStatusChange = { newStatus ->
                viewModel.updateTaskStatus(task.id, newStatus)
                selectedTask = null
            },
            screenWidth = screenWidth,
            screenHeight = screenHeight
        )
    }

    println("🖼️ Screen recomposed - tasks: ${uiState.tasks.size}")

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 1,
                onHomeClick = onNavigateToHome,
                onProjectsClick = onNavigateToProjects,
                onChatClick = onNavigateToChat,
                onProfileClick = onNavigateToProfile
            )
        },
        containerColor = Color(0xFF071372),
        contentWindowInsets = WindowInsets.systemBars
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ÜST KISIM - Koyu mavi header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF071372))
                    .padding(screenWidth * 0.04f)
            ) {
                // Back button ve başlık
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = screenHeight * 0.025f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(screenWidth * 0.11f)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(screenWidth * 0.07f)
                        )
                    }

                    Spacer(modifier = Modifier.width(screenWidth * 0.02f))

                    Text(
                        text = uiState.project?.title ?: "Ai Lab - Demirağ",
                        fontSize = (screenWidth.value * 0.05f).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // İÇERİK ALANI - Açık gri/beyaz
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = screenWidth * 0.075f, topEnd = screenWidth * 0.075f))
                    .background(Color(0xFFE8EAF6))
                    .padding(screenWidth * 0.04f)
            ) {
                // Güncel Proje Durumu Kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(screenWidth * 0.04f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(screenWidth * 0.04f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Güncel Proje Durumu",
                            fontSize = (screenWidth.value * 0.04f).sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF071372)
                        )
                        Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                        Text(
                            text = "KTR Hazırlık Aşaması",
                            fontSize = (screenWidth.value * 0.05f).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE53935)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                // Filtre Butonları
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.02f)
                ) {
                    FilterChip(
                        text = "All",
                        isSelected = uiState.selectedFilter == TaskFilter.ALL,
                        onClick = { viewModel.setFilter(TaskFilter.ALL) },
                        modifier = Modifier.weight(1f),
                        screenWidth = screenWidth,
                        screenHeight = screenHeight
                    )
                    FilterChip(
                        text = "To - Do",
                        isSelected = uiState.selectedFilter == TaskFilter.TO_DO,
                        onClick = { viewModel.setFilter(TaskFilter.TO_DO) },
                        modifier = Modifier.weight(1f),
                        screenWidth = screenWidth,
                        screenHeight = screenHeight
                    )
                    FilterChip(
                        text = "In Prog",
                        isSelected = uiState.selectedFilter == TaskFilter.IN_PROGRESS,
                        onClick = { viewModel.setFilter(TaskFilter.IN_PROGRESS) },
                        modifier = Modifier.weight(1f),
                        screenWidth = screenWidth,
                        screenHeight = screenHeight
                    )
                    FilterChip(
                        text = "Done",
                        isSelected = uiState.selectedFilter == TaskFilter.DONE,
                        onClick = { viewModel.setFilter(TaskFilter.DONE) },
                        modifier = Modifier.weight(1f),
                        screenWidth = screenWidth,
                        screenHeight = screenHeight
                    )
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                // Görev Listesi
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.015f)
                ) {
                    items(filteredTasks) { task ->
                        TaskCard(
                            task = task,
                            onClick = { selectedTask = task },
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(screenHeight * 0.05f),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF071372) else Color(0xFFB0B8D4),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(screenWidth * 0.05f)
    ) {
        Text(
            text = text,
            fontSize = (screenWidth.value * 0.03f).sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit = {},
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(screenWidth * 0.04f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenWidth * 0.03f)
        ) {
            Text(
                text = task.title,
                fontSize = (screenWidth.value * 0.04f).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF071372)
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.01f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tarih ve saat
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF071372),
                        modifier = Modifier.size(screenWidth * 0.045f)
                    )
                    Spacer(modifier = Modifier.width(screenWidth * 0.015f))
                    Text(
                        text = "${task.dueDate} ${task.dueTime}",
                        fontSize = (screenWidth.value * 0.032f).sp,
                        color = Color(0xFF071372)
                    )
                }

                // Durum badge
                Surface(
                    color = when (task.status) {
                        TaskStatus.IN_PROGRESS -> Color(0xFFFF9800).copy(alpha = 0.2f)
                        TaskStatus.TO_DO -> Color(0xFF9FA8DA).copy(alpha = 0.3f)
                        TaskStatus.DONE -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(screenWidth * 0.03f)
                ) {
                    Text(
                        text = when (task.status) {
                            TaskStatus.IN_PROGRESS -> "In Progress"
                            TaskStatus.TO_DO -> "To - Do"
                            TaskStatus.DONE -> "Done"
                        },
                        fontSize = (screenWidth.value * 0.03f).sp,
                        fontWeight = FontWeight.Medium,
                        color = when (task.status) {
                            TaskStatus.IN_PROGRESS -> Color(0xFFFF9800)
                            TaskStatus.TO_DO -> Color(0xFF5C6BC0)
                            TaskStatus.DONE -> Color(0xFF4CAF50)
                        },
                        modifier = Modifier.padding(
                            horizontal = screenWidth * 0.03f,
                            vertical = screenHeight * 0.0075f
                        )
                    )
                }
            }
        }
    }
}

// Yeni Dialog Composable
@Composable
fun TaskDetailDialog(
    task: Task,
    onDismiss: () -> Unit,
    onStatusChange: (TaskStatus) -> Unit,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = task.title,
                    fontSize = (screenWidth.value * 0.05f).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF071372)
                )
                Spacer(modifier = Modifier.height(screenHeight * 0.005f))
                Text(
                    text = "Takım Kaptanı: ${task.takimKaptani}",
                    fontSize = (screenWidth.value * 0.035f).sp,
                    color = Color(0xFF071372).copy(alpha = 0.7f)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Tarih ve Saat
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = screenHeight * 0.01f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF071372),
                        modifier = Modifier.size(screenWidth * 0.05f)
                    )
                    Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                    Text(
                        text = "${task.dueDate} ${task.dueTime}",
                        fontSize = (screenWidth.value * 0.035f).sp,
                        color = Color(0xFF071372)
                    )
                }

                // Durum - TIKLANABİLİR
                Box {
                    Surface(
                        color = when (task.status) {
                            TaskStatus.IN_PROGRESS -> Color(0xFFFF9800).copy(alpha = 0.2f)
                            TaskStatus.TO_DO -> Color(0xFF9FA8DA).copy(alpha = 0.3f)
                            TaskStatus.DONE -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        },
                        shape = RoundedCornerShape(screenWidth * 0.03f),
                        modifier = Modifier
                            .padding(vertical = screenHeight * 0.01f)
                            .clickable { showStatusMenu = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                horizontal = screenWidth * 0.03f,
                                vertical = screenHeight * 0.0075f
                            )
                        ) {
                            Text(
                                text = when (task.status) {
                                    TaskStatus.IN_PROGRESS -> "In Progress"
                                    TaskStatus.TO_DO -> "To - Do"
                                    TaskStatus.DONE -> "Done"
                                },
                                fontSize = (screenWidth.value * 0.03f).sp,
                                fontWeight = FontWeight.Medium,
                                color = when (task.status) {
                                    TaskStatus.IN_PROGRESS -> Color(0xFFFF9800)
                                    TaskStatus.TO_DO -> Color(0xFF5C6BC0)
                                    TaskStatus.DONE -> Color(0xFF4CAF50)
                                }
                            )
                            Spacer(modifier = Modifier.width(screenWidth * 0.01f))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Durum değiştir",
                                tint = when (task.status) {
                                    TaskStatus.IN_PROGRESS -> Color(0xFFFF9800)
                                    TaskStatus.TO_DO -> Color(0xFF5C6BC0)
                                    TaskStatus.DONE -> Color(0xFF4CAF50)
                                },
                                modifier = Modifier.size(screenWidth * 0.04f)
                            )
                        }
                    }

                    // Dropdown Menu
                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(screenWidth * 0.03f)
                                            .background(
                                                Color(0xFF9FA8DA),
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                                    Text("To - Do", fontSize = (screenWidth.value * 0.035f).sp)
                                }
                            },
                            onClick = {
                                onStatusChange(TaskStatus.TO_DO)
                                showStatusMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(screenWidth * 0.03f)
                                            .background(
                                                Color(0xFFFF9800),
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                                    Text("In Progress", fontSize = (screenWidth.value * 0.035f).sp)
                                }
                            },
                            onClick = {
                                onStatusChange(TaskStatus.IN_PROGRESS)
                                showStatusMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(screenWidth * 0.03f)
                                            .background(
                                                Color(0xFF4CAF50),
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                                    Text("Done", fontSize = (screenWidth.value * 0.035f).sp)
                                }
                            },
                            onClick = {
                                onStatusChange(TaskStatus.DONE)
                                showStatusMenu = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                // Detaylı Açıklama
                Text(
                    text = "Görev Detayları",
                    fontSize = (screenWidth.value * 0.04f).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF071372)
                )

                Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                Text(
                    text = task.detayAciklamasi,
                    fontSize = (screenWidth.value * 0.035f).sp,
                    color = Color(0xFF071372).copy(alpha = 0.8f),
                    lineHeight = (screenWidth.value * 0.05f).sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Kapat",
                    color = Color(0xFF071372),
                    fontWeight = FontWeight.Bold,
                    fontSize = (screenWidth.value * 0.035f).sp
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(screenWidth * 0.05f)
    )
}