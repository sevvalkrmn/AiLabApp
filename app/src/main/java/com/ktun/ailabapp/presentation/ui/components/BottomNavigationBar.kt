package com.ktun.ailabapp.presentation.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ktun.ailabapp.ui.theme.PrimaryBlue
import com.ktun.ailabapp.ui.theme.White

@Composable
fun BottomNavigationBar(
    selectedItem: Int = 0,
    onHomeClick: () -> Unit = {},
    onProjectsClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = PrimaryBlue
    ) {
        // Home
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = {
                if (selectedItem != 0) onHomeClick()  // ← Sadece farklı ekrandaysa git
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
                indicatorColor = PrimaryBlue.copy(alpha = 0.3f)
            )
        )

        // Projects
        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = {
                if (selectedItem != 1) onProjectsClick()  // ← Sadece farklı ekrandaysa git
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
                indicatorColor = PrimaryBlue.copy(alpha = 0.3f)
            )
        )

        // Chat/Announcements
        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = {
                if (selectedItem != 2) onChatClick()  // ← Sadece farklı ekrandaysa git
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Duyurular",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Duyurular") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = White,
                selectedTextColor = White,
                unselectedIconColor = White.copy(alpha = 0.6f),
                unselectedTextColor = White.copy(alpha = 0.6f),
                indicatorColor = PrimaryBlue.copy(alpha = 0.3f)
            )
        )

        // Profile
        NavigationBarItem(
            selected = selectedItem == 3,
            onClick = {
                if (selectedItem != 3) onProfileClick()  // ← Sadece farklı ekrandaysa git
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
                indicatorColor = PrimaryBlue.copy(alpha = 0.3f)
            )
        )
    }
}