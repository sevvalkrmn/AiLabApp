package com.ktun.ailabapp.presentation.ui.screens.admin.users.roles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.presentation.ui.screens.admin.roles.ManageRolesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageRolesScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    viewModel: ManageRolesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            kotlinx.coroutines.delay(500)
            onNavigateBack()
        }
    }

    if (uiState.showCaptainWarning) {
        CaptainWarningDialog(
            projectName = uiState.selectedProjectName ?: "",
            onConfirm = { viewModel.confirmCaptainAssignment("Captain") },
            onDismiss = { viewModel.dismissCaptainWarning() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rol Yönetimi") },
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
        if (uiState.isLoadingUser) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF071372))
            }
        } else if (uiState.user == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Kullanıcı yüklenemedi",
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            // ✅ Local variable ile smart cast sorunu çözülür
            val user = uiState.user!!
            val projects = user.projects.orEmpty() // ✅ null-safe

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserInfoCard(user = user)

                if (projects.isEmpty()) {
                    EmptyProjectsCard()
                } else {
                    Text(
                        text = "Proje Seçin:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF071372)
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(projects) { project -> // ✅ Artık smart cast sorunu yok
                            ProjectSelectionCard(
                                projectName = project.name,
                                currentRole = project.role ?: "Member",
                                isSelected = uiState.selectedProjectId == project.id,
                                onClick = {
                                    viewModel.selectProject(
                                        projectId = project.id,
                                        projectName = project.name,
                                        currentRole = project.role
                                    )
                                }
                            )
                        }
                    }
                }

                if (uiState.selectedProjectId != null) {
                    Divider(color = Color.Gray.copy(alpha = 0.3f))

                    Text(
                        text = "Yeni Rol Seçin:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF071372)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        uiState.availableRoles.forEach { role ->
                            RoleButton(
                                role = role,
                                isSelected = uiState.currentRole == role,
                                onClick = { viewModel.selectRole(role) },
                                modifier = Modifier.weight(1f),
                                isLoading = uiState.isLoading
                            )
                        }
                    }
                }

                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun UserInfoCard(user: User) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF071372).copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = user.fullName.take(1).uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF071372)
                    )
                }
            }

            Column {
                Text(
                    text = user.fullName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF071372)
                )
                Text(
                    text = user.email,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ProjectSelectionCard(
    projectName: String,
    currentRole: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF071372).copy(alpha = 0.1f) else Color.White,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF071372) else Color.Gray.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = projectName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF071372)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mevcut Rol: $currentRole",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF071372)
                )
            }
        }
    }
}

@Composable
private fun RoleButton(
    role: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF071372) else Color.White,
            contentColor = if (isSelected) Color.White else Color(0xFF071372)
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFF071372)),
        enabled = !isLoading
    ) {
        if (isLoading && isSelected) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = role,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyProjectsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFEBEE)
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Bu kullanıcının henüz projesi yok",
                fontSize = 14.sp,
                color = Color(0xFFD32F2F)
            )
        }
    }
}

@Composable
private fun CaptainWarningDialog(
    projectName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "⚠️ Kaptan Ataması",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text("\"$projectName\" projesine yeni bir kaptan atamak üzeresiniz.")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "UYARI: Bir projede yalnızca bir kaptan olabilir. Mevcut kaptan varsa otomatik olarak Member rolüne düşürülecektir.",
                    fontSize = 12.sp,
                    color = Color.Red
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF071372)
                )
            ) {
                Text("Devam Et", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        },
        containerColor = Color.White
    )
}
