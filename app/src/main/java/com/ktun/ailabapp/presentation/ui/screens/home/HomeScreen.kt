package com.ktun.ailabapp.presentation.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ktun.ailabapp.presentation.ui.components.BottomNavigationBar
import com.ktun.ailabapp.presentation.ui.components.DebugButton
import com.ktun.ailabapp.presentation.ui.components.FeedbackDialog

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
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

    // HomeScreen.kt içinde bir yere ekleyin
    Button(onClick = { onNavigateToProjects() }) {
        Text("Test: Projects'e Git")
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 0,
                onHomeClick = { },
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
            // ÜST KISIM - Koyu mavi (071372)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF071372))
                    .padding(screenWidth * 0.04f)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = screenHeight * 0.02f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hi, ${uiState.userName}",
                            fontSize = (screenWidth.value * 0.045f).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = uiState.greeting,
                            fontSize = (screenWidth.value * 0.032f).sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    DebugButton(
                        onClick = { showFeedbackDialog = true }
                    )
                }
            }

            // ORTA KISIM - Açık beyaz/gri (F4F6FC)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = screenWidth * 0.075f, topEnd = screenWidth * 0.075f))
                    .background(Color(0xFFF4F6FC))
                    .padding(screenWidth * 0.04f)
            ) {
                // Laboratuvar doluluk kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(screenWidth * 0.05f)
                ) {
                    Column(modifier = Modifier.padding(screenWidth * 0.04f)) {
                        Text(
                            text = "Laboratuvar doluluğu oranı",
                            fontSize = (screenWidth.value * 0.032f).sp,
                            color = Color(0xFF071372),
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(screenHeight * 0.012f))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(screenHeight * 0.03f)
                                .clip(RoundedCornerShape(screenWidth * 0.06f))
                                .background(Color(0xFFE0E0E0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.17f)
                                    .clip(RoundedCornerShape(screenWidth * 0.06f))
                                    .background(Color(0xFF071372))
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = screenWidth * 0.045f),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "3",
                                    fontSize = (screenWidth.value * 0.04f).sp,
                                    fontWeight = FontWeight.Light,
                                    color = Color.White
                                )
                                Text(
                                    text = "18",
                                    fontSize = (screenWidth.value * 0.04f).sp,
                                    fontWeight = FontWeight.Light,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.015f))

                // Kullanıcı bilgisi kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF071372)),
                    shape = RoundedCornerShape(screenWidth * 0.05f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(screenWidth * 0.04f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Sol taraf - Profil resmi ve Points
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(screenWidth * 0.175f)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )

                            Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                            Text(
                                text = "35",
                                fontSize = (screenWidth.value * 0.06f).sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Points",
                                fontSize = (screenWidth.value * 0.025f).sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.width(screenWidth * 0.03f))

                        Box(
                            modifier = Modifier
                                .width(screenWidth * 0.005f)
                                .height(screenHeight * 0.15f)
                                .background(Color(0xFFF4F6FC))
                        )

                        Spacer(modifier = Modifier.width(screenWidth * 0.04f))

                        // Sağ taraf - İkonlu bilgiler
                        Column(modifier = Modifier.weight(1f)) {
                            // Son Giriş Tarihi
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(screenWidth * 0.07f)
                                )
                                Spacer(modifier = Modifier.width(screenWidth * 0.03f))
                                Column {
                                    Text(
                                        text = "Son Giriş Tarihim",
                                        fontSize = (screenWidth.value * 0.035f).sp,
                                        color = Color(0xFFF4F6FC)
                                    )
                                    Text(
                                        text = "14.12.2003",
                                        fontSize = (screenWidth.value * 0.04f).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                            // Yatay çizgi
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(screenHeight * 0.001f)
                                    .background(Color(0xFFF4F6FC))
                            )

                            Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                            // Laboratuvardaki Takım
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Group,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(screenWidth * 0.07f)
                                )
                                Spacer(modifier = Modifier.width(screenWidth * 0.03f))
                                Column {
                                    Text(
                                        text = "Laboratuvardaki",
                                        fontSize = (screenWidth.value * 0.035f).sp,
                                        color = Color(0xFFF4F6FC)
                                    )
                                    Text(
                                        text = "Takım Arkadaşlarım",
                                        fontSize = (screenWidth.value * 0.035f).sp,
                                        color = Color(0xFFF4F6FC)
                                    )
                                    Text(
                                        text = "1/5",
                                        fontSize = (screenWidth.value * 0.04f).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(top = screenHeight * 0.002f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.015f))

                // Güncel Görevler - SCROLL EKLENMİŞ
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeight * 0.22f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF071372)),
                    shape = RoundedCornerShape(screenWidth * 0.05f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(screenWidth * 0.04f)
                    ) {
                        Text(
                            text = "Güncel Görevler",
                            fontSize = (screenWidth.value * 0.04f).sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFF4F6FC)
                        )

                        Spacer(modifier = Modifier.height(screenHeight * 0.012f))

                        // Scroll eklenmiş liste
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(screenHeight * 0.01f)
                        ) {
                            TaskItemCompact(
                                title = "ÖTR Yazıla...",
                                frequency = "Monthly",
                                status = "In Progress",
                                statusColor = Color(0xFFFF9800),
                                screenWidth = screenWidth,
                                screenHeight = screenHeight
                            )

                            TaskItemCompact(
                                title = "1500 Etiket...",
                                frequency = "Monthly",
                                status = "Done",
                                statusColor = Color(0xFF4CAF50),
                                screenWidth = screenWidth,
                                screenHeight = screenHeight
                            )

                            TaskItemCompact(
                                title = "UI Tasarım Güncelleme",
                                frequency = "Weekly",
                                status = "To Do",
                                statusColor = Color(0xFF9FA8DA),
                                screenWidth = screenWidth,
                                screenHeight = screenHeight
                            )

                            TaskItemCompact(
                                title = "Backend API Entegrasyonu",
                                frequency = "Monthly",
                                status = "In Progress",
                                statusColor = Color(0xFFFF9800),
                                screenWidth = screenWidth,
                                screenHeight = screenHeight
                            )

                            TaskItemCompact(
                                title = "Test Senaryoları",
                                frequency = "Weekly",
                                status = "Done",
                                statusColor = Color(0xFF4CAF50),
                                screenWidth = screenWidth,
                                screenHeight = screenHeight
                            )

                            TaskItemCompact(
                                title = "Dokümantasyon",
                                frequency = "Monthly",
                                status = "To Do",
                                statusColor = Color(0xFF9FA8DA),
                                screenWidth = screenWidth,
                                screenHeight = screenHeight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.015f))

                // Takım Sıralaması
                TeamRankingCard(screenWidth = screenWidth, screenHeight = screenHeight)
            }
        }
    }
}

