package com.ktunailab.ailabapp.presentation.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ktunailab.ailabapp.presentation.ui.components.AvatarPickerDialog // ✅ EKLE
import com.ktunailab.ailabapp.presentation.ui.components.BottomNavigationBar
import com.ktunailab.ailabapp.presentation.ui.components.DebugButton
import com.ktunailab.ailabapp.presentation.ui.components.FeedbackDialog
import com.ktunailab.ailabapp.presentation.ui.components.LogoutDialog
import com.ktunailab.ailabapp.presentation.ui.screens.announcement.AnnouncementViewModel
import com.ktunailab.ailabapp.ui.theme.PrimaryBlue
import com.ktunailab.ailabapp.ui.theme.BackgroundLight
import com.ktunailab.ailabapp.ui.theme.White
import com.ktunailab.ailabapp.util.AvatarUtils

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    announcementViewModel: AnnouncementViewModel,
    onNavigateToHome: () -> Unit = {},
    onNavigateToProjects: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val announcementUiState by announcementViewModel.uiState.collectAsState()

    // Ekran boyutlarını al
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val unreadCount = remember(announcementUiState.announcements) {
        announcementUiState.announcements.count { !it.isRead }
    }

    // Dialog states
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAvatarPicker by remember { mutableStateOf(false) } // ✅ EKLE

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

    // Logout Dialog
    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false

                // ✅ Önce duyuruları temizle
                announcementViewModel.clearAnnouncements()

                // Sonra logout yap
                viewModel.logout(onSuccess = {
                    Toast.makeText(context, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
                    onLogout()
                })
            }
        )
    }

    // ✅ Avatar Picker Dialog
    if (showAvatarPicker) {
        AvatarPickerDialog(
            currentAvatarUrl = uiState.avatarUrl,
            onDismiss = { showAvatarPicker = false },
            onAvatarSelected = { avatar ->
                viewModel.updateAvatar(avatar.id)
                showAvatarPicker = false
                Toast.makeText(context, "Avatar güncelleniyor...", Toast.LENGTH_SHORT).show()
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

    // ✅ DEBUG: Avatar kontrolü
    LaunchedEffect(uiState.avatarUrl) {
        android.util.Log.d("ProfileScreen", """
        Avatar URL: '${uiState.avatarUrl}'
        Is Null: ${uiState.avatarUrl == null}
        Is Empty: ${uiState.avatarUrl?.isEmpty()}
        Local Drawable ID: ${AvatarUtils.getAvatarDrawable(uiState.avatarUrl)}
    """.trimIndent())
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 3,
                onHomeClick = onNavigateToHome,
                onProjectsClick = onNavigateToProjects,
                onChatClick = onNavigateToChat,
                onProfileClick = onNavigateToProfile,
                unreadAnnouncementCount = unreadCount
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
                        val localAvatarDrawable = AvatarUtils.getAvatarDrawable(uiState.avatarUrl)

                        if (localAvatarDrawable != null) {
                            // ✅ Local avatar varsa drawable'dan göster
                            Image(
                                painter = painterResource(id = localAvatarDrawable),
                                contentDescription = "Profil Fotoğrafı",
                                modifier = Modifier
                                    .size(screenWidth * 0.35f)
                                    .clip(CircleShape)
                                    .border(screenWidth * 0.01f, White, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else if (!uiState.avatarUrl.isNullOrEmpty()) {
                            // ✅ Remote URL varsa Coil ile yükle
                            AsyncImage(
                                model = uiState.avatarUrl,
                                contentDescription = "Profil Fotoğrafı",
                                modifier = Modifier
                                    .size(screenWidth * 0.35f)
                                    .clip(CircleShape)
                                    .border(screenWidth * 0.01f, White, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // ✅ Avatar yoksa placeholder göster
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
                        text = uiState.fullName,
                        fontSize = (screenWidth.value * 0.06f).sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(screenHeight * 0.005f))

                    Text(
                        text = uiState.email,
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
                                text = uiState.totalScore.toString(),
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
                        onClick = { showAvatarPicker = true }, // ✅ GÜNCELLENDI
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

                    // ÇIKIŞ YAP BUTONU
                    Button(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.07f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(screenWidth * 0.03f)
                    ) {
                        Icon(
                            Icons.Default.ExitToApp,
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