package com.ktun.ailabapp.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(
    selectedItem: Int,
    onHomeClick: () -> Unit,
    onProjectsClick: () -> Unit,
    onChatClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    // Ekran boyutlarını al
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight * 0.08f)  // ← Ekran yüksekliğinin %8'i
            .windowInsetsPadding(WindowInsets.navigationBars),  // ← Sistem barlarına uyum
        color = Color(0xFF071372),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = screenWidth * 0.08f,
                    vertical = screenHeight * 0.01f
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                isSelected = selectedItem == 0,
                onClick = onHomeClick,
                screenWidth = screenWidth,
                screenHeight = screenHeight
            )

            BottomNavItem(
                icon = Icons.Default.Menu,
                isSelected = selectedItem == 1,
                onClick = onProjectsClick,
                screenWidth = screenWidth,
                screenHeight = screenHeight
            )

            BottomNavItem(
                icon = Icons.Default.Notifications,
                isSelected = selectedItem == 2,
                onClick = onChatClick,
                screenWidth = screenWidth,
                screenHeight = screenHeight
            )

            BottomNavItem(
                icon = Icons.Default.Person,
                isSelected = selectedItem == 3,
                onClick = onProfileClick,
                screenWidth = screenWidth,
                screenHeight = screenHeight
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .size(
                width = screenWidth * 0.14f,
                height = screenHeight * 0.06f
            )
            .clip(CircleShape)
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color(0xFF071372) else Color(0xFFB0B8D4),
            modifier = Modifier.size(screenWidth * 0.065f)
        )
    }
}