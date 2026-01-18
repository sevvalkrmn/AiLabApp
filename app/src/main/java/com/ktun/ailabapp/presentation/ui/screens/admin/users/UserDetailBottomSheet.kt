// screens/admin/users/UserDetailBottomSheet.kt

package com.ktun.ailabapp.presentation.ui.screens.admin.users

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
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
import coil.compose.AsyncImage
import com.ktun.ailabapp.data.model.User


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailBottomSheet(
    user: User,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onSendAnnouncement: (String, String) -> Unit,
    onManageRoles: (String) -> Unit
) {
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

            ProjectsSection(user = user)
            Spacer(modifier = Modifier.height(16.dp))

            // ✅ onSendAnnouncement callback'i aktar
            ActionButtonsSection(
                onSendAnnouncement = {
                    onSendAnnouncement(user.id, user.fullName) // ✅ DEĞİŞTİR
                },

                onSendNotification = {
                    onManageRoles(user.id) // ✅ DEĞİŞTİR
                },
                onViewProfile = { /* TODO */ },
                onEditPhoto = onEditClick,
                onViewActivity = { /* TODO */ },
                onChangeRFID = { /* TODO */ },
                onDeactivate = { /* TODO */ },
                onDeleteAccount = { /* TODO */ }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun UserDetailHeader(
    user: User,
    onEditClick: () -> Unit,
    onDismiss: () -> Unit
) {
    // ✅ Sadece kullanıcı ismi - icon'lar kaldırıldı
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = user.fullName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A237E)
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
                InfoRow(label = "Point", value = "${user.points ?: 0}")
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
            // ✅ Rolleri virgülle ayrılmış şekilde göster
            Text(
                text = user.roles.joinToString(", "), // "Captain, Member"
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ProjectsSection(user: User) {
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• ${project.name}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )

                    // ✅ Rol badge'i
                    project.role?.let { role ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (role.equals("Captain", ignoreCase = true)) {
                                Color(0xFFFFD700).copy(alpha = 0.2f) // Altın (açık)
                            } else {
                                Color(0xFFE0E0E0) // Gri
                            },
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (role.equals("Captain", ignoreCase = true)) {
                                    Color(0xFFFFD700) // Altın
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
                                    Color(0xFFB8860B) // Koyu altın
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
                color = Color(0xFFFF9800) // Turuncu
            )
            ActionButton(
                text = "Sistemden Sil",
                onClick = onDeleteAccount,
                modifier = Modifier.weight(1f),
                color = Color(0xFFD32F2F) // Kırmızı
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