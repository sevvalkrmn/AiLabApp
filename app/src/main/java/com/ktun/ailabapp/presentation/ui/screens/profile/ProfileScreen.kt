package com.ktunailab.ailabapp.presentation.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ktunailab.ailabapp.R
import com.ktunailab.ailabapp.presentation.ui.components.BottomNavigationBar
import com.ktunailab.ailabapp.presentation.ui.components.DebugButton
import com.ktunailab.ailabapp.presentation.ui.components.FeedbackDialog
import com.ktunailab.ailabapp.presentation.ui.components.LogoutDialog
import com.ktunailab.ailabapp.ui.theme.PrimaryBlue
import com.ktunailab.ailabapp.ui.theme.BackgroundLight
import com.ktunailab.ailabapp.ui.theme.White

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToProjects: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Ekran boyutlarını al
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Dialog states
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }  // ← YENİ EKLENDI

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

    // Logout Dialog - YENİ EKLENDI
    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout(onSuccess = {
                    Toast.makeText(context, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
                    onLogout()
                })
            }
        )
    }

    // Loading indicator
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 3,
                onHomeClick = onNavigateToHome,
                onProjectsClick = onNavigateToProjects,
                onChatClick = onNavigateToChat,
                onProfileClick = onNavigateToProfile
            )
        },
        containerColor = PrimaryBlue,
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
                    .background(PrimaryBlue)
                    .padding(screenWidth * 0.04f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = screenHeight * 0.02f)
                ) {
                    Text(
                        text = "Profilim",
                        fontSize = (screenWidth.value * 0.06f).sp,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    DebugButton(
                        onClick = { showFeedbackDialog = true },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }

            // İÇERİK ALANI
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = screenWidth * 0.075f, topEnd = screenWidth * 0.075f))
                    .background(BackgroundLight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(screenWidth * 0.06f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(screenHeight * 0.03f))

                    // Profil Fotoğrafı
                    Box(
                        modifier = Modifier.size(screenWidth * 0.35f)
                    ) {
                        if (uiState.avatarUrl != null) {
                            AsyncImage(
                                model = uiState.avatarUrl,
                                contentDescription = "Profil Fotoğrafı",
                                modifier = Modifier
                                    .size(screenWidth * 0.35f)
                                    .clip(CircleShape)
                                    .border(screenWidth * 0.01f, White, CircleShape)
                            )
                        } else {
                            // Avatar yoksa placeholder göster
                            Box(
                                modifier = Modifier
                                    .size(screenWidth * 0.35f)
                                    .clip(CircleShape)
                                    .border(screenWidth * 0.01f, White, CircleShape)
                                    .background(PrimaryBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Varsayılan Avatar",
                                    modifier = Modifier.size(screenWidth * 0.2f),
                                    tint = PrimaryBlue
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                    Text(
                        text = uiState.fullName,  // ← Backend'den gelen fullName
                        fontSize = (screenWidth.value * 0.06f).sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(screenHeight * 0.005f))

                    Text(
                        text = uiState.email,  // ← Backend'den gelen email
                        fontSize = (screenWidth.value * 0.035f).sp,
                        color = PrimaryBlue.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(screenHeight * 0.03f))

                    // Puan Kartı
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = White
                        ),
                        shape = RoundedCornerShape(screenWidth * 0.04f),
                        elevation = CardDefaults.cardElevation(screenWidth * 0.005f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(screenWidth * 0.05f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.totalScore.toString(),  // ← totalScore kullanıldı (points değil)
                                fontSize = (screenWidth.value * 0.12f).sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                            Text(
                                text = "Points",
                                fontSize = (screenWidth.value * 0.04f).sp,
                                color = PrimaryBlue.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(screenHeight * 0.04f))

                    // Menü Öğeleri
                    ProfileMenuItem(
                        icon = Icons.Default.Person,
                        text = "Profil Fotoğrafını Değiştir",
                        onClick = { /* TODO */ },
                        screenWidth = screenWidth,
                        screenHeight = screenHeight
                    )

                    Spacer(modifier = Modifier.height(screenHeight * 0.015f))

                    ProfileMenuItem(
                        icon = Icons.Default.Email,
                        text = "E-posta Adresini Değiştir",
                        onClick = { /* TODO */ },
                        screenWidth = screenWidth,
                        screenHeight = screenHeight
                    )

                    Spacer(modifier = Modifier.height(screenHeight * 0.015f))

                    ProfileMenuItem(
                        icon = Icons.Default.Lock,
                        text = "Şifreyi Değiştir",
                        onClick = { /* TODO */ },
                        screenWidth = screenWidth,
                        screenHeight = screenHeight
                    )

                    Spacer(modifier = Modifier.height(screenHeight * 0.015f))

                    ProfileMenuItem(
                        icon = Icons.Default.Phone,
                        text = "Telefon Numarasını Değiştir",
                        onClick = { /* TODO */ },
                        screenWidth = screenWidth,
                        screenHeight = screenHeight
                    )

                    Spacer(modifier = Modifier.height(screenHeight * 0.04f))

                    // ÇIKIŞ YAP BUTONU - GÜNCELLENDİ
                    Button(
                        onClick = { showLogoutDialog = true },  // ← Dialog'u göster
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.07f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(screenWidth * 0.03f)
                    ) {
                        Icon(
                            Icons.Default.ExitToApp,  // ← Icon değişti
                            contentDescription = "Çıkış Yap",
                            modifier = Modifier.size(screenWidth * 0.05f)
                        )
                        Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                        Text(
                            "Çıkış Yap",
                            fontSize = (screenWidth.value * 0.04f).sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(screenHeight * 0.03f))
                }
            }
        }
    }
}

// ProfileMenuItem fonksiyonu aynı kalacak...
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        shape = RoundedCornerShape(screenWidth * 0.03f),
        elevation = CardDefaults.cardElevation(screenWidth * 0.0025f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenWidth * 0.04f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = if (isDestructive) MaterialTheme.colorScheme.error else PrimaryBlue,
                    modifier = Modifier.size(screenWidth * 0.06f)
                )
                Spacer(modifier = Modifier.width(screenWidth * 0.04f))
                Text(
                    text = text,
                    fontSize = (screenWidth.value * 0.035f).sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDestructive) MaterialTheme.colorScheme.error else PrimaryBlue
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Git",
                tint = PrimaryBlue.copy(alpha = 0.3f),
                modifier = Modifier.size(screenWidth * 0.05f)
            )
        }
    }
}