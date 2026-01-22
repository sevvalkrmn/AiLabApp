package com.ktun.ailabapp.presentation.ui.screens.projects

import android.app.DatePickerDialog
import android.widget.DatePicker
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
import coil.compose.AsyncImage
import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktun.ailabapp.data.remote.dto.response.ProjectMember
import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import com.ktun.ailabapp.data.remote.dto.response.TaskStatistics
import com.ktun.ailabapp.util.formatDate
import com.ktun.ailabapp.ui.theme.BackgroundLight
import com.ktun.ailabapp.ui.theme.PrimaryBlue
import com.ktun.ailabapp.ui.theme.White
import java.util.Calendar

import com.ktun.ailabapp.presentation.ui.components.TaskDetailDialog // ✅ Import added

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
// ...
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
                    item {
                        ProjectInfoCard(
                            project = uiState.project!!,
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }

                    item {
                        TaskStatisticsCard(
                            statistics = uiState.project!!.taskStatistics,
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Görevler",
                                fontSize = (screenWidth.value * 0.045f).sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )

                            if (uiState.canEdit) {
                                IconButton(onClick = { viewModel.showCreateTaskDialog() }) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Görev Ekle",
                                        tint = PrimaryBlue
                                    )
                                }
                            }
                        }
                    }

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
                                onClick = { 
                                    // ✅ Görev detayını API'den çek
                                    viewModel.loadTaskDetail(task.id) 
                                },
                                screenWidth = screenWidth,
                                screenHeight = screenHeight
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                        Text(
                            text = "Proje Üyeleri",
                            fontSize = (screenWidth.value * 0.045f).sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }

                    items(uiState.project!!.captains) { captain ->
                        MemberCard(
                            member = captain,
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }

                    items(uiState.project!!.members) { member ->
                        MemberCard(
                            member = member,
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }

                    if (uiState.canEdit) {
                        item {
                            AdminActionsSection(
                                onAddMember = { viewModel.showAddMemberDialog() },
                                onRemoveMember = { viewModel.showRemoveMemberDialog() },
                                onDeleteProject = { viewModel.showDeleteProjectDialog() },
                                screenWidth = screenWidth,
                                screenHeight = screenHeight,
                                isCaptain = uiState.isCaptain,
                                isAdmin = uiState.isAdmin
                            )
                        }
                    }
                }
            }
        }
    }

    // ✅ YENİ: Görev Detay Dialog (API'den gelen veriye göre)
    if (uiState.isTaskDetailLoading) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = White)
        }
    } else if (uiState.selectedTask != null) {
        TaskDetailDialog(
            task = uiState.selectedTask!!,
            onDismiss = { viewModel.clearSelectedTask() }
        )
    }

    if (uiState.showCreateTaskDialog) {
        CreateTaskDialog(
            members = (uiState.project?.captains.orEmpty() + uiState.project?.members.orEmpty()),
            onDismiss = { viewModel.hideCreateTaskDialog() },
            onConfirm = { title, description, assigneeId, dueDate ->
                viewModel.createTask(title, description, assigneeId, dueDate)
            }
        )
    }

    if (uiState.showAddMemberDialog) {
        AddMemberDialog(
            availableUsers = uiState.availableUsers,
            onDismiss = { viewModel.hideAddMemberDialog() },
            onConfirm = { userId, role ->
                viewModel.addMember(userId, role)
            }
        )
    }

    if (uiState.showRemoveMemberDialog) {
        RemoveMemberDialog(
            members = uiState.project?.members ?: emptyList(),
            onDismiss = { viewModel.hideRemoveMemberDialog() },
            onConfirm = { userId ->
                viewModel.removeMember(userId)
            }
        )
    }

    if (uiState.showDeleteProjectDialog) {
        DeleteProjectDialog(
            projectName = uiState.project?.name ?: "",
            onDismiss = { viewModel.hideDeleteProjectDialog() },
            onConfirm = {
                viewModel.deleteProject(onSuccess = onNavigateBack)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskDialog(
    members: List<ProjectMember>,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, String?, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedAssignee by remember { mutableStateOf<ProjectMember?>(null) }
    var dueDate by remember { mutableStateOf("") }
    var assigneeExpanded by remember { mutableStateOf(false) }

    // Date Picker Logic
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val formattedDate = String.format("%04d-%02d-%02dT23:59:59Z", year, month + 1, dayOfMonth)
            dueDate = formattedDate
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Görev Oluştur") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Açıklama (Opsiyonel)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                ExposedDropdownMenuBox(
                    expanded = assigneeExpanded,
                    onExpandedChange = { assigneeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedAssignee?.fullName ?: "Atanacak Kişi Seç",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kime Atanacak?") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = assigneeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = assigneeExpanded,
                        onDismissRequest = { assigneeExpanded = false }
                    ) {
                        members.forEach { member ->
                            DropdownMenuItem(
                                text = { Text(member.fullName) },
                                onClick = {
                                    selectedAssignee = member
                                    assigneeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(text = if (dueDate.isNotEmpty()) dueDate.take(10) else "Bitiş Tarihi Seç")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, description.ifBlank { null }, selectedAssignee?.userId, dueDate.ifBlank { null })
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Oluştur")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun TaskCard(
    task: TaskResponse,
    onStatusChange: (String) -> Unit,
    onClick: () -> Unit,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                    fontSize = (screenWidth.value * 0.03f).sp,
                    color = PrimaryBlue.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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

// ... ProjectInfoCard, TaskStatisticsCard, StatItem, MemberCard, AdminActionsSection, AddMemberDialog, RemoveMemberDialog, DeleteProjectDialog ...
// (Helper fonksiyonlar aynen kalacak, dosya içeriğini tam olarak yukarıdaki gibi yazıyorum)

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

            Row(verticalAlignment = Alignment.CenterVertically) {
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
fun AdminActionsSection(
    onAddMember: () -> Unit,
    onRemoveMember: () -> Unit,
    onDeleteProject: () -> Unit,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp,
    isCaptain: Boolean,
    isAdmin: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = screenHeight * 0.02f)
    ) {
        Text(
            text = if (isCaptain) "🔧 Proje Yönetimi (Kaptan)" else "🔧 Proje Yönetimi (Admin)",
            fontSize = (screenWidth.value * 0.045f).sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )

        Spacer(Modifier.height(screenHeight * 0.015f))

        // Üye Ekle
        Button(
            onClick = onAddMember,
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.06f),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            )
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null)
            Spacer(Modifier.width(screenWidth * 0.02f))
            Text("Üye Ekle", fontSize = (screenWidth.value * 0.04f).sp)
        }

        Spacer(Modifier.height(screenHeight * 0.01f))

        // Üye Çıkar
        Button(
            onClick = onRemoveMember,
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.06f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800)
            )
        ) {
            Icon(Icons.Default.PersonRemove, contentDescription = null)
            Spacer(Modifier.width(screenWidth * 0.02f))
            Text("Üye Çıkar", fontSize = (screenWidth.value * 0.04f).sp)
        }

        if (isAdmin) {
            Spacer(Modifier.height(screenHeight * 0.01f))

            // Projeyi Sil
            Button(
                onClick = onDeleteProject,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.06f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(screenWidth * 0.02f))
                Text("Projeyi Sil", fontSize = (screenWidth.value * 0.04f).sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberDialog(
    availableUsers: List<User>,
    onDismiss: () -> Unit,
    onConfirm: (userId: String, role: String) -> Unit
) {
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var selectedRole by remember { mutableStateOf("Member") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Üye Ekle") },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedUser?.fullName ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kullanıcı Seç") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        if (availableUsers.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Kullanıcı bulunamadı") },
                                onClick = {},
                                enabled = false
                            )
                        } else {
                            availableUsers.forEach { user ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(user.fullName, style = MaterialTheme.typography.bodyMedium)
                                            Text(
                                                user.email,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedUser = user
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text("Rol:", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))

                Row {
                    FilterChip(
                        selected = selectedRole == "Member",
                        onClick = { selectedRole = "Member" },
                        label = { Text("Member") }
                    )
                    Spacer(Modifier.width(8.dp))
                    FilterChip(
                        selected = selectedRole == "Captain",
                        onClick = { selectedRole = "Captain" },
                        label = { Text("Captain") }
                    )
                }

                Spacer(Modifier.height(8.dp))

                if (selectedRole == "Captain") {
                    Text(
                        "⚠️ Projede zaten Captain varsa eklenemez",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedUser?.let { user ->
                        onConfirm(user.id, selectedRole)
                    }
                },
                enabled = selectedUser != null
            ) {
                Text("Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveMemberDialog(
    members: List<ProjectMember>,
    onDismiss: () -> Unit,
    onConfirm: (userId: String) -> Unit
) {
    var selectedMember by remember { mutableStateOf<ProjectMember?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Üye Çıkar") },
        text = {
            Column {
                Text(
                    "⚠️ Captain çıkarılamaz",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )

                Spacer(Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedMember?.fullName ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Üye Seç") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        if (members.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Çıkarılabilir üye yok") },
                                onClick = {},
                                enabled = false
                            )
                        } else {
                            members.forEach { member ->
                                DropdownMenuItem(
                                    text = { Text("${member.fullName}") },
                                    onClick = {
                                        selectedMember = member
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedMember?.let { member ->
                        onConfirm(member.userId)
                    }
                },
                enabled = selectedMember != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text("Çıkar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun DeleteProjectDialog(
    projectName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Projeyi Sil") },
        text = {
            Column {
                Text("⚠️ Bu işlem geri alınamaz!", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("\"$projectName\" projesini silmek istediğinizden emin misiniz?")
                Spacer(Modifier.height(8.dp))
                Text(
                    "Not: Aktif görevler varsa silme işlemi engellenecektir.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text("Sil")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

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
