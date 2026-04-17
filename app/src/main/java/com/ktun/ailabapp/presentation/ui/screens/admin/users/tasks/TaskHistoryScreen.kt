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
import com.ktun.ailabapp.ui.theme.*
import com.ktun.ailabapp.presentation.ui.components.navigation.AiLabTopBar

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
        containerColor = TaskHistoryBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AiLabTopBar(title = "Görev Geçmişi", onBackClick = onNavigateBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppSpacing.lg)
            ) {
            // User Info
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = White,
                shadowElevation = AppDimensions.cardElevation
            ) {
                Column(
                    modifier = Modifier.padding(AppSpacing.lg)
                ) {
                    Text(
                        text = userName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.xxs))
                    Text(
                        text = "Toplam ${uiState.tasks.size} görev",
                        fontSize = 14.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
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

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            // Task List
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
                            color = TextGray
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
                    ) {
                        items(viewModel.getFilteredTasks()) { task ->
                            TaskCard(task = task)
                        }
                    }
                }
            }
            } // inner Column
        } // outer Column
    }
}

@Composable
private fun FilterChip(
    label: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = PrimaryBlue
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color else White,
            contentColor = if (isSelected) White else color
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
        color = White,
        shadowElevation = AppDimensions.cardElevation
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
                    color = PrimaryBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.projectName,
                    fontSize = 12.sp,
                    color = TextGray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = task.createdAt,
                    fontSize = 11.sp,
                    color = TextGray.copy(alpha = 0.7f)
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