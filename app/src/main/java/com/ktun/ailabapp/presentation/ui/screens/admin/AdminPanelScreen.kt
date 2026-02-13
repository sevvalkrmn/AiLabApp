// screens/admin/AdminPanelScreen.kt

package com.ktun.ailabapp.presentation.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast
import com.ktun.ailabapp.ui.theme.BackgroundLight
import com.ktun.ailabapp.ui.theme.ErrorRed
import com.ktun.ailabapp.ui.theme.PrimaryBlue
import com.ktun.ailabapp.ui.theme.SuccessGreen
import com.ktun.ailabapp.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onNavigateBack: () -> Unit,
    onNavigateToUsersList: () -> Unit = {},
    onNavigateToCreateProject: () -> Unit,
    onNavigateToAllProjects: () -> Unit,
    onNavigateToLabPeople: () -> Unit = {},
    onNavigateToPendingTasks: () -> Unit = {},
    onNavigateToSendAnnouncement: () -> Unit = {},
    viewModel: AdminPanelViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Scaffold(
        containerColor = PrimaryBlue,
        contentWindowInsets = WindowInsets.systemBars
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ✅ ÜST KISIM - Koyu Mavi Header (Home gibi)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
                    .padding(screenWidth * 0.04f)
            ) {
                // Back Button + Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = screenHeight * 0.02f),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(screenWidth * 0.08f)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = White,
                            modifier = Modifier.size(screenWidth * 0.06f)
                        )
                    }

                    Spacer(modifier = Modifier.width(screenWidth * 0.02f))

                    Column {
                        Text(
                            text = "Admin Control Panel",
                            fontSize = (screenWidth.value * 0.05f).sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Text(
                            text = "Welcome back to your panel",
                            fontSize = (screenWidth.value * 0.035f).sp,
                            color = White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // ✅ İÇERİK KISMI - Beyaz Card (Home gibi)
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = screenWidth * 0.04f, vertical = screenHeight * 0.03f),
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.015f)
                ) {
                    AdminPanelItem(
                        title = "Tüm Kullanıcıları Listele",
                        subtitle = "Tüm Kullanıcıları Sayfalar Bir Şekilde Listeler.",
                        onClick = onNavigateToUsersList,
                        screenWidth = screenWidth
                    )
                    AdminPanelItem(
                        title = "Proje Oluştur",
                        subtitle = "Yeni bir proje oluşturur.",
                        onClick = onNavigateToCreateProject,
                        screenWidth = screenWidth
                    )

                    AdminPanelItem(
                        title = "Tüm Projeleri Listele",
                        subtitle = "Tüm Projeleri Sayfalı Olarak Listeler.",
                        onClick = onNavigateToAllProjects,
                        screenWidth = screenWidth
                    )

                    AdminPanelItem(
                        title = "Lab'da Bulunan Kişileri Listele",
                        subtitle = "Anlık Olarak Lab İçerisinde Bulunan Kişileri Gösterir.",
                        onClick = onNavigateToLabPeople,
                        screenWidth = screenWidth
                    )

                    AdminPanelItem(
                        title = "Mevcut Görevleri Puanlandır",
                        subtitle = "Üyelere Atanan Görevlerin Önem Derecesini Belirle",
                        onClick = onNavigateToPendingTasks,
                        screenWidth = screenWidth
                    )

                    AdminPanelItem(
                        title = "Tüm Kullanıcılara Duyuru Gönder",
                        subtitle = "Sisteme Kayıtlı Tüm Kullanıcılara Duyuru Gönderir.",
                        onClick = onNavigateToSendAnnouncement,
                        screenWidth = screenWidth
                    )

                    // ✅ DİNAMİK BUTON (Düzeltildi: 0 = Tüm Üyeler)
                    AdminPanelItem(
                        title = "Lab'a Giriş İznini Ayarla",
                        subtitle = "Laboratuvara Giriş Yetkisini Düzenler.",
                        onClick = { viewModel.toggleAccessMode() },
                        badge = if (uiState.accessMode == 0) "Tüm Üyelere Açık" else "Sadece Adminlere Açık",
                        badgeColor = if (uiState.accessMode == 0) SuccessGreen else ErrorRed, // 0: Yeşil, 1: Kırmızı
                        screenWidth = screenWidth,
                        isLoading = uiState.isLoading
                    )

                    AdminPanelItem(
                        title = "Kapı Kontrolcüsünü Yeniden Başlat",
                        subtitle = "Kapının Kontrolünü Sağlayan Cihazı Yeniden Başlatır.",
                        onClick = { /* TODO */ },
                        screenWidth = screenWidth
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminPanelItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    badge: String? = null,
    badgeColor: Color = ErrorRed,
    screenWidth: androidx.compose.ui.unit.Dp,
    isLoading: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoading, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = White,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.5.dp,
                    color = PrimaryBlue.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )

                        if (isLoading && badge != null) {
                             Spacer(modifier = Modifier.width(8.dp))
                             CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = PrimaryBlue)
                        } else if (badge != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = badgeColor
                            ) {
                                Text(
                                    text = badge,
                                    fontSize = 10.sp,
                                    color = White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = PrimaryBlue.copy(alpha = 0.6f),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
