package com.ktun.ailabapp.presentation.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
                val items = listOf(
                    NavItem(Icons.Default.Home, "Ana Sayfa", 0, { if (selectedItem != 0) onHomeClick() }),
                    NavItem(Icons.Default.List, "Projeler", 1, { if (selectedItem != 1) onProjectsClick() }),
                    NavItem(Icons.Filled.Notifications, "Duyurular", 2, onChatClick),
                    NavItem(Icons.Default.Person, "Profil", 3, { if (selectedItem != 3) onProfileClick() })
                )

                items.forEach { item ->
                    val isSelected = selectedItem == item.index
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.2f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "iconScale"
                    )
                    val iconSize by animateDpAsState(
                        targetValue = if (isSelected) 28.dp else 22.dp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "iconSize"
                    )

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = item.onClick,
                        icon = {
                            if (item.index == 2) {
                                BadgeIcon(
                                    icon = item.icon,
                                    contentDescription = item.label,
                                    badgeCount = unreadAnnouncementCount,
                                    tint = if (isSelected) White else White.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .size(iconSize)
                                        .scale(iconScale)
                                )
                            } else {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier
                                        .size(iconSize)
                                        .scale(iconScale)
                                )
                            }
                        },
                        label = {
                            Text(
                                item.label,
                                fontSize = if (isSelected) 12.sp else 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
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
}

private data class NavItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val index: Int,
    val onClick: () -> Unit
)
