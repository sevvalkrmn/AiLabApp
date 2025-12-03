package com.ktunailab.ailabapp.presentation.ui.screens.home

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ktunailab.ailabapp.data.remote.dto.response.TaskResponse
import com.ktunailab.ailabapp.presentation.ui.components.BottomNavigationBar
import com.ktunailab.ailabapp.presentation.ui.screens.announcement.AnnouncementViewModel
import com.ktunailab.ailabapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToProjects: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    announcementViewModel: AnnouncementViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val announcementUiState by announcementViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadUserData()

        // ✅ Duyuruları da yükle
        android.util.Log.d("HomeScreen", "📥 Loading announcements on Home screen...")
        announcementViewModel.loadAnnouncements()
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val unreadCount = remember(announcementUiState.announcements) {
        announcementUiState.announcements.count { !it.isRead }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 0,
                onHomeClick = { /* Zaten Home'dayız */ },
                onProjectsClick = onNavigateToProjects,
                onChatClick = onNavigateToChat,
                onProfileClick = onNavigateToProfile,
                unreadAnnouncementCount = unreadCount

            )
        },
        containerColor = PrimaryBlue
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(screenWidth * 0.04f)
                    .padding(top = screenHeight * 0.02f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hi, ${uiState.user?.fullName ?: ""}",
                        fontSize = (screenWidth.value * 0.05f).sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = "Good Night",
                        fontSize = (screenWidth.value * 0.035f).sp,
                        color = White.copy(alpha = 0.8f)
                    )
                }

                IconButton(onClick = { /* Bildirimler */ }) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Bildirimler",
                        tint = White,
                        modifier = Modifier.size(screenWidth * 0.07f)
                    )
                }
            }

            // Ana içerik - Beyaz kart
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = screenHeight * 0.02f),
                colors = CardDefaults.cardColors(containerColor = BackgroundLight),
                shape = RoundedCornerShape(
                    topStart = screenWidth * 0.08f,
                    topEnd = screenWidth * 0.08f
                )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(screenWidth * 0.04f),
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.02f)
                ) {
                    // Laboratuvar Doluluk Oranı
                    item {
                        LabOccupancyCard(
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }

                    // Profil Kartı
                    item {
                        ProfileCard(
                            totalScore = uiState.user?.totalScore ?: 0,  // ← Backend'den puan
                            avatarUrl = uiState.user?.profileImageUrl,  // ← Backend'den avatar
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }

                    // Güncel Görevler
                    item {
                        CurrentTasksCard(
                            tasks = uiState.currentTasks,
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }

                    // Alt kart (3 daire)
                    item {
                        BottomCard(
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LabOccupancyCard(
    screenWidth: Dp,
    screenHeight: Dp
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(screenWidth * 0.04f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenWidth * 0.04f)
        ) {
            Text(
                text = "Laboratuvar doluluğu oranı",
                fontSize = (screenWidth.value * 0.035f).sp,
                color = PrimaryBlue,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.01f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sol taraf - Mavi kapsül
                Box(
                    modifier = Modifier
                        .width(screenWidth * 0.15f)
                        .height(screenHeight * 0.04f)
                        .background(PrimaryBlue, RoundedCornerShape(50))
                        .padding(horizontal = screenWidth * 0.02f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "3",
                        color = White,
                        fontSize = (screenWidth.value * 0.04f).sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Progress bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(screenHeight * 0.04f)
                        .padding(horizontal = screenWidth * 0.02f)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
                )

                // Sağ taraf - Açık gri kapsül
                Box(
                    modifier = Modifier
                        .width(screenWidth * 0.15f)
                        .height(screenHeight * 0.04f)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
                        .padding(horizontal = screenWidth * 0.02f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "18",
                        color = Color.Gray,
                        fontSize = (screenWidth.value * 0.04f).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileCard(
    totalScore: Int,
    avatarUrl: String?,
    screenWidth: Dp,
    screenHeight: Dp
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
        shape = RoundedCornerShape(screenWidth * 0.04f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenWidth * 0.04f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol taraf - Avatar + Puan
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = screenWidth * 0.04f)
            ) {
                // ✅ GÜNCELLENEN Avatar
                if (!avatarUrl.isNullOrEmpty()) {
                    // Firebase URL'den yükle
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Profil Fotoğrafı",
                        modifier = Modifier
                            .size(screenWidth * 0.18f)
                            .clip(CircleShape)
                            .background(White),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder
                    Box(
                        modifier = Modifier
                            .size(screenWidth * 0.18f)
                            .clip(CircleShape)
                            .background(White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(screenWidth * 0.1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                // Puan
                Text(
                    text = "$totalScore",
                    fontSize = (screenWidth.value * 0.08f).sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    text = "Points",
                    fontSize = (screenWidth.value * 0.03f).sp,
                    color = White.copy(alpha = 0.9f)
                )
            }

            // Dikey çizgi
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(screenHeight * 0.12f)
                    .background(White.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.width(screenWidth * 0.04f))

            // Sağ taraf - Bilgiler
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Son Giriş Tarihi
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(screenWidth * 0.05f)
                    )
                    Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                    Column {
                        Text(
                            text = "Son Giriş Tarihim",
                            fontSize = (screenWidth.value * 0.03f).sp,
                            color = White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "14.12.2003",
                            fontSize = (screenWidth.value * 0.04f).sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.015f))

                HorizontalDivider(
                    color = White.copy(alpha = 0.3f),
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(screenHeight * 0.015f))

                // Takım Arkadaşları
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Group,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(screenWidth * 0.05f)
                    )
                    Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                    Column {
                        Text(
                            text = "Laboratuvardaki",
                            fontSize = (screenWidth.value * 0.03f).sp,
                            color = White.copy(alpha = 0.8f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Takım Arkadaşlarım",
                                fontSize = (screenWidth.value * 0.04f).sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                            Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                            Text(
                                text = "1/5",
                                fontSize = (screenWidth.value * 0.04f).sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentTasksCard(
    tasks: List<TaskResponse>,  // ← Backend'den gelen görevler
    screenWidth: Dp,
    screenHeight: Dp
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
        shape = RoundedCornerShape(screenWidth * 0.04f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenWidth * 0.04f)
        ) {
            Text(
                text = "Güncel Görevler",
                fontSize = (screenWidth.value * 0.04f).sp,
                fontWeight = FontWeight.Bold,
                color = White,
                modifier = Modifier.padding(bottom = screenHeight * 0.02f)
            )

            if (tasks.isEmpty()) {
                // Görev yoksa
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = screenHeight * 0.02f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aktif görev yok",
                        fontSize = (screenWidth.value * 0.035f).sp,
                        color = White.copy(alpha = 0.7f)
                    )
                }
            } else {
                // Görevler varsa
                tasks.forEachIndexed { index, task ->
                    if (index > 0) {
                        Spacer(modifier = Modifier.height(screenHeight * 0.015f))
                    }

                    TaskItem(
                        icon = Icons.Default.Create,
                        title = task.title.take(12) + if (task.title.length > 12) "..." else "",  // Max 12 karakter
                        frequency = "Monthly",  // TODO: Backend'den gelebilir
                        status = when (task.status) {
                            "InProgress" -> "In Progress"
                            "Done" -> "Done"
                            "Todo" -> "To Do"
                            else -> task.status
                        },
                        statusColor = when (task.status) {
                            "InProgress" -> Color(0xFFFFA726)  // Turuncu
                            "Done" -> Color(0xFF66BB6A)  // Yeşil
                            "Todo" -> Color(0xFF42A5F5)  // Mavi
                            else -> Color.Gray
                        },
                        screenWidth = screenWidth,
                        screenHeight = screenHeight
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    icon: ImageVector,
    title: String,
    frequency: String,
    status: String,
    statusColor: Color,
    screenWidth: Dp,
    screenHeight: Dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White, RoundedCornerShape(screenWidth * 0.03f))
            .padding(screenWidth * 0.03f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(screenWidth * 0.12f)
                .background(PrimaryBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(screenWidth * 0.06f)
            )
        }

        Spacer(modifier = Modifier.width(screenWidth * 0.03f))

        // Text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = (screenWidth.value * 0.04f).sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            Text(
                text = frequency,
                fontSize = (screenWidth.value * 0.03f).sp,
                color = Color.Gray
            )
        }

        // Status
        Text(
            text = status,
            fontSize = (screenWidth.value * 0.03f).sp,
            fontWeight = FontWeight.Bold,
            color = statusColor
        )
    }
}

@Composable
fun BottomCard(
    screenWidth: Dp,
    screenHeight: Dp
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
        shape = RoundedCornerShape(screenWidth * 0.04f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenWidth * 0.04f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Sol daire - Beyaz
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(screenWidth * 0.15f)
                        .background(White, CircleShape)
                )
                Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                Box(
                    modifier = Modifier
                        .width(screenWidth * 0.1f)
                        .height(screenHeight * 0.01f)
                        .background(White, RoundedCornerShape(50))
                )
            }

            // Orta daire - Sarı (Seçili)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(screenWidth * 0.15f)
                        .background(Color(0xFFFFD54F), CircleShape)
                        .padding(4.dp)
                        .background(White, CircleShape)
                )
                Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                Box(
                    modifier = Modifier
                        .width(screenWidth * 0.1f)
                        .height(screenHeight * 0.01f)
                        .background(Color(0xFFFFD54F), RoundedCornerShape(50))
                )
            }

            // Sağ daire - Turuncu
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(screenWidth * 0.15f)
                        .background(Color(0xFFFF9800), CircleShape)
                        .padding(4.dp)
                        .background(White, CircleShape)
                )
                Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                Box(
                    modifier = Modifier
                        .width(screenWidth * 0.1f)
                        .height(screenHeight * 0.01f)
                        .background(Color(0xFFFF9800), RoundedCornerShape(50))
                )
            }
        }
    }
}