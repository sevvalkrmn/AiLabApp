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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ktun.ailabapp.data.model.Announcement
import com.ktun.ailabapp.data.model.AnnouncementFilter
import com.ktun.ailabapp.data.model.AnnouncementType
import com.ktun.ailabapp.presentation.ui.components.BottomNavigationBar
import com.ktun.ailabapp.presentation.ui.components.DebugButton
import com.ktun.ailabapp.presentation.ui.components.FeedbackDialog
import com.ktun.ailabapp.presentation.ui.components.ShimmerBox // ✅ Import
import com.ktun.ailabapp.ui.theme.*

@Composable
fun AnnouncementScreen(
    viewModel: AnnouncementViewModel,
    onNavigateToHome: () -> Unit = {},
    onNavigateToProjects: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    announcementViewModel: AnnouncementViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadAnnouncements()
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    var showFeedbackDialog by remember { mutableStateOf(false) }

    val unreadCount = remember(uiState.announcements) {
        uiState.announcements.count { !it.isRead }
    }

    if (showFeedbackDialog) {
        FeedbackDialog(
            pageInfo = "announcements-screen",
            onDismiss = { showFeedbackDialog = false },
            onSubmit = { feedback ->
                android.widget.Toast.makeText(context, "Geri bildiriminiz alındı!", android.widget.Toast.LENGTH_SHORT).show()
                showFeedbackDialog = false
            }
        )
    }

    val filteredAnnouncements = remember(uiState.selectedFilter, uiState.announcements) {
        viewModel.getFilteredAnnouncements()
    }

    var selectedAnnouncement by remember { mutableStateOf<Announcement?>(null) }

    selectedAnnouncement?.let { announcement ->
        LaunchedEffect(announcement.id) {
            viewModel.loadAnnouncementDetail(announcement.id)
        }

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
            val count = viewModel.getUnreadCount()
            BottomNavigationBar(
                selectedItem = 2,
                onHomeClick = onNavigateToHome,
                onProjectsClick = onNavigateToProjects,
                onChatClick = onNavigateToChat,
                onProfileClick = onNavigateToProfile,
                unreadAnnouncementCount = count
            )
        },
        containerColor = TaskHistoryBg, // ✅ Arka plan
        contentWindowInsets = WindowInsets.systemBars
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // ✅ HEADER (Kıvrımlı ve Kesintisiz)
            Surface(
                color = PrimaryBlue,
                shape = RoundedCornerShape(bottomStart = screenWidth * 0.1f, bottomEnd = screenWidth * 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars) // ✅ Beyaz çizgiyi kapatır
                        .padding(screenWidth * 0.04f)
                        .padding(top = screenHeight * 0.01f, bottom = screenHeight * 0.02f)
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    // .clip(...) <-- Artık Header kıvrımlı, buna gerek yok
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(screenWidth * 0.04f)
                ) {
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

                    // Duyuru Listesi veya Skeleton
                    if (uiState.isLoading && uiState.announcements.isEmpty()) {
                        // ✅ Shimmer Skeleton
                        AnnouncementScreenSkeleton(screenWidth, screenHeight)
                    } else {
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
}

@Composable
fun AnnouncementScreenSkeleton(screenWidth: androidx.compose.ui.unit.Dp, screenHeight: androidx.compose.ui.unit.Dp) {
    Column(
        verticalArrangement = Arrangement.spacedBy(screenHeight * 0.015f)
    ) {
        repeat(6) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.12f),
                shape = RoundedCornerShape(screenWidth * 0.04f)
            )
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
            containerColor = if (isSelected) PrimaryBlue else FilterChipUnselected,
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
            BorderStroke(screenWidth * 0.00125f, PrimaryBlue)
        else
            null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenWidth * 0.03f),
            horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.03f)
        ) {
            if (announcement.type == AnnouncementType.PERSONAL && announcement.senderImage != null) {
                AsyncImage(
                    model = announcement.senderImage,
                    contentDescription = "Profil",
                    modifier = Modifier
                        .size(screenWidth * 0.12f)
                        .clip(CircleShape)
                        .border(screenWidth * 0.005f, PrimaryBlue.copy(alpha = 0.1f), CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(screenWidth * 0.12f)
                        .background(
                            PrimaryBlue,
                            RoundedCornerShape(screenWidth * 0.03f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.ktun.ailabapp.R.drawable.ailablogo),
                        contentDescription = "AiLab Logo",
                        modifier = Modifier.size(screenWidth * 0.08f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = screenHeight * 0.0025f)
            ) {
                Text(
                    text = announcement.title,
                    fontSize = (screenWidth.value * 0.04f).sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )

                Spacer(modifier = Modifier.height(screenHeight * 0.0075f))

                Text(
                    text = announcement.content,
                    fontSize = (screenWidth.value * 0.032f).sp,
                    color = PrimaryBlue.copy(alpha = 0.7f),
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
                if (announcement.type == AnnouncementType.PERSONAL && announcement.senderImage != null) {
                    AsyncImage(
                        model = announcement.senderImage,
                        contentDescription = "Profil",
                        modifier = Modifier
                            .size(screenWidth * 0.12f)
                            .clip(CircleShape)
                            .border(screenWidth * 0.005f, PrimaryBlue.copy(alpha = 0.1f), CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(screenWidth * 0.12f)
                            .background(
                                PrimaryBlue,
                                RoundedCornerShape(screenWidth * 0.03f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.ktun.ailabapp.R.drawable.ailablogo),
                            contentDescription = "AiLab Logo",
                            modifier = Modifier.size(screenWidth * 0.08f)
                        )
                    }
                }

                Column {
                    Text(
                        text = announcement.title,
                        fontSize = (screenWidth.value * 0.045f).sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(screenHeight * 0.0025f))
                    Text(
                        text = announcement.timestamp,
                        fontSize = (screenWidth.value * 0.03f).sp,
                        color = PrimaryBlue.copy(alpha = 0.5f)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Surface(
                    color = when (announcement.type) {
                        AnnouncementType.ALL -> AnnouncementBadgeBg.copy(alpha = 0.2f)
                        AnnouncementType.TEAM -> WarningOrange.copy(alpha = 0.2f)
                        AnnouncementType.PERSONAL -> SuccessGreen.copy(alpha = 0.2f)
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
                            AnnouncementType.ALL -> AnnouncementBadgeText
                            AnnouncementType.TEAM -> WarningOrange
                            AnnouncementType.PERSONAL -> SuccessGreen
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
                    color = PrimaryBlue.copy(alpha = 0.8f),
                    lineHeight = (screenWidth.value * 0.05f).sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Kapat",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = (screenWidth.value * 0.035f).sp
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(screenWidth * 0.05f)
    )
}
