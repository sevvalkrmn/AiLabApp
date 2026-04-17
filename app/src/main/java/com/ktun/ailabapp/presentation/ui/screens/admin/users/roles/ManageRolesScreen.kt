package com.ktun.ailabapp.presentation.ui.screens.admin.users.roles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
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
import com.ktun.ailabapp.data.remote.dto.response.ProjectMember
import com.ktun.ailabapp.presentation.ui.components.navigation.AiLabTopBar
import com.ktun.ailabapp.presentation.ui.screens.admin.roles.ManageRolesViewModel
import com.ktun.ailabapp.ui.theme.*

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

    if (uiState.showConfirmDialog) {
        TransferConfirmDialog(
            projectName = uiState.selectedProjectName ?: "",
            newCaptainName = uiState.selectedNewCaptain?.fullName ?: "",
            onConfirm = { viewModel.transferOwnership() },
            onDismiss = { viewModel.dismissConfirmDialog() }
        )
    }

    Scaffold(
        containerColor = TaskHistoryBg
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AiLabTopBar(title = "Kaptan Değişimi", onBackClick = onNavigateBack)
            when {
                uiState.isLoadingUser -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = PrimaryBlue) }

                uiState.user == null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text(text = "Kullanıcı yüklenemedi", color = MaterialTheme.colorScheme.error) }

                else -> {
                    val user = uiState.user!!
                    val captainProjects = uiState.captainProjects
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(AppSpacing.lg),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
                    ) {
                UserInfoCard(user = user)

                if (captainProjects.isEmpty()) {
                    EmptyProjectsCard()
                } else {
                    Text(
                        text = "Kaptan Olduğu Projeyi Seçin:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(captainProjects) { project ->
                            ProjectSelectionCard(
                                projectName = project.name,
                                isSelected = uiState.selectedProjectId == project.id,
                                onClick = {
                                    viewModel.selectProject(
                                        projectId = project.id,
                                        projectName = project.name
                                    )
                                }
                            )
                        }

                        // Proje seçildiyse üyeleri göster
                        if (uiState.selectedProjectId != null) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = TextGray.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Yeni Kaptan Seçin:",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }

                            if (uiState.isLoadingMembers) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = PrimaryBlue,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            } else if (uiState.projectMembers.isEmpty()) {
                                item {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        color = ErrorRed.copy(alpha = 0.1f)
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Bu projede başka üye bulunmuyor",
                                                fontSize = 14.sp,
                                                color = ErrorRed
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(uiState.projectMembers) { member ->
                                    MemberSelectionCard(
                                        member = member,
                                        isSelected = uiState.selectedNewCaptain?.userId == member.userId,
                                        onClick = { viewModel.selectNewCaptain(member) }
                                    )
                                }
                            }

                            // Transfer butonu
                            if (uiState.selectedNewCaptain != null) {
                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { viewModel.showConfirmDialog() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PrimaryBlue
                                        ),
                                        shape = RoundedCornerShape(24.dp),
                                        enabled = !uiState.isLoading
                                    ) {
                                        if (uiState.isLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = White,
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text(
                                                text = "Kaptanlığı Devret",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
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

                if (uiState.isSuccess) {
                    Text(
                        text = "Kaptan değişimi başarıyla tamamlandı!",
                        color = SuccessGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } // else ->
        } // when
        } // outer Column
    }
}

@Composable
private fun UserInfoCard(user: User) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = White,
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
                color = PrimaryBlue.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = user.fullName.take(1).uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }

            Column {
                Text(
                    text = user.fullName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                Text(
                    text = user.email,
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
        }
    }
}

@Composable
private fun ProjectSelectionCard(
    projectName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else White,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) PrimaryBlue else TextGray.copy(alpha = 0.3f)
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
                    color = PrimaryBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mevcut Rol: Kaptan",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryBlue
                )
            }
        }
    }
}

@Composable
private fun MemberSelectionCard(
    member: ProjectMember,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else White,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) PrimaryBlue else TextGray.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(18.dp),
                color = PrimaryBlue.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.fullName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue
                )
                Text(
                    text = member.email,
                    fontSize = 12.sp,
                    color = TextGray
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryBlue
                )
            }
        }
    }
}

@Composable
private fun EmptyProjectsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = ErrorRed.copy(alpha = 0.1f)
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Bu kullanıcının kaptan olduğu proje yok",
                fontSize = 14.sp,
                color = ErrorRed
            )
        }
    }
}

@Composable
private fun TransferConfirmDialog(
    projectName: String,
    newCaptainName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Kaptan Değişimi Onayı",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text("\"$projectName\" projesinin kaptanlığını \"$newCaptainName\" kişisine devretmek istediğinize emin misiniz?")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bu işlem sonucunda mevcut kaptan Member rolüne düşürülecektir.",
                    fontSize = 12.sp,
                    color = ErrorRed
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = PrimaryBlue
                )
            ) {
                Text("Devret", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        },
        containerColor = White
    )
}
