package com.ktun.ailabapp.presentation.ui.screens.projects

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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ktun.ailabapp.presentation.ui.components.BottomNavigationBar
import com.ktun.ailabapp.presentation.ui.components.DebugButton
import com.ktun.ailabapp.presentation.ui.components.FeedbackDialog

@Composable
fun ProjectsScreen(
    viewModel: ProjectsViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToProjectDetail: (String) -> Unit = {}
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

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 1,
                onHomeClick = onNavigateToHome,
                onProjectsClick = { },
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
                    Spacer(modifier = Modifier.width(screenWidth * 0.1f))  // Sol boşluk

                    Text(
                        text = "Projelerim",
                        fontSize = (screenWidth.value * 0.06f).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    DebugButton(
                        onClick = { showFeedbackDialog = true }
                    )
                }
            }

            // ORTA KISIM - Açık gri/beyaz (E8EAF6)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = screenWidth * 0.06f, topEnd = screenWidth * 0.06f))
                    .background(Color(0xFFE8EAF6))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(screenWidth * 0.04f)
                ) {
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF071372),
                                modifier = Modifier.size(screenWidth * 0.12f)
                            )
                        }
                    } else {
                        // Proje Listesi
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            items(uiState.projects) { project ->
                                ProjectCard(
                                    title = project.title,
                                    description = project.description,
                                    logoResId = project.logoResId,
                                    logoLetter = project.logoLetter,
                                    onClick = {
                                        onNavigateToProjectDetail(project.id)
                                    },
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
fun ProjectCard(
    title: String,
    description: String,
    logoResId: Int? = null,
    logoLetter: String = "A",
    onClick: () -> Unit,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = screenHeight * 0.02f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(screenWidth * 0.15f)
                    .clip(RoundedCornerShape(screenWidth * 0.03f))
                    .background(Color(0xFF071372)),
                contentAlignment = Alignment.Center
            ) {
                if (logoResId != null) {
                    // Gerçek logo varsa onu göster
                    Image(
                        painter = painterResource(id = logoResId),
                        contentDescription = null,
                        modifier = Modifier.size(screenWidth * 0.12f)
                    )
                } else {
                    // Yoksa harf göster
                    Text(
                        text = logoLetter,
                        fontSize = (screenWidth.value * 0.06f).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(screenWidth * 0.04f))

            // Proje Bilgileri
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = (screenWidth.value * 0.045f).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF071372)
                )
                Spacer(modifier = Modifier.height(screenHeight * 0.005f))
                Text(
                    text = description,
                    fontSize = (screenWidth.value * 0.035f).sp,
                    color = Color(0xFF071372).copy(alpha = 0.8f),
                    lineHeight = (screenWidth.value * 0.05f).sp
                )
            }
        }

        // Ayırıcı Çizgi
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = screenHeight * 0.00125f,
            color = Color(0xFF071372).copy(alpha = 0.2f)
        )
    }
}