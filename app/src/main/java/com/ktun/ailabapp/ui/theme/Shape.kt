package com.ktun.ailabapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(12.dp),
    large      = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

object AppCustomShapes {
    val pill      = RoundedCornerShape(percent = 50)
    val topBar    = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    val bottomNav = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
}