@Composable
fun TaskItemCompact(
    title: String,
    frequency: String,
    status: String,
    statusColor: Color,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(screenWidth * 0.04f))
            .background(Color.White)
            .height(screenHeight * 0.075f)
            .padding(screenWidth * 0.025f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(screenWidth * 0.12f)
                .background(
                    color = Color(0xFF071372),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(screenWidth * 0.06f)
            )
        }

        Spacer(modifier = Modifier.width(screenWidth * 0.03f))

        // Content with dividers
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title
            Text(
                text = title,
                fontSize = (screenWidth.value * 0.037f).sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.weight(1f, fill = false)
            )

            // Divider 1
            Spacer(modifier = Modifier.width(screenWidth * 0.02f))
            Box(
                modifier = Modifier
                    .width(screenWidth * 0.0025f)
                    .height(screenHeight * 0.025f)
                    .background(Color(0xFFD1D5DB))
            )
            Spacer(modifier = Modifier.width(screenWidth * 0.02f))

            // Frequency
            Text(
                text = frequency,
                fontSize = (screenWidth.value * 0.032f).sp,
                color = Color(0xFF6B7280)
            )

            // Divider 2
            Spacer(modifier = Modifier.width(screenWidth * 0.02f))
            Box(
                modifier = Modifier
                    .width(screenWidth * 0.0025f)
                    .height(screenHeight * 0.025f)
                    .background(Color(0xFFD1D5))
            )
            Spacer(modifier = Modifier.width(screenWidth * 0.02f))

            // Status
            Text(
                text = status,
                fontSize = (screenWidth.value * 0.032f).sp,
                fontWeight = FontWeight.Medium,
                color = statusColor
            )
        }
    }
}

@Composable
fun TeamRankingCard(
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight * 0.22f),  // ← Yükseklik küçültüldü
        colors = CardDefaults.cardColors(containerColor = Color(0xFF071372)),
        shape = RoundedCornerShape(screenWidth * 0.05f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(screenWidth * 0.04f),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2. sıra (Sol)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(screenWidth * 0.18f)  // ← 0.22'den 0.18'e düşürüldü
                            .border(screenWidth * 0.01f, Color(0xFFC0C0C0), CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                    )

                    Spacer(modifier = Modifier.height(screenHeight * 0.008f))

                    Box(
                        modifier = Modifier
                            .width(screenWidth * 0.1f)  // ← 0.12'den 0.1'e düşürüldü
                            .height(screenHeight * 0.022f)  // ← 0.025'ten 0.022'ye düşürüldü
                            .clip(RoundedCornerShape(screenWidth * 0.025f))
                            .background(Color(0xFFC0C0C0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "2",
                            fontSize = (screenWidth.value * 0.03f).sp,  // ← 0.035'ten 0.03'e düşürüldü
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // 1. sıra (Orta) - Biraz yukarıda
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .offset(y = -(screenHeight * 0.02f))  // ← 0.025'ten 0.02'ye
                ) {
                    Box(
                        modifier = Modifier
                            .size(screenWidth * 0.18f)  // ← 0.22'den 0.18'e düşürüldü
                            .border(screenWidth * 0.01f, Color(0xFFFFD700), CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                    )

                    Spacer(modifier = Modifier.height(screenHeight * 0.008f))

                    Box(
                        modifier = Modifier
                            .width(screenWidth * 0.1f)  // ← 0.12'den 0.1'e düşürüldü
                            .height(screenHeight * 0.022f)
                            .clip(RoundedCornerShape(screenWidth * 0.025f))
                            .background(Color(0xFFFFD700)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "1",
                            fontSize = (screenWidth.value * 0.03f).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // 3. sıra (Sağ)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(screenWidth * 0.18f)  // ← 0.22'den 0.18'e düşürüldü
                            .border(screenWidth * 0.01f, Color(0xFFCD7F32), CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                    )

                    Spacer(modifier = Modifier.height(screenHeight * 0.008f))

                    Box(
                        modifier = Modifier
                            .width(screenWidth * 0.1f)  // ← 0.12'den 0.1'e düşürüldü
                            .height(screenHeight * 0.022f)
                            .clip(RoundedCornerShape(screenWidth * 0.025f))
                            .background(Color(0xFFCD7F32)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "3",
                            fontSize = (screenWidth.value * 0.03f).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}