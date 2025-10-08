package com.ktun.ailabapp.presentation.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ktun.ailabapp.presentation.ui.components.BottomNavigationBar

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToProjects: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 0,
                onHomeClick = { },  // Zaten Home'dayız
                onProjectsClick = onNavigateToProjects,  // ← BURAYI KONTROL EDİN
                onChatClick = onNavigateToChat,
                onProfileClick = onNavigateToProfile
            ) },
        containerColor = Color(0xFF071372),
        contentWindowInsets = WindowInsets(0.dp)
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
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hi, Welcome Back",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Good Morning",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // ORTA KISIM - Açık beyaz/gri (F4F6FC)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(Color(0xFFF4F6FC))
                    .padding(16.dp)
            ) {
                // Laboratuvar doluluk kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Laboratuvar doluluğu oranı",
                            fontSize = 13.sp,
                            color = Color(0xFF071372),
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(25.dp)
                                .clip(RoundedCornerShape(23.dp))
                                .background(Color(0xFFE0E0E0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.17f)
                                    .clip(RoundedCornerShape(23.dp))
                                    .background(Color(0xFF071372))
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 18.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "3",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Light,
                                    color = Color.White
                                )
                                Text(
                                    text = "18",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Light,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Kullanıcı bilgisi kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF071372)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Sol taraf - Profil resmi ve Points
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "35",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Points",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(120.dp)
                                .background(Color(0xFFF4F6FC))
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Sağ taraf - İkonlu bilgiler
                        Column(modifier = Modifier.weight(1f)) {
                            // Son Giriş Tarihi
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Son Giriş Tarihim",
                                        fontSize = 14.sp,
                                        color = Color(0xFFF4F6FC)
                                    )
                                    Text(
                                        text = "14.12.2003",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Yatay çizgi
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(1.dp)
                                    .background(Color(0xFFF4F6FC))
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Laboratuvardaki Takım
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Group,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Laboratuvardaki",
                                        fontSize = 14.sp,
                                        color = Color(0xFFF4F6FC)
                                    )
                                    Text(
                                        text = "Takım Arkadaşlarım",
                                        fontSize = 14.sp,
                                        color = Color(0xFFF4F6FC)
                                    )
                                    Text(
                                        text = "1/5",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Güncel Görevler
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF071372)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Güncel Görevler",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFF4F6FC)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        TaskItemCompact(
                            title = "ÖTR Yazıla...",
                            frequency = "Monthly",
                            status = "In Progress",
                            statusColor = Color(0xFFFF9800)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TaskItemCompact(
                            title = "1500 Etiket...",
                            frequency = "Monthly",
                            status = "Done",
                            statusColor = Color(0xFF4CAF50)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))


                // Takım Sıralaması
                TeamRankingCard()
            }
        }
    }
}

@Composable
fun TaskItemCompact(
    title: String,
    frequency: String,
    status: String,
    statusColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .height(60.dp)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color(0xFF071372),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Loop,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content with dividers
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.weight(1f, fill = false)
            )

            // Divider 1
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(20.dp)
                    .background(Color(0xFFD1D5DB))
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Frequency
            Text(
                text = frequency,
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )

            // Divider 2
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(20.dp)
                    .background(Color(0xFFD1D5DB))
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Status
            Text(
                text = status,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = statusColor
            )
        }
    }
}

@Composable
fun TeamRankingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF071372)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .offset(y = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2. sıra (Sol)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(75.dp)
                            .offset(y = (-10).dp)
                            .border(4.dp, Color(0xFFC0C0C0), CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .zIndex(1f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .offset(y = (-15).dp)
                            .clip(CircleShape)
                            .background(Color(0xFFC0C0C0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "2",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 1. sıra (Orta)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(85.dp)
                            .offset(y = (-20).dp)
                            .border(4.dp, Color(0xFFFFD700), CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .zIndex(2f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .offset(y = (-20).dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "1",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 3. sıra (Sağ)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(75.dp)
                            .offset(y = (-10).dp)
                            .border(4.dp, Color(0xFFCD7F32), CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .zIndex(1f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .offset(y = (-15).dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCD7F32)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "3",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}