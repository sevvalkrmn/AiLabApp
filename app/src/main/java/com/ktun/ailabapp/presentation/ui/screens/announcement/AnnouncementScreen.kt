package com.ktun.ailabapp.presentation.ui.screens.announcement

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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ktun.ailabapp.data.model.Announcement
import com.ktun.ailabapp.data.model.AnnouncementFilter
import com.ktun.ailabapp.data.model.AnnouncementType
import com.ktun.ailabapp.presentation.ui.components.BottomNavigationBar
import com.ktun.ailabapp.presentation.ui.components.DebugButton
import com.ktun.ailabapp.presentation.ui.components.FeedbackDialog
import com.ktun.ailabapp.presentation.ui.components.sendFeedbackEmail

@Composable
fun AnnouncementScreen(
    viewModel: AnnouncementViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToProjects: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Ekran boyutlarını al
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Dialog state
    var showFeedbackDialog by remember { mutableStateOf(false) }

    // Feedback Dialog
    if (showFeedbackDialog) {
        FeedbackDialog(
            onDismiss = { showFeedbackDialog = false },
            onSubmit = { feedback ->
                println("📝 Feedback: $feedback")
                showFeedbackDialog = false
            }
        )
    }

    val filteredAnnouncements = remember(uiState.selectedFilter, uiState.announcements) {
        viewModel.getFilteredAnnouncements()
    }

    var selectedAnnouncement by remember { mutableStateOf<Announcement?>(null) }

    // Dialog
    selectedAnnouncement?.let { announcement ->
        AnnouncementDetailDialog(
            announcement = announcement,
            onDismiss = {
                viewModel.markAsRead(announcement.id)
                selectedAnnouncement = null
            },
            screenWidth = screenWidth,
            screenHeight = screenHeight
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
        contentWindowInsets = WindowInsets.systemBars
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
                    .padding(screenWidth * 0.04f)
            ) {
                // Başlık ve debug butonu
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = screenHeight * 0.02f)
                ) {
                    Text(
                        text = "Duyurular",
                        fontSize = (screenWidth.value * 0.06f).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    DebugButton(
                        onClick = {showFeedbackDialog = true},
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }

            // İÇERİK ALANI - ProfileScreen ile aynı yapı
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)  // ← ProfileScreen gibi
                    .clip(RoundedCornerShape(topStart = screenWidth * 0.075f, topEnd = screenWidth * 0.075f))
                    .background(Color(0xFFE8EAF6))
            ) {  // ← Burada padding yok, ProfileScreen gibi
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(screenWidth * 0.04f)  // ← İçerideki Column'da padding
                ) {
                    // Filtre Butonları
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.02f)
                    ) {
                        AnnouncementFilterChip(
                            text = "Tümü",
                            isSelected = uiState.selectedFilter == AnnouncementFilter.ALL,
                            onClick = { viewModel.setFilter(AnnouncementFilter.ALL) },
                            modifier = Modifier.weight(1f),
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                        AnnouncementFilterChip(
                            text = "Genel",
                            isSelected = uiState.selectedFilter == AnnouncementFilter.GENERAL,
                            onClick = { viewModel.setFilter(AnnouncementFilter.GENERAL) },
                            modifier = Modifier.weight(1f),
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                        AnnouncementFilterChip(
                            text = "Takım",
                            isSelected = uiState.selectedFilter == AnnouncementFilter.TEAM,
                            onClick = { viewModel.setFilter(AnnouncementFilter.TEAM) },
                            modifier = Modifier.weight(1f),
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                        AnnouncementFilterChip(
                            text = "Kişisel",
                            isSelected = uiState.selectedFilter == AnnouncementFilter.PERSONAL,
                            onClick = { viewModel.setFilter(AnnouncementFilter.PERSONAL) },
                            modifier = Modifier.weight(1f),
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }

                    Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                    // Duyuru Listesi
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(screenHeight * 0.015f)
                    ) {
                        items(filteredAnnouncements) { announcement ->
                            AnnouncementCard(
                                announcement = announcement,
                                onClick = { selectedAnnouncement = announcement },
                                screenWidth = screenWidth,
                                screenHeight = screenHeight
                            )
                        }
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
    modifier: Modifier = Modifier,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(screenHeight * 0.05f),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF071372) else Color(0xFFB0B8D4),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(screenWidth * 0.05f)
    ) {
        Text(
            text = text,
            fontSize = (screenWidth.value * 0.03f).sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun AnnouncementCard(
    announcement: Announcement,
    onClick: () -> Unit,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
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
        shape = RoundedCornerShape(screenWidth * 0.04f),
        border = if (!announcement.isRead)
            BorderStroke(screenWidth * 0.00125f, Color(0xFF071372))
        else
            null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenWidth * 0.03f),
            horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.03f)
        ) {
            // Sol taraf - İkon veya profil resmi
            if (announcement.type == AnnouncementType.PERSONAL && announcement.senderImage != null) {
                // Profil resmi
                AsyncImage(
                    model = announcement.senderImage,
                    contentDescription = "Profil",
                    modifier = Modifier
                        .size(screenWidth * 0.12f)
                        .clip(CircleShape)
                        .border(screenWidth * 0.005f, Color(0xFF071372).copy(alpha = 0.1f), CircleShape)
                )
            } else {
                // Lab ikonu
                Box(
                    modifier = Modifier
                        .size(screenWidth * 0.12f)
                        .background(
                            Color(0xFF071372).copy(alpha = 0.1f),
                            RoundedCornerShape(screenWidth * 0.03f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔬",
                        fontSize = (screenWidth.value * 0.06f).sp
                    )
                }
            }

            // Sağ taraf - İçerik
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = screenHeight * 0.0025f)
            ) {
                Text(
                    text = announcement.title,
                    fontSize = (screenWidth.value * 0.04f).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF071372)
                )

                Spacer(modifier = Modifier.height(screenHeight * 0.0075f))

                Text(
                    text = announcement.content,
                    fontSize = (screenWidth.value * 0.032f).sp,
                    color = Color(0xFF071372).copy(alpha = 0.7f),
                    maxLines = 2,
                    lineHeight = (screenWidth.value * 0.045f).sp
                )
            }
        }
    }
}

