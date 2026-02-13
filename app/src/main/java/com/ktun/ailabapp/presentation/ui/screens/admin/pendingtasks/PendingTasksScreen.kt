package com.ktun.ailabapp.presentation.ui.screens.admin.pendingtasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ktun.ailabapp.data.remote.dto.response.PendingTaskResponse
import com.ktun.ailabapp.presentation.ui.components.StaggeredAnimatedItem
import com.ktun.ailabapp.ui.theme.BackgroundLight
import com.ktun.ailabapp.ui.theme.InfoBlue
import com.ktun.ailabapp.ui.theme.PrimaryBlue
import com.ktun.ailabapp.ui.theme.SuccessGreen
import com.ktun.ailabapp.ui.theme.WarningOrange
import com.ktun.ailabapp.ui.theme.White
import com.ktun.ailabapp.util.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingTasksScreen(
    onNavigateBack: () -> Unit,
    viewModel: PendingTasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTask by remember { mutableStateOf<PendingTaskResponse?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isRefreshing = false
        }
    }

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            // Toast or Snackbar could be shown here
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Puanlama Bekleyenler", color = White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.loadPendingTasks()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading && !isRefreshing -> {
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
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Hata: ${uiState.errorMessage}", color = Color.Red)
                            Button(onClick = { viewModel.loadPendingTasks() }) {
                                Text("Tekrar Dene")
                            }
                        }
                    }
                }
                uiState.tasks.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Puanlanacak görev yok.")
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.tasks.size) { index ->
                            StaggeredAnimatedItem(index = index) {
                                PendingTaskItem(
                                    task = uiState.tasks[index],
                                    onRateClick = { selectedTask = uiState.tasks[index] }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedTask != null) {
        ScorePickerDialog(
            taskTitle = selectedTask!!.title,
            onDismiss = { selectedTask = null },
            onConfirm = { scoreCategory ->
                viewModel.assignScore(selectedTask!!.id, scoreCategory)
                selectedTask = null
            }
        )
    }
}

@Composable
fun PendingTaskItem(
    task: PendingTaskResponse,
    onRateClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )
                
                Surface(
                    color = when (task.status) {
                        0 -> WarningOrange
                        1 -> InfoBlue
                        2 -> SuccessGreen
                        else -> Color.Gray
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (task.status) {
                            0 -> "Todo"
                            1 -> "In Progress"
                            2 -> "Done"
                            else -> "Unknown"
                        },
                        color = White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Proje: ${task.projectName ?: "Bilinmiyor"}", fontSize = 14.sp, color = Color.Gray)
            Text("Atanan: ${task.assigneeName ?: "Atanmamış"}", fontSize = 14.sp, color = Color.Gray)
            Text("Tarih: ${formatDate(task.createdAt)}", fontSize = 12.sp, color = Color.LightGray)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onRateClick,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Puanla")
            }
        }
    }
}

@Composable
fun ScorePickerDialog(
    taskTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<Int?>(null) }

    val categories = listOf(
        0 to "0 Puan (Etkisiz / Rutin)",
        1 to "+0.25 Puan (Düşük Öncelikli)",
        2 to "+1.00 Puan (Standart Görev)",
        3 to "+1.50 Puan (Kritik / Zorlu)"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = White,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Görevi Puanla",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = taskTitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                categories.forEach { (id, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCategory = id }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategory == id,
                            onClick = { selectedCategory = id },
                            colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("İptal", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { selectedCategory?.let { onConfirm(it) } },
                        enabled = selectedCategory != null,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Kaydet")
                    }
                }
            }
        }
    }
}
