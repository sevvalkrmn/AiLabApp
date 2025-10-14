package com.ktun.ailabapp.presentation.ui.screens.announcements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
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
import coil.compose.AsyncImage
import com.ktun.ailabapp.data.model.Announcement
import com.ktun.ailabapp.data.model.AnnouncementFilter
import com.ktun.ailabapp.data.model.AnnouncementType
import com.ktun.ailabapp.presentation.ui.components.BottomNavigationBar

@Composable
fun AnnouncementScreen(
    viewModel: AnnouncementViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToProjects: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredAnnouncements = viewModel.getFilteredAnnouncements()

    var selectedAnnouncement by remember { mutableStateOf<Announcement?>(null) }

    // Dialog
    selectedAnnouncement?.let { announcement ->
        AnnouncementDetailDialog(
            announcement = announcement,
            onDismiss = {
                viewModel.markAsRead(announcement.id)
                selectedAnnouncement = null
            }
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 2,
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
                // Başlık ve bildirim ikonu
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Duyurular",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(
                        onClick = { /* Bildirim ayarları */ },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Bildirimler",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
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
                // Filtre Butonları
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnnouncementFilterChip(
                        text = "Tümü",
                        isSelected = uiState.selectedFilter == AnnouncementFilter.ALL,
                        onClick = { viewModel.setFilter(AnnouncementFilter.ALL) },
                        modifier = Modifier.weight(1f)
                    )
                    AnnouncementFilterChip(
                        text = "Genel",
                        isSelected = uiState.selectedFilter == AnnouncementFilter.GENERAL,
                        onClick = { viewModel.setFilter(AnnouncementFilter.GENERAL) },
                        modifier = Modifier.weight(1f)
                    )
                    AnnouncementFilterChip(
                        text = "Takım",
                        isSelected = uiState.selectedFilter == AnnouncementFilter.TEAM,
                        onClick = { viewModel.setFilter(AnnouncementFilter.TEAM) },
                        modifier = Modifier.weight(1f)
                    )
                    AnnouncementFilterChip(
                        text = "Kişisel",
                        isSelected = uiState.selectedFilter == AnnouncementFilter.PERSONAL,
                        onClick = { viewModel.setFilter(AnnouncementFilter.PERSONAL) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Duyuru Listesi
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAnnouncements) { announcement ->
                        AnnouncementCard(
                            announcement = announcement,
                            onClick = { selectedAnnouncement = announcement }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnnouncementFilterChip(
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
fun AnnouncementCard(
    announcement: Announcement,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (announcement.isRead)
                Color.White.copy(alpha = 0.7f)
            else
                Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (!announcement.isRead)
            BorderStroke(2.dp, Color(0xFF071372).copy(alpha = 0.2f))
        else
            null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sol taraf - İkon veya profil resmi
            if (announcement.type == AnnouncementType.PERSONAL && announcement.senderImage != null) {
                // Profil resmi
                AsyncImage(
                    model = announcement.senderImage,
                    contentDescription = "Profil",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFF071372).copy(alpha = 0.1f), CircleShape)
                )
            } else {
                // Lab ikonu
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color(0xFF071372).copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔬",
                        fontSize = 24.sp
                    )
                }
            }

            // Sağ taraf - İçerik
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp)
            ) {
                Text(
                    text = announcement.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF071372)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = announcement.content,
                    fontSize = 13.sp,
                    color = Color(0xFF071372).copy(alpha = 0.7f),
                    maxLines = 2,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun AnnouncementDetailDialog(
    announcement: Announcement,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // İkon veya profil resmi
                if (announcement.type == AnnouncementType.PERSONAL && announcement.senderImage != null) {
                    AsyncImage(
                        model = announcement.senderImage,
                        contentDescription = "Profil",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFF071372).copy(alpha = 0.1f), CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Color(0xFF071372).copy(alpha = 0.1f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔬",
                            fontSize = 24.sp
                        )
                    }
                }

                Column {
                    Text(
                        text = announcement.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF071372)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = announcement.timestamp,
                        fontSize = 12.sp,
                        color = Color(0xFF071372).copy(alpha = 0.5f)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Kategori badge
                Surface(
                    color = when (announcement.type) {
                        AnnouncementType.ALL -> Color(0xFF9FA8DA).copy(alpha = 0.2f)
                        AnnouncementType.TEAM -> Color(0xFFFF9800).copy(alpha = 0.2f)
                        AnnouncementType.PERSONAL -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = when (announcement.type) {
                            AnnouncementType.ALL -> "Genel Duyuru"
                            AnnouncementType.TEAM -> "Takım Duyurusu"
                            AnnouncementType.PERSONAL -> "Kişisel Mesaj"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (announcement.type) {
                            AnnouncementType.ALL -> Color(0xFF5C6BC0)
                            AnnouncementType.TEAM -> Color(0xFFFF9800)
                            AnnouncementType.PERSONAL -> Color(0xFF4CAF50)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = announcement.content,
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