@Composable
fun AnnouncementDetailDialog(
    announcement: Announcement,
    onDismiss: () -> Unit,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.03f)
            ) {
                // İkon veya profil resmi
                if (announcement.type == AnnouncementType.PERSONAL && announcement.senderImage != null) {
                    AsyncImage(
                        model = announcement.senderImage,
                        contentDescription = "Profil",
                        modifier = Modifier
                            .size(screenWidth * 0.12f)
                            .clip(CircleShape)
                            .border(screenWidth * 0.005f, Color(0xFF071372).copy(alpha = 0.1f), CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(screenWidth * 0.12f)
                            .background(
                                Color(0xFF071372).copy(alpha = 0.1f),
                                RoundedCornerShape(screenWidth * 0.03f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔬",
                            fontSize = (screenWidth.value * 0.06f).sp
                        )
                    }
                }

                Column {
                    Text(
                        text = announcement.title,
                        fontSize = (screenWidth.value * 0.045f).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF071372)
                    )
                    Spacer(modifier = Modifier.height(screenHeight * 0.0025f))
                    Text(
                        text = announcement.timestamp,
                        fontSize = (screenWidth.value * 0.03f).sp,
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
                    shape = RoundedCornerShape(screenWidth * 0.03f)
                ) {
                    Text(
                        text = when (announcement.type) {
                            AnnouncementType.ALL -> "Genel Duyuru"
                            AnnouncementType.TEAM -> "Takım Duyurusu"
                            AnnouncementType.PERSONAL -> "Kişisel Mesaj"
                        },
                        fontSize = (screenWidth.value * 0.027f).sp,
                        fontWeight = FontWeight.Medium,
                        color = when (announcement.type) {
                            AnnouncementType.ALL -> Color(0xFF5C6BC0)
                            AnnouncementType.TEAM -> Color(0xFFFF9800)
                            AnnouncementType.PERSONAL -> Color(0xFF4CAF50)
                        },
                        modifier = Modifier.padding(
                            horizontal = screenWidth * 0.03f,
                            vertical = screenHeight * 0.0075f
                        )
                    )
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                Text(
                    text = announcement.content,
                    fontSize = (screenWidth.value * 0.035f).sp,
                    color = Color(0xFF071372).copy(alpha = 0.8f),
                    lineHeight = (screenWidth.value * 0.05f).sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Kapat",
                    color = Color(0xFF071372),
                    fontWeight = FontWeight.Bold,
                    fontSize = (screenWidth.value * 0.035f).sp
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(screenWidth * 0.05f)
    )
}