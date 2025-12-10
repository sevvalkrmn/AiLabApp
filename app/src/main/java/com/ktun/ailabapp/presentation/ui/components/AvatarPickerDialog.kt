package com.ktun.ailabapp.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource  // ✅ BUNU EKLE
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.ktun.ailabapp.R  // ✅ DOĞRU R IMPORT'U
import com.ktun.ailabapp.ui.theme.PrimaryBlue

@Composable
fun AvatarPickerDialog(
    currentAvatarUrl: String?,
    availableAvatars: List<String>, // ✅ Backend'den gelen avatar URL listesi
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onAvatarSelected: (String) -> Unit // ✅ URL döndürüyoruz artık
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = screenHeight * 0.75f),
            shape = RoundedCornerShape(screenWidth * 0.04f),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(screenWidth * 0.04f)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Avatar Seç",
                        fontSize = (screenWidth.value * 0.05f).sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = PrimaryBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                // Loading durumu
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.3f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                } else if (availableAvatars.isEmpty()) {
                    // Avatar listesi boşsa
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.3f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Avatar bulunamadı",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    // Avatar Grid - 3 sütun
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.03f),
                        verticalArrangement = Arrangement.spacedBy(screenHeight * 0.015f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(availableAvatars) { avatarUrl ->
                            AvatarOptionItem(
                                avatarUrl = avatarUrl,
                                isSelected = currentAvatarUrl == avatarUrl,
                                onClick = {
                                    onAvatarSelected(avatarUrl)
                                    onDismiss()
                                },
                                screenWidth = screenWidth
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                // İptal butonu
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "İptal",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun AvatarOptionItem(
    avatarUrl: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    screenWidth: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .size(screenWidth * 0.22f)
            .clip(CircleShape)
            .background(if (isSelected) PrimaryBlue.copy(alpha = 0.15f) else Color.Transparent)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) PrimaryBlue else Color.Gray.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable { onClick() }
            .padding(if (isSelected) 6.dp else 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // ✅ Coil ile Firebase URL'den resim yükleme
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder
            error = painterResource(id = R.drawable.ic_launcher_foreground) // Hata durumunda
        )
    }
}