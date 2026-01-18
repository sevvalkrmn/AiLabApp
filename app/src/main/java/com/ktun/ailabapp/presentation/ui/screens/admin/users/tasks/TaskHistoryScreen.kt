package com.ktun.ailabapp.presentation.ui.screens.admin.users.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ktun.ailabapp.data.remote.dto.response.TaskHistory
import com.ktun.ailabapp.data.remote.dto.response.TaskStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskHistoryScreen(
    userId: String,
    userName: String,
    onNavigateBack: () -> Unit,
    viewModel: TaskHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadTaskHistory(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Görev Geçmişi") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF071372),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFE8EAF6)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // User Info
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = userName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF071372)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Toplam ${uiState.tasks.size} görev",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    label = "Tümü",
                    count = uiState.tasks.size,
                    isSelected = uiState.selectedFilter == null,
                    onClick = { viewModel.setFilter(null) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    label = "Yapılacak",
                    count = uiState.tasks.count { it.status == TaskStatus.TODO },
                    isSelected = uiState.selectedFilter == TaskStatus.TODO,
                    onClick = { viewModel.setFilter(TaskStatus.TODO) },
                    modifier = Modifier.weight(1f),
                    color = Color(TaskStatus.TODO.color)
                )
                FilterChip(
                    label = "Devam",
                    count = uiState.tasks.count { it.status == TaskStatus.IN_PROGRESS },
                    isSelected = uiState.selectedFilter == TaskStatus.IN_PROGRESS,
                    onClick = { viewModel.setFilter(TaskStatus.IN_PROGRESS) },
                    modifier = Modifier.weight(1f),
                    color = Color(TaskStatus.IN_PROGRESS.color)
                )
                FilterChip(
                    label = "Bitti",
                    count = uiState.tasks.count { it.status == TaskStatus.DONE },
                    isSelected = uiState.selectedFilter == TaskStatus.DONE,
                    onClick = { viewModel.setFilter(TaskStatus.DONE) },
                    modifier = Modifier.weight(1f),
                    color = Color(TaskStatus.DONE.color)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Task List
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF071372))
                    }
                }

                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                viewModel.getFilteredTasks().isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Görev bulunamadı",
                            color = Color.Gray
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.getFilteredTasks()) { task ->
                            TaskCard(task = task)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF071372)
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color else Color.White,
            contentColor = if (isSelected) Color.White else color
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, color)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = count.toString(),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun TaskCard(task: TaskHistory) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        Color(task.status.color),
                        RoundedCornerShape(6.dp)
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF071372)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.projectName,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = task.createdAt,
                    fontSize = 11.sp,
                    color = Color.Gray.copy(alpha = 0.7f)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(task.status.color).copy(alpha = 0.2f)
            ) {
                Text(
                    text = task.status.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(task.status.color),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}