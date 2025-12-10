package com.ktun.ailabapp.presentation.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ktun.ailabapp.data.remote.dto.response.TaskResponse
import com.ktun.ailabapp.presentation.ui.components.BottomNavigationBar
import com.ktun.ailabapp.presentation.ui.components.DebugButton
import com.ktun.ailabapp.presentation.ui.components.FeedbackDialog
import com.ktun.ailabapp.presentation.ui.screens.announcement.AnnouncementViewModel
import com.ktun.ailabapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

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

    var isRefreshing by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }  // ✅ YENİ EKLE

    // ✅ YENİ: Feedback Dialog
    if (showFeedbackDialog) {
        FeedbackDialog(
            onDismiss = { showFeedbackDialog = false },
            onSubmit = { feedback ->
                android.util.Log.d("HomeScreen", "📝 Feedback: $feedback")
                android.widget.Toast.makeText(context, "Geri bildiriminiz alındı!", android.widget.Toast.LENGTH_SHORT).show()
                showFeedbackDialog = false
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadUserData()
        android.util.Log.d("HomeScreen", "📥 Loading announcements on Home screen...")
        announcementViewModel.loadAnnouncements()
    }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isRefreshing = false
        }
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
                onHomeClick = { },
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
                        text = uiState.greeting,
                        fontSize = (screenWidth.value * 0.035f).sp,
                        color = White.copy(alpha = 0.8f)
                    )
                }

                // ✅ DÜZELTME: align() kaldırıldı
                DebugButton(
                    onClick = { showFeedbackDialog = true }
                )
            }

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
                androidx.compose.material3.pulltorefresh.PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        isRefreshing = true
                        viewModel.refreshUserData()
                        android.util.Log.d("HomeScreen", "🔄 Pull-to-refresh triggered")
                    }
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(screenWidth * 0.04f),
                        verticalArrangement = Arrangement.spacedBy(screenHeight * 0.02f)
                    ) {
                        item {
                            LabOccupancyCard(
                                currentOccupancy = uiState.currentOccupancy,
                                totalCapacity = uiState.totalCapacity,
                                screenWidth = screenWidth,
                                screenHeight = screenHeight
                            )
                        }

                        item {
                            ProfileCard(
                                totalScore = uiState.user?.totalScore ?: 0,
                                avatarUrl = uiState.user?.profileImageUrl,
                                lastEntryDate = uiState.lastEntryDate,
                                teammatesInside = uiState.teammatesInside,
                                totalTeammates = uiState.totalTeammates,
                                screenWidth = screenWidth,
                                screenHeight = screenHeight
                            )
                        }

                        item {
                            CurrentTasksCard(
                                tasks = uiState.currentTasks,
                                screenWidth = screenWidth,
                                screenHeight = screenHeight
                            )
                        }

                        item {
                            BottomCard(
                                topUsers = uiState.topUsers,
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
fun LabOccupancyCard(
    currentOccupancy: Int,
    totalCapacity: Int,
    screenWidth: Dp,
    screenHeight: Dp
) {
    val progress = if (totalCapacity > 0) {
        (currentOccupancy.toFloat() / totalCapacity.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = screenWidth * 0.02f)
    ) {
        Text(
            text = "Laboratuvar doluluğu oranı",
            fontSize = (screenWidth.value * 0.04f).sp,
            color = PrimaryBlue,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(bottom = screenHeight * 0.015f)
                .padding(start = screenWidth * 0.02f)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.031f)
                .background(
                    color = Color(0xFFB8C5D6),
                    shape = RoundedCornerShape(screenWidth * 0.08f)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(
                        color = PrimaryBlue,
                        shape = RoundedCornerShape(screenWidth * 0.08f)
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = screenWidth * 0.035f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$currentOccupancy",
                    color = White,
                    fontSize = (screenWidth.value * 0.035f).sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$totalCapacity",
                    color = White,
                    fontSize = (screenWidth.value * 0.035f).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ProfileCard(
    totalScore: Int,
    avatarUrl: String?,
    lastEntryDate: String?,
    teammatesInside: Int,
    totalTeammates: Int,
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
                if (!avatarUrl.isNullOrEmpty()) {
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
                // Son Giriş Tarihi (Backend'den)
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
                        // ✅ Direkt tarihi göster, boşsa hiçbir şey gözükmez
                        val formattedDate = formatDate(lastEntryDate)
                        if (formattedDate.isNotEmpty()) {  // ✅ Sadece dolu ise göster
                            Text(
                                text = formattedDate,
                                fontSize = (screenWidth.value * 0.04f).sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.015f))

                HorizontalDivider(
                    color = White.copy(alpha = 0.3f),
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(screenHeight * 0.015f))

                // Takım Arkadaşları (Backend'den)
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
                            text = "Lab'daki Takım Arkadaşları",
                            fontSize = (screenWidth.value * 0.03f).sp,
                            color = White.copy(alpha = 0.8f)
                        )
                            Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                            Text(
                                text = "$teammatesInside / $totalTeammates",
                                fontSize = (screenWidth.value * 0.04f).sp,
                                fontWeight = FontWeight.Bold,  // ✅ Sadece sayılar kalın
                                color = White
                            )
                        }
                    }
                }
            }
        }
    }


@Composable
fun CurrentTasksCard(
    tasks: List<TaskResponse>,
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
                tasks.forEachIndexed { index, task ->
                    if (index > 0) {
                        Spacer(modifier = Modifier.height(screenHeight * 0.015f))
                    }

                    TaskItem(
                        icon = Icons.Default.Create,
                        title = task.title.take(12) + if (task.title.length > 12) "..." else "",
                        frequency = "Monthly",
                        status = when (task.status) {
                            "InProgress" -> "In Progress"
                            "Done" -> "Done"
                            "Todo" -> "To Do"
                            else -> task.status
                        },
                        statusColor = when (task.status) {
                            "InProgress" -> Color(0xFFFFA726)
                            "Done" -> Color(0xFF66BB6A)
                            "Todo" -> Color(0xFF42A5F5)
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
    topUsers: List<TopUserItem>,
    screenWidth: Dp,
    screenHeight: Dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight * 0.25f),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
        shape = RoundedCornerShape(screenWidth * 0.04f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(screenWidth * 0.04f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ✅ BAŞLIK - Sadece Material Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = screenHeight * 0.015f)
            ) {
                // Sol icon
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(screenWidth * 0.05f)
                )

                Spacer(modifier = Modifier.width(screenWidth * 0.02f))

                // Başlık metni
                Text(
                    text = "LEADERBOARD",
                    fontSize = (screenWidth.value * 0.04f).sp,
                    fontWeight = FontWeight.Light,
                    color = White,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.width(screenWidth * 0.02f))

                // Sağ icon
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(screenWidth * 0.05f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                LeaderboardUser(
                    user = topUsers.getOrNull(1),
                    borderColor = Color(0xFFC0C0C0),
                    rank = 2,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight
                )

                LeaderboardUser(
                    user = topUsers.getOrNull(0),
                    borderColor = Color(0xFFFFD700),
                    rank = 1,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight,
                    isFirst = true
                )

                LeaderboardUser(
                    user = topUsers.getOrNull(2),
                    borderColor = Color(0xFFFF9800),
                    rank = 3,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight
                )
            }
        }
    }
}

@Composable
fun LeaderboardUser(
    user: TopUserItem?,
    borderColor: Color,
    rank: Int,
    screenWidth: Dp,
    screenHeight: Dp,
    isFirst: Boolean = false
) {
    val avatarSize = if (isFirst) screenWidth * 0.2f else screenWidth * 0.16f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(screenWidth * 0.28f)
    ) {
        if (user != null) {
            Box(contentAlignment = Alignment.Center) {
                if (!user.avatarUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(avatarSize)
                            .clip(CircleShape)
                            .background(borderColor)
                            .padding(3.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(avatarSize)
                            .background(borderColor, CircleShape)
                            .padding(3.dp)
                            .background(White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(avatarSize * 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.008f))

            Text(
                text = user.name.split(" ").firstOrNull() ?: user.name,
                fontSize = (screenWidth.value * 0.032f).sp,
                fontWeight = FontWeight.Bold,
                color = White,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.005f))

            Box(
                modifier = Modifier
                    .background(borderColor, RoundedCornerShape(50))
                    .padding(horizontal = screenWidth * 0.025f, vertical = screenHeight * 0.004f)
            ) {
                Text(
                    text = "${user.score} pts",
                    fontSize = (screenWidth.value * 0.028f).sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .background(White.copy(alpha = 0.2f), CircleShape)
            )
            Spacer(modifier = Modifier.height(screenHeight * 0.008f))
            Box(
                modifier = Modifier
                    .width(screenWidth * 0.15f)
                    .height(screenHeight * 0.015f)
                    .background(White.copy(alpha = 0.2f), RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.height(screenHeight * 0.005f))
            Box(
                modifier = Modifier
                    .width(screenWidth * 0.12f)
                    .height(screenHeight * 0.012f)
                    .background(White.copy(alpha = 0.2f), RoundedCornerShape(50))
            )
        }
    }
}

private fun formatDate(isoDate: String?): String {
    // ✅ Null veya boşsa boş string döndür (hiçbir şey gösterme)
    if (isoDate.isNullOrEmpty()) return ""

    return try {
        android.util.Log.d("HomeScreen", "Parse ediliyor: $isoDate")

        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(isoDate)

        if (date == null) {
            android.util.Log.e("HomeScreen", "Parse başarısız: $isoDate")
            return ""  // ✅ Hata durumunda da boş
        }

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val result = formatter.format(date)
        android.util.Log.d("HomeScreen", "Formatlanmış tarih: $result")
        result

    } catch (e: Exception) {
        android.util.Log.e("HomeScreen", "Tarih parse hatası: ${e.message}, Gelen: $isoDate")
        ""  // ✅ Exception durumunda da boş
    }
}