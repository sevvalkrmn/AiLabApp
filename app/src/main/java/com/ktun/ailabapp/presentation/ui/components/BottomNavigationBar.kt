package com.ktun.ailabapp.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ktun.ailabapp.ui.theme.BackgroundLight
import com.ktun.ailabapp.ui.theme.PrimaryBlue
import com.ktun.ailabapp.ui.theme.White

@Composable
fun BottomNavigationBar(
    selectedItem: Int = 0,
    onHomeClick: () -> Unit = {},
    onProjectsClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    unreadAnnouncementCount: Int = 0
) {
    // ✅ Altına açık renk zemin ekleyerek kıvrımların her yerde görünmesini sağladık
    Surface(
        color = BackgroundLight,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            color = PrimaryBlue,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            shadowElevation = 16.dp
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                // Home
                NavigationBarItem(
                    selected = selectedItem == 0,
                    onClick = {
                        if (selectedItem != 0) onHomeClick()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Ana Sayfa",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Ana Sayfa") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = White,
                        selectedTextColor = White,
                        unselectedIconColor = White.copy(alpha = 0.6f),
                        unselectedTextColor = White.copy(alpha = 0.6f),
                        indicatorColor = White.copy(alpha = 0.2f)
                    )
                )

                // Projects
                NavigationBarItem(
                    selected = selectedItem == 1,
                    onClick = {
                        if (selectedItem != 1) onProjectsClick()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Projeler",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Projeler") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = White,
                        selectedTextColor = White,
                        unselectedIconColor = White.copy(alpha = 0.6f),
                        unselectedTextColor = White.copy(alpha = 0.6f),
                        indicatorColor = White.copy(alpha = 0.2f)
                    )
                )

                // Chat/Announcements
                NavigationBarItem(
                    selected = selectedItem == 2,
                    onClick = onChatClick,
                    icon = {
                        BadgeIcon(
                            icon = Icons.Filled.Notifications,
                            contentDescription = "Duyurular",
                            badgeCount = unreadAnnouncementCount,
                            tint = if (selectedItem == 2) Color.White else Color.White.copy(alpha = 0.6f)
                        )
                    },
                    label = {
                        Text(
                            "Duyurular",
                            fontSize = 12.sp,
                            fontWeight = if (selectedItem == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f),
                        indicatorColor = Color.White.copy(alpha = 0.2f)
                    )
                )

                // Profile
                NavigationBarItem(
                    selected = selectedItem == 3,
                    onClick = {
                        if (selectedItem != 3) onProfileClick()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text("Profil") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = White,
                        selectedTextColor = White,
                        unselectedIconColor = White.copy(alpha = 0.6f),
                        unselectedTextColor = White.copy(alpha = 0.6f),
                        indicatorColor = White.copy(alpha = 0.2f)
                    )
                )
            }
        }
    }
}

