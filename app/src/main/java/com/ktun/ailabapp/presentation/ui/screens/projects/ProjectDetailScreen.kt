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

    //Dialog State
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    // SideEffect - recomposition'da çalışmaz, sadece ilk kez
    SideEffect {
        println("📱 SideEffect: Loading project $projectId")
        viewModel.loadProjectDetails(projectId)
    }

    // Task detay dialogu
    selectedTask?.let { task ->  // ← BUNU EKLEYİN
        TaskDetailDialog(
            task = task,
            onDismiss = { selectedTask = null },
            onStatusChange = { newStatus ->
                viewModel.updateTaskStatus(task.id, newStatus)
                selectedTask = null
            }
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
        contentWindowInsets = WindowInsets(0.dp)
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
                    .padding(16.dp)
            ) {
                // Back button ve başlık
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = uiState.project?.title ?: "Ai Lab - Demirağ",
                        fontSize = 20.sp,
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
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(Color(0xFFE8EAF6))
                    .padding(16.dp)
            ) {
                // Güncel Proje Durumu Kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Güncel Proje Durumu",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF071372)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "KTR Hazırlık Aşaması",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE53935)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filtre Butonları
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        text = "All",
                        isSelected = uiState.selectedFilter == TaskFilter.ALL,
                        onClick = { viewModel.setFilter(TaskFilter.ALL) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        text = "To - Do",
                        isSelected = uiState.selectedFilter == TaskFilter.TO_DO,
                        onClick = { viewModel.setFilter(TaskFilter.TO_DO) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        text = "In Prog",
                        isSelected = uiState.selectedFilter == TaskFilter.IN_PROGRESS,
                        onClick = { viewModel.setFilter(TaskFilter.IN_PROGRESS) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        text = "Done",
                        isSelected = uiState.selectedFilter == TaskFilter.DONE,
                        onClick = { viewModel.setFilter(TaskFilter.DONE) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Görev Listesi
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTasks) { task ->
                        TaskCard(
                            task = task,
                            onClick = { selectedTask = task }
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
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF071372) else Color(0xFFB0B8D4),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit = {}
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            Text(
                text = task.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF071372)
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${task.dueDate} ${task.dueTime}",
                        fontSize = 13.sp,
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
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = when (task.status) {
                            TaskStatus.IN_PROGRESS -> "In Progress"
                            TaskStatus.TO_DO -> "To - Do"
                            TaskStatus.DONE -> "Done"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (task.status) {
                            TaskStatus.IN_PROGRESS -> Color(0xFFFF9800)
                            TaskStatus.TO_DO -> Color(0xFF5C6BC0)
                            TaskStatus.DONE -> Color(0xFF4CAF50)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
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
    onStatusChange: (TaskStatus) -> Unit
) {

    var showStatusMenu by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = task.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF071372)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Takım Kaptanı: ${task.takimKaptani}",
                    fontSize = 14.sp,
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
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF071372),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${task.dueDate} ${task.dueTime}",
                        fontSize = 14.sp,
                        color = Color(0xFF071372)
                    )
                }

                // Durum - TIKLANABİLİR (Bu kısmı değiştirin)
                Box {  // ← Surface yerine Box ile sarın
                    Surface(
                        color = when (task.status) {
                            TaskStatus.IN_PROGRESS -> Color(0xFFFF9800).copy(alpha = 0.2f)
                            TaskStatus.TO_DO -> Color(0xFF9FA8DA).copy(alpha = 0.3f)
                            TaskStatus.DONE -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable { showStatusMenu = true }
                    ) {
                        Row(  // ← Text yerine Row kullanın
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = when (task.status) {
                                    TaskStatus.IN_PROGRESS -> "In Progress"
                                    TaskStatus.TO_DO -> "To - Do"
                                    TaskStatus.DONE -> "Done"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = when (task.status) {
                                    TaskStatus.IN_PROGRESS -> Color(0xFFFF9800)
                                    TaskStatus.TO_DO -> Color(0xFF5C6BC0)
                                    TaskStatus.DONE -> Color(0xFF4CAF50)
                                }
                            )
                            Spacer(modifier = Modifier.width(4.dp))  // ← BUNU EKLEYİN
                            Icon(  // ← BUNU EKLEYİN
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Durum değiştir",
                                tint = when (task.status) {
                                    TaskStatus.IN_PROGRESS -> Color(0xFFFF9800)
                                    TaskStatus.TO_DO -> Color(0xFF5C6BC0)
                                    TaskStatus.DONE -> Color(0xFF4CAF50)
                                },
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Dropdown Menu - BUNU EKLEYİN
                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(
                                                Color(0xFF9FA8DA),
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("To - Do")
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
                                            .size(12.dp)
                                            .background(
                                                Color(0xFFFF9800),
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("In Progress")
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
                                            .size(12.dp)
                                            .background(
                                                Color(0xFF4CAF50),
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Done")
                                }
                            },
                            onClick = {
                                onStatusChange(TaskStatus.DONE)
                                showStatusMenu = false
                            }
                        )
                    }
                }  // ← Box'ın kapanışı

                Spacer(modifier = Modifier.height(16.dp))

                // Detaylı Açıklama
                Text(
                    text = "Görev Detayları",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF071372)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = task.detayAciklamasi,
                    fontSize = 14.sp,
                    color = Color(0xFF071372).copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Kapat",
                    color = Color(0xFF071372),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}