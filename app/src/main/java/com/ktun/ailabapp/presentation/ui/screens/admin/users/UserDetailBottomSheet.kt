// screens/admin/users/UserDetailBottomSheet.kt

package com.ktun.ailabapp.presentation.ui.screens.admin.users

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ktun.ailabapp.data.model.User
import com.ktun.ailabapp.presentation.ui.screens.admin.users.profile.UpdateProfileImageDialog
import com.ktun.ailabapp.presentation.ui.screens.admin.users.score.AdjustScoreDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailBottomSheet(
    user: User,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onSendAnnouncement: (String, String) -> Unit,
    onManageRoles: (String) -> Unit,
    onViewTaskHistory: (String, String) -> Unit,
    onImageUpdated: () -> Unit = {},
    onScoreUpdated: () -> Unit = {},
    onProjectClick: (String) -> Unit = {},
    onRfidClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {} // ✅ YENİ PARAMETRE
) {
    // ✅ STATE TANIMLA
    var showAdjustScoreDialog by remember { mutableStateOf(false) }
    var showUpdateImageDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            UserInfoCard(user = user)
            Spacer(modifier = Modifier.height(16.dp))

            LabInfoSection(user = user)
            Spacer(modifier = Modifier.height(16.dp))

            RolesSection(user = user)
            Spacer(modifier = Modifier.height(16.dp))

            ProjectsSection(user = user, onProjectClick = onProjectClick)
            Spacer(modifier = Modifier.height(16.dp))

            ActionButtonsSection(
                onSendAnnouncement = {
                    onSendAnnouncement(user.id, user.fullName)
                },
                onSendNotification = {
                    onManageRoles(user.id)
                },
                onViewProfile = {
                    showAdjustScoreDialog = true
                },
                onViewActivity = {
                    onViewTaskHistory(user.id, user.fullName)
                    onDismiss()
                },
                onEditPhoto = {
                    showUpdateImageDialog = true
                },
                onChangeRFID = {
                    onRfidClick(user.id)
                },
                onDeactivate = { /* TODO */ },
                onDeleteAccount = {
                    onDeleteClick(user.id) // ✅ BAĞLA
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // ✅ Dialog
    if (showAdjustScoreDialog) {
        AdjustScoreDialog(
            userId = user.id,
            userName = user.fullName,
            currentScore = user.points ?: 0.0,
            onDismiss = {
                showAdjustScoreDialog = false
                onScoreUpdated()
            }
        )
    }

    if (showUpdateImageDialog) {
        UpdateProfileImageDialog(
            userId = user.id,
            userName = user.fullName,
            currentImageUrl = user.profileImageUrl,
            onDismiss = {
                showUpdateImageDialog = false
                onImageUpdated()
            }
        )
    }
}

@Composable
private fun UserInfoCard(user: User) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF5F5FF),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            AsyncImage(
                model = user.profileImageUrl ?: "https://ui-avatars.com/api/?name=${user.fullName}&background=1A237E&color=fff",
                contentDescription = "Profil resmi",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFF1A237E), CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // User Info
            Column {
                InfoRow(label = "Email", value = user.email)
                Spacer(modifier = Modifier.height(4.dp))
                InfoRow(label = "Phone", value = user.phoneNumber ?: "N/A")
                Spacer(modifier = Modifier.height(4.dp))
                InfoRow(label = "Student ID", value = user.studentNumber ?: "N/A")
                Spacer(modifier = Modifier.height(4.dp))
                InfoRow(label = "Username", value = user.username ?: "N/A")
                Spacer(modifier = Modifier.height(4.dp))
                InfoRow(label = "Status", value = if (user.isActive) "Active" else "Inactive")
                Spacer(modifier = Modifier.height(4.dp))
                InfoRow(label = "Point", value = "${user.points ?: 0.0}")
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row {
        Text(
            text = "$label: ",
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = Color(0xFF1A237E),
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun LabInfoSection(user: User) {
    Column {
        Text(
            text = "Lab's Son Giriş:",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A237E)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = user.lastLabEntry ?: "Henüz giriş yapılmadı",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun RolesSection(user: User) {
    Column {
        Text(
            text = "Roles:",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A237E)
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (user.roles.isNullOrEmpty()) {
            Text(
                text = "Rol atanmamış",
                fontSize = 12.sp,
                color = Color.Gray
            )
        } else {
            Text(
                text = user.roles.joinToString(", "),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ProjectsSection(
    user: User,
    onProjectClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Projects:",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A237E)
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (user.projects.isNullOrEmpty()) {
            Text(
                text = "Henüz proje ataması yapılmamış",
                fontSize = 12.sp,
                color = Color.Gray
            )
        } else {
            user.projects.forEach { project ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProjectClick(project.id) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• ${project.name}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )

                    project.role?.let { role ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (role.equals("Captain", ignoreCase = true)) {
                                Color(0xFFFFD700).copy(alpha = 0.2f)
                            } else {
                                Color(0xFFE0E0E0)
                            },
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (role.equals("Captain", ignoreCase = true)) {
                                    Color(0xFFFFD700)
                                } else {
                                    Color.Gray.copy(alpha = 0.5f)
                                }
                            ),
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text(
                                text = role,
                                fontSize = 10.sp,
                                color = if (role.equals("Captain", ignoreCase = true)) {
                                    Color(0xFFB8860B)
                                } else {
                                    Color.Gray
                                },
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (project != user.projects.last()) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun ActionButtonsSection(
    onSendAnnouncement: () -> Unit,
    onSendNotification: () -> Unit,
    onViewProfile: () -> Unit,
    onEditPhoto: () -> Unit,
    onViewActivity: () -> Unit,
    onChangeRFID: () -> Unit,
    onDeactivate: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                text = "Özel Duyuru Gönder",
                onClick = onSendAnnouncement,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                text = "Yeni Rol Ata / Rolü Çıkar",
                onClick = onSendNotification,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                text = "Puan Ekle / Azalt",
                onClick = onViewProfile,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                text = "Profil Fotoğrafını Değiştir",
                onClick = onEditPhoto,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                text = "Görev Geçmişini Görüntüle",
                onClick = onViewActivity,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                text = "RFID Kart Kayıt Yap",
                onClick = onChangeRFID,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 4
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                text = "Deactive Duruma Getir",
                onClick = onDeactivate,
                modifier = Modifier.weight(1f),
                color = Color(0xFFFF9800)
            )
            ActionButton(
                text = "Sistemden Sil",
                onClick = onDeleteAccount,
                modifier = Modifier.weight(1f),
                color = Color(0xFFD32F2F)
            )
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF1A237E)
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color
        )
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}